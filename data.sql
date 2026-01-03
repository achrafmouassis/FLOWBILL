-- ===============================================================================================
-- FLOWBILL REALISTIC SEED DATA
-- ===============================================================================================

-- 1. AUTH SERVICE DATA
-- Roles
INSERT INTO roles (name) VALUES 
('ROLE_SUPER_ADMIN'), 
('ROLE_ADMIN_ENTREPRISE'), 
('ROLE_USER') 
ON CONFLICT (name) DO NOTHING;

-- USERS
INSERT INTO users (email, password, enabled, full_name, telephone, tenant_id) VALUES
('superadmin@flowbill.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'FlowBill SuperAdmin', '+1000000000', NULL)
ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;

INSERT INTO users (email, password, enabled, full_name, telephone, tenant_id) VALUES
('admin@acme.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Alice Acme (Admin)', '+12025550101', 'acme'),
('manager1@acme.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Bob Manager', '+12025550102', 'acme'),
('manager2@acme.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Carol Lead', '+12025550103', 'acme'),
('dev1@acme.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Dave Developer', '+12025550104', 'acme'),
('dev2@acme.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Eve Engineer', '+12025550105', 'acme'),
('dev3@acme.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Frank Fullstack', '+12025550106', 'acme'),
('dev4@acme.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Grace Git', '+12025550107', 'acme'),
('dev5@acme.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Hank Hacker', '+12025550108', 'acme'),
('viewer1@acme.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Ivy Intern', '+12025550109', 'acme'),
('viewer2@acme.com', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Jack Junior', '+12025550110', 'acme')
ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;

INSERT INTO users (email, password, enabled, full_name, telephone, tenant_id) VALUES
('ceo@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Sarah Founder', '+33612345678', 'startupflow'),
('cto@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Mike CTO', '+33612345679', 'startupflow'),
('dev.lead@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Tom TechLead', '+33612345680', 'startupflow'),
('frontend@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Anna React', '+33612345681', 'startupflow'),
('backend@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Ben Java', '+33612345682', 'startupflow'),
('devops@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Chris Docker', '+33612345683', 'startupflow'),
('mobile@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Diana Dart', '+33612345684', 'startupflow'),
('data@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Eric SQL', '+33612345685', 'startupflow'),
('investor1@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Fiona VC', '+33612345686', 'startupflow'),
('product@startupflow.io', '$2a$10$yS4xTCwRXfdc8YiEqJZcj.0UpVqFkYxnh8xYhlhUlTESyPlf5YPua', true, 'Greg PM', '+33612345687', 'startupflow')
ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;

-- Assign Roles
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email='superadmin@flowbill.com' AND r.name='ROLE_SUPER_ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email='admin@acme.com' AND r.name='ROLE_ADMIN_ENTREPRISE'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email='ceo@startupflow.io' AND r.name='ROLE_ADMIN_ENTREPRISE'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- 2. PROJECT SERVICE DATA (Idempotent using WHERE NOT EXISTS)
-- ACME Projects
INSERT INTO projects (name, description, tenant_id)
SELECT 'ACME ERP Migration', 'Migrating legacy ERP to Cloud', 'acme'
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE name='ACME ERP Migration' AND tenant_id='acme');

INSERT INTO projects (name, description, tenant_id)
SELECT 'Flowbill MVP', 'Core SaaS platform development', 'startupflow'
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE name='Flowbill MVP' AND tenant_id='startupflow');

-- Sprints
INSERT INTO sprints (name, start_date, end_date, goal, status, project_id, tenant_id)
SELECT 'Sprint 1: Foundation', NOW() - INTERVAL '5 days', NOW() + INTERVAL '9 days', 'Setup infrastructure', 'ACTIVE', 
    (SELECT id FROM projects WHERE name='ACME ERP Migration' AND tenant_id='acme' LIMIT 1), 'acme'
WHERE NOT EXISTS (SELECT 1 FROM sprints WHERE name='Sprint 1: Foundation' AND tenant_id='acme');

INSERT INTO sprints (name, start_date, end_date, goal, status, project_id, tenant_id)
SELECT 'Sprint Alpha', NOW() - INTERVAL '2 days', NOW() + INTERVAL '12 days', 'Core Features', 'ACTIVE', 
    (SELECT id FROM projects WHERE name='Flowbill MVP' AND tenant_id='startupflow' LIMIT 1), 'startupflow'
WHERE NOT EXISTS (SELECT 1 FROM sprints WHERE name='Sprint Alpha' AND tenant_id='startupflow');

-- Tasks
INSERT INTO tasks (title, description, status, estimated_hours, project_id, sprint_id, tenant_id, assigned_user_id, type)
SELECT 'Setup AWS VPC', 'Create VPC, Subnets, SG', 'DONE', 3, 
    (SELECT id FROM projects WHERE name='ACME ERP Migration' AND tenant_id='acme' LIMIT 1),
    (SELECT id FROM sprints WHERE name='Sprint 1: Foundation' AND tenant_id='acme' LIMIT 1), 
    'acme', (SELECT id FROM users WHERE email='dev1@acme.com' LIMIT 1), 'TASK'
WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE title='Setup AWS VPC' AND tenant_id='acme');

INSERT INTO tasks (title, description, status, estimated_hours, project_id, sprint_id, tenant_id, assigned_user_id, type)
SELECT 'Backend API Init', 'Spring Boot Setup', 'DONE', 3, 
    (SELECT id FROM projects WHERE name='Flowbill MVP' AND tenant_id='startupflow' LIMIT 1),
    (SELECT id FROM sprints WHERE name='Sprint Alpha' AND tenant_id='startupflow' LIMIT 1), 
    'startupflow', (SELECT id FROM users WHERE email='backend@startupflow.io' LIMIT 1), 'TASK'
WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE title='Backend API Init' AND tenant_id='startupflow');

-- END OF SEED DATA
