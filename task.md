# FLOWBILL MVP Task List

## Phase 1: Specifications & Design
- [x] Audit Technique et Fonctionnel (AUDIT_MVP.md)
- [x] Spécifications Détaillées du Dashboard (SPEC_DASHBOARD_MVP.md)
- [x] Spécifications Gestion de Projet (SPEC_PROJECT_MANAGEMENT.md) <!-- id: 100 -->

## Phase 2: Backend Implementation (Foundation)
- [x] Backend: Update Data Model (User Stories, Priorities, Acceptance Criteria) <!-- id: 0 -->
- [x] Backend: Implement Role-Based Access Control logic <!-- id: 1 -->
- [x] Backend: Create new entities (TaskHistory, TaskDependencies, ActivityLog) <!-- id: 2 -->
- [x] Backend: Implement Sprint Closure Logic <!-- id: 20 -->
- [x] Backend: Implement Task History Logging <!-- id: 21 -->
- [x] Backend: Implement User Dashboard Preferences <!-- id: 22 -->

## Phase 3: Dashboard API Implementation
- [x] API: Implement Global Indicators Endpoint (Block 1) <!-- id: 3 -->
- [x] API: Implement Alerts System Endpoint (Block 2) <!-- id: 4 -->
- [x] API: Implement Active Projects Endpoint (Block 3) <!-- id: 5 -->
- [x] API: Implement Activity Timeline Endpoint (Block 4) <!-- id: 6 -->
- [x] API: Implement Velocity Analytics Endpoint (Block 5) <!-- id: 7 -->
- [x] API: Implement Team Capacity Endpoint (Block 6) <!-- id: 8 -->
- [x] API: Implement Active Sprints Endpoint (Block 7) <!-- id: 9 -->

## Phase 4: Frontend Implementation
- [x] Frontend: Dashboard Layout & Shell <!-- id: 10 -->
- [x] Frontend: Integrate all Dashboard Widgets <!-- id: 11 -->

## Phase 5: Verification
- [x] Verify Tenant Data Isolation on Dashboard <!-- id: 12 -->
- [x] Verify Role Permissions (Admin vs Dev) <!-- id: 13 -->

## Phase 6: Project Management System (Backend)
- [x] Backend: Update Project Entity & Create ProjectTeamMember Entity <!-- id: 30 -->
- [x] Backend: Create DTOs (ProjectDTO, ProjectSummaryDTO, CreateProjectRequest) <!-- id: 31 -->
- [x] Backend: Update ProjectRepository (Search, Filters) <!-- id: 32 -->
- [x] Backend: Update ProjectService (Create logic with Wizard steps) <!-- id: 33 -->
- [x] Backend: Update ProjectController (Endpoints) <!-- id: 34 -->

## Phase 7: Project Management System (Frontend)
- [x] Frontend: Update Types & ProjectService API <!-- id: 40 -->
- [x] Frontend: Implement Common Components (Status, Type, Team) <!-- id: 41 -->
- [x] Frontend: Implement Project List View (Grid/List/Timeline) <!-- id: 42 -->
- [x] Frontend: Implement Project Wizard (Steps 1-5) <!-- id: 43 -->
- [x] Frontend: Implement Project Detail View (Overview Tab) <!-- id: 44 -->

## Phase 8: Runtime Gaps & Platform Stability
- [x] Fix Flyway crash in `reporting-service`
- [x] Fix Nginx upstream resolution in `frontend`
- [x] Fix ambiguous mapping in `DashboardController`
- [x] Fix Gateway DataSource dependency conflict
- [x] Resolving Gateway Classpath Conflict (MVC vs Reactive)
- [x] Fix Nginx rewrite rule for `/api` stripping
- [x] Phase 15: Fixing Security Gaps & Missing Routes (403/404/502)
    - [x] Fix Gateway `JwtAuthenticationFilter` header propagation
    - [x] Add missing Gateway routes for `activity` and `sprints`
    - [x] Align `SprintController` with frontend top-level API
    - [x] Verify fix in browser
- [x] Phase 16: Hardening Security & Context Propagation
    - [x] Standardize `HeaderAuthenticationFilter` for Spring Security 6
    - [x] Fix potential duplicate headers in Gateway
    - [/] Verify dashboard access
- [x] Phase 17: Final Debugging & Exception Handling (403 Cleanup)
    - [x] Add `GlobalExceptionHandler` to services for 403 details
    - [x] Add detailed tracing logs in `HeaderAuthenticationFilter`
    - [x] Robust role extraction in Gateway
    - [x] Restart and verify with browser console details

- [x] Phase 27: Debugging SQL and Endpoint Errors
    - [x] Identify root causes for SQL `lower(bytea)` error
    - [x] Identify root causes for Sprint endpoint 404s
    - [x] Identify root causes for Reporting endpoint 404s
    - [x] Create Implementation Plan
    - [x] Fix SQL Type Mismatch in `TaskRepository`
    - [x] Fix Sprint Mapping in `SprintController`
    - [x] Fix Reporting Mapping in `DashboardController`
    - [x] Fix Active Dashboard endpoint mapping
    - [x] Fix Sprint date format deserialization
    - [x] Restart services and verify fixes
- [x] Phase 18: Security Hardening (P0 Fixes)
    - [x] Update Repositories with `findByIdAndTenantId`
    - [x] Secure `ProjectService` lookups
    - [x] Secure `TaskService` cross-tenant linkage
    - [x] Secure `SprintService` lookups
    - [x] Verify fix by restarting and manual testing

- [x] Phase 19: Debugging 403 Forbidden Errors (Final Resolution)
    - [x] Implement robust role parsing (bracket cleaning) in Gateway
    - [x] Implement robust role parsing in `reporting-service` and `project-service`
    - [x] Standardize security context details (Map format) across microservices
    - [x] Add detailed tracing logs for security context establishment

- [x] Phase 20: Gateway Route Filter Configuration
    - [x] Rename class to `JwtAuthenticationGatewayFilterFactory` (Standard Convention)
    - [x] Relax validation in `CreateStoryRequest` (title/desc min 1)
    - [x] Fix unused variables in `ProjectDetailPage.tsx` for build success
    - [x] Verify fix by observing microservice logs for security context logs

## Phase 21: Developer Interface Improvements
- [x] Frontend: Enhance `UserDashboard` (Developer Dashboard) <!-- id: 50 -->
    - [x] Add Projects list with links
    - [x] Add summary statistics (Tasks, Sprints)
    - [x] Improve UI/UX (Premium feel)
- [x] Frontend: Secure & Adapt `ProjectDetailPage` for Developers <!-- id: 51 -->
    - [x] Hide sensitive tabs (Billing, Settings) based on role
    - [x] Dynamic Back button navigation
    - [x] UX consistency for non-admin users

## Phase 22: Data Seeder & Query Optimization
- [x] Analyze Data Seeder and SQL queries for related data display.
- [x] Optimize `data.sql` to include linked users, projects, and task hierarchies.
- [x] Verify project progress and team display with new seeded data.

## Phase 23: Dynamic Admin Reporting & Multi-tenancy
- [x] Fix hardcoded dashboard filters <!-- id: 60 -->
- [x] Implement tenant-wide and project-specific metrics <!-- id: 61 -->
- [x] Remove MVP hardcoded placeholders ("activeProjects = 1", etc.) <!-- id: 62 -->

## Phase 24: Scaling Seed Data & Dashboard Realism
- [x] Scale `data.sql` with more tenants, projects, and users <!-- id: 70 -->
- [x] Implement dense backlog and task hierarchies per project <!-- id: 71 -->
- [x] Seed `ActivityLog` for realistic dashboard timeline <!-- id: 72 -->
- [x] Verify Overview charts and indicators for high-data volume <!-- id: 73 -->

## Phase 25: Debugging Backlog & Story Creation
- [x] Investigate Backlog story display issues <!-- id: 74 -->
- [x] Fix Story creation validation and persistence <!-- id: 75 -->
- [x] Verify Backlog refresh after creation <!-- id: 76 -->

## Phase 26: Debugging Billing (Invoices & Quotes)
- [x] Investigate Invoice creation failure <!-- id: 77 -->
- [x] Investigate Quote (Devis) creation failure <!-- id: 78 -->
- [x] Verify Billing dashboard updates <!-- id: 79 -->
