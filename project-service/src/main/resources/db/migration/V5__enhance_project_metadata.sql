-- Enhancement of projects table with metadata
ALTER TABLE projects ADD COLUMN code VARCHAR(50);
ALTER TABLE projects ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE'; -- ACTIVE, PLANNED, ON_HOLD, COMPLETED, ARCHIVED
ALTER TABLE projects ADD COLUMN type VARCHAR(50); -- WEB, MOBILE, API, etc.
ALTER TABLE projects ADD COLUMN client_name VARCHAR(255);
ALTER TABLE projects ADD COLUMN start_date DATE;
ALTER TABLE projects ADD COLUMN target_date DATE;
ALTER TABLE projects ADD COLUMN description_detail TEXT;
ALTER TABLE projects ADD COLUMN workflow_config TEXT; -- JSON configuration for Kanban columns
ALTER TABLE projects ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE projects ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Create Project Team Members table
CREATE TABLE project_team_members (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(100),
    capacity_hours INTEGER,
    joined_at DATE DEFAULT CURRENT_DATE,
    tenant_id VARCHAR(50),
    CONSTRAINT fk_team_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Index for performance
CREATE INDEX idx_team_project ON project_team_members(project_id);
CREATE INDEX idx_team_user ON project_team_members(user_id);
