CREATE TABLE IF NOT EXISTS time_entries (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT,
    user_id BIGINT,
    hours DOUBLE PRECISION,
    date TIMESTAMP,
    approved BOOLEAN
);
