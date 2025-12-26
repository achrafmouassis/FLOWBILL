CREATE TABLE time_entries (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    hours DOUBLE PRECISION NOT NULL,
    date TIMESTAMP NOT NULL,
    approved BOOLEAN DEFAULT FALSE,
    comments TEXT,
    tenant_id VARCHAR(50) NOT NULL
);
