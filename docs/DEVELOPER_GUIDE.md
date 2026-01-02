# 👨‍💻 FLOWBILL Developer Guide

Welcome to the **Flowbill** developer documentation. This guide is intended for contributors who want to understand the internal architecture, add new features, or debug existing ones.

## 📚 Table of Contents
1.  [System Architecture Deep Dive](#system-architecture-deep-dive)
2.  [Backend Development](#backend-development)
3.  [Frontend Development](#frontend-development)
4.  [Multi-Tenancy Implementation](#multi-tenancy-implementation)
5.  [Security Model](#security-model)

---

## 🏗 System Architecture Deep Dive

Flowbill is a **Multi-Tenant SaaS** built on microservices.
*   **Gateway (Zuul/Spring Cloud Gateway)**: Entry point. Validates JWT. Injects `X-Tenant-ID`.
*   **Auth Service**: Central identity provider. Issues JWTs containing `tenant_id` claim.
*   **Project/Time/Billing Services**: Resource servers. They extract tenant context and isolate data.

### Key Class: `TenantContext`
Located in `common` library or duplicated in services (MVP).
```java
public class TenantContext {
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    // methods to set/get/clear
}
```
*   **Interceptor**: A filter intercepts every HTTP request, reads `X-Tenant-ID`, and sets `TenantContext`.
*   **Hibernate Filter**: All entities should ideally use a `@Filter` or the service layer must explicitly call `repository.findByTenantId(...)`.

---

## ☕ Backend Development

### Standard Layered Architecture
For every new feature (e.g., "Tags"), follow this pattern:

1.  **Entity**: `Tag.java`. Must include `tenantId`.
    ```java
    @Entity
    public class Tag {
        @Id @GeneratedValue...
        private Long id;
        private String name;
        private String tenantId; // Critical for isolation
    }
    ```
2.  **Repository**: `TagRepository.java`.
    *   **Always** extend `JpaRepository`.
    *   **Always** ensure methods filter by tenant (e.g., `findAllByTenantId(String tenantId)`).
3.  **Service**: `TagService.java`.
    *   Inject `TenantContext`.
    *   Business logic validation.
4.  **Controller**: `TagController.java`.
    *   REST Endpoints.
    *   `@PreAuthorize` for RBAC.

### Database Migrations (Flyway)
*   Location: `src/main/resources/db/migration`
*   Naming: `V{Version}__Description.sql` (e.g., `V10__add_tags_table.sql`).
*   **Rule**: Never modify an existing script after it has been run. Create a new version.

---

## ⚛️ Frontend Development

The frontend is a **React + TypeScript** SPA using Vite.

### Project Structure
*   `src/api/`: Axios instances and service methods.
*   `src/components/`: Reusable UI bricks (Cards, Modals, Lists).
*   `src/pages/`: Full page views (`UserDashboard.tsx`, `ProjectDetail.tsx`).
*   `src/types.ts`: Centralized TypeScript interfaces.

### Adding a New Widget (Dashboard)
1.  **Define Type**: Add interface in `types.ts`.
2.  **Create Service**: Add fetch method in `projectService.ts` (or relevant service).
3.  **Create Component**: `src/components/dashboard/MyNewWidget.tsx`.
4.  **Integrate**: Add to `UserDashboard.tsx`.

---

## 🔐 Security Model

### Roles
*   `ROLE_SUPER_ADMIN`: Platform owner. Can create new tenants.
*   `ROLE_ADMIN_ENTREPRISE`: Tenant Manager. Can manage users and projects within their tenant.
*   `ROLE_USER`: Standard employee. Can only view/edit their assigned tasks.

### Annotations
Use Spring Security annotations on Controllers:
```java
@PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
public ResponseEntity<?> createProject(...) { ... }
```

### Ownership Security
For sensitive data (Tasks, Invoices), Role checks are not enough. You must verify ownership:
```java
if (!task.getAssignedUserId().equals(currentUser.getId())) {
    throw new AccessDeniedException("Not your task!");
}
```

---

## 🧪 Testing

*   **Unit Tests**: Use JUnit 5 and Mockito.
*   **Integration Tests**: Use `@SpringBootTest` with Testcontainers (future roadmap).
*   **Manual Testing**: Use the **Seed Data** to login as different personas (`admin@acme.com` vs `dev1@acme.com`).
