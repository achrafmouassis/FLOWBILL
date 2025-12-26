-- Projects: Add tenant_id
ALTER TABLE projects ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255);

-- Tasks: Add description, estimated_hours, created_at
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS estimated_hours DOUBLE PRECISION;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

-- Time Entries: Add tenant_id, comments
ALTER TABLE time_entries ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255);
ALTER TABLE time_entries ADD COLUMN IF NOT EXISTS comments TEXT;

-- Invoices: Add tenant_id, project_id, invoice_number, status, due_date, pdf_path
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255);
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS project_id BIGINT;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS invoice_number VARCHAR(255) UNIQUE;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS status VARCHAR(255);
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS due_date DATE;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS pdf_path VARCHAR(255);
