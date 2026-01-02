-- Table : quotes (Devis)
CREATE TABLE quotes (
    id BIGSERIAL PRIMARY KEY,
    quote_number VARCHAR(50) UNIQUE NOT NULL, -- DEV-ABC-2026-001
    tenant_id VARCHAR(50) NOT NULL,
    project_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'DRAFT', -- DRAFT, SENT, ACCEPTED, REJECTED, EXPIRED
    
    amount_ht DECIMAL(12, 2) NOT NULL, -- Montant Hors Taxes
    tva_rate DECIMAL(5, 2) DEFAULT 20.00, -- Taux TVA (20%)
    tva_amount DECIMAL(12, 2) NOT NULL, -- Montant TVA calculé
    amount_ttc DECIMAL(12, 2) NOT NULL, -- Montant TTC
    
    currency VARCHAR(3) DEFAULT 'MAD',
    
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    sent_at TIMESTAMP,
    accepted_at TIMESTAMP,
    accepted_by BIGINT,
    
    valid_until DATE, -- Date d'expiration
    
    notes TEXT
);

-- Table : quote_lines (Lignes de détail du devis)
CREATE TABLE quote_lines (
    id BIGSERIAL PRIMARY KEY,
    quote_id BIGINT NOT NULL,
    
    description TEXT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL, -- Heures : 42.5
    unit_price DECIMAL(12, 2) NOT NULL, -- Prix unitaire : 500 MAD/h
    total_ht DECIMAL(12, 2) NOT NULL, -- quantity * unit_price
    
    task_id BIGINT, -- Référence optionnelle à une tâche
    time_entry_ids JSONB, -- [123, 456, 789] IDs des time_entries inclus
    
    FOREIGN KEY (quote_id) REFERENCES quotes(id) ON DELETE CASCADE
);

-- Table : invoices (Factures)
CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE NOT NULL, -- INV-ABC-2026-001
    tenant_id VARCHAR(50) NOT NULL,
    quote_id BIGINT NOT NULL, -- Lien vers le devis source
    project_id BIGINT NOT NULL,
    
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, PAID, CANCELLED
    
    amount_ht DECIMAL(12, 2) NOT NULL,
    tva_rate DECIMAL(5, 2) NOT NULL,
    tva_amount DECIMAL(12, 2) NOT NULL,
    amount_ttc DECIMAL(12, 2) NOT NULL,
    
    currency VARCHAR(3) DEFAULT 'MAD',
    
    issue_date DATE NOT NULL, -- Date d'émission
    due_date DATE NOT NULL, -- Date d'échéance (ex: issue_date + 30 jours)
    
    paid_at TIMESTAMP,
    paid_amount DECIMAL(12, 2),
    
    pdf_url VARCHAR(500), -- URL du PDF généré (S3, local storage)
    pdf_generated_at TIMESTAMP,
    
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    cancelled_at TIMESTAMP,
    cancelled_by BIGINT,
    cancellation_reason TEXT,
    
    FOREIGN KEY (quote_id) REFERENCES quotes(id)
);

-- Table : invoice_lines (Lignes de détail facture)
CREATE TABLE invoice_lines (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    
    description TEXT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    total_ht DECIMAL(12, 2) NOT NULL,
    
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE
);

-- Table : billing_events (Audit Log)
CREATE TABLE billing_events (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    entity_type VARCHAR(20) NOT NULL, -- QUOTE, INVOICE
    entity_id BIGINT NOT NULL,
    
    event_type VARCHAR(50) NOT NULL, -- CREATED, ACCEPTED, REJECTED, INVOICED, PAID
    
    actor_user_id BIGINT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    before_state JSONB, -- État avant l'action
    after_state JSONB, -- État après l'action
    
    metadata JSONB -- Données supplémentaires
);

-- Index pour performance
CREATE INDEX idx_quotes_tenant ON quotes(tenant_id);
CREATE INDEX idx_quotes_status ON quotes(status);
CREATE INDEX idx_invoices_tenant ON invoices(tenant_id);
CREATE INDEX idx_invoices_quote ON invoices(quote_id);
CREATE INDEX idx_billing_events_entity ON billing_events(entity_type, entity_id);
