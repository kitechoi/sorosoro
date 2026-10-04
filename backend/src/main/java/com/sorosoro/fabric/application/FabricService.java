package com.sorosoro.fabric.application;

import com.sorosoro.common.exception.*;
import com.sorosoro.common.response.PageResponse;
import com.sorosoro.fabric.domain.*;
import com.sorosoro.fabric.dto.*;
import com.sorosoro.fabric.repository.FabricRepository;
import com.sorosoro.user.domain.User;
import com.sorosoro.user.repository.UserRepository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Service
public class FabricService {
    private final FabricRepository fabrics;
    private final UserRepository users;
    private final JdbcTemplate jdbc;

    public FabricService(FabricRepository fabrics, UserRepository users, JdbcTemplate jdbc) {
        this.fabrics = fabrics;
        this.users = users;
        this.jdbc = jdbc;
    }

    public User user(long id) {
        User user =
                users.findById(id).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (user.isDeleted()) throw new ApiException(ErrorCode.DELETED_USER);
        return user;
    }

    public Fabric owned(long userId, long id) {
        user(userId);
        Fabric f =
                fabrics.findById(id)
                        .orElseThrow(() -> new ApiException(ErrorCode.FABRIC_NOT_FOUND));
        if (!f.getUser().getId().equals(userId)) throw new ApiException(ErrorCode.FORBIDDEN);
        return f;
    }

    @Transactional
    public FabricResponse create(long userId, FabricWriteRequest input) {
        return FabricResponse.from(fabrics.saveAndFlush(input.toEntity(user(userId))));
    }

    @Transactional(readOnly = true)
    public FabricResponse get(long userId, long id) {
        return FabricResponse.from(owned(userId, id));
    }

    @Transactional
    public FabricResponse update(long userId, long id, FabricWriteRequest input) {
        // Lock import metadata before modifying Fabric; enrichment follows the same lock order.
        var items =
                jdbc.queryForList(
                        "SELECT id,material_locked,width_locked,url_locked FROM fabric_import_items"
                            + " WHERE fabric_id=? FOR UPDATE",
                        id);
        Fabric f = owned(userId, id);
        Fabric value = input.toEntity(f.getUser());
        boolean identityChanged =
                !Objects.equals(f.getProductName(), value.getProductName())
                        || !Objects.equals(f.getProductCode(), value.getProductCode())
                        || !Objects.equals(f.getStoreName(), value.getStoreName())
                        || !Objects.equals(f.getColor(), value.getColor())
                        || !Objects.equals(f.getSize(), value.getSize());
        boolean materialChanged =
                !Objects.equals(f.getMaterialComposition(), value.getMaterialComposition());
        boolean widthChanged = !Objects.equals(f.getWidth(), value.getWidth());
        boolean urlChanged = !Objects.equals(f.getProductUrl(), value.getProductUrl());
        f.reviseFrom(value);
        if (identityChanged && !items.isEmpty()) {
            var locks = items.get(0);
            f.clearGeneratedDetails(
                    materialChanged || Boolean.TRUE.equals(locks.get("material_locked")),
                    widthChanged || Boolean.TRUE.equals(locks.get("width_locked")),
                    urlChanged || Boolean.TRUE.equals(locks.get("url_locked")));
            jdbc.update("DELETE FROM fabric_product_photos WHERE fabric_id=?", id);
        }
        jdbc.update(
                "UPDATE fabric_import_items SET edited=true,material_locked=material_locked OR"
                    + " ?,width_locked=width_locked OR ?,url_locked=url_locked OR ? WHERE"
                    + " fabric_id=?",
                materialChanged,
                widthChanged,
                urlChanged,
                id);
        if (identityChanged) {
            jdbc.update(
                    "UPDATE fabric_import_items SET"
                        + " enrichment_status='PENDING',enrichment_attempts=0,enrichment_token=NULL,enrichment_reason=NULL"
                        + " WHERE fabric_id=?",
                    id);
        } else {
            // A manual detail edit can make a completed record incomplete. A running
            // extraction keeps its lease and sees the field locks before applying its result.
            jdbc.update(
                    "UPDATE fabric_import_items SET enrichment_status=?,enrichment_reason=? WHERE"
                        + " fabric_id=? AND enrichment_status NOT IN ('PENDING','PROCESSING')",
                    f.detailsComplete() ? "COMPLETE" : "INCOMPLETE",
                    f.detailsComplete() ? null : "MISSING_DETAILS",
                    id);
        }
        return FabricResponse.from(fabrics.saveAndFlush(f));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> productPhoto(long userId, long id) {
        owned(userId, id);
        var rows =
                jdbc.queryForList(
                        "SELECT bytes,mime_type FROM fabric_product_photos WHERE fabric_id=?", id);
        if (rows.isEmpty()) throw new ApiException(ErrorCode.FABRIC_NOT_FOUND);
        return rows.get(0);
    }

    @Transactional
    public void delete(long userId, long id) {
        jdbc.queryForList("SELECT id FROM fabric_import_items WHERE fabric_id=? FOR UPDATE", id);
        Fabric f = owned(userId, id);
        // Photo storage is not implemented in AI-001; avoid orphaning a future attached image.
        if (jdbc.queryForObject(
                        "SELECT count(*) FROM photos WHERE owner_type='FABRIC' AND owner_id=?",
                        Long.class,
                        id)
                > 0) throw new ApiException(ErrorCode.IMPORT_CONFLICT);
        jdbc.update(
                "UPDATE fabric_import_items SET"
                    + " status='DISMISSED',enrichment_status='SKIPPED',enrichment_token=NULL WHERE"
                    + " fabric_id=?",
                id);
        repositoryDelete(f);
    }

    private void repositoryDelete(Fabric f) {
        fabrics.delete(f);
        fabrics.flush();
    }

    @Transactional(readOnly = true)
    public PageResponse<FabricResponse> list(
            long userId,
            String keyword,
            String storeName,
            RepurchaseIntention intention,
            int page,
            int size) {
        user(userId);
        Specification<Fabric> spec = (root, q, cb) -> cb.equal(root.get("user").get("id"), userId);
        if (keyword != null && !keyword.isBlank()) {
            String term =
                    "%"
                            + keyword.trim()
                                    .toLowerCase()
                                    .replace("\\", "\\\\")
                                    .replace("%", "\\%")
                                    .replace("_", "\\_")
                            + "%";
            spec =
                    spec.and(
                            (root, q, cb) ->
                                    cb.or(
                                            cb.like(cb.lower(root.get("name")), term, '\\'),
                                            cb.like(cb.lower(root.get("productName")), term, '\\'),
                                            cb.like(
                                                    cb.lower(root.get("productCode")),
                                                    term,
                                                    '\\')));
        }
        if (storeName != null && !storeName.isBlank())
            spec = spec.and((r, q, c) -> c.equal(r.get("storeName"), storeName));
        if (intention != null)
            spec = spec.and((r, q, c) -> c.equal(r.get("repurchaseIntention"), intention));
        return PageResponse.from(
                fabrics.findAll(
                                spec,
                                PageRequest.of(
                                        Math.max(0, page),
                                        Math.min(100, Math.max(1, size)),
                                        Sort.by(Sort.Direction.DESC, "createdAt", "id")))
                        .map(FabricResponse::from));
    }
}
