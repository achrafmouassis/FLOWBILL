CREATE TABLE IF NOT EXISTS invoices (
    id BIGSERIAL PRIMARY KEY,
    amount DECIMAL,
    generated_at TIMESTAMP,
    details TEXT
);
