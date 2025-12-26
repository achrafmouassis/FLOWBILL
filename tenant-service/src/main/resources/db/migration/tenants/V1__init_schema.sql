-- Project Service Tables
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    tenant_id VARCHAR(50) NOT NULL
);

CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'TODO',
    estimated_hours DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    project_id BIGINT NOT NULL,
    assigned_user_id BIGINT,
    CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id)
);

-- Time Service Tables
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

-- Billing Service Tables
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
