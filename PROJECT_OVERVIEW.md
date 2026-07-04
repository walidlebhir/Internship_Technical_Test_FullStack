# Projet Nemo - Feature Flag Platform

## Project Structure

```
Projet_Nemo_test/
├── Backend/                          # Spring Boot 3.5.16 (Java 17)
│   ├── pom.xml
│   └── src/main/java/com/backend/feature_flag_platform/
│       ├── annotation/Audited.java
│       ├── aspect/AuditAspect.java
│       ├── Config/WebConfig.java          # CORS config
│       ├── Controllers/
│       │   ├── DomainController.java      # /api/v1/domains
│       │   ├── FeaturController.java      # /api/v1/features
│       │   ├── StrategyController.java    # /api/v1/strategies
│       │   ├── EvaluationController.java  # /api/v1/features/{key}/evaluate
│       │   └── AuditController.java       # /api/v1/audit
│       ├── DTO/ (request/response records)
│       ├── Entity/ (Domain, Feature, Strategy, AuditEntry)
│       ├── evaluation_core/ (strategy evaluation engine)
│       ├── exception/ (global handler + custom exceptions)
│       ├── fundamentals/RolloutCalculator.java
│       ├── MappedStructer/ (manual mapping components)
│       ├── Repository/ (Spring Data JPA)
│       └── Service/ (business logic layer)
│
├── FrontEnd/                         # React 19 + Vite 8 + TypeScript 6
│   ├── package.json
│   ├── vite.config.ts                # Port 3000, proxy /api -> localhost:8081
│   └── src/
│       ├── app/
│       │   ├── config/env.ts
│       │   ├── providers/AppProvider.tsx
│       │   ├── query/client.ts
│       │   └── router/
│       │       ├── index.tsx
│       │       └── routes.tsx
│       ├── modules/
│       │   ├── audit/     (AuditListPage)
│       │   ├── dashboard/ (DashboardPage)
│       │   ├── domains/   (DomainListPage, DomainDetailPage)
│       │   ├── evaluation/(EvaluationPage)
│       │   ├── features/  (FeatureListPage, FeatureDetailPage)
│       │   ├── settings/  (SettingsPage)
│       │   └── strategies/(StrategyListPage, StrategyDetailPage)
│       ├── shared/
│       │   ├── components/ui/ (Button, Card)
│       │   ├── constants/     (api.ts, routes.ts)
│       │   ├── hooks/         (useMediaQuery)
│       │   ├── i18n/          (en/fr locales)
│       │   ├── layouts/       (AuthLayout, DashboardLayout)
│       │   ├── lib/           (axios, cn)
│       │   ├── providers/     (I18nProvider, QueryProvider, ThemeProvider)
│       │   ├── services/      (httpClient)
│       │   ├── types/         (api.ts)
│       │   └── utils/         (queryKeys)
│       └── styles/            (global.css, theme.css, variables.css)
│
└── README.md
```

## Available Commands

### Backend (Spring Boot)

| Commande | Description |
|---|---|
| `./mvnw spring-boot:run` | Lancer le backend sur http://localhost:8081 |
| `./mvnw clean install` | Compiler et exécuter les tests |
| `./mvnw test` | Exécuter uniquement les tests |
| `./mvnw clean` | Nettoyer le build |

### Frontend (React)

| Commande | Description |
|---|---|
| `npm run dev` | Démarrer le dev server sur http://localhost:3000 |
| `npm run build` | Compiler pour la production |
| `npm run lint` | Vérifier le code avec ESLint |
| `npm run preview` | Prévisualiser le build |

## Configuration

| Propriété | Valeur |
|---|---|
| Backend Port | 8081 |
| Frontend Port | 3000 |
| Base de données | PostgreSQL (feature_flag_db) |
| CORS | http://localhost:3000 autorisé |
| API Prefix | `/api/v1` |

## API Endpoints

| Méthode | Endpoint | Description |
|---|---|---|
| GET/POST | `/api/v1/domains` | Lister / Créer un domaine |
| GET/PUT/DELETE | `/api/v1/domains/{id}` | Détail / Modifier / Supprimer |
| GET/POST | `/api/v1/features` | Lister / Créer une feature |
| GET/PUT/DELETE | `/api/v1/features/{id}` | Détail / Modifier / Supprimer |
| PATCH | `/api/v1/features/{id}/enable` | Activer une feature |
| PATCH | `/api/v1/features/{id}/disable` | Désactiver une feature |
| GET/POST | `/api/v1/strategies` | Lister / Créer une stratégie |
| GET/PUT/DELETE | `/api/v1/strategies/{id}` | Détail / Modifier / Supprimer |
| PATCH | `/api/v1/strategies/{id}/enable` | Activer une stratégie |
| PATCH | `/api/v1/strategies/{id}/disable` | Désactiver une stratégie |
| GET | `/api/v1/features/{key}/evaluate` | Évaluer une feature flag |
| GET | `/api/v1/audit` | Journal d'audit |

## Notes

- Le frontend utilise Vite proxy pour rediriger `/api` vers `localhost:8081` (pas de CORS en dev)
- Le backend autorise CORS pour `http://localhost:3000` (production ou sans proxy)
- Base H2 disponible si PostgreSQL n'est pas configuré (changer `spring.datasource.url` dans `application.properties`)
- Thème sombre implémenté avec Tailwind CSS 4 + Zustand (themeStore)
- Internationalisation avec i18next (français / anglais)
