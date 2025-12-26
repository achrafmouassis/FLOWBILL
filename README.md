# Flowbill SaaS Platform

**Flowbill** is a modern B2B multi-tenant SaaS platform designed to manage Projects, Billing, Time Tracking, and Reporting. It is built using a **Microservices Architecture** with **Spring Boot 3**, **Docker**, a **Schema-per-Tenant** database strategy, and a responsive **React/Tailwind** Frontend.

---

## 🏗 Architecture

The platform follows a distributed microservices architecture where domains are separated.
All services share a single **PostgreSQL** instance (`postgres-data`), utilizing logical isolation via **PostgreSQL Schemas** (Multi-tenancy).

### Core Components
| Service | Port | Description | DB Access |
| :--- | :--- | :--- | :--- |
| **Frontend** | `3000` | React + Vite + Tailwind UI. | N/A (Consumes Gateway API) |
| **Gateway** | `8080` | Centralized entry point. Handles routing & JWT validation. | None |
| **Auth Service** | `8081` | User management (Super Admin, Enterprise Admin, Users) & JWT issuance. | `public` schema |
| **Tenant Service** | `8082` | Tenant lifecycle & Schema provisioning. | `public` schema (with CREATE privs) |
| **Project Service** | `8083` | Project & Task management. | `tenant_id` schema |
| **Time Service** | `8084` | Time entries tracking. | `tenant_id` schema |
| **Billing Service** | `8085` | Invoices & subscription billing. | `tenant_id` schema |
| **Reporting Service** | `8086` | Analytics & Reports. | `tenant_id` schema |

### Multi-Tenancy Strategy
*   **Schema-per-Tenant**: Data is isolated in separate schemas (e.g., `acme`, `corp_b`).
*   **Routing**: The `Gateway` extracts `tenantId` from the JWT `tenantId` claim.
*   **Propagation**: The `X-Tenant-ID` header is passed to downstream services.
*   **Resolution**: Services use a custom `TenantDataSource` to execute `SET search_path TO {tenant_id}` for every request.
*   **Migrations**: **Flyway** is used for database migrations. `baseline-on-migrate` is enabled to handle existing schemas.

---

## 🛠 Tech Stack

### Backend
*   **Language**: Java 17
*   **Framework**: Spring Boot 3.2.1
*   **Build Tool**: Maven (Multi-module)
*   **Database**: PostgreSQL 15 (Dockerized)
*   **Migrations**: Flyway
*   **Security**: Spring Security + JWT (Stateless)

### Frontend
*   **Framework**: React 18
*   **Build Tool**: Vite
*   **Styling**: Tailwind CSS
*   **Routing**: React Router DOM (with Protected Routes)
*   **State/API**: Axios, JWT Decode

### DevOps
*   **Containerization**: Docker & Docker Compose
*   **Automation**: `start_platform.bat` script for one-click startup.

---

## 🚀 Getting Started

### Prerequisites
*   [Docker Desktop](https://www.docker.com/products/docker-desktop/) (must be running and accessible via CLI)
*   Windows OS (for `start_platform.bat`)

### Installation & Launch

1.  **Clone the repository**.
2.  **Start the Platform**:
    Double-click or run `start_platform.bat` script.
    
    This script will automatically:
    *   Detect your Java installation.
    *   Build all Backend Services (Maven).
    *   Build and Dockerize the Frontend.
    *   Start the entire stack using Docker Compose.
    *   Clean up old conflicting volumes (`docker-compose down -v` logic included).

3.  **Access the Application**:
    *   **Frontend**: `http://localhost:3000`
    *   **Microservices Gateway**: `http://localhost:8080/api`

---

## 🔐 Authentication & Roles

The system implements a hierarchical Role-Based Access Control (RBAC) system:

### 1. Super Admin (`ROLE_SUPER_ADMIN`)
*   **Description**: Platform owner.
*   **Default Credentials**: `admin@flowbill.com` / `password`
*   **Capabilities**:
    *   Access **Super Admin Dashboard** (`http://localhost:3000/admin`).
    *   **Create Enterprises (Tenants)**: When creating an enterprise (e.g., "Beta Corp" with schema `tenant_beta`), the system **automatically creates a Tenant Admin (Chef de Projet)** for that enterprise.
    *   View list of all registered enterprises.

### 2. Tenant Admin / Chef de Projet (`ROLE_ADMIN_ENTREPRISE`)
*   **Description**: Administrator for a specific Enterprise/Tenant.
*   **Creation**: Automatically created by Super Admin.
*   **Capabilities**:
    *   Access **Enterprise Dashboard** (`http://localhost:3000/tenant-admin`).
    *   **User Management**: Create standard Users (Employees) for their specific enterprise.
    *   **Password Management**: Auto-generates passwords for new users and displays them once. Can change their own password.

### 3. User (`ROLE_USER`)
*   **Description**: Standard employee of a tenant.
*   **Creation**: Created by their Tenant Admin.
*   **Capabilities**:
    *   Access **User Dashboard** (`http://localhost:3000/user`).
    *   Log in using the credentials provided by their admin.
    *   **Change Password**: Can change their generated password after login.
    *   (Coming Soon): Manage tasks and view projects within their tenant scope.

---

## 📄 Verified Features

### Recent Enhancements
*   **Full End-to-End Auth Flow**: From Super Admin -> Tenant Admin -> User.
*   **Frontend Security**: Routes are protected. `ProtectedRoute` component checks both authentication (Token existence) and Authorization (Role verification).
*   **UI/UX**:
    *   Modern Tailwind-based UI.
    *   "Tenant" terminology replaced with "Entreprise" in UI.
    *   User-friendly forms for creating tenants and users.
    *   Password change modals.
*   **Backend Stability**:
    *   Resolved `UnknownHostException` in Gateway.
    *   Fixed Flyway checksum mismatches and migration conflicts.
    *   Configured `tenant-service` to use `db/migration/default` to prevent conflicts with tenant-specific schemas.

---

## 📂 Project Structure

```
FLOWBILL/
├── common/                 # Shared logic (Security, Multitenancy, Utils)
├── gateway/                # Spring Cloud Gateway (Routes /api -> Services)
├── auth-service/           # User & Role Mgmt, JWT, Password Logic
├── tenant-service/         # Enterprise/Tenant Lifecycle
├── project-service/        # Projects Domain
├── frontend/               # React + Tailwind Web App
├── docker-compose.yml      # Full stack orchestration
├── start_platform.bat      # Automation script
└── ...                     # Other microservices
```

---

## 🔮 Roadmap

*   **Project Module Integration**: Connect User Dashboard to Project Service to list assigned tasks.
*   **Invoicing**: Enable billing viewing for Tenant Admins.
*   **Secrets Management**: Externalize sensitive prod secrets (currently dev defaults).

---
*Generated by Antigravity Agent*
