# Analyse Complète du Backend et Base de Données FLOWBILL

Ce document détaille l'analyse technique du backend (Microservices Spring Boot) et de la base de données (PostgreSQL), fichier par fichier, pour le projet FLOWBILL. Il recense toutes les fonctionnalités implémentées et fonctionnelles, ainsi que la structure exacte de la base de données.

## Architecture Globale
Le projet utilise une architecture Microservices orchestrée par un **Gateway**. Chaque service gère son propre domaine métier.

** Services Actifs (Docker Compose):**
1.  **Gateway** (Port 8080)
2.  **Auth Service** (Port 8081)
3.  **Tenant Service** (Port 8082)
4.  **Project Service** (Port 8083)
5.  **Time Service** (Port 8084)
6.  **Billing Service** (Port 8085)

---

## 1. Auth Service (Service d'Authentification)
**Rôle :** Gère les utilisateurs, les rôles, l'authentification (JWT) et la sécurité.

### Fonctionnalités Backend (Endpoints)
*Fichier : `AuthController.java`*
*   `POST /auth/login` : Authentification utilisateur (retourne un JWT).
*   `POST /auth/register` : Inscription d'un nouvel utilisateur.
    *   Champs gérés : email, password, tenantId, role, fullName, telephone, competencies (JSON).
*   `POST /auth/change-password` : Changement de mot de passe.
*   `GET /auth/users/{tenantId}` : Récupération de la liste des utilisateurs d'un tenant.

### Base de Données (Schéma)
*Fichiers Migration : `V1__init_auth.sql`, `V2__add_user_details.sql`*

#### Table `roles`
| Colonne | Type | Description |
| :--- | :--- | :--- |
| `id` | BIGSERIAL (PK) | Identifiant unique |
| `name` | VARCHAR(50) | Nom du rôle (ex: ROLE_USER, ROLE_ADMIN_ENTREPRISE) |

#### Table `users`
| Colonne | Type | Description |
| :--- | :--- | :--- |
| `id` | BIGSERIAL (PK) | Identifiant unique |
| `email` | VARCHAR(255) | Email (Unique) |
| `password` | VARCHAR(255) | Mot de passe hashé |
| `enabled` | BOOLEAN | Compte actif ou non |
| `tenant_id` | VARCHAR(50) | ID du locataire (Tenant) associé |
| `full_name` | VARCHAR(255) | Nom complet |
| `telephone` | VARCHAR(50) | Numéro de téléphone |
| `competencies` | TEXT | Compétences (stockées en JSON) |

#### Table `user_roles` (Table de liaison)
| Colonne | Type | Description |
| :--- | :--- | :--- |
| `user_id` | BIGINT (FK) | Référence `users(id)` |
| `role_id` | BIGINT (FK) | Référence `roles(id)` |

---

## 2. Tenant Service (Service de Gestion des Locataires)
**Rôle :** Gère l'enregistrement des entreprises (Tenants) utilisatrices de la plateforme.

### Fonctionnalités Backend (Endpoints)
*Fichier : `TenantController.java`*
*   `GET /tenants` : Liste tous les tenants.
*   `POST /tenants` : Crée un nouveau tenant.
*   `GET /tenants/{id}` : Récupère les détails d'un tenant.

### Base de Données (Schéma)
*Fichier Migration : `V1__init_tenants.sql`*

#### Table `tenants`
| Colonne | Type | Description |
| :--- | :--- | :--- |
| `id` | VARCHAR(50) (PK) | Identifiant texte unique du tenant |
| `name` | VARCHAR(255) | Nom de l'entreprise |
| `schema_name` | VARCHAR(255) | Nom du schéma DB dédié (si utilisé) |
| `active` | BOOLEAN | Statut du tenant |
| `created_at` | TIMESTAMP | Date de création |

*Note : Ce service contient également des scripts de migration (`db/migration/tenants/`) pour initialiser le schéma des données métier (projets, factures, etc.) spécifique à chaque tenant, bien que ces tables soient principalement gérées par les services respectifs dans l'architecture actuelle.*

---

## 3. Project Service (Service de Gestion de Projets)
**Rôle :** Gère les projets, les sprints et les tâches (Méthodologie agile/Scrumban).

### Fonctionnalités Backend (Endpoints)
*Fichiers : `ProjectController.java`, `SprintController.java`, `TaskController.java`*
*   **Projets** :
    *   `GET /projects` : Liste des projets.
    *   `POST /projects` : Création de projet.
*   **Sprints** :
    *   `POST /projects/{projectId}/sprints` : Création d'un sprint.
    *   `GET /projects/{projectId}/sprints` : Liste des sprints d'un projet.
*   **Tâches** :
    *   `POST /tasks` : Création de tâche.
    *   `GET /tasks` : Liste des tâches (filtre par `projectId` ou `sprintId`).
    *   `GET /tasks/backlog` : Liste des tâches du backlog (non assignées à un sprint?).
    *   `PUT /tasks/{taskId}` : Mise à jour d'une tâche (statut, assignation, etc.).

### Base de Données (Schéma)
*Fichiers Migration : `V1__init_projects.sql`, `V2__add_sprint_table.sql`, `V3...`*

#### Table `projects`
| Colonne | Type | Description |
| :--- | :--- | :--- |
| `id` | BIGSERIAL (PK) | Identifiant unique |
| `name` | VARCHAR(255) | Nom du projet |
| `description` | TEXT | Description |
| `tenant_id` | VARCHAR(50) | Tenant associé |

#### Table `sprints`
| Colonne | Type | Description |
| :--- | :--- | :--- |
| `id` | BIGSERIAL (PK) | Identifiant unique |
| `name` | VARCHAR(255) | Nom du sprint |
| `start_date` | TIMESTAMP | Date de début |
| `end_date` | TIMESTAMP | Date de fin |
| `project_id` | BIGINT (FK) | Projet associé |
| `status` | VARCHAR(50) | 'PLANNED', 'ACTIVE', 'COMPLETED' |
| `goal` | TEXT | Objectif du sprint |
| `tenant_id` | VARCHAR(50) | Tenant associé |

#### Table `tasks`
| Colonne | Type | Description |
| :--- | :--- | :--- |
| `id` | BIGSERIAL (PK) | Identifiant unique |
| `title` | VARCHAR(255) | Titre de la tâche |
| `description` | TEXT | Description détaillée |
| `status` | VARCHAR(50) | 'TODO', 'IN_PROGRESS', etc. |
| `priority` | VARCHAR(20) | 'LOW', 'MEDIUM', 'HIGH', 'URGENT' |
| `estimation` | INT | Points de complexité (Story Points) |
| `estimated_hours`| DOUBLE | Heures estimées |
| `due_date` | TIMESTAMP | Date d'échéance |
| `project_id` | BIGINT (FK) | Projet parent |
| `sprint_id` | BIGINT (FK) | Sprint associé (nullable) |
| `assigned_user_id`| BIGINT | ID utilisateur assigné |
| `created_at` | TIMESTAMP | Date de création |
| `tenant_id` | VARCHAR(50) | Tenant associé |

---

## 4. Time Service (Service de Gestion des Temps)
**Rôle :** Permet aux utilisateurs de saisir leurs heures (Timesheets) sur les tâches.

### Fonctionnalités Backend (Endpoints)
*Fichier : `TimeController.java`*
*   `GET /times` : Récupérer toutes les entrées de temps.
*   `POST /times` : Saisir une nouvelle entrée de temps (Log time).
*   `PUT /times/{id}/approve` : Approuver une saisie de temps (souvent pour facturation).
*   `GET /times/billable` : Récupérer les entrées approuvées prêtes à être facturées.

### Base de Données (Schéma)
*Fichier Migration : `V1__init_time.sql`*

#### Table `time_entries`
| Colonne | Type | Description |
| :--- | :--- | :--- |
| `id` | BIGSERIAL (PK) | Identifiant unique |
| `task_id` | BIGINT | ID de la tâche concernée |
| `user_id` | BIGINT | ID de l'utilisateur qui déclare |
| `hours` | DOUBLE | Nombre d'heures effectuées |
| `date` | TIMESTAMP | Date de la saisie |
| `approved` | BOOLEAN | Si validé par un admin |
| `comments` | TEXT | Commentaire optionnel |
| `tenant_id` | VARCHAR(50) | Tenant associé |

---

## 5. Billing Service (Service de Facturation)
**Rôle :** Génération de factures basées sur les temps saisis.

### Fonctionnalités Backend (Endpoints)
*Fichier : `InvoiceController.java`*
*   `POST /billing/generate` : Générer une facture (Calculmocké actuellement à 1000.00).

### Base de Données (Schéma)
*Fichier Migration : `V1__init_billing.sql`*

#### Table `invoices`
| Colonne | Type | Description |
| :--- | :--- | :--- |
| `id` | BIGSERIAL (PK) | Identifiant unique |
| `invoice_number` | VARCHAR(50) | Numéro de facture unique |
| `amount` | DECIMAL(19,2) | Montant total |
| `status` | VARCHAR(50) | 'DRAFT', etc. |
| `due_date` | DATE | Date d'échéance |
| `generated_at` | TIMESTAMP | Date de génération |
| `details` | TEXT | Détails de la facture |
| `pdf_path` | VARCHAR(255) | Chemin vers le PDF généré |
| `tenant_id` | VARCHAR(50) | Tenant associé |
| `project_id` | BIGINT | Projet associé (nullable) |

---

## Résumé Technique
*   **Langage** : Java (Spring Boot)
*   **Build** : Maven / Docker
*   **DB Migration** : Flyway (fichiers `.sql` dans `src/main/resources/db/migration`)
*   **Communication** : HTTP REST via Gateway.
*   **Sécurité** : JWT Token transmis via Header `Authorization: Bearer ...`.
*   **Multi-tenancy** : Discriminator column `tenant_id` présente dans la quasi-totalité des tables métier (`projects`, `tasks`, `sprints`, `time_entries`, `invoices`, `users`).
