# Flowbill SaaS Platform

**Flowbill** is a comprehensive, enterprise-grade B2B Multi-Tenant SaaS platform designed for managing Projects, Time Tracking, and Billing. Built with a robust **Microservices Architecture**, it leverages **Spring Boot 3**, **Docker**, and a **Schema-per-Tenant** database strategy to ensure data isolation and scalability. The frontend is a modern Single Page Application (SPA) built with **React**, **TypeScript**, and **Tailwind CSS**.

---

## 🏗 Architecture Overview

The system follows a strict distributed systems pattern where domain responsibilities are decoupled into individual services.

### Global System Design
```mermaid
graph TD
    User((User)) -->|HTTPS| Nginx[Frontend (React)]
    Nginx -->|API Calls (start with /api)| Gateway[API Gateway (Spring Cloud)]
    
    subgraph "Backend Services (Private Network)"
        Gateway -->|Auth & JWT| AuthService[Auth Service]
        Gateway -->|Tenant Mgmt| TenantService[Tenant Service]
        Gateway -->|Projects| ProjectService[Project Service]
        Gateway -->|Time Tracking| TimeService[Time Service]
        Gateway -->|Billing| BillingService[Billing Service]
    end

    subgraph "Data Layer"
        AuthService -->|Schema: public| DB[(PostgreSQL)]
        TenantService -->|Schema: public| DB
        ProjectService -->|Schema: tenant_id| DB
        TimeService -->|Schema: tenant_id| DB
        BillingService -->|Schema: tenant_id| DB
    end
```

### Key Architectural Concepts
1.  **Microservices**: 5+ distinct services, each independently deployable.
2.  **API Gateway**: A single entry point (Port `8080`) that handles Routing, Load Balancing, and central JWT Validation.
3.  **Multi-Tenancy (Schema-per-Tenant)**:
    *   **Isolation**: Every tenant (Enterprise) gets their own PostgreSQL schema (e.g., `tenant_acme`, `tenant_beta`).
    *   **Dynamic Routing**: The backend inspects the `X-Tenant-ID` header (injected by Gateway from JWT) and executes `SET search_path TO tenant_xxx` before every query.
4.  **Security**:
    *   **Stateless**: Uses JWT (JSON Web Tokens) for authentication.
    *   **RBAC**: Role-Based Access Control (`ROLE_SUPER_ADMIN`, `ROLE_ADMIN_ENTREPRISE`, `ROLE_USER`).

---

## 📦 Modules & Project Structure

The repository is organized as a **Mono-repo** containing all services and the frontend.

| Directory | Type | Description |
| :--- | :--- | :--- |
| `common/` | **Library** | Shared code (Security Configs, Tenant Context, Exception Handling) used by all services. |
| `gateway/` | **Service** | Spring Cloud Gateway. Filters requests, validates JWTs, extracts `tenantId`. |
| `auth-service/` | **Service** | Manages Users, Roles, JWT Generation, and Auth API (`/auth/login`). |
| `tenant-service/` | **Service** | Manages Tenant lifecycle (Create Enterprise) and provisions new DB schemas. |
| `project-service/` | **Service** | Manages Projects and Tasks. Multi-tenant aware. |
| `time-service/` | **Service** | Manages Time Entries and Work logs. Multi-tenant aware. |
| `billing-service/` | **Service** | Manages Invoices and Quotes. Multi-tenant aware. |
| `frontend/` | **App** | React + TypeScript SPA. |
| `docker-compose.yml` | **Config** | Orchestration for all services + PostgreSQL database. |
| `start_platform.bat` | **Script** | One-click automation script for building and running the platform. |

---

## 🛠 Technology Stack

### Backend
*   **Framework**: Spring Boot 3.2.1
*   **Language**: Java 17
*   **Build System**: Maven (Multi-module structure)
*   **Database**: PostgreSQL 15
*   **Migration Tool**: Flyway (Automatic schema versioning & migration)
*   **Security**: Spring Security 6 + JJWT (Java JWT)
*   **Inter-service**: REST Template / Feign Client pattern (via Gateway)

### Frontend
*   **Library**: React 18
*   **Compiler**: Vite (Fast HMR)
*   **Language**: TypeScript
*   **Styling**: Tailwind CSS (Utility-first)
*   **Routing**: React Router DOM 6
*   **HTTP**: Axios (with Interceptors for JWT injection)

---

## 🚀 Installation & Execution

### Prerequisites
*   **Docker Desktop** (Installed and Running) - Essential for DB and Services.
*   **Java 17 JDK** (Verified with `java -version`).
*   **Ports Available**: 3000 (UI), 8080 (Gateway), 5432 (Postgres), 8081-8085 (Services).

### Step-by-Step Guide

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/your-username/FLOWBILL.git
    cd FLOWBILL
    ```

2.  **Start the Platform**
    We provide a robust automation script for Windows.
    *   **Run**: Double-click **`start_platform.bat`** (or run in terminal).
    *   **What it does**:
        *   Stops any running containers & removes old volumes (Clean Slate).
        *   Builds all Maven modules (`mvn clean package`).
        *   Builds the Frontend Docker image.
        *   Starts the stack with `docker-compose up -d`.
        *   Streams logs for immediate feedback.

    > **Note**: The first run may take 5-10 minutes to download dependencies and Docker images.

3.  **Access the Application**
    *   **Web Interface**: [http://localhost:3000](http://localhost:3000)
    *   **API Gateway**: [http://localhost:8080](http://localhost:8080)

---

## 🔐 Security & Roles

The application is pre-seeded with default accounts for immediate testing.

### 1. Super Admin (Platform Owner)
*   **URL**: `/admin`
*   **Email**: `admin@flowbill.com`
*   **Password**: `password`
*   **Capabilities**: Create "Enterprises" (Tenants), View Platform Stats.

### 2. Tenant Admin (Enterprise Manager)
*   **URL**: `/tenant-admin`
*   **Email**: `admin@acme.com` (Default for 'Acme Corp')
*   **Password**: `password`
*   **Capabilities**: Create Users (Employees), Manage Projects, View Billing.

### 3. User (Employee)
*   **URL**: `/user`
*   **Credentials**: Created by Tenant Admin.
*   **Capabilities**: Log time, View assigned projects.

---

## 🔌 API Reference (Endpoints)

All API requests should be directed to the **Gateway** (`http://localhost:8080`).

### Authentication (`/api/auth`)
| Method | Endpoint | Description | Public? |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` | Returns JWT Token. | ✅ Yes |
| `POST` | `/auth/register` | Register new user. | ✅ Yes |

### Tenant Management (`/api/tenants`)
| Method | Endpoint | Description | Role |
| :--- | :--- | :--- | :--- |
| `POST` | `/tenants` | Create a new Enterprise (provisions schema). | `SUPER_ADMIN` |
| `GET` | `/tenants` | List all registered enterprises. | `SUPER_ADMIN` |

### Projects (`/api/projects`)
*Context-Aware: Data returned depends on the logged-in user's Tenant.*
| Method | Endpoint | Description | Role |
| :--- | :--- | :--- | :--- |
| `GET` | `/projects` | List projects for MY company. | `ADMIN`, `USER` |
| `POST` | `/projects` | Create a new project. | `ADMIN` |

---

## ⚙️ Configuration & Environment

Configuration is centralized in `application.yml` files but can be overridden by Environment Variables (defined in `docker-compose.yml`).

### Key Variables
*   **`POSTGRES_HOST`**: Database hostname (`postgres` in Docker network).
*   **`JWT_SECRET`**: Secret key for signing tokens (`auth-service` and `gateway` must match).
*   **`SPRING_PROFILES_ACTIVE`**: `docker` (default for containers).

### Database Schemas
*   **`public`**: Shared cross-tenant data (Users, Roles, Tenants table).
*   **`tenant_{id}`**: Isolated tenant data (Tables: `projects`, `time_entries`, `invoices`).

---

## 💡 Developer Notes

### Adding a New Service
1.  Create a new Spring Boot module.
2.  Add dependencies: `spring-boot-starter-web`, `common` (local module).
3.  Configure `application.yml` to use `TenantRoutingDataSource`.
4.  Add Flyway scripts in `db/migration` (V1__init.sql).
5.  Register in `docker-compose.yml` and `gateway` routes.

### Troubleshooting
*   **"Missing Table" Error**: The system uses `baseline-on-migrate: true` and `baseline-version: 0`. If you see this, run `start_platform.bat` again to force migration execution.
*   **Login Redirect Loop**: Ensure your browser cache is cleared or use Incognito mode if roles were recently changed.

---

*Verified and Documented by Antigravity Agent*
