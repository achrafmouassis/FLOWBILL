-- Ajout d'index pour la performance multi-tenant
CREATE INDEX idx_users_tenant ON users(tenant_id);
