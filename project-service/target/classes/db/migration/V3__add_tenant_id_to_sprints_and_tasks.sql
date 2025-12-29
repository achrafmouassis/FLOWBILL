-- Add tenant_id to sprints table
ALTER TABLE sprints ADD COLUMN tenant_id VARCHAR(50);
-- Update existing records to have a default tenant_id if necessary, or just make it nullable for now? 
-- The entities mark it as nullable=true (by default), but Project had it nullable=false.
-- Let's make it nullable initially to avoid issues with existing data, 
-- but since this is DEV environment and we likely have few rows, we can try to enforce it or just leave it nullable.
-- Flowbill architecture seems to rely on tenant_id strictness.
-- But for migration safety (if rows exist), we'll add it as nullable first.

-- Ideally we should update existing rows.
-- UPDATE sprints SET tenant_id = (SELECT tenant_id FROM projects WHERE projects.id = sprints.project_id);

-- Add tenant_id to tasks table
ALTER TABLE tasks ADD COLUMN tenant_id VARCHAR(50);
-- UPDATE tasks SET tenant_id = (SELECT tenant_id FROM projects WHERE projects.id = tasks.project_id);
