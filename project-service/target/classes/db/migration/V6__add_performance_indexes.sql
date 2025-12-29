-- Ajout d'index pour optimiser les requêtes multi-tenant et de dashboard
CREATE INDEX idx_projects_tenant ON projects(tenant_id);
CREATE INDEX idx_sprints_tenant ON sprints(tenant_id);
CREATE INDEX idx_tasks_tenant ON tasks(tenant_id);
CREATE INDEX idx_tasks_assigned_user ON tasks(assigned_user_id);
CREATE INDEX idx_task_history_tenant ON task_history(tenant_id);
CREATE INDEX idx_activity_log_tenant ON activity_log(tenant_id);
CREATE INDEX idx_tasks_project ON tasks(project_id);
CREATE INDEX idx_sprints_project ON sprints(project_id);
