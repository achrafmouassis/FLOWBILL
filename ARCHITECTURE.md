# Architecture Flowbill SaaS

## 🏗 Architecture Globale

Le projet **Flowbill** est une plateforme SaaS multi-tenant basée sur une architecture microservices.

### Principes Clés
1.  **Multi-tenancy avec Isolation par Schéma** : Chaque tenant dispose de son propre schéma PostgreSQL (`tenant_{id}`), garantissant une isolation stricte des données (Users, Projects, Tasks, Invoices).
2.  **Gestion Centralisée des Tenants** : Le `tenant-service` gère le cycle de vie des tenants et orchestre la création des schémas via Flyway.
3.  ** Sécurité des Secrets** : Aucune information sensible n'est stockée dans le code. Tout passe par des variables d'environnement (`.env`) injectées dans les conteneurs.
4.  **Service Discovery MVP** : Les services communiquent via le réseau Docker interne en utilisant les alias DNS (`auth-service`, `project-service`, etc.) et des URLs configurables.

---

## 🔄 Flux de Création d'un Tenant

1.  **Demande** : Un client (ou admin) appelle `POST /tenants` sur le `tenant-service`.
2.  **Enregistrement** : Le service crée une entrée dans la table globale `tenants` (schéma `public`).
3.  **Migration** :
    *   Le `TenantMigrationService` détecte le nouveau tenant.
    *   Il crée le schéma PostgreSQL dédié (ex: `tenant_acme`).
    *   Il déclenche **Flyway** programmatiquement pour exécuter les scripts de `db/migration/tenants/` dans ce nouveau schéma.
4.  **Résultat** : Le tenant est prêt avec toutes ses tables (`users`, `projects`, etc.) initialisées.

---

## 🛠 Gestion des Secrets

La configuration sensible est externalisée.

**Fichiers :**
- `.env` : Contient les valeurs réelles (NON commité).
- `.env.example` : Template pour les développeurs.
- `docker-compose.yml` : Consomme les variables du `.env`.

**Clés principales :**
- `POSTGRES_PASSWORD`
- `JWT_SECRET`
- `SERVICE_*_URL`

---

## 🚀 Stratégie de Migration (Flyway)

Nous utilisons deux niveaux de migration :

1.  **Niveau Global (`public`)**
    *   Géré automatiquement au démarrage de `tenant-service`.
    *   Scripts : `db/migration/default/`
    *   Contenu : Table `tenants` pour le routage.

2.  **Niveau Tenant (`tenant_*`)**
    *   Déclenché par `TenantMigrationService` à la création (ou mise à jour) d'un tenant.
    *   Scripts : `db/migration/tenants/`
    *   Contenu : Tables métier isolées (`users`, `projects`, `tasks`, `invoices`, `time_entries`).

---

## 🌐 Communication Inter-Services

L'architecture évite les URLs hardcodées. Chaque service déclare ses dépendances via `application.yml` :

```yaml
project:
  service:
    url: ${SERVICE_PROJECT_URL} # Injecté par Docker
```

Par défaut dans Docker : `http://project-service:8083`.
