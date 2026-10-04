ALTER TABLE fabrics ADD COLUMN purchase_quantity VARCHAR(100);
CREATE TABLE fabric_import_jobs (
    id UUID PRIMARY KEY, user_id BIGINT NOT NULL REFERENCES users(id),
    fingerprint VARCHAR(64) NOT NULL, mime_type VARCHAR(40) NOT NULL, image BYTEA,
    seller_hint VARCHAR(150), status VARCHAR(20) NOT NULL DEFAULT 'QUEUED',
    attempts INTEGER NOT NULL DEFAULT 0, claim_token UUID, lease_until TIMESTAMPTZ,
    error_message VARCHAR(500), created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(user_id, fingerprint),
    CHECK(status IN ('QUEUED','PROCESSING','COMPLETED','FAILED','CANCELLED'))
);
CREATE INDEX ix_import_jobs_queue ON fabric_import_jobs(status, created_at);
CREATE TABLE fabric_import_items (
    id BIGSERIAL PRIMARY KEY, job_id UUID NOT NULL REFERENCES fabric_import_jobs(id) ON DELETE CASCADE,
    item_index INTEGER NOT NULL, product_name VARCHAR(200), product_code VARCHAR(100),
    color VARCHAR(100), size VARCHAR(100), quantity VARCHAR(100),
    amount_text VARCHAR(300), amount_type VARCHAR(30), line_total INTEGER CHECK(line_total >= 0),
    purchased_at DATE, seller VARCHAR(150), order_number VARCHAR(200),
    identity_key VARCHAR(600), raw_json TEXT NOT NULL,
    status VARCHAR(30) NOT NULL, warning VARCHAR(1000),
    fabric_id BIGINT REFERENCES fabrics(id) ON DELETE SET NULL,
    enrichment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    enrichment_attempts INTEGER NOT NULL DEFAULT 0,
    enrichment_token UUID, enrichment_lease TIMESTAMPTZ,
    edited BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(job_id,item_index),
    CHECK(status IN ('REGISTERED','NEEDS_REVIEW','DISMISSED')),
    CHECK(enrichment_status IN ('PENDING','PROCESSING','COMPLETE','SKIPPED','FAILED'))
);
CREATE INDEX ix_import_items_order ON fabric_import_items(seller, order_number, identity_key);
