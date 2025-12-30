-- Ajout des champs Agile (MoSCoW, WSJF) à la table tasks
ALTER TABLE tasks ADD COLUMN moscow_priority VARCHAR(20); -- MUST_HAVE, SHOULD_HAVE, COULD_HAVE, WONT_HAVE
ALTER TABLE tasks ADD COLUMN business_value INTEGER;
ALTER TABLE tasks ADD COLUMN time_criticality INTEGER;
ALTER TABLE tasks ADD COLUMN risk_reduction INTEGER;
ALTER TABLE tasks ADD COLUMN wsjf_score DECIMAL(5,2);
ALTER TABLE tasks ADD COLUMN manual_order INTEGER;

-- Ajout de la vélocité cible au sprint
ALTER TABLE sprints ADD COLUMN target_velocity INTEGER;

-- Table pour les snapshots quotidiens de sprint (Burndown Chart)
CREATE TABLE sprint_daily_snapshots (
    id BIGSERIAL PRIMARY KEY,
    sprint_id BIGINT NOT NULL,
    snapshot_date DATE NOT NULL,
    remaining_sp INTEGER DEFAULT 0,
    completed_sp INTEGER DEFAULT 0,
    remaining_tasks INTEGER DEFAULT 0,
    completed_tasks INTEGER DEFAULT 0,
    tenant_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_snapshot_sprint FOREIGN KEY (sprint_id) REFERENCES sprints(id) ON DELETE CASCADE,
    CONSTRAINT uk_sprint_date UNIQUE (sprint_id, snapshot_date)
);

-- Index pour optimiser les recherches Backlog
CREATE INDEX idx_tasks_moscow ON tasks(moscow_priority);
CREATE INDEX idx_tasks_wsjf ON tasks(wsjf_score DESC);
CREATE INDEX idx_tasks_manual_order ON tasks(manual_order);
