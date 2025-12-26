CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE,
    amount DECIMAL(19, 2),
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date DATE,
    status VARCHAR(50) DEFAULT 'DRAFT',
    details TEXT,
    pdf_path VARCHAR(255),
    tenant_id VARCHAR(50) NOT NULL,
    project_id BIGINT
);
