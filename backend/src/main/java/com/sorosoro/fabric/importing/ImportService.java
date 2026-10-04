package com.sorosoro.fabric.importing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorosoro.common.exception.*;
import com.sorosoro.fabric.application.FabricService;
import com.sorosoro.fabric.domain.Fabric;
import com.sorosoro.fabric.dto.FabricWriteRequest;
import com.sorosoro.fabric.repository.FabricRepository;
import com.sorosoro.user.domain.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.*;
import java.util.*;

@Service
public class ImportService {
    public record Work(UUID id, UUID token, byte[] image, String mimeType, String seller) {}

    public record Enrichment(
            long itemId, UUID token, String seller, String name, String productCode) {}

    private final JdbcTemplate db;
    private final FabricService fabrics;
    private final FabricRepository repository;
    private final ObjectMapper mapper;

    public ImportService(
            JdbcTemplate db,
            FabricService fabrics,
            FabricRepository repository,
            ObjectMapper mapper) {
        this.db = db;
        this.fabrics = fabrics;
        this.repository = repository;
        this.mapper = mapper;
    }

    private Map<String, Object> owned(long userId, UUID id, boolean lock) {
        fabrics.user(userId);
        var rows =
                db.queryForList(
                        "SELECT id,user_id,status,attempts,error_message,created_at,(image IS NOT"
                                + " NULL) image_available FROM fabric_import_jobs WHERE id=?"
                                + (lock ? " FOR UPDATE" : ""),
                        id);
        if (rows.isEmpty()) throw new ApiException(ErrorCode.IMPORT_NOT_FOUND);
        if (((Number) rows.get(0).get("user_id")).longValue() != userId)
            throw new ApiException(ErrorCode.FORBIDDEN);
        return rows.get(0);
    }

    @Transactional
    public Map<String, Object> submit(long userId, byte[] image, String mime, String seller) {
        fabrics.user(userId);
        if (image.length == 0 || image.length > 10 * 1024 * 1024 || !validImage(image, mime))
            throw new ApiException(ErrorCode.INVALID_IMAGE);
        if (seller != null && seller.length() > 150)
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        String hash;
        try {
            hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(image));
        } catch (java.security.NoSuchAlgorithmException impossible) {
            throw new IllegalStateException(impossible);
        }
        UUID id = UUID.randomUUID();
        db.update(
                "INSERT INTO fabric_import_jobs(id,user_id,fingerprint,mime_type,image,seller_hint)"
                        + " VALUES(?,?,?,?,?,?) ON CONFLICT(user_id,fingerprint) DO NOTHING",
                id,
                userId,
                hash,
                mime,
                image,
                seller);
        var row =
                db.queryForMap(
                        "SELECT id,status FROM fabric_import_jobs WHERE user_id=? AND fingerprint=?"
                                + " FOR UPDATE",
                        userId,
                        hash);
        id = (UUID) row.get("id");
        if ("CANCELLED".equals(row.get("status"))) {
            db.update("DELETE FROM fabric_import_items WHERE job_id=?", id);
            db.update(
                    "UPDATE fabric_import_jobs SET"
                        + " image=?,seller_hint=?,status='QUEUED',attempts=0,claim_token=NULL,error_message=NULL,updated_at=now()"
                        + " WHERE id=?",
                    image,
                    seller,
                    id);
        }
        return get(userId, id);
    }

    private boolean validImage(byte[] b, String mime) {
        if (b.length < 12) return false;
        return switch (mime) {
            case "image/png" ->
                    Arrays.equals(
                            Arrays.copyOf(b, 8),
                            new byte[] {(byte) 137, 80, 78, 71, 13, 10, 26, 10});
            case "image/jpeg" -> b[0] == (byte) 255 && b[1] == (byte) 216 && b[2] == (byte) 255;
            case "image/webp" ->
                    new String(b, 0, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("RIFF")
                            && new String(b, 8, 4, java.nio.charset.StandardCharsets.US_ASCII)
                                    .equals("WEBP");
            default -> false;
        };
    }

    @Transactional(readOnly = true)
    public Map<String, Object> get(long userId, UUID id) {
        var result = new LinkedHashMap<>(owned(userId, id, false));
        result.remove("user_id");
        result.put(
                "items",
                db.queryForList(
                        "SELECT"
                            + " id,item_index,product_name,product_code,color,size,quantity,amount_text,amount_type,line_total,purchased_at,seller,order_number,status,warning,fabric_id,enrichment_status"
                            + " FROM fabric_import_items WHERE job_id=? ORDER BY item_index",
                        id));
        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> list(long userId) {
        fabrics.user(userId);
        return db.queryForList(
                "SELECT id,status,error_message,created_at,(image IS NOT NULL) image_available FROM"
                        + " fabric_import_jobs WHERE user_id=? ORDER BY created_at DESC LIMIT 30",
                userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> image(long userId, UUID id) {
        owned(userId, id, false);
        var row = db.queryForMap("SELECT image,mime_type FROM fabric_import_jobs WHERE id=?", id);
        if (row.get("image") == null) throw new ApiException(ErrorCode.IMPORT_NOT_FOUND);
        return row;
    }

    @Transactional
    public Map<String, Object> retry(long userId, UUID id) {
        var row = owned(userId, id, true);
        if (!"FAILED".equals(row.get("status")) || !Boolean.TRUE.equals(row.get("image_available")))
            throw new ApiException(ErrorCode.IMPORT_CONFLICT);
        db.update(
                "UPDATE fabric_import_jobs SET"
                    + " status='QUEUED',attempts=0,error_message=NULL,claim_token=NULL,updated_at=now()"
                    + " WHERE id=?",
                id);
        return get(userId, id);
    }

    @Transactional
    public Map<String, Object> register(
            long userId, UUID id, long itemId, FabricWriteRequest input) {
        var job = owned(userId, id, true);
        if (!"COMPLETED".equals(job.get("status")))
            throw new ApiException(ErrorCode.IMPORT_CONFLICT);
        var rows =
                db.queryForList(
                        "SELECT fabric_id,status FROM fabric_import_items WHERE id=? AND job_id=?"
                                + " FOR UPDATE",
                        itemId,
                        id);
        if (rows.isEmpty()) throw new ApiException(ErrorCode.IMPORT_NOT_FOUND);
        if ("DISMISSED".equals(rows.get(0).get("status")))
            throw new ApiException(ErrorCode.IMPORT_CONFLICT);
        if (rows.get(0).get("fabric_id") == null) {
            long fabricId = fabrics.create(userId, input).id();
            db.update(
                    "UPDATE fabric_import_items SET"
                        + " fabric_id=?,status='REGISTERED',edited=true,warning=NULL,enrichment_status='SKIPPED'"
                        + " WHERE id=?",
                    fabricId,
                    itemId);
        }
        return get(userId, id);
    }

    @Transactional
    public void dismiss(long userId, UUID id, long itemId) {
        var job = owned(userId, id, true);
        if (!"COMPLETED".equals(job.get("status")))
            throw new ApiException(ErrorCode.IMPORT_CONFLICT);
        var rows =
                db.queryForList(
                        "SELECT fabric_id FROM fabric_import_items WHERE id=? AND job_id=? FOR"
                                + " UPDATE",
                        itemId,
                        id);
        if (rows.isEmpty()) throw new ApiException(ErrorCode.IMPORT_NOT_FOUND);
        Object fid = rows.get(0).get("fabric_id");
        if (fid != null) fabrics.delete(userId, ((Number) fid).longValue());
        db.update(
                "UPDATE fabric_import_items SET"
                    + " status='DISMISSED',enrichment_status='SKIPPED',enrichment_token=NULL WHERE"
                    + " id=?",
                itemId);
    }

    @Transactional
    public void cancel(long userId, UUID id) {
        owned(userId, id, true);
        var items =
                db.queryForList(
                        "SELECT fabric_id FROM fabric_import_items WHERE job_id=? FOR UPDATE", id);
        for (var item : items)
            if (item.get("fabric_id") != null) {
                long fid = ((Number) item.get("fabric_id")).longValue();
                if (db.queryForObject(
                                "SELECT count(*) FROM project_fabrics WHERE fabric_id=?",
                                Long.class,
                                fid)
                        > 0) throw new ApiException(ErrorCode.IMPORT_CONFLICT);
                fabrics.delete(userId, fid);
            }
        repository.flush();
        db.update(
                "UPDATE fabric_import_jobs SET"
                    + " status='CANCELLED',image=NULL,claim_token=NULL,updated_at=now() WHERE id=?",
                id);
        db.update(
                "UPDATE fabric_import_items SET enrichment_status='SKIPPED',enrichment_token=NULL"
                        + " WHERE job_id=?",
                id);
    }

    @Transactional
    public Work claim() {
        db.update(
                "UPDATE fabric_import_jobs SET status='FAILED',claim_token=NULL,error_message='처리가"
                        + " 중단되었습니다. 다시 시도해주세요.',updated_at=now() WHERE status='PROCESSING' AND"
                        + " lease_until<now() AND attempts>=3");
        db.update(
                "UPDATE fabric_import_jobs SET image=NULL WHERE status IN ('COMPLETED','FAILED')"
                        + " AND updated_at<now()-interval '7 days' AND image IS NOT NULL");
        var rows =
                db.queryForList(
                        "SELECT id,image,mime_type,seller_hint FROM fabric_import_jobs WHERE"
                            + " (status='QUEUED' OR (status='PROCESSING' AND lease_until<now()))"
                            + " AND attempts<3 ORDER BY created_at FOR UPDATE SKIP LOCKED LIMIT 1");
        if (rows.isEmpty()) return null;
        var row = rows.get(0);
        UUID id = (UUID) row.get("id"), token = UUID.randomUUID();
        db.update(
                "UPDATE fabric_import_jobs SET"
                        + " status='PROCESSING',claim_token=?,lease_until=now()+interval '3"
                        + " minutes',attempts=attempts+1,updated_at=now() WHERE id=?",
                token,
                id);
        return new Work(
                id,
                token,
                (byte[]) row.get("image"),
                (String) row.get("mime_type"),
                (String) row.get("seller_hint"));
    }

    @Transactional
    public void failed(Work work, String message) {
        db.update(
                "UPDATE fabric_import_jobs SET"
                        + " status='FAILED',error_message=?,claim_token=NULL,updated_at=now() WHERE"
                        + " id=? AND claim_token=? AND status='PROCESSING'",
                message,
                work.id(),
                work.token());
    }

    @Transactional
    public void complete(Work work, JsonNode receipt) {
        var jobs =
                db.queryForList(
                        "SELECT user_id FROM fabric_import_jobs WHERE id=? AND claim_token=? AND"
                                + " status='PROCESSING' FOR UPDATE",
                        work.id(),
                        work.token());
        if (jobs.isEmpty()) return; // cancelled or a newer execution owns the lease
        JsonNode items = receipt.path("items");
        if (!items.isArray() || items.isEmpty() || items.size() > 100)
            throw new IllegalArgumentException("invalid receipt result");
        long userId = ((Number) jobs.get(0).get("user_id")).longValue();
        User user = fabrics.user(userId);
        db.queryForList("SELECT id FROM users WHERE id=? FOR UPDATE", userId);
        String seller = text(receipt, "seller", 150), order = text(receipt, "orderNumber", 200);
        if (seller == null) seller = work.seller();
        LocalDate date = date(receipt.path("purchasedAt").asText(null));
        int index = 0;
        for (JsonNode item : items) {
            String name = text(item, "productName", 200),
                    code = text(item, "productCode", 100),
                    color = text(item, "color", 100),
                    size = text(item, "size", 100);
            String quantity = text(item, "quantity", 100),
                    amountType = text(item, "amountType", 30),
                    amount = text(item, "amountText", 300);
            Integer total = null;
            if ("LINE_TOTAL".equals(amountType)
                    && "KRW".equals(item.path("currency").asText())
                    && item.path("amount").isIntegralNumber()
                    && item.path("amount").canConvertToInt()
                    && item.path("amount").asInt() >= 0) total = item.path("amount").asInt();
            String identity =
                    (Objects.toString(code, name == null ? "" : name)
                                    + "|"
                                    + Objects.toString(color, "")
                                    + "|"
                                    + Objects.toString(size, ""))
                            .toLowerCase(Locale.ROOT)
                            .replaceAll("\\s+", "");
            boolean duplicate =
                    order != null
                            && seller != null
                            && db.queryForObject(
                                            "SELECT count(*) FROM fabric_import_items i JOIN"
                                                + " fabric_import_jobs j ON i.job_id=j.id WHERE"
                                                + " j.user_id=? AND j.status='COMPLETED' AND"
                                                + " j.id<>? AND i.seller=? AND i.order_number=? AND"
                                                + " i.identity_key=? AND i.fabric_id IS NOT NULL",
                                            Long.class,
                                            userId,
                                            work.id(),
                                            seller,
                                            order,
                                            identity)
                                    > 0;
            String warning = text(item, "warning", 800);
            if (name == null) warning = "상품명을 읽지 못했습니다. 원본을 확인해주세요.";
            else if (duplicate) warning = "같은 주문의 원단이 이미 등록되어 있을 수 있습니다. 별도 구매라면 등록해주세요.";
            Long fid = null;
            if (name != null && !duplicate) {
                fid =
                        repository
                                .saveAndFlush(
                                        Fabric.builder()
                                                .user(user)
                                                .name(
                                                        name.substring(
                                                                0, Math.min(150, name.length())))
                                                .productName(name)
                                                .productCode(code)
                                                .purchaseQuantity(quantity)
                                                .color(color)
                                                .size(size)
                                                .storeName(seller)
                                                .purchasedAt(date)
                                                .purchasePrice(total)
                                                .build())
                                .getId();
            }
            db.update(
                    "INSERT INTO"
                        + " fabric_import_items(job_id,item_index,product_name,product_code,color,size,quantity,amount_text,amount_type,line_total,purchased_at,seller,order_number,identity_key,raw_json,status,warning,fabric_id,enrichment_status)"
                        + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    work.id(),
                    index++,
                    name,
                    code,
                    color,
                    size,
                    quantity,
                    amount,
                    amountType,
                    total,
                    date,
                    seller,
                    order,
                    identity,
                    item.toString(),
                    fid == null ? "NEEDS_REVIEW" : "REGISTERED",
                    warning,
                    fid,
                    fid == null ? "SKIPPED" : "PENDING");
        }
        db.update(
                "UPDATE fabric_import_jobs SET"
                        + " status='COMPLETED',claim_token=NULL,error_message=NULL,updated_at=now()"
                        + " WHERE id=?",
                work.id());
    }

    @Transactional
    public Enrichment claimEnrichment() {
        db.update(
                "UPDATE fabric_import_items SET enrichment_status='FAILED',enrichment_token=NULL"
                        + " WHERE enrichment_status='PROCESSING' AND enrichment_lease<now() AND"
                        + " enrichment_attempts>=2");
        var rows =
                db.queryForList(
                        "SELECT i.id,i.seller,i.product_name,i.product_code FROM"
                            + " fabric_import_items i JOIN fabric_import_jobs j ON i.job_id=j.id"
                            + " WHERE j.status='COMPLETED' AND i.fabric_id IS NOT NULL AND"
                            + " (i.enrichment_status='PENDING' OR (i.enrichment_status='PROCESSING'"
                            + " AND i.enrichment_lease<now())) AND i.enrichment_attempts<2 ORDER BY"
                            + " i.id FOR UPDATE OF i SKIP LOCKED LIMIT 1");
        if (rows.isEmpty()) return null;
        var row = rows.get(0);
        long id = ((Number) row.get("id")).longValue();
        UUID token = UUID.randomUUID();
        db.update(
                "UPDATE fabric_import_items SET"
                    + " enrichment_status='PROCESSING',enrichment_token=?,enrichment_lease=now()+interval"
                    + " '3 minutes',enrichment_attempts=enrichment_attempts+1 WHERE id=?",
                token,
                id);
        return new Enrichment(
                id,
                token,
                (String) row.get("seller"),
                (String) row.get("product_name"),
                (String) row.get("product_code"));
    }

    @Transactional
    public void enriched(Enrichment work, JsonNode result) {
        var rows =
                db.queryForList(
                        "SELECT fabric_id,edited FROM fabric_import_items WHERE id=? AND"
                            + " enrichment_token=? AND enrichment_status='PROCESSING' FOR UPDATE",
                        work.itemId(),
                        work.token());
        if (rows.isEmpty()) return;
        String state = "SKIPPED";
        var row = rows.get(0);
        if (result != null
                && "COMPLETE".equals(result.path("status").asText())
                && row.get("fabric_id") != null
                && !Boolean.TRUE.equals(row.get("edited"))) {
            Fabric f =
                    repository.findById(((Number) row.get("fabric_id")).longValue()).orElse(null);
            String url = text(result, "productUrl", 2000);
            if (f != null
                    && url != null
                    && url.matches(
                            "https://(?:www\\.)?fashionstart\\.net/goods/goods_view\\.php\\?goodsNo=[0-9]+")) {
                f.enrich(
                        url, text(result, "materialComposition", 5000), text(result, "width", 100));
                repository.saveAndFlush(f);
                state = "COMPLETE";
            }
        } else if (result == null) state = "FAILED";
        db.update(
                "UPDATE fabric_import_items SET enrichment_status=?,enrichment_token=NULL WHERE"
                        + " id=?",
                state,
                work.itemId());
    }

    private String text(JsonNode node, String key, int max) {
        if (!node.path(key).isTextual()) return null;
        String value = node.path(key).asText().trim();
        return value.isEmpty() ? null : value.substring(0, Math.min(max, value.length()));
    }

    private LocalDate date(String value) {
        try {
            return value == null ? null : LocalDate.parse(value);
        } catch (Exception e) {
            return null;
        }
    }
}
