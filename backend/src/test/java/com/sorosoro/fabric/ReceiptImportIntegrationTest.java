package com.sorosoro.fabric;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.*;
import com.sorosoro.auth.domain.JwtTokenProvider;
import com.sorosoro.fabric.importing.*;
import com.sorosoro.fabric.repository.FabricRepository;
import com.sorosoro.support.RepositoryTestContainerConfig;
import com.sorosoro.user.domain.User;
import com.sorosoro.user.repository.UserRepository;
import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.*;
import org.springframework.test.web.servlet.MockMvc;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest(
        properties = {
            "fabric.import.enabled=false",
            "fabric.import.worker-token=test-worker-token"
        })
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ReceiptImportIntegrationTest {
    static final AtomicReference<String> RESULT = new AtomicReference<>();
    static final AtomicReference<String> LAST_REQUEST = new AtomicReference<>();
    static final HttpServer WORKER;

    static {
        try {
            WORKER = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            WORKER.createContext(
                    "/extract",
                    exchange -> {
                        LAST_REQUEST.set(
                                new String(
                                        exchange.getRequestBody().readAllBytes(),
                                        StandardCharsets.UTF_8));
                        int code =
                                "Bearer test-worker-token"
                                                .equals(
                                                        exchange.getRequestHeaders()
                                                                .getFirst("Authorization"))
                                        ? 200
                                        : 401;
                        String json = RESULT.get();
                        if (json == null) {
                            code = 502;
                            json = "{}";
                        }
                        byte[] response = json.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(code, response.length);
                        exchange.getResponseBody().write(response);
                        exchange.close();
                    });
            WORKER.start();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        RepositoryTestContainerConfig.registerProperties(registry);
        registry.add(
                "fabric.import.worker-url",
                () -> "http://127.0.0.1:" + WORKER.getAddress().getPort());
    }

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UserRepository users;
    @Autowired FabricRepository fabrics;
    @Autowired JwtTokenProvider tokens;
    @Autowired ImportService imports;
    @Autowired ExtractionClient client;
    @Autowired JdbcTemplate db;
    User user, other;
    String auth, otherAuth;
    final byte[] image = new byte[] {(byte) 137, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 0, 1};

    @BeforeEach
    void setup() {
        db.update("DELETE FROM photos");
        db.update("DELETE FROM fabric_import_items");
        db.update("DELETE FROM fabric_import_jobs");
        db.update("DELETE FROM project_fabrics");
        db.update("DELETE FROM fabrics");
        user =
                users.saveAndFlush(
                        User.builder()
                                .kakaoId(UUID.randomUUID().toString())
                                .nickname("owner")
                                .build());
        other =
                users.saveAndFlush(
                        User.builder()
                                .kakaoId(UUID.randomUUID().toString())
                                .nickname("other")
                                .build());
        auth = "Bearer " + tokens.generateAccessToken(user);
        otherAuth = "Bearer " + tokens.generateAccessToken(other);
        RESULT.set(
                """
                {"seller":"천가게","orderNumber":"ORDER-001","purchasedAt":null,"items":[
                {"productName":"면 원단","color":"흰색","quantity":"2마","amountType":"LINE_TOTAL","amount":16000,"currency":"KRW","amountText":"합계 16,000원"},
                {"productName":"면 원단","color":"검정","quantity":"1마","amountType":"UNIT_PRICE","amount":8000,"currency":"KRW","amountText":"8,000원/마"}]}
                """);
    }

    UUID submit(byte[] bytes) throws Exception {
        String body =
                mvc.perform(
                                multipart("/api/v1/fabric-imports")
                                        .file(
                                                new MockMultipartFile(
                                                        "image", "receipt.png", "image/png", bytes))
                                        .header("Authorization", auth))
                        .andExpect(status().isAccepted())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        return UUID.fromString(json.readTree(body).path("id").asText());
    }

    JsonNode job(UUID id) throws Exception {
        return json.valueToTree(imports.get(user.getId(), id));
    }

    void process() {
        new ImportProcessor(imports, client).tick();
    }

    @Test
    void uploadThroughPrivateHttpAutoRegistersAndPreservesPurchaseMeaning() throws Exception {
        UUID id = submit(image);
        assertThat(job(id).path("status").asText()).isEqualTo("QUEUED");
        process();
        JsonNode result = job(id);
        assertThat(result.path("status").asText()).isEqualTo("COMPLETED");
        assertThat(fabrics.count()).isEqualTo(2);
        var saved = fabrics.findByUserOrderByCreatedAtDesc(user);
        assertThat(saved)
                .allMatch(
                        f ->
                                f.getPurchasedAt() == null
                                        && f.getProductUrl() == null
                                        && f.getStoreName().equals("천가게"));
        assertThat(
                        saved.stream()
                                .filter(f -> f.getColor().equals("흰색"))
                                .findFirst()
                                .orElseThrow()
                                .getPurchasePrice())
                .isEqualTo(16000);
        assertThat(
                        saved.stream()
                                .filter(f -> f.getColor().equals("검정"))
                                .findFirst()
                                .orElseThrow()
                                .getPurchasePrice())
                .isNull();
        assertThat(json.readTree(LAST_REQUEST.get()).path("image").asText())
                .isEqualTo(Base64.getEncoder().encodeToString(image));
        mvc.perform(get("/api/v1/fabrics").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void repeatedImageIsIdempotentAndOwnerScoped() throws Exception {
        UUID id = submit(image);
        assertThat(submit(image)).isEqualTo(id);
        process();
        assertThat(submit(image)).isEqualTo(id);
        process();
        assertThat(fabrics.count()).isEqualTo(2);
        String body =
                mvc.perform(
                                multipart("/api/v1/fabric-imports")
                                        .file(
                                                new MockMultipartFile(
                                                        "image", "receipt.png", "image/png", image))
                                        .header("Authorization", otherAuth))
                        .andExpect(status().isAccepted())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        assertThat(json.readTree(body).path("id").asText()).isNotEqualTo(id.toString());
    }

    @Test
    void overlappingOrderNeedsReviewButNewOrderCanRegister() throws Exception {
        submit(image);
        process();
        byte[] next = image.clone();
        next[12] = 2;
        UUID second = submit(next);
        process();
        assertThat(fabrics.count()).isEqualTo(2);
        assertThat(job(second).path("items").get(0).path("status").asText())
                .isEqualTo("NEEDS_REVIEW");
        next[12] = 3;
        RESULT.set(RESULT.get().replace("ORDER-001", "ORDER-002"));
        submit(next);
        process();
        assertThat(fabrics.count()).isEqualTo(4);
    }

    @Test
    void ownerAndAuthenticationAreEnforced() throws Exception {
        UUID id = submit(image);
        process();
        long fid = fabrics.findAll().get(0).getId();
        mvc.perform(get("/api/v1/fabric-imports/" + id)).andExpect(status().isUnauthorized());
        for (String path :
                List.of(
                        "/api/v1/fabric-imports/" + id,
                        "/api/v1/fabric-imports/" + id + "/image",
                        "/api/v1/fabrics/" + fid))
            mvc.perform(get(path).header("Authorization", otherAuth))
                    .andExpect(status().isForbidden());
        mvc.perform(delete("/api/v1/fabric-imports/" + id).header("Authorization", otherAuth))
                .andExpect(status().isForbidden());
        mvc.perform(
                        put("/api/v1/fabrics/" + fid)
                                .header("Authorization", otherAuth)
                                .contentType("application/json")
                                .content("{\"name\":\"stolen\"}"))
                .andExpect(status().isForbidden());
        assertThat(fabrics.count()).isEqualTo(2);
    }

    @Test
    void missingNameStaysReviewableAndCanBeRegisteredOnce() throws Exception {
        RESULT.set("{\"items\":[{\"productName\":null},{\"productName\":\"면 원단\"}]}");
        UUID id = submit(image);
        process();
        assertThat(fabrics.count()).isEqualTo(1);
        long itemId = job(id).path("items").get(0).path("id").asLong();
        for (int i = 0; i < 2; i++)
            mvc.perform(
                            post("/api/v1/fabric-imports/" + id + "/items/" + itemId + "/register")
                                    .header("Authorization", auth)
                                    .contentType("application/json")
                                    .content("{\"name\":\"수정한 원단\"}"))
                    .andExpect(status().isOk());
        assertThat(fabrics.count()).isEqualTo(2);
    }

    @Test
    void failureCanRetryWithoutDuplicatingCompletedRecords() throws Exception {
        UUID id = submit(image);
        String original = RESULT.get();
        RESULT.set(null);
        process();
        assertThat(job(id).path("status").asText()).isEqualTo("FAILED");
        RESULT.set(original);
        imports.retry(user.getId(), id);
        process();
        assertThat(fabrics.count()).isEqualTo(2);
        mvc.perform(post("/api/v1/fabric-imports/" + id + "/retry").header("Authorization", auth))
                .andExpect(status().isConflict());
    }

    @Test
    void expiredLeaseRejectsOldResultAndRecovers() throws Exception {
        UUID id = submit(image);
        var old = imports.claim();
        db.update(
                "UPDATE fabric_import_jobs SET lease_until=now()-interval '1 second' WHERE id=?",
                id);
        var current = imports.claim();
        assertThat(current.token()).isNotEqualTo(old.token());
        imports.complete(old, json.readTree(RESULT.get()));
        assertThat(fabrics.count()).isZero();
        imports.complete(current, json.readTree(RESULT.get()));
        assertThat(fabrics.count()).isEqualTo(2);
    }

    @Test
    void cancellationRejectsLateResultAndAllowsFreshUpload() throws Exception {
        UUID id = submit(image);
        var work = imports.claim();
        imports.cancel(user.getId(), id);
        imports.complete(work, json.readTree(RESULT.get()));
        assertThat(fabrics.count()).isZero();
        assertThat(submit(image)).isEqualTo(id);
        process();
        assertThat(fabrics.count()).isEqualTo(2);
        imports.cancel(user.getId(), id);
        assertThat(fabrics.count()).isZero();
        assertThat(job(id).path("image_available").asBoolean()).isFalse();
    }

    @Test
    void cancellationPreservesFabricsWithAttachedPhotos() throws Exception {
        UUID id = submit(image);
        process();
        long fid = fabrics.findAll().get(0).getId();
        db.update(
                "INSERT INTO photos(user_id,owner_type,owner_id,original_key,sort_order,status)"
                        + " VALUES(?,'FABRIC',?,'test-photo',0,'READY')",
                user.getId(),
                fid);
        mvc.perform(delete("/api/v1/fabric-imports/" + id).header("Authorization", auth))
                .andExpect(status().isConflict());
        assertThat(fabrics.count()).isEqualTo(2);
        assertThat(job(id).path("status").asText()).isEqualTo("COMPLETED");
    }

    @Test
    void enrichmentCannotOverwriteUserEditsOrPurchaseFacts() throws Exception {
        UUID id = submit(image);
        process();
        var work = imports.claimEnrichment();
        long fid =
                db.queryForObject(
                        "SELECT fabric_id FROM fabric_import_items WHERE id=?",
                        Long.class,
                        work.itemId());
        mvc.perform(
                        put("/api/v1/fabrics/" + fid)
                                .header("Authorization", auth)
                                .contentType("application/json")
                                .content(
                                        "{\"name\":\"내가 고친"
                                            + " 원단\",\"purchasePrice\":1234,\"width\":\"사용자 값\"}"))
                .andExpect(status().isOk());
        imports.enriched(
                work,
                json.readTree(
                        "{\"status\":\"COMPLETE\",\"productUrl\":\"https://fashionstart.net/goods/goods_view.php?goodsNo=1\",\"width\":\"999\",\"purchasePrice\":9999}"));
        var f = fabrics.findById(fid).orElseThrow();
        assertThat(f.getWidth()).isEqualTo("사용자 값");
        assertThat(f.getPurchasePrice()).isEqualTo(1234);
        assertThat(f.getProductUrl()).isNull();
    }

    @Test
    void invalidImageAndInvalidEditsAreRejected() throws Exception {
        mvc.perform(
                        multipart("/api/v1/fabric-imports")
                                .file(
                                        new MockMultipartFile(
                                                "image", "x.png", "image/png", new byte[20]))
                                .header("Authorization", auth))
                .andExpect(status().isBadRequest());
        UUID id = submit(image);
        process();
        long fid = fabrics.findAll().get(0).getId();
        mvc.perform(
                        put("/api/v1/fabrics/" + fid)
                                .header("Authorization", auth)
                                .contentType("application/json")
                                .content("{\"name\":\"x\",\"productUrl\":\"javascript:alert(1)\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void privateReceiptBytesNeverAppearInPublicList() throws Exception {
        UUID id = submit(image);
        mvc.perform(get("/api/v1/fabric-imports/" + id).header("Authorization", auth))
                .andExpect(jsonPath("$.image").doesNotExist());
        mvc.perform(get("/api/v1/fabric-imports/" + id + "/image").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"));
    }

    @Test
    void individualExclusionPreservesOtherRecordsAndCannotResurrect() throws Exception {
        UUID id = submit(image);
        process();
        long item = job(id).path("items").get(0).path("id").asLong();
        mvc.perform(
                        delete("/api/v1/fabric-imports/" + id + "/items/" + item)
                                .header("Authorization", otherAuth))
                .andExpect(status().isForbidden());
        mvc.perform(
                        delete("/api/v1/fabric-imports/" + id + "/items/" + item)
                                .header("Authorization", auth))
                .andExpect(status().isNoContent());
        assertThat(fabrics.count()).isEqualTo(1);
        assertThat(submit(image)).isEqualTo(id);
        mvc.perform(
                        post("/api/v1/fabric-imports/" + id + "/items/" + item + "/register")
                                .header("Authorization", auth)
                                .contentType("application/json")
                                .content("{\"name\":\"다시 생성\"}"))
                .andExpect(status().isConflict());
        assertThat(job(id).path("items").get(0).path("status").asText()).isEqualTo("DISMISSED");
    }

    @Test
    void unreadableItemCanBeDismissedWithoutDeletingGoodFabric() throws Exception {
        RESULT.set("{\"items\":[{\"productName\":null},{\"productName\":\"정상 원단\"}]}");
        UUID id = submit(image);
        process();
        long item = job(id).path("items").get(0).path("id").asLong();
        imports.dismiss(user.getId(), id, item);
        assertThat(fabrics.count()).isEqualTo(1);
        assertThat(job(id).path("items").get(0).path("status").asText()).isEqualTo("DISMISSED");
    }

    @Test
    void successfulEnrichmentFillsOnlyDetails() throws Exception {
        submit(image);
        process();
        var work = imports.claimEnrichment();
        long fid =
                db.queryForObject(
                        "SELECT fabric_id FROM fabric_import_items WHERE id=?",
                        Long.class,
                        work.itemId());
        imports.enriched(
                work,
                json.readTree(
                        "{\"status\":\"COMPLETE\",\"productUrl\":\"https://fashionstart.net/goods/goods_view.php?goodsNo=1\",\"width\":\"110cm\",\"purchasePrice\":999}"));
        var f = fabrics.findById(fid).orElseThrow();
        assertThat(f.getWidth()).isEqualTo("110cm");
        assertThat(f.getPurchasePrice()).isEqualTo(16000);
    }

    @Test
    void receiptRetentionDoesNotDeletePurchaseRecords() throws Exception {
        UUID id = submit(image);
        process();
        db.update(
                "UPDATE fabric_import_jobs SET updated_at=now()-interval '8 days' WHERE id=?", id);
        imports.claim();
        assertThat(job(id).path("image_available").asBoolean()).isFalse();
        assertThat(fabrics.count()).isEqualTo(2);
    }

    com.fasterxml.jackson.databind.node.ObjectNode fullDetails() throws Exception {
        var photo =
                new java.awt.image.BufferedImage(96, 96, java.awt.image.BufferedImage.TYPE_INT_RGB);
        var output = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(photo, "JPEG", output);
        var value = json.createObjectNode();
        value.put("status", "COMPLETE");
        value.put("productUrl", "https://fashionstart.net/goods/goods_view.php?goodsNo=1");
        value.put("materialComposition", "면100%");
        value.put("width", "110cm");
        value.put("imageMimeType", "image/jpeg");
        value.put("imageSourceUrl", "https://img.kohasid.com/photos/goods/test/photo.jpg");
        value.put("imageBase64", Base64.getEncoder().encodeToString(output.toByteArray()));
        return value;
    }

    @Test
    void photoAndAllDetailsPersistWithPrivateReadAndCascadeDelete() throws Exception {
        UUID id = submit(image);
        process();
        var work = imports.claimEnrichment();
        long fid =
                db.queryForObject(
                        "SELECT fabric_id FROM fabric_import_items WHERE id=?",
                        Long.class,
                        work.itemId());
        var details = fullDetails();
        imports.enriched(work, details);
        assertThat(job(id).path("items").get(0).path("enrichment_status").asText())
                .isEqualTo("COMPLETE");
        mvc.perform(get("/api/v1/fabrics/" + fid).header("Authorization", auth))
                .andExpect(jsonPath("$.detailsComplete").value(true))
                .andExpect(
                        jsonPath("$.thumbnailUrl")
                                .value("/api/v1/fabrics/" + fid + "/product-photo"));
        String path = "/api/v1/fabrics/" + fid + "/product-photo";
        mvc.perform(get(path)).andExpect(status().isUnauthorized());
        mvc.perform(get(path).header("Authorization", otherAuth)).andExpect(status().isForbidden());
        mvc.perform(get(path).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(
                        content()
                                .bytes(
                                        Base64.getDecoder()
                                                .decode(details.path("imageBase64").asText())));
        db.update(
                "UPDATE fabric_import_jobs SET updated_at=now()-interval '8 days' WHERE id=?", id);
        imports.claim();
        assertThat(job(id).path("image_available").asBoolean()).isFalse();
        mvc.perform(get(path).header("Authorization", auth)).andExpect(status().isOk());
        imports.cancel(user.getId(), id);
        imports.enriched(work, details);
        assertThat(db.queryForObject("SELECT count(*) FROM fabric_product_photos", Long.class))
                .isZero();
        assertThat(fabrics.count()).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"imageBase64", "materialComposition", "width"})
    void missingAnyRequiredDetailCannotClaimCompletion(String missing) throws Exception {
        UUID id = submit(image);
        process();
        var work = imports.claimEnrichment();
        var details = fullDetails();
        details.remove(missing);
        imports.enriched(work, details);
        assertThat(job(id).path("items").get(0).path("enrichment_status").asText())
                .isEqualTo("INCOMPLETE");
        assertThat(fabrics.count()).isEqualTo(2);
    }

    @Test
    void corruptOrOffsitePhotoDoesNotCountAsComplete() throws Exception {
        UUID id = submit(image);
        process();
        var work = imports.claimEnrichment();
        var details = fullDetails();
        details.put("imageBase64", Base64.getEncoder().encodeToString("not JPEG".getBytes()));
        imports.enriched(work, details);
        assertThat(job(id).path("items").get(0).path("enrichment_status").asText())
                .isEqualTo("INCOMPLETE");
        imports.retryEnrichment(user.getId(), id, work.itemId());
        work = imports.claimEnrichment();
        details = fullDetails();
        details.put("imageSourceUrl", "https://evil.test/photo.jpg");
        imports.enriched(work, details);
        assertThat(db.queryForObject("SELECT count(*) FROM fabric_product_photos", Long.class))
                .isZero();
    }

    @Test
    void nameOnlyEditDoesNotBlockPhotoAndSpecs() throws Exception {
        UUID id = submit(image);
        process();
        var work = imports.claimEnrichment();
        long fid =
                db.queryForObject(
                        "SELECT fabric_id FROM fabric_import_items WHERE id=?",
                        Long.class,
                        work.itemId());
        mvc.perform(
                        put("/api/v1/fabrics/" + fid)
                                .header("Authorization", auth)
                                .contentType("application/json")
                                .content(
                                        "{\"name\":\"나의 셔츠용 원단\",\"productName\":\"면"
                                                + " 원단\",\"storeName\":\"천가게\",\"color\":\"흰색\"}"))
                .andExpect(status().isOk());
        imports.enriched(work, fullDetails());
        var f = fabrics.findById(fid).orElseThrow();
        assertThat(f.getName()).isEqualTo("나의 셔츠용 원단");
        assertThat(f.detailsComplete()).isTrue();
    }

    @Test
    void correctedIdentityInvalidatesOldResultAndRetryPreservesClearedWidth() throws Exception {
        UUID id = submit(image);
        process();
        var old = imports.claimEnrichment();
        long fid =
                db.queryForObject(
                        "SELECT fabric_id FROM fabric_import_items WHERE id=?",
                        Long.class,
                        old.itemId());
        mvc.perform(
                        put("/api/v1/fabrics/" + fid)
                                .header("Authorization", auth)
                                .contentType("application/json")
                                .content(
                                        "{\"name\":\"고친 상품\",\"productName\":\"체크"
                                            + " 원단\",\"productCode\":\"73-929\",\"storeName\":\"패션스타트\"}"))
                .andExpect(status().isOk());
        imports.enriched(old, fullDetails());
        assertThat(db.queryForObject("SELECT count(*) FROM fabric_product_photos", Long.class))
                .isZero();
        var current = imports.claimEnrichment();
        assertThat(current.productCode()).isEqualTo("73-929");
        assertThat(current.name()).isEqualTo("체크 원단");
        imports.enriched(current, fullDetails());
        mvc.perform(
                        put("/api/v1/fabrics/" + fid)
                                .header("Authorization", auth)
                                .contentType("application/json")
                                .content(
                                        "{\"name\":\"고친 상품\",\"productName\":\"체크"
                                            + " 원단\",\"productCode\":\"73-929\",\"storeName\":\"패션스타트\",\"materialComposition\":\"사용자"
                                            + " 소재\",\"width\":null}"))
                .andExpect(status().isOk());
        mvc.perform(
                        post("/api/v1/fabric-imports/"
                                        + id
                                        + "/items/"
                                        + old.itemId()
                                        + "/retry-enrichment")
                                .header("Authorization", otherAuth))
                .andExpect(status().isForbidden());
        imports.retryEnrichment(user.getId(), id, old.itemId());
        imports.enriched(imports.claimEnrichment(), fullDetails());
        var f = fabrics.findById(fid).orElseThrow();
        assertThat(f.getWidth()).isNull();
        assertThat(f.getMaterialComposition()).isEqualTo("사용자 소재");
        assertThat(f.detailsComplete()).isFalse();
        assertThat(
                        db.queryForObject(
                                "SELECT count(*) FROM fabric_product_photos WHERE fabric_id=?",
                                Long.class,
                                fid))
                .isEqualTo(1);
        mvc.perform(delete("/api/v1/fabrics/" + fid).header("Authorization", auth))
                .andExpect(status().isNoContent());
        assertThat(
                        db.queryForObject(
                                "SELECT count(*) FROM fabric_product_photos WHERE fabric_id=?",
                                Long.class,
                                fid))
                .isZero();
    }

    @Test
    void uiLoadsWithoutAuthenticationButUserDataDoesNot() throws Exception {
        mvc.perform(get("/imports/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
        mvc.perform(get("/api/v1/import-ui-config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.demo").value(false));
        mvc.perform(get("/api/v1/fabrics")).andExpect(status().isUnauthorized());
    }
}
