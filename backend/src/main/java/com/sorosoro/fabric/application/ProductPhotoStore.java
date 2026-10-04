package com.sorosoro.fabric.application;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;

import javax.imageio.ImageIO;

/** Automatic product photos; independent of the user's S3-backed Photo attachments. */
@Component
public class ProductPhotoStore {
    private final JdbcTemplate db;

    public ProductPhotoStore(JdbcTemplate db) {
        this.db = db;
    }

    public boolean save(long fabricId, JsonNode result) {
        String encoded = result.path("imageBase64").asText("");
        String source = result.path("imageSourceUrl").asText("");
        if (encoded.isBlank()
                || encoded.length() > 700000
                || !"image/jpeg".equals(result.path("imageMimeType").asText())
                || !source.matches("https://img\\.kohasid\\.com/photos/goods/[a-zA-Z0-9/_.,%-]+"))
            return false;
        byte[] bytes;
        String digest;
        try {
            bytes = Base64.getDecoder().decode(encoded);
            if (bytes.length > 524288) return false;
            try (var stream = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
                var readers = ImageIO.getImageReaders(stream);
                if (!readers.hasNext()) return false;
                var reader = readers.next();
                try {
                    reader.setInput(stream);
                    if (!"JPEG".equalsIgnoreCase(reader.getFormatName())
                            || reader.getWidth(0) < 64
                            || reader.getHeight(0) < 64
                            || reader.getWidth(0) > 1200
                            || reader.getHeight(0) > 1200) return false;
                    reader.read(0); // Validate the actual compressed pixels before persisting.
                } finally {
                    reader.dispose();
                }
            }
            digest = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (Exception invalidImage) {
            return false;
        }
        db.update(
                "INSERT INTO fabric_product_photos(fabric_id,bytes,mime_type,source_url,sha256)"
                    + " VALUES(?,?,'image/jpeg',?,?) ON CONFLICT(fabric_id) DO UPDATE SET"
                    + " bytes=excluded.bytes,source_url=excluded.source_url,sha256=excluded.sha256",
                fabricId,
                bytes,
                source,
                digest);
        return true;
    }
}
