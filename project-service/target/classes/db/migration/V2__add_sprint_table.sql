-- Create Sprints table
CREATE TABLE sprints (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    project_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'PLANNED', -- PLANNED, ACTIVE, COMPLETED
    goal TEXT,
    CONSTRAINT fk_sprints_project FOREIGN KEY (project_id) REFERENCES projects(id)
);

-- Update Tasks table to support Scrumban
ALTER TABLE tasks ADD COLUMN sprint_id BIGINT;
ALTER TABLE tasks ADD COLUMN priority VARCHAR(20) DEFAULT 'MEDIUM'; -- LOW, MEDIUM, HIGH, URGENT
ALTER TABLE tasks ADD COLUMN estimation INT; -- Story Points
ALTER TABLE tasks ADD COLUMN due_date TIMESTAMP;

ALTER TABLE tasks ADD CONSTRAINT fk_tasks_sprint FOREIGN KEY (sprint_id) REFERENCES sprints(id);
