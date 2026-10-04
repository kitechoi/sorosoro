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
        jdbc.queryForList("SELECT id FROM fabric_import_items WHERE fabric_id=? FOR UPDATE", id);
        Fabric f = owned(userId, id);
        f.reviseFrom(input.toEntity(f.getUser()));
        jdbc.update("UPDATE fabric_import_items SET edited=true WHERE fabric_id=?", id);
        return FabricResponse.from(fabrics.saveAndFlush(f));
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
