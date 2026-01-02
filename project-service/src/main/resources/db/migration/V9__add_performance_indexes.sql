-- V9: Add composite indexes for multi-tenant query optimization
-- Date: 2025-12-30
-- Purpose: Improve dashboard and backlog query performance

-- Optimize dashboard queries by tenant, project, and status
CREATE INDEX IF NOT EXISTS idx_tasks_tenant_project_status 
    ON tasks(tenant_id, project_id, status);

-- Optimize sprint board queries
CREATE INDEX IF NOT EXISTS idx_tasks_tenant_sprint_status 
    ON tasks(tenant_id, sprint_id, status);

-- Optimize backlog queries (stories only)
CREATE INDEX IF NOT EXISTS idx_tasks_tenant_type_moscow 
    ON tasks(tenant_id, type, moscow_priority) 
    WHERE type = 'STORY';

-- Optimize sprint queries by tenant and project
CREATE INDEX IF NOT EXISTS idx_sprints_tenant_project_status 
    ON sprints(tenant_id, project_id, status);

-- Optimize project queries by tenant
CREATE INDEX IF NOT EXISTS idx_projects_tenant_status 
    ON projects(tenant_id, status);

-- Optimize snapshot queries for burndown charts
CREATE INDEX IF NOT EXISTS idx_snapshots_sprint_date 
    ON sprint_snapshots(sprint_id, snapshot_date);

-- Add index on task dependencies for blocker queries
CREATE INDEX IF NOT EXISTS idx_task_dependencies_blocked 
    ON task_dependencies(blocked_id, blocker_id);

COMMENT ON INDEX idx_tasks_tenant_project_status IS 'Optimizes dashboard metrics by tenant and project';
COMMENT ON INDEX idx_tasks_tenant_sprint_status IS 'Optimizes sprint board and Kanban queries';
COMMENT ON INDEX idx_tasks_tenant_type_moscow IS 'Optimizes backlog story filtering by MoSCoW priority';
