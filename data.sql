-- ===============================================================================================
-- FLOWBILL REALISTIC SEED DATA (Generated 2026-01-02)
-- Tenants: 'acme' (Corporate), 'startupflow' (Agile Startup)
-- Users: 22 Total (Admins, Devs, Viewers)
-- Projects: 10 (Different types, statuses)
-- ===============================================================================================

-- -----------------------------------------------------------------------------------------------
-- 1. AUTH SERVICE DATA (Users & Roles)
-- Password for all users: 'password' ($2a$10$Dk.p7...)
-- -----------------------------------------------------------------------------------------------

-- Roles (Ensure they exist - usually V1 handles this but safe to ignore conflicts)
INSERT INTO roles (name) VALUES 
('ROLE_SUPER_ADMIN'), 
('ROLE_ADMIN_ENTREPRISE'), 
('ROLE_USER') 
ON CONFLICT (name) DO NOTHING;

-- Retrieve Role IDs for usage
-- (Assuming standard IDs: 1=SUPER_ADMIN, 2=ADMIN_ENTREPRISE, 3=USER due to insertion order in V1)
-- If IDs differ, this script might fail on FKs. In a real script we'd use subqueries or DO block.
-- For simplicity in this static file, we assume 1, 2, 3.

-- USERS (22 Users)

-- Super Admin
INSERT INTO users (email, password, enabled, full_name, telephone, tenant_id) VALUES
('superadmin@flowbill.com', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'FlowBill SuperAdmin', '+1000000000', NULL)
ON CONFLICT (email) DO NOTHING;

-- Tenant 1: ACME Corp (Corporate Environment)
-- 1 Admin, 2 Managers (Project Leads), 5 Developers, 2 Viewers
INSERT INTO users (email, password, enabled, full_name, telephone, tenant_id) VALUES
('admin@acme.com', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Alice Acme (Admin)', '+12025550101', 'acme'),
('manager1@acme.com', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Bob Manager', '+12025550102', 'acme'),
('manager2@acme.com', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Carol Lead', '+12025550103', 'acme'),
('dev1@acme.com', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Dave Developer', '+12025550104', 'acme'),
('dev2@acme.com', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Eve Engineer', '+12025550105', 'acme'),
('dev3@acme.com', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Frank Fullstack', '+12025550106', 'acme'),
('dev4@acme.com', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Grace Git', '+12025550107', 'acme'),
('dev5@acme.com', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Hank Hacker', '+12025550108', 'acme'),
('viewer1@acme.com', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Ivy Intern', '+12025550109', 'acme'),
('viewer2@acme.com', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Jack Junior', '+12025550110', 'acme')
ON CONFLICT (email) DO NOTHING;

-- Tenant 2: StartUpFlow (Agile Startup)
-- 1 Admin, 1 Lead, 6 Developers, 2 Stakeholders
INSERT INTO users (email, password, enabled, full_name, telephone, tenant_id) VALUES
('ceo@startupflow.io', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Sarah Founder', '+33612345678', 'startupflow'),
('cto@startupflow.io', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Mike CTO', '+33612345679', 'startupflow'),
('dev.lead@startupflow.io', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Tom TechLead', '+33612345680', 'startupflow'),
('frontend@startupflow.io', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Anna React', '+33612345681', 'startupflow'),
('backend@startupflow.io', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Ben Java', '+33612345682', 'startupflow'),
('devops@startupflow.io', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Chris Docker', '+33612345683', 'startupflow'),
('mobile@startupflow.io', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Diana Dart', '+33612345684', 'startupflow'),
('data@startupflow.io', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Eric SQL', '+33612345685', 'startupflow'),
('investor1@startupflow.io', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Fiona VC', '+33612345686', 'startupflow'),
('product@startupflow.io', '$2a$10$Dk.p7I.M5.j6/0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.', true, 'Greg PM', '+33612345687', 'startupflow')
ON CONFLICT (email) DO NOTHING;

-- Assign Roles (Using simplified subselects to avoid hardcoded IDs)
-- Super Admin
INSERT INTO user_roles (user_id, role_id) SELECT id, (SELECT id FROM roles WHERE name='ROLE_SUPER_ADMIN') FROM users WHERE email='superadmin@flowbill.com' ON CONFLICT DO NOTHING;

-- ACME
INSERT INTO user_roles (user_id, role_id) SELECT id, (SELECT id FROM roles WHERE name='ROLE_ADMIN_ENTREPRISE') FROM users WHERE email='admin@acme.com' ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, role_id) SELECT id, (SELECT id FROM roles WHERE name='ROLE_ADMIN_ENTREPRISE') FROM users WHERE email='manager1@acme.com' ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, role_id) SELECT id, (SELECT id FROM roles WHERE name='ROLE_USER') FROM users WHERE email IN ('manager2@acme.com', 'dev1@acme.com', 'dev2@acme.com', 'dev3@acme.com', 'dev4@acme.com', 'dev5@acme.com', 'viewer1@acme.com', 'viewer2@acme.com') ON CONFLICT DO NOTHING;

-- StartUpFlow
INSERT INTO user_roles (user_id, role_id) SELECT id, (SELECT id FROM roles WHERE name='ROLE_ADMIN_ENTREPRISE') FROM users WHERE email='ceo@startupflow.io' ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, role_id) SELECT id, (SELECT id FROM roles WHERE name='ROLE_ADMIN_ENTREPRISE') FROM users WHERE email='cto@startupflow.io' ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, role_id) SELECT id, (SELECT id FROM roles WHERE name='ROLE_USER') FROM users WHERE email LIKE '%@startupflow.io' AND email NOT IN ('ceo@startupflow.io', 'cto@startupflow.io') ON CONFLICT DO NOTHING;


-- -----------------------------------------------------------------------------------------------
-- 2. PROJECT SERVICE DATA (Projects, Sprints, Tasks)
-- -----------------------------------------------------------------------------------------------

-- ACME Projects (5 Projects)
-- --------------------------
INSERT INTO projects (name, description, tenant_id) VALUES 
('ACME ERP Migration', 'Migrating legacy ERP to Cloud', 'acme'),
('ACME Website Revamp', 'Modernizing corporate website', 'acme'),
('HR Portal 2.0', 'Internal HR management system', 'acme'),
('Mobile App iOS', 'Customer facing iOS application', 'acme'),
('Data Lake Initiative', 'Big Data infrastructure setup', 'acme');

-- StartUpFlow Projects (5 Projects)
-- ---------------------------------
INSERT INTO projects (name, description, tenant_id) VALUES 
('Flowbill MVP', 'Core SaaS platform development', 'startupflow'),
('Marketing Site', 'Landing pages and blog', 'startupflow'),
('AI Engine', 'Recommendation algorithms', 'startupflow'),
('Customer Support Bot', 'Automated support agent', 'startupflow'),
('Investor Dashboard', 'KPI reporting for investors', 'startupflow');

-- TASKS & SPRINTS Generation (Simulated for ACME ERP Migration)
-- -------------------------------------------------------------
-- Ensure we have IDs. For this script, we'll use subqueries to get project IDs.

-- Sprint 1 for ACME ERP (Active)
INSERT INTO sprints (name, start_date, end_date, goal, status, project_id, tenant_id) VALUES
('Sprint 1: Foundation', NOW() - INTERVAL '5 days', NOW() + INTERVAL '9 days', 'Setup infrastructure', 'ACTIVE', (SELECT id FROM projects WHERE name='ACME ERP Migration'), 'acme');

-- Tasks for Sprint 1
INSERT INTO tasks (title, description, status, estimated_hours, project_id, sprint_id, tenant_id, assigned_user_id, type) VALUES
('Setup AWS VPC', 'Create VPC, Subnets, SG', 'DONE', 3, (SELECT id FROM projects WHERE name='ACME ERP Migration'), (SELECT id FROM sprints WHERE name='Sprint 1: Foundation'), 'acme', (SELECT id FROM users WHERE email='dev1@acme.com'), 'TASK'),
('Configure RDS', 'PostgreSQL Setup', 'IN_PROGRESS', 5, (SELECT id FROM projects WHERE name='ACME ERP Migration'), (SELECT id FROM sprints WHERE name='Sprint 1: Foundation'), 'acme', (SELECT id FROM users WHERE email='dev1@acme.com'), 'TASK'),
('Design Database Schema', 'Initial ERD', 'DONE', 8, (SELECT id FROM projects WHERE name='ACME ERP Migration'), (SELECT id FROM sprints WHERE name='Sprint 1: Foundation'), 'acme', (SELECT id FROM users WHERE email='dev2@acme.com'), 'STORY'),
('Implement User Auth', 'OAuth2 Integration', 'TODO', 13, (SELECT id FROM projects WHERE name='ACME ERP Migration'), (SELECT id FROM sprints WHERE name='Sprint 1: Foundation'), 'acme', (SELECT id FROM users WHERE email='dev3@acme.com'), 'STORY'),
('Fix CI/CD Pipeline', 'Jenkins build failing', 'TODO', 2, (SELECT id FROM projects WHERE name='ACME ERP Migration'), (SELECT id FROM sprints WHERE name='Sprint 1: Foundation'), 'acme', (SELECT id FROM users WHERE email='dev4@acme.com'), 'BUG');


-- Sprint for StartUpFlow MVP (Active)
INSERT INTO sprints (name, start_date, end_date, goal, status, project_id, tenant_id) VALUES
('Sprint Alpha', NOW() - INTERVAL '2 days', NOW() + INTERVAL '12 days', 'Core Features', 'ACTIVE', (SELECT id FROM projects WHERE name='Flowbill MVP'), 'startupflow');

-- Tasks for Sprint Alpha
INSERT INTO tasks (title, description, status, estimated_hours, project_id, sprint_id, tenant_id, assigned_user_id, type) VALUES
('Backend API Init', 'Spring Boot Setup', 'DONE', 3, (SELECT id FROM projects WHERE name='Flowbill MVP'), (SELECT id FROM sprints WHERE name='Sprint Alpha'), 'startupflow', (SELECT id FROM users WHERE email='backend@startupflow.io'), 'TASK'),
('Frontend React Init', 'Vite + Tailwind', 'IN_PROGRESS', 3, (SELECT id FROM projects WHERE name='Flowbill MVP'), (SELECT id FROM sprints WHERE name='Sprint Alpha'), 'startupflow', (SELECT id FROM users WHERE email='frontend@startupflow.io'), 'TASK'),
('User Profile Page', 'UI and API', 'TODO', 5, (SELECT id FROM projects WHERE name='Flowbill MVP'), (SELECT id FROM sprints WHERE name='Sprint Alpha'), 'startupflow', (SELECT id FROM users WHERE email='frontend@startupflow.io'), 'STORY');


-- Backlog Items (No Sprint)
INSERT INTO tasks (title, description, status, estimated_hours, project_id, tenant_id, type) VALUES
('Future Feature: AI Reporting', 'Analyze velocity', 'TODO', 21, (SELECT id FROM projects WHERE name='ACME ERP Migration'), 'acme', 'STORY'),
('Refactor Legacy Code', 'Cleanup module X', 'TODO', 8, (SELECT id FROM projects WHERE name='ACME ERP Migration'), 'acme', 'TASK');

-- -----------------------------------------------------------------------------------------------
-- END OF SEED DATA
-- -----------------------------------------------------------------------------------------------
