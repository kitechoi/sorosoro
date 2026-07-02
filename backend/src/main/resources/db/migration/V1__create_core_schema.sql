CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    kakao_id VARCHAR(100) NOT NULL,
    nickname VARCHAR(100),
    profile_image_url TEXT,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_kakao_id UNIQUE (kakao_id),
    CONSTRAINT ck_users_role CHECK (role IN ('USER', 'ADMIN')),
    CONSTRAINT ck_users_status CHECK (status IN ('ACTIVE', 'DELETED'))
);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token TEXT NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uk_refresh_tokens_token UNIQUE (token)
);

CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    status VARCHAR(30) NOT NULL,
    started_at DATE,
    completed_at DATE,
    retrospective TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_projects_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT ck_projects_status CHECK (status IN ('IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'ARCHIVED')),
    CONSTRAINT ck_projects_completed_at CHECK (completed_at IS NULL OR started_at IS NULL OR completed_at >= started_at)
);

CREATE TABLE project_specifications (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    pattern_method VARCHAR(30),
    pattern_name VARCHAR(150),
    finished_width_cm NUMERIC(6, 2),
    finished_height_cm NUMERIC(6, 2),
    finished_size_memo TEXT,
    sewing_machine_setting_memo TEXT,
    needle_memo TEXT,
    thread_memo TEXT,
    memo TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_project_specifications_project_id FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT uk_project_specifications_project_id UNIQUE (project_id),
    CONSTRAINT ck_project_specifications_pattern_method CHECK (
        pattern_method IS NULL OR pattern_method IN ('SELF_DRAFTED', 'COPIED', 'MODIFIED')
    )
);

CREATE TABLE project_references (
    id BIGSERIAL PRIMARY KEY,
    project_specification_id BIGINT NOT NULL,
    url TEXT NOT NULL,
    title VARCHAR(200),
    memo TEXT,
    sort_order INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_project_references_project_specification_id
        FOREIGN KEY (project_specification_id) REFERENCES project_specifications (id) ON DELETE CASCADE,
    CONSTRAINT ck_project_references_sort_order CHECK (sort_order >= 0)
);

CREATE TABLE daily_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    worked_date DATE,
    title VARCHAR(150),
    work_types TEXT,
    duration_minutes INTEGER NOT NULL DEFAULT 0,
    memo TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_daily_logs_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_daily_logs_project_id FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT ck_daily_logs_status CHECK (status IN ('DRAFT', 'PUBLISHED')),
    CONSTRAINT ck_daily_logs_duration_non_negative CHECK (duration_minutes >= 0),
    CONSTRAINT ck_daily_logs_published_worked_date CHECK (status <> 'PUBLISHED' OR worked_date IS NOT NULL)
);

CREATE TABLE daily_log_time_entries (
    id BIGSERIAL PRIMARY KEY,
    daily_log_id BIGINT NOT NULL,
    started_at TIME NOT NULL,
    ended_at TIME NOT NULL,
    duration_minutes INTEGER NOT NULL,
    memo TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_daily_log_time_entries_daily_log_id FOREIGN KEY (daily_log_id) REFERENCES daily_logs (id) ON DELETE CASCADE,
    CONSTRAINT ck_daily_log_time_entries_time_range CHECK (ended_at > started_at),
    CONSTRAINT ck_daily_log_time_entries_duration_positive CHECK (duration_minutes > 0)
);

CREATE TABLE fabrics (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    product_name VARCHAR(200),
    product_code VARCHAR(100),
    product_url TEXT,
    store_name VARCHAR(150),
    purchased_at DATE,
    purchase_price INTEGER,
    color VARCHAR(100),
    size VARCHAR(100),
    width VARCHAR(100),
    material_composition TEXT,
    memo TEXT,
    rating INTEGER,
    repurchase_intention VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fabrics_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT ck_fabrics_rating_range CHECK (rating IS NULL OR rating BETWEEN 1 AND 5),
    CONSTRAINT ck_fabrics_purchase_price_non_negative CHECK (purchase_price IS NULL OR purchase_price >= 0),
    CONSTRAINT ck_fabrics_repurchase_intention CHECK (repurchase_intention IN ('YES', 'NO', 'UNKNOWN'))
);

CREATE TABLE project_fabrics (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    fabric_id BIGINT NOT NULL,
    memo TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_project_fabrics_project_id FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT fk_project_fabrics_fabric_id FOREIGN KEY (fabric_id) REFERENCES fabrics (id) ON DELETE CASCADE,
    CONSTRAINT uk_project_fabrics_project_id_fabric_id UNIQUE (project_id, fabric_id)
);

CREATE TABLE photos (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    owner_type VARCHAR(30) NOT NULL,
    owner_id BIGINT NOT NULL,
    is_thumbnail BOOLEAN NOT NULL DEFAULT FALSE,
    original_key TEXT NOT NULL,
    medium_key TEXT,
    thumbnail_key TEXT,
    width INTEGER,
    height INTEGER,
    size_bytes BIGINT,
    sort_order INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    processed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_photos_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT ck_photos_owner_type CHECK (owner_type IN ('PROJECT', 'DAILY_LOG', 'FABRIC')),
    CONSTRAINT ck_photos_status CHECK (status IN ('UPLOADING', 'PROCESSING', 'READY', 'FAILED')),
    CONSTRAINT ck_photos_sort_order CHECK (sort_order >= 0),
    CONSTRAINT ck_photos_width_positive CHECK (width IS NULL OR width > 0),
    CONSTRAINT ck_photos_height_positive CHECK (height IS NULL OR height > 0),
    CONSTRAINT ck_photos_size_bytes_positive CHECK (size_bytes IS NULL OR size_bytes > 0)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_projects_user_status ON projects (user_id, status);
CREATE INDEX idx_projects_user_created_at ON projects (user_id, created_at);
CREATE INDEX idx_project_references_project_specification_id ON project_references (project_specification_id);
CREATE INDEX idx_daily_logs_user_status_worked_date ON daily_logs (user_id, status, worked_date);
CREATE INDEX idx_daily_logs_project_status_worked_date ON daily_logs (project_id, status, worked_date);
CREATE INDEX idx_daily_log_time_entries_daily_log_id ON daily_log_time_entries (daily_log_id);
CREATE INDEX idx_fabrics_user_created_at ON fabrics (user_id, created_at);
CREATE INDEX idx_fabrics_user_store_name ON fabrics (user_id, store_name);
CREATE INDEX idx_fabrics_user_product_code ON fabrics (user_id, product_code);
CREATE INDEX idx_project_fabrics_project_id ON project_fabrics (project_id);
CREATE INDEX idx_project_fabrics_fabric_id ON project_fabrics (fabric_id);
CREATE INDEX idx_photos_owner_type_owner_id_sort_order ON photos (owner_type, owner_id, sort_order);
CREATE INDEX idx_photos_user_status ON photos (user_id, status);
