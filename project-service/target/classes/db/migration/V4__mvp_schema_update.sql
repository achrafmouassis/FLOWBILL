-- Ajout du type de tâche et relation parent/enfant (User Story -> Tâches)
ALTER TABLE tasks ADD COLUMN type VARCHAR(20) DEFAULT 'TASK'; -- Valeurs: STORY, TASK, BUG
ALTER TABLE tasks ADD COLUMN parent_story_id BIGINT;
ALTER TABLE tasks ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Contrainte de clé étrangère pour la sous-tâche
ALTER TABLE tasks ADD CONSTRAINT fk_tasks_parent FOREIGN KEY (parent_story_id) REFERENCES tasks(id);

-- Table pour les critères d'acceptation des User Stories
CREATE TABLE acceptance_criteria (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_criteria_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

-- Table pour les dépendances entre tâches (Blocages)
CREATE TABLE task_dependencies (
    blocker_id BIGINT NOT NULL,
    blocked_id BIGINT NOT NULL,
    dependency_type VARCHAR(20) DEFAULT 'BLOCKING',
    PRIMARY KEY (blocker_id, blocked_id),
    CONSTRAINT fk_dep_blocker FOREIGN KEY (blocker_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_dep_blocked FOREIGN KEY (blocked_id) REFERENCES tasks(id) ON DELETE CASCADE
);

-- Table pour l'historique des changements d'état (Pour Cycle Time / Lead Time / Burndown)
CREATE TABLE task_history (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    from_status VARCHAR(50),
    to_status VARCHAR(50),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by_user_id BIGINT,
    tenant_id VARCHAR(50),
    CONSTRAINT fk_history_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

-- Table pour le journal d'activité global (Timeline Dashboard)
CREATE TABLE activity_log (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    event_type VARCHAR(50) NOT NULL, -- TASK_COMPLETED, SPRINT_STARTED, etc.
    actor_user_id BIGINT,
    entity_type VARCHAR(50), -- TASK, SPRINT, STORY
    entity_id BIGINT,
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table pour les préférences du dashboard par utilisateur
CREATE TABLE user_dashboard_preferences (
    user_id BIGINT PRIMARY KEY,
    layout_config TEXT, -- JSON stocké en TEXT
    alert_thresholds TEXT, -- JSON stocké en TEXT
    display_settings TEXT -- JSON stocké en TEXT
);
