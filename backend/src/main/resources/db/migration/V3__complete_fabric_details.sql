CREATE TABLE fabric_product_photos (
    fabric_id BIGINT PRIMARY KEY REFERENCES fabrics(id) ON DELETE CASCADE,
    bytes BYTEA NOT NULL CHECK(octet_length(bytes) BETWEEN 1 AND 524288),
    mime_type VARCHAR(40) NOT NULL CHECK(mime_type = 'image/jpeg'),
    source_url TEXT NOT NULL,
    sha256 VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
ALTER TABLE fabrics ADD COLUMN has_product_photo BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE fabric_import_items ADD COLUMN enrichment_reason VARCHAR(80);
ALTER TABLE fabric_import_items ADD COLUMN material_locked BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE fabric_import_items ADD COLUMN width_locked BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE fabric_import_items ADD COLUMN url_locked BOOLEAN NOT NULL DEFAULT FALSE;
UPDATE fabric_import_items SET material_locked=TRUE,width_locked=TRUE,url_locked=TRUE WHERE edited;
ALTER TABLE fabric_import_items DROP CONSTRAINT fabric_import_items_enrichment_status_check;
ALTER TABLE fabric_import_items ADD CONSTRAINT fabric_import_items_enrichment_status_check
    CHECK(enrichment_status IN ('PENDING','PROCESSING','COMPLETE','INCOMPLETE','SKIPPED','FAILED'));
-- Old COMPLETE did not require a photo; re-evaluate with the new completion rule.
UPDATE fabric_import_items SET enrichment_status='PENDING',enrichment_attempts=0,
    enrichment_token=NULL,enrichment_reason=NULL WHERE enrichment_status='COMPLETE' AND fabric_id IS NOT NULL;
