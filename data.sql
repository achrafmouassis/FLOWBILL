-- ===============================================================================================
-- FLOWBILL REALISTIC SEED DATA (FULLY CONNECTED)
-- ===============================================================================================

-- 1. AUTH SERVICE DATA
-- Roles
INSERT INTO roles (name) VALUES 
('ROLE_SUPER_ADMIN'), 
('ROLE_ADMIN_ENTREPRISE'), 
('ROLE_USER') 
ON CONFLICT (name) DO NOTHING;

-- USERS (StartupFlow, Acme, EcoCorp)
INSERT INTO users (email, password, enabled, full_name, telephone, tenant_id) VALUES
('superadmin@flowbill.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'FlowBill SuperAdmin', '+1000000000', NULL),
('admin@acme.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Alice Acme (Admin)', '+12025550101', 'acme'),
('dev1@acme.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Dave Developer', '+12025550104', 'acme'),
('dev2@acme.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Eve Engineer', '+12025550105', 'acme'),
('ceo@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Sarah Founder', '+33612345678', 'startupflow'),
('cto@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Mike CTO', '+33612345679', 'startupflow'),
('dev.lead@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Tom TechLead', '+33612345680', 'startupflow'),
('frontend@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Anna React', '+33612345681', 'startupflow'),
('backend@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Ben Java', '+33612345682', 'startupflow'),
('admin@ecocorp.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Jean Green (Admin)', '+33122334455', 'ecocorp'),
('lead@ecocorp.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Marc Lead', '+33122334456', 'ecocorp'),
('dev@ecocorp.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Sophie Dev', '+33122334457', 'ecocorp')
ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;

-- Assign Roles
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r WHERE u.email IN ('admin@acme.com', 'ceo@startupflow.io', 'admin@ecocorp.com') AND r.name='ROLE_ADMIN_ENTREPRISE'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r WHERE u.email IN ('dev1@acme.com', 'dev2@acme.com', 'cto@startupflow.io', 'dev.lead@startupflow.io', 'frontend@startupflow.io', 'backend@startupflow.io', 'lead@ecocorp.com', 'dev@ecocorp.com') AND r.name='ROLE_USER'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- 2. PROJECT SERVICE DATA
-- StartupFlow Projects
INSERT INTO projects (name, code, description, client_name, status, type, start_date, target_date, tenant_id)
SELECT 'FlowBill MVP', 'FB-MVP', 'Minimum Viable Product for the billing platform', 'Internal', 'ACTIVE', 'WEB', CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE + INTERVAL '60 days', 'startupflow'
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE code='FB-MVP' AND tenant_id='startupflow');

-- Acme Projects
INSERT INTO projects (name, code, description, client_name, status, type, start_date, target_date, tenant_id)
SELECT 'Legacy Migration', 'ACME-LEG', 'Migrating legacy ERP to cloud', 'Acme Corp Internal', 'ACTIVE', 'INFRA', CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE + INTERVAL '90 days', 'acme'
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE code='ACME-LEG' AND tenant_id='acme');

INSERT INTO projects (name, code, description, client_name, status, type, start_date, target_date, tenant_id)
SELECT 'Cybersecurity Audit', 'ACME-SEC', 'Enterprise-wide security audit and hardening', 'Acme Corp Internal', 'ON_HOLD', 'INFRA', CURRENT_DATE - INTERVAL '60 days', CURRENT_DATE - INTERVAL '5 days', 'acme'
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE code='ACME-SEC' AND tenant_id='acme');

-- Sprints (StartupFlow)
INSERT INTO sprints (name, start_date, end_date, goal, status, project_id, tenant_id)
SELECT 'Sprint 1: Auth & Billing', CURRENT_DATE - INTERVAL '14 days', CURRENT_DATE - INTERVAL '1 day', 'Implement core auth and basic billing', 'COMPLETED', 
    (SELECT id FROM projects WHERE code='FB-MVP' AND tenant_id='startupflow' LIMIT 1), 'startupflow'
WHERE NOT EXISTS (SELECT 1 FROM sprints WHERE name='Sprint 1: Auth & Billing' AND tenant_id='startupflow');

INSERT INTO sprints (name, start_date, end_date, goal, status, project_id, tenant_id)
SELECT 'Sprint 2: Project Management', CURRENT_DATE, CURRENT_DATE + INTERVAL '13 days', 'Start project service features', 'ACTIVE', 
    (SELECT id FROM projects WHERE code='FB-MVP' AND tenant_id='startupflow' LIMIT 1), 'startupflow'
WHERE NOT EXISTS (SELECT 1 FROM sprints WHERE name='Sprint 2: Project Management' AND tenant_id='startupflow');

-- Tasks for StartupFlow (Dependency for ActivityLogs)
INSERT INTO tasks (title, description, status, estimation, project_id, sprint_id, tenant_id, assigned_user_id, type)
SELECT 'Database Migrations', 'Setup Flyway and initial schema', 'DONE', 3, 
    (SELECT id FROM projects WHERE code='FB-MVP' AND tenant_id='startupflow' LIMIT 1),
    (SELECT id FROM sprints WHERE name='Sprint 1: Auth & Billing' AND tenant_id='startupflow' LIMIT 1), 
    'startupflow', (SELECT id FROM users WHERE email='backend@startupflow.io' LIMIT 1), 'TASK'
WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE title='Database Migrations' AND tenant_id='startupflow');

-- Team Assignments (StartupFlow)
INSERT INTO project_team_members (project_id, user_id, role, capacity_hours, tenant_id)
SELECT p.id, u.id, 'Backend Developer', 40, 'startupflow'
FROM projects p, users u WHERE p.code='FB-MVP' AND u.email='backend@startupflow.io'
AND NOT EXISTS (SELECT 1 FROM project_team_members WHERE project_id=p.id AND user_id=u.id);

INSERT INTO project_team_members (project_id, user_id, role, capacity_hours, tenant_id)
SELECT p.id, u.id, 'Frontend Developer', 35, 'startupflow'
FROM projects p, users u WHERE p.code='FB-MVP' AND u.email='frontend@startupflow.io'
AND NOT EXISTS (SELECT 1 FROM project_team_members WHERE project_id=p.id AND user_id=u.id);

-- Team Assignments (Acme)
INSERT INTO project_team_members (project_id, user_id, role, capacity_hours, tenant_id)
SELECT p.id, u.id, 'Developer', 40, 'acme'
FROM projects p, users u WHERE p.code='ACME-LEG' AND u.email='dev1@acme.com'
AND NOT EXISTS (SELECT 1 FROM project_team_members WHERE project_id=p.id AND user_id=u.id);

-- EcoCorp Projects
INSERT INTO projects (name, code, description, client_name, status, type, start_date, target_date, tenant_id)
SELECT 'Solar Grid Optimizer', 'SGO-2024', 'AI-driven optimization for community solar grids', 'GreenCity Council', 'ACTIVE', 'WEB', CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE + INTERVAL '120 days', 'ecocorp'
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE code='SGO-2024' AND tenant_id='ecocorp');

INSERT INTO projects (name, code, description, client_name, status, type, start_date, target_date, tenant_id)
SELECT 'Wind Turbine Monitor', 'WTM-V2', 'Real-time IoT monitoring for offshore wind farms', 'EcoEnergy NV', 'PLANNED', 'MOBILE', CURRENT_DATE + INTERVAL '5 days', CURRENT_DATE + INTERVAL '180 days', 'ecocorp'
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE code='WTM-V2' AND tenant_id='ecocorp');

-- Team Assignments (EcoCorp)
INSERT INTO project_team_members (project_id, user_id, role, capacity_hours, tenant_id)
SELECT p.id, u.id, 'Project Lead', 40, 'ecocorp'
FROM projects p, users u WHERE p.code='SGO-2024' AND u.email='lead@ecocorp.com'
AND NOT EXISTS (SELECT 1 FROM project_team_members WHERE project_id=p.id AND user_id=u.id);

INSERT INTO project_team_members (project_id, user_id, role, capacity_hours, tenant_id)
SELECT p.id, u.id, 'Fullstack Developer', 35, 'ecocorp'
FROM projects p, users u WHERE p.code='SGO-2024' AND u.email='dev@ecocorp.com'
AND NOT EXISTS (SELECT 1 FROM project_team_members WHERE project_id=p.id AND user_id=u.id);

-- Sprints (EcoCorp)
INSERT INTO sprints (name, start_date, end_date, goal, status, project_id, tenant_id)
SELECT 'Grid Init: Data Connectors', CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '9 days', 'Establish real-time data ingestion', 'ACTIVE', 
    (SELECT id FROM projects WHERE code='SGO-2024' AND tenant_id='ecocorp' LIMIT 1), 'ecocorp'
WHERE NOT EXISTS (SELECT 1 FROM sprints WHERE name='Grid Init: Data Connectors' AND tenant_id='ecocorp');

-- Dense Backlog for EcoCorp SGO-2024
-- Story 1: Data Ingestion
INSERT INTO tasks (title, description, status, estimation, project_id, sprint_id, tenant_id, type, moscow_priority, wsjf_score)
SELECT 'IoT Connector API', 'Ingest data from smart meters', 'IN_PROGRESS', 13, 
    (SELECT id FROM projects WHERE code='SGO-2024' AND tenant_id='ecocorp' LIMIT 1),
    (SELECT id FROM sprints WHERE name='Grid Init: Data Connectors' AND tenant_id='ecocorp' LIMIT 1), 
    'ecocorp', 'STORY', 'MUST_HAVE', 25.0
WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE title='IoT Connector API' AND tenant_id='ecocorp');

-- Story 2: Visualization
INSERT INTO tasks (title, description, status, estimation, project_id, sprint_id, tenant_id, type, moscow_priority, wsjf_score)
SELECT 'Real-time Map View', 'Visualize energy flow across the grid', 'TODO', 21, 
    (SELECT id FROM projects WHERE code='SGO-2024' AND tenant_id='ecocorp' LIMIT 1),
    (SELECT id FROM sprints WHERE name='Grid Init: Data Connectors' AND tenant_id='ecocorp' LIMIT 1), 
    'ecocorp', 'STORY', 'SHOULD_HAVE', 12.0
WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE title='Real-time Map View' AND tenant_id='ecocorp');

-- Subtasks for EcoCorp SGO-2024
INSERT INTO tasks (title, description, status, estimation, project_id, sprint_id, tenant_id, assigned_user_id, type, parent_story_id)
SELECT 'Mapbox Integration', 'Setup Mapbox GL JS', 'DONE', 5, 
    (SELECT id FROM projects WHERE code='SGO-2024' AND tenant_id='ecocorp' LIMIT 1),
    (SELECT id FROM sprints WHERE name='Grid Init: Data Connectors' AND tenant_id='ecocorp' LIMIT 1), 
    'ecocorp', (SELECT id FROM users WHERE email='dev@ecocorp.com' LIMIT 1), 'TASK',
    (SELECT id FROM tasks WHERE title='Real-time Map View' AND tenant_id='ecocorp' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE title='Mapbox Integration' AND tenant_id='ecocorp');

-- 3. ACTIVITY LOGS (Realistic timeline data)
INSERT INTO activity_log (tenant_id, event_type, actor_user_id, entity_type, entity_id, message, created_at)
SELECT 'startupflow', 'TASK_COMPLETED', (SELECT id FROM users WHERE email='backend@startupflow.io' LIMIT 1), 'TASK',
    (SELECT id FROM tasks WHERE title='Database Migrations' AND tenant_id='startupflow' LIMIT 1),
    'Ben Java a terminé la tâche : Database Migrations', CURRENT_TIMESTAMP - INTERVAL '1 day';

INSERT INTO activity_log (tenant_id, event_type, actor_user_id, entity_type, entity_id, message, created_at)
SELECT 'startupflow', 'SPRINT_STARTED', (SELECT id FROM users WHERE email='ceo@startupflow.io' LIMIT 1), 'SPRINT',
    (SELECT id FROM sprints WHERE name='Sprint 1: Auth & Billing' AND tenant_id='startupflow' LIMIT 1),
    'Sarah Founder a démarré le sprint : Sprint 1: Auth & Billing', CURRENT_TIMESTAMP - INTERVAL '2 days';

INSERT INTO activity_log (tenant_id, event_type, actor_user_id, entity_type, entity_id, message, created_at)
SELECT 'ecocorp', 'SPRINT_STARTED', (SELECT id FROM users WHERE email='admin@ecocorp.com' LIMIT 1), 'SPRINT',
    (SELECT id FROM sprints WHERE name='Grid Init: Data Connectors' AND tenant_id='ecocorp' LIMIT 1),
    'Jean Green a démarré le sprint : Grid Init: Data Connectors', CURRENT_TIMESTAMP - INTERVAL '5 days';

INSERT INTO activity_log (tenant_id, event_type, actor_user_id, entity_type, entity_id, message, created_at)
SELECT 'ecocorp', 'TASK_COMPLETED', (SELECT id FROM users WHERE email='dev@ecocorp.com' LIMIT 1), 'TASK',
    (SELECT id FROM tasks WHERE title='Mapbox Integration' AND tenant_id='ecocorp' LIMIT 1),
    'Sophie Dev a terminé la tâche : Mapbox Integration', CURRENT_TIMESTAMP - INTERVAL '12 hours';

-- More Stories for Backend (StartupFlow)
INSERT INTO tasks (title, description, status, estimation, project_id, sprint_id, tenant_id, type, moscow_priority, wsjf_score)
SELECT 'Stripe Integration', 'Connect to Stripe API for payments', 'BLOCKED', 8, 
    (SELECT id FROM projects WHERE code='FB-MVP' AND tenant_id='startupflow' LIMIT 1),
    (SELECT id FROM sprints WHERE name='Sprint 1: Auth & Billing' AND tenant_id='startupflow' LIMIT 1), 
    'startupflow', 'STORY', 'MUST_HAVE', 18.0
WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE title='Stripe Integration' AND tenant_id='startupflow');

-- END OF SEED DATA (Scaled with multiple tenants and projects)
