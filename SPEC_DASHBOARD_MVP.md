# Spécifications Fonctionnelles et Techniques : Dashboard Chef de Projet (MVP)

**Version :** 1.0  
**Date :** 29 Décembre 2025  
**Auteur :** Analyste Fonctionnel (Agent AI)

---

## 1. Vue d'Ensemble

Le Dashboard Chef de Projet est le centre de commande pour le rôle `ROLE_ADMIN_ENTREPRISE`. Il offre une vue temps réel sur la santé des projets, des sprints et de l'équipe.
**Objectif UX :** Permettre de détecter les blocages en < 5 secondes et d'agir en < 2 clics.

---

## 2. Architecture des Composants (Blocks)

### BLOC 1 : Indicateurs Globaux (KPI Cards)
**Layout :** Grid 4 colonnes.

#### Données & Règles
| Métrique | Règle de Calcul | Format | Endpoint / Source |
| :--- | :--- | :--- | :--- |
| **Projets Actifs** | `COUNT(projects)` où `status='ACTIVE'` | Chiffre | `global-metrics.activeProjects` |
| **Sprints Actifs** | `COUNT(sprints)` où `status='ACTIVE'` | Chiffre | `global-metrics.activeSprintsCount` |
| **Tâches Actives** | `COUNT(tasks)` où `status IN ('TODO', 'IN_PROGRESS')` | Chiffre ("X Todo, Y In Progress") | `global-metrics.activeTasks` |
| **Stories Backlog** | `COUNT(tasks)` où `type='STORY' AND sprint_id IS NULL` | Chiffre | `global-metrics.backlogStories` |

#### Endpoint API : `GET /api/reports/global-metrics`
```json
{
  "activeProjects": 12,
  "activeSprintsCount": 3,
  "teamSize": 8,
  "activeTasks": { "todo": 45, "inProgress": 23, "total": 68 },
  "backlogStories": 34,
  "tasksCompletedThisWeek": 42
}
```

---

### BLOC 2 : Alertes et Notifications
**Layout :** Liste verticale priorisée (Critique > Attention > Info).

#### Règles de Gestion (Backend Algorithm)
1.  **Sprint en Retard (CRITICAL)** :
    *   *Condition* : `(SP_restants / jours_restants) > (SP_faits / jours_écoulés * 1.5)`
    *   *Action* : Afficher bouton "Gérer Sprint".
2.  **Surcharge Développeur (CRITICAL)** :
    *   *Condition* : `heures_assignées > user.weekly_capacity`
    *   *Action* : "Réaffecter".
3.  **Tâches Bloquées (CRITICAL)** :
    *   *Condition* : `status='BLOCKED' AND updated_at < NOW() - 3 days`

#### Endpoint API : `GET /api/reports/alerts`
```json
{
  "critical": [
    {
      "id": "alert_1",
      "type": "SPRINT_RISK",
      "message": "Sprint Authentification risque de retard",
      "entityId": 42
    }
  ],
  "warnings": []
}
```

---

### BLOC 3 : Projets Actifs (Vue Synthétique)
**Layout :** Liste de cartes (Top 5).

#### Algorithme "Santé Projet"
*   **Green** : `Vélocité Actuelle >= Vélocité Moyenne * 0.9` ET Pas de blocages.
*   **Red** : `Vélocité Actuelle < Vélocité Moyenne * 0.75` OU Blocage critique > 48h.
*   **Orange** : Entre les deux.

#### Endpoint API : `GET /api/reports/active-projects`
```json
[
  {
    "id": 101,
    "name": "Refonte Web",
    "progress": 49,
    "healthStatus": "GREEN",
    "currentSprint": { "name": "Sprint 3", "daysLeft": 4 },
    "teamAvatars": ["url1", "url2"]
  }
]
```

---

### BLOC 4 : Activité Récente (Timeline)
**Layout :** Fil d'actualité scrollable.

#### Modèle de Données (`activity_log`)
Table nécessaire pour historiser les événements.
```sql
CREATE TABLE activity_log (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    event_type VARCHAR(50), -- TASK_DONE, SPRINT_START...
    actor_id BIGINT,
    message TEXT,
    created_at TIMESTAMP
);
```

#### Endpoint API : `GET /api/reports/activity-timeline`
```json
[
  {
    "type": "TASK_COMPLETED",
    "message": "Ahmed a terminé 'Login API'",
    "timeAgo": "2h",
    "actor": "Ahmed"
  }
]
```

---

### BLOC 5 : Vélocité (Analytics)
**Layout :** Graphique en courbes (Chart.js / Recharts).

#### Calculs
*   **Vélocité Planifiée** : Somme des `estimations` des tâches dans le sprint au moment du start.
*   **Vélocité Réalisée** : Somme des `estimations` des tâches `DONE` à la fin du sprint.
*   **Moyenne Mobile** : Moyenne glissante sur 3 sprints.

#### Endpoint API : `GET /api/reports/velocity-chart`
```json
{
  "sprints": ["S1", "S2", "S3"],
  "planned": [30, 32, 30],
  "completed": [28, 30, 25],
  "trend": "STABLE"
}
```

---

### BLOC 6 : Charge Équipe
**Layout :** Liste avec Jauges de charge.

#### Formule de Charge
`Charge % = (Heures assignées (TODO+IN_PROGRESS) / Capacité Hebdo (40h)) * 100`

*   **< 30%** : Vert (Disponible)
*   **> 90%** : Rouge (Surcharge)

#### Endpoint API : `GET /api/reports/team-capacity`
```json
[
  {
    "userId": 5,
    "name": "Ahmed El Amrani",
    "loadPercent": 80,
    "status": "LOADED",
    "assignedTaskCount": 5
  }
]
```

---

### BLOC 7 : Sprints en Cours
**Layout :** Cartes détaillées.

#### Endpoint API : `GET /api/reports/active-sprints`
Donne les détails temps réel pour le pilotage quotidien.
```json
[
  {
    "id": 55,
    "name": "Sprint 4",
    "daysRemaining": 2,
    "progressPercent": 85,
    "risk": "LOW"
  }
]
```

---

## 3. Personnalisation (Table `user_dashboard_preferences`)
Pour sauvegarder l'ordre des blocs et les filtres.
```sql
CREATE TABLE user_dashboard_preferences (
    user_id BIGINT PRIMARY KEY,
    layout_config JSONB -- { "order": [1, 3, 2], "hidden": [5] }
);
```

## 4. Scénarios d'Usage

1.  **Morning Check (9h00)** : Le Chef de Projet ouvre le dashboard. Il regarde le **Bloc 2 (Alertes)**. Il voit qu'un sprint est en retard. Il clique sur l'alerte, arrive sur le détail du sprint, et réassigne une tâche.
2.  **Assignation (14h00)** : Une nouvelle tâche urgente arrive. Il regarde le **Bloc 6 (Équipe)**, repère un développeur en "Vert" (<30% charge), et lui assigne la tâche directement.
3.  **Comité de Pilotage (Vendredi)** : Il utilise le **Bloc 5 (Vélocité)** pour montrer au management que l'équipe est stable et performante.

## 5. Exigences Techniques Non-Fonctionnelles
*   **Performance** : Dashboard doit charger en < 1.5s.
*   **Cache** : Utiliser `@Cacheable` sur les endpoints lourds (Active Projects, Velocity) avec un TTL court (ex: 5min).
*   **Erreurs** : Si un bloc échoue (ex: timeout), afficher un placeholder "Données indisponibles" sans crasher toute la page.
