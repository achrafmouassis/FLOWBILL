# Audit Technique et Fonctionnel - FLOWBILL MVP
**Date :** 29 Décembre 2025
**Auteur :** Architecte Tech (Agent AI)

## 1. Executive Summary

Le projet FLOWBILL dispose d'une architecture microservices solide et fonctionnelle pour la gestion de base (Gateway, Auth, Multi-tenancy). Le mécanisme d'isolation des données par schéma PostgreSQL (`SET search_path`) via `TenantRoutingDataSource` est correctement implémenté et sécurisé via la Gateway qui propage le `X-Tenant-ID`.

Cependant, le MVP "Gestion de Projet Agile" est actuellement à **environ 30-40% de complétude**. Si les structures fondamentales (Projets, Sprints, Tâches basiques) sont là, la majorité des fonctionnalités "métier" avancées exigées pour le MVP manquent : distinction User Story/Tâche technique, critères d'acceptation, calculs de métriques (Vélocité, Burndown), et surtout la ségrégation stricte des vues par rôle (Développeur vs Chef de Projet) au niveau des endpoints API.

**Estimation du reste à faire :** Environ 15-20 jours/homme de développement pour atteindre un MVP complet et robuste, principalement concentrés sur l'enrichissement du modèle de données (Service Project) et l'implémentation des règles métier et métriques (Service Reporting & Project).

---

## 2. Tableau de Mapping Fonctionnel

| Fonctionnalité MVP | Statut Actuel | Composants Backend Existants | Composants Manquants | Priorité | Complexité |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **AUTHENTIFICATION & TENANT** | | | | | |
| Login / Register / JWT | ✅ Complet | `AuthService`, `JwtAuthenticationFilter` | - | P0 | N/A |
| Isolation des données (Multi-tenant) | ✅ Complet | `TenantRoutingDataSource`, `Gateway` | - | P0 | N/A |
| Gestion Profil Utilisateur (Compétences) | 🟡 Partiel | Champs ajoutés dans BD (`V2__add_user_details.sql`) | Logique métier de matching | P2 | Faible |
| **GESTION PROJETS & SPRINTS** | | | | | |
| CRUD Projets | ✅ Complet | `ProjectController` | - | P0 | Faible |
| CRUD Sprints | ✅ Complet | `SprintController`, Table `sprints` | Validation des dates / chevauchements | P0 | Faible |
| Machine à état Sprints (Planifié/Actif/Fini) | 🟡 Partiel | Champ `status` existe | Logique de transition et clôture | P1 | Moyenne |
| **GESTION BACKLOG (User Stories)** | | | | | |
| Création User Story | ❌ Manquant | Utilise entité `Task` générique | Entité `UserStory` distincte ou Type sur Task | P0 | Moyenne |
| Critères d'Acceptation | ❌ Manquant | - | Table `acceptance_criteria` | P1 | Faible |
| Estimation / Priorisation (MoSCoW/WSJF) | 🟡 Partiel | Champ `priority` et `estimation` sur Task | Calcul WSJF, validation MoSCoW | P1 | Faible |
| Dépendances entre items | ❌ Manquant | - | Table `task_dependencies` | P2 | Moyenne |
| **GESTION TÂCHES & KANBAN** | | | | | |
| Tableau Kanban (Colonnes) | 🟡 Partiel | Champ `status` (String) | Configuration dynamique des colonnes | P1 | Moyenne |
| Assignation Tâches | ✅ Complet | Champ `assigned_user_id` | - | P0 | Faible |
| Restrictions Vue Développeur | ❌ Manquant | `TaskController` retourne tout | Filtre `WHERE assigned_user_id = current_user` | P0 | Faible |
| Limites WIP | ❌ Manquant | - | Validation backend avant transition | P2 | Faible |
| **DASHBOARD & MÉTRIQUES** | | | | | |
| Dashboard Synthétique | ⚠️ À vérifier | `DashboardController` (basique) | Requêtes erronées (ex: table `invoices`) | P1 | Faible |
| Burndown Chart | ❌ Manquant | - | Historisation des restes à faire (Daily Snapshot) | P1 | Élevée |
| Vélocité | ❌ Manquant | - | Service de calcul sur historique sprints | P1 | Moyenne |
| Lead Time / Cycle Time | ❌ Manquant | created_at uniquement | Historique des changements de statut | P2 | Élevée |

---

## 3. Schéma de Base de Données : Actuel vs Cible

### 3.1 Existant (Audité)
Les tables suivantes sont présentes et fonctionnelles :
*   `tenants` (id, name, schema...)
*   `users` (id, email, password, tenant_id, full_name, competencies...)
*   `roles`, `user_roles`
*   `projects` (id, name, tenant_id...)
*   `sprints` (id, name, start_date, end_date, status, project_id, tenant_id)
*   `tasks` (id, title, status, estimation, priority, project_id, sprint_id, assigned_user_id, tenant_id)

### 3.2 Cible MVP (Ajouts nécessaires)

Il manque plusieurs tables ou relations pour supporter le périmètre Agile complet.

```sql
-- 1. Séparation claire ou typage des User Stories vs Tâches Techniques
-- Option recommandée : Ajouter un type 'TYPE' (STORY, TASK, BUG) dans la table tasks ou créer une table stories.
-- Pour le MVP, ajout de colonnes dans 'tasks' est le plus simple (Single Table Inheritance logique).

ALTER TABLE tasks ADD COLUMN type VARCHAR(20) DEFAULT 'TASK'; -- STORY, TASK, BUG
ALTER TABLE tasks ADD COLUMN parent_story_id BIGINT; -- Pour lier une tâche technique à une story
ALTER TABLE tasks ADD CONSTRAINT fk_tasks_parent FOREIGN KEY (parent_story_id) REFERENCES tasks(id);

-- 2. Critères d'Acceptation (pour les Stories)
CREATE TABLE acceptance_criteria (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL, -- Doit être une Story
    description TEXT NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (task_id) REFERENCES tasks(id)
);

-- 3. Gestion des dépendances (Blocking)
CREATE TABLE task_dependencies (
    blocker_id BIGINT NOT NULL,
    blocked_id BIGINT NOT NULL,
    dependency_type VARCHAR(20), -- FINISH_TO_START, etc.
    PRIMARY KEY (blocker_id, blocked_id),
    FOREIGN KEY (blocker_id) REFERENCES tasks(id),
    FOREIGN KEY (blocked_id) REFERENCES tasks(id)
);

-- 4. Historique pour les Métriques (Cycle Time, Burndown)
CREATE TABLE task_history (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    from_status VARCHAR(50),
    to_status VARCHAR(50),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by_user_id BIGINT,
    FOREIGN KEY (task_id) REFERENCES tasks(id)
);

-- 5. Configuration Kanban (Si colonnes dynamiques)
CREATE TABLE kanban_columns (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    "order" INT NOT NULL,
    wip_limit INT DEFAULT 0,
    FOREIGN KEY (project_id) REFERENCES projects(id)
);
```

---

## 4. Architecture Microservices & Responsabilités

L'architecture actuelle est saine. Recommandation pour le MVP : ne pas créer de nouveau microservice (Analytics), mais enrichir `project-service` et `reporting-service`.

1.  **Project-Service** : Cœur du métier (Écriture).
    *   Doit gérer toute la logique d'état (State Machine) des tâches et sprints.
    *   Doit valider les règles métier (ex : "On ne peut pas fermer une story si critères non validés").
2.  **Reporting-Service** : Lecture et Agrégation.
    *   Actuellement trop simple (`SELECT COUNT`).
    *   Doit porter les **requêtes analytiques lourdes** (Vélocité, Burndown) pour ne pas ralentir le transactionnel.
    *   Doit exposer des endpoints DTO optimisés pour les graphiques frontend.

---

## 5. Matrice de Permissions

| Action API | Chef de Projet (Admin) | Développeur | Implémentation Requise |
| :--- | :---: | :---: | :--- |
| `GET /projects` | ✅ Tous les projets du tenant | ❌ / ⚠️ Projets assignés uniquement | Filtre SQL ou `@PostFilter` |
| `POST /projects` | ✅ Autorisé | ❌ Interdit | `@PreAuthorize("hasRole('ADMIN')")` |
| `POST /sprints` | ✅ Autorisé | ❌ Interdit | `@PreAuthorize` |
| `GET /tasks` | ✅ Tout voir | ✅ Uniquement ses tâches / son équipe | Paramètre optionnel "my_tasks" forcé pour dev |
| `POST /tasks` (Créer Story) | ✅ Autorisé | ❌ Interdit (sauf Tâche tech) | Vérification du `type` dans le DTO |
| `PUT /tasks` (Status) | ✅ Tout modifier | ✅ Uniquement ses tâches | Vérification ownership ou assignee |
| `GET /reports/*` | ✅ Full Dashboard | ⚠️ Dashboard Personnel (ses stats) | Endpoint distinct ou filtré |

**Attention :** Actuellement, le code ne semble pas filtrer les listes de tâches (`getAllTasksMulti`) par utilisateur. Un développeur pourrait voir toutes les tâches du projet, ce qui contredit la règle "Les développeurs ne voient QUE leurs tâches assignées".

---

## 6. Plan de Développement Priorisé

### Phase 1 : Consolidation du Modèle de Données (Semaine 1)
1.  **Migration DB (`project-service`)** : Ajouter tables `task_history`, colonnes `type` (Story/Task), et `parent_id`.
2.  **Entités JPA** : Mettre à jour `Task.java` pour supporter le polymorphisme simple (Flag type) et les sous-tâches.
3.  **Refonte DTOs** : `TaskRequest` doit accepter des critères d'acceptation et des liens parent/enfant.

### Phase 2 : Logique Métier & Sécurité (Semaine 1-2)
1.  **Sécuriser les contrôleurs** : Ajouter les annotations `@PreAuthorize` manquantes.
2.  **Filtrage Développeur** : Modifier `TaskRepository` pour inclure des méthodes `findByAssignedUserId`.
3.  **Logique Sprints** : Implémenter la clôture de sprint (bascule des tâches non finies vers Backlog ou Sprint suivant).

### Phase 3 : Métriques & Dashboard (Semaine 2-3)
1.  **Historisation** : Coder un `EntityListener` ou Service pour peupler `task_history` à chaque changement de statut.
2.  **Service Burndown** : Créer un endpoint qui agrège `task_history` pour générer les points du graphique jour par jour.
3.  **Calcul Vélocité** : Endpoint calculant la moyenne des Story Points des sprints terminés `COMPLETED`.

---

## 7. Risques et Points d'Attention

1.  **Risque de Performance (Reporting)** : Le calcul du Burndown Chart à la volée sur `task_history` peut être lourd.
    *   *Mitigation* : Calculer les points du burndown une fois par nuit (Batch) ou à chaque modif de tâche et stocker le résultat dans une table `sprint_burn_stats`.
2.  **Gros trou fonctionnel (Factures)** : Le `DashboardController` actuel essaie de lire une table `invoices` qui n'existe probablement pas (service Billing hors périmètre). **Cela fera crasher le dashboard.**
    *   *Action immédiate* : Retirer la ligne `invoices` du code du dashboard.
3.  **Sécurité des Données** : La restriction stricte "Développeur ne voit que SES tâches" est difficilement compatible avec un travail d'équipe Agile (Daily meeting, entraide).
    *   *Validation Métier* : Confirmer que c'est vraiment "ses tâches strictes" et pas "les tâches du projet auquel il participe". Si c'est strict, le tableau Kanban sera quasi vide pour un dev.

**Conclusion :** La base est là, mais le code métier "Agile" est à construire. L'architecture supporte très bien l'évolution sans refonte majeure.
