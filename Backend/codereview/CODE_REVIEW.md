# Code Review — Feature Flag Platform

## Overview

**Project** : `feature-flag-platform`  
**Stack** : Java 17, Spring Boot 3.5.16, PostgreSQL, H2, Maven  
**Architecture** : Clean Architecture / Layered (Controller → Service → Repository → Entity)  
**Key Pattern** : Strategy Pattern (Feature Flag Evaluation)  
**Port** : 8081  

---

## 1. Project Structure

```
src/main/java/com/backend/feature_flag_platform/
├── FeatureFlagPlatformApplication.java    # Entry point + @EnableCaching
├── Controllers/
│   ├── DomainController.java              # CRUD /api/v1/domains
│   ├── FeaturController.java              # CRUD /api/v1/features
│   ├── EvaluationController.java          # GET /api/v1/features/{key}/evaluate
│   └── StrategyController.java            # CRUD /api/v1/strategies
├── DTO/
│   ├── DomainRequest.java / DomainResponse.java
│   ├── FeatureRequest.java / FeatureResponse.java
│   ├── StrategyRequest.java / StrategyResponse.java
│   └── EvaluationResponse.java
├── Entity/
│   ├── Domain.java
│   ├── Feature.java
│   ├── Strategy.java
│   ├── AuditEntry.java
│   └── Enum/
│       ├── StrategyType.java              # PERCENTAGE, ALLOWLIST, ENVIRONMENT, DATE
│       ├── EntityType.java
│       └── AuditAction.java
├── Repository/
│   ├── DomainRepository.java
│   ├── FeatureRepository.java
│   └── StrategyRepository.java
├── Service/
│   ├── DomainService.java
│   ├── FeatureService.java
│   ├── FeatureEvaluationService.java      # Cacheable evaluation orchestrator
│   ├── StrategyService.java
│   └── RolloutService.java                # Percentage bucket hashing
├── evaluation/
│   ├── StrategyEvaluator.java             # Interface — Strategy Pattern contract
│   ├── EvaluationContext.java             # Immutable record (featureKey, userId, env, now)
│   ├── ContextProvider.java               # Interface — replaceable context factory
│   ├── DefaultContextProvider.java        # Default implementation
│   ├── StrategyRegistry.java              # Auto-discovers & indexes evaluators
│   ├── EvaluationEngine.java              # AND-logic loop, no switch on type
│   ├── PercentageStrategyEvaluator.java   # Delegates to RolloutService
│   ├── AllowListStrategyEvaluator.java    # Checks userId in allowlist
│   └── EnvironmentStrategyEvaluator.java  # Checks environment match
├── MappedStructer/
│   ├── DomainMapping.java
│   ├── FeatureMapping.java
│   └── StrategyMapping.java
└── exception/
    ├── ResourceNotFoundException.java
    ├── BadRequestException.java
    ├── DuplicateResourceException.java
    ├── InvalidStrategyConfigException.java
    └── GlobalExceptionHandler.java        # @RestControllerAdvice
```

---

## 2. Detailed Class Analysis

### 2.1 Entity Layer

#### `Domain.java`
- **Annotations** : `@Entity`, `@Table("domain")`, `@Getter/@Setter/@Builder`
- **ID** : `UUID` with `@UuidGenerator` (Hibernate 6)
- **Fields** : `name`, `description`, `CreatedAt`, `UpdatedAt`
- **Relations** : `@OneToMany` → `Feature`
- **Timestamps** : `@CreationTimestamp` / `@UpdateTimestamp` (Hibernate annotations)
- **Review** : Correct JPA mapping. The `CreatedAt`/`UpdatedAt` naming is slightly inconsistent (camelCase vs capitalized), but functional.

#### `Feature.java`
- **Annotations** : `@Entity`, `@Table("feature")`, `@Getter/@Setter/@Builder`
- **ID** : `Long` with `GenerationType.IDENTITY`
- **Fields** : `key` (business identifier), `description`, `enabled` (default `false`)
- **Relations** :
  - `@ManyToOne` → `Domain` (lazy)
  - `@OneToMany` → `Strategy` (cascade ALL, orphan removal)
- **Lifecycle** : `@PrePersist` / `@PreUpdate` for `createdAt`/`updatedAt`
- **Review** : `key` is the natural business identifier used in evaluation lookups. No unique constraint on `key` at the DB level, but `findByKey` returns `Optional` so it's handled in code.

#### `Strategy.java`
- **Annotations** : `@Entity`, `@Table("strategy")`, `@Getter/@Setter/@Builder`
- **ID** : `Long` with `GenerationType.IDENTITY`
- **Fields** : `type` (StrategyType enum, stored as STRING), `config` (JSON text via `@Lob`), `active` (default `true`)
- **Relations** : `@ManyToOne` → `Feature` (lazy)
- **Review** : Clean mapping. The `config` field stores raw JSON which each evaluator parses independently. The `@Lob` annotation is appropriate for potentially large JSON configs.

#### `AuditEntry.java`
- **Purpose** : Scaffolding for future auditing (entity not yet referenced in any service)
- **Fields** : `timestamp`, `action` (AuditAction), `entityType` (EntityType), `entityId`, `who`
- **Review** : Well-structured but unused. Ready for when auditing is required.

#### `StrategyType.java` (Enum)
```java
PERCENTAGE, ALLOWLIST, ENVIRONMENT, DATE
```
- **Review** : `DATE` is declared but has no corresponding `StrategyEvaluator` implementation. The `EvaluationEngine` will log a warning and return `false` if a DATE strategy is encountered. This is safe (fail-closed) but means DATE is currently non-functional.

---

### 2.2 Repository Layer

All three repositories extend `JpaRepository` with `@Repository`:

| Repository | Entity Type | ID Type | Custom Methods |
|---|---|---|---|
| `DomainRepository` | `Domain` | `UUID` | `findByName(String)` |
| `FeatureRepository` | `Feature` | `Long` | `findByKey(String)` → `Optional<Feature>` |
| `StrategyRepository` | `Strategy` | `Long` | `findByFeatureId(Long)`, `findByFeatureIdAndActiveTrue(Long)` |

**Review** : Clean, minimal. The `findByFeatureIdAndActiveTrue` method is the key query used by the evaluation engine to retrieve only active strategies.

---

### 2.3 DTO Layer

All DTOs are Java 16+ `record` types (immutable by design) :

| DTO | Direction | Key Fields |
|---|---|---|
| `DomainRequest` | Input | `name` (@NotBlank, @Size 2-100), `description` (@Size max 500) |
| `DomainResponse` | Output | `id` (UUID), `name`, `description`, `createdAt` |
| `FeatureRequest` | Input | `key`, `description`, `id_domain` (UUID) |
| `FeatureResponse` | Output | `id` (Long), `key`, `description`, `enabled`, `domainId`, `createdAt`, `updatedAt` |
| `StrategyRequest` | Input | `type` (@NotNull), `config` (@NotNull), `id_feature` (@NotNull) |
| `StrategyResponse` | Output | `id`, `type`, `config`, `active`, `featureId` |
| `EvaluationResponse` | Output | `featureKey`, `userId`, `enabled` |

**Review** : Consistent use of Java records. Validation annotations on request DTOs. The `EvaluationResponse` is returned by the evaluation endpoint with a clean structure.

---

### 2.4 Mapper Layer (`MappedStructer/`)

Three manual mapper classes (not MapStruct, despite the package name) :

- `DomainMapping.mapDomainToResponse(Domain)` → `DomainResponse`
- `FeatureMapping.mapFeatureToResponse(Feature)` → `FeatureResponse`
- `StrategyMapping.mapStrategyToResponse(Strategy)` → `StrategyResponse`

**Review** : Simple, no magic. Each mapper exposes only relevant fields (e.g., `feature.getDomain().getId()` instead of the full domain object). The package name `MappedStructer` is a minor typo of "MapStruct".

---

### 2.5 Service Layer

#### `DomainService`
- **Methods** : `createDomain`, `updateDomain`, `deleteDomain`, `getDomainById`, `getAllDomains`
- **Validation** : Duplicate name check on create
- **Review** : Standard CRUD service. Uses constructor injection. Clean.

#### `FeatureService`
- **Methods** : `createFeature`, `getFeatureById`, `getAllFeatures`, `updateFeature`, `deleteFeature`, `enableFeature`, `disableFeature`
- **Cache** : Calls `featureEvaluationService.evictAllEvaluationCaches()` on `updateFeature`, `deleteFeature`, `enableFeature`, `disableFeature`
- **Review** : Circular dependency alert — `FeatureService` depends on `FeatureEvaluationService`, and `FeatureEvaluationService` depends on `FeatureEvaluationService`... wait, no. `FeatureEvaluationService` depends on `FeatureRepository` and `EvaluationEngine`, not `FeatureService`. So there's no circular dependency. The cache eviction calls ensure data consistency.

#### `StrategyService`
- **Methods** : `createStrategy`, `getStrategyById`, `getAllStrategies`, `getStrategiesByFeatureId`, `updateStrategy`, `deleteStrategy`, `enableStrategy`, `disableStrategy`
- **Validation** : `validateConfig(StrategyType, String)` performs type-specific JSON schema validation:
  - **PERCENTAGE** : requires `{"percentage": int}` where 0 ≤ percentage ≤ 100
  - **ALLOWLIST** : requires `{"userIds": ["string",...]}` non-empty
  - **ENVIRONMENT** : requires `{"environments": ["string",...]}` non-empty
  - **DATE** : basic non-empty check only
- **Cache** : Calls `featureEvaluationService.evictAllEvaluationCaches()` on every mutation
- **Review** : Robust config validation protects against malformed data at persistence time. The `switch` on `StrategyType` is acceptable here because it's validation logic, not evaluation logic.

#### `FeatureEvaluationService`
- **The heart of the platform.**
- **Method `evaluate(featureKey, userId, environment)`** :
  1. `@Cacheable` with key `featureKey + "::" + userId + "::" + environment`
  2. Loads `Feature` by key or throws 404
  3. If `enabled == false` → OFF
  4. Creates `EvaluationContext` via `ContextProvider`
  5. Fetches active strategies from DB
  6. If no active strategies → ON
  7. Delegates to `EvaluationEngine`
  8. Returns `EvaluationResponse`
- **Method `evictAllEvaluationCaches()`** : `@CacheEvict(allEntries = true)`, called by `FeatureService` and `StrategyService` on mutations
- **Review** : Clean separation of concerns. Private helper methods (`findFeatureOrThrow`, `isFeatureDisabled`, `buildOffResponse`) improve readability. Caching prevents repeated DB lookups for the same feature/user/environment combination.

#### `RolloutService`
- **Method `isInRollout(userId, featureKey, percentage)`** :
  - Returns `false` if userId is null/blank
  - Returns `false` if percentage ≤ 0
  - Returns `true` if percentage ≥ 100
  - Otherwise hashes `featureKey + "::" + userId` to a bucket 0-99 and compares against percentage
- **Review** : The combined hash `featureKey + "::" + userId` ensures stable, feature-specific bucketing. A user at 25% for feature A may be at 75% for feature B. This is correct behavior for independent feature rollouts.

---

### 2.6 Evaluation Package

#### `StrategyEvaluator` (Interface)
```java
public interface StrategyEvaluator {
    StrategyType supportedType();
    boolean evaluate(String config, EvaluationContext context);
}
```
- **Purpose** : Strategy Pattern contract. Each implementation handles exactly one `StrategyType`.
- **Contract** : `evaluate()` must never throw — log and return `false` on error.

#### `EvaluationContext` (Record)
```java
public record EvaluationContext(
    String featureKey,
    String userId,
    String environment,
    LocalDateTime currentDateTime,
    Map<String, String> attributes
) {}
```
- **Review** : Immutable, extensible via `attributes` map. Contains all runtime information evaluators might need.

#### `ContextProvider` (Interface)
```java
public interface ContextProvider {
    EvaluationContext provide(String featureKey, String userId, String environment, Map<String, String> extraAttributes);
    EvaluationContext provide(String featureKey, String userId, String environment);
}
```
- **Purpose** : Strategy for context creation. Replaceable for different environments (e.g., JWT-based context, multi-tenant, etc.).

#### `DefaultContextProvider` (Implementation)
- Sets `currentDateTime = LocalDateTime.now()`
- Copies extra attributes into an unmodifiable map
- **Review** : Simple, safe, replaceable.

#### `StrategyRegistry`
```java
@Component
public class StrategyRegistry {
    private final Map<StrategyType, StrategyEvaluator> evaluators;

    public StrategyRegistry(List<StrategyEvaluator> evaluatorList) {
        // Converts List<StrategyEvaluator> → EnumMap<StrategyType, StrategyEvaluator>
    }

    public StrategyEvaluator getEvaluator(StrategyType type) { ... }
}
```
- **Purpose** : Auto-discovers all `StrategyEvaluator` beans via Spring constructor injection, indexes them by type in an `EnumMap`.
- **Review** : This is the key to the Open/Closed Principle. Adding a new strategy type requires only a new `@Component` implementing `StrategyEvaluator` — it's automatically picked up.

#### `EvaluationEngine`
```java
@Component
public class EvaluationEngine {
    public boolean evaluate(List<Strategy> activeStrategies, EvaluationContext context) {
        if (activeStrategies.isEmpty()) return true;

        for (Strategy strategy : activeStrategies) {
            StrategyEvaluator evaluator = registry.getEvaluator(strategy.getType());
            if (evaluator == null) {
                log.warn("No evaluator for {} — returning false", strategy.getType());
                return false;
            }
            if (!evaluator.evaluate(strategy.getConfig(), context)) {
                return false;  // Short-circuit AND logic
            }
        }
        return true;
    }
}
```
- **Review** : Zero switches on `StrategyType`. Zero if/else chains on type. Pure delegation to `StrategyRegistry`. The short-circuit on first `false` is efficient for AND logic. This class is closed for modification, open for extension.

#### `PercentageStrategyEvaluator`
- Parses `{"percentage": N}` from JSON
- Delegates to `RolloutService.isInRollout(userId, featureKey, percentage)`
- Catches all exceptions → logs error → returns `false`

#### `AllowListStrategyEvaluator`
- Parses `{"userIds": ["u1", "u2"]}` from JSON
- Returns `true` if `context.userId()` is in the list
- Returns `false` if userId is null/blank
- Catches all exceptions → logs error → returns `false`

#### `EnvironmentStrategyEvaluator`
- Parses `{"environments": ["DEV", "PROD"]}` from JSON
- Returns `true` if `context.environment()` matches a listed value
- Returns `false` if environment is null/blank
- Catches all exceptions → logs error → returns `false`

---

### 2.7 Controller Layer

#### `DomainController`
- **Base path** : `/api/v1/domains`
- **Endpoints** : POST, GET/{id}, GET, PUT/{id}, DELETE/{id}
- **Review** : Clean CRUD controller. No business logic.

#### `FeaturController`
- **Base path** : `/api/v1/features`
- **Endpoints** : POST, GET/{id}, GET, PUT/{id}, DELETE/{id}, PATCH/{id}/enable, PATCH/{id}/disable
- **Review** : Pure CRUD. The evaluate endpoint was extracted to `EvaluationController` for SRP compliance.

#### `EvaluationController`
- **Base path** : `/api/v1/features`
- **Endpoint** : `GET /{key}/evaluate?userId=...&environment=...`
- **Response** : `EvaluationResponse` (JSON)
- **Review** : Dedicated controller for evaluation. Thin layer — delegates entirely to `FeatureEvaluationService`.

#### `StrategyController`
- **Base path** : `/api/v1`
- **Endpoints** :
  - `POST /strategies`
  - `GET /strategies`, `GET /strategies/{id}`
  - `PUT /strategies/{id}`
  - `DELETE /strategies/{id}`
  - `PATCH /strategies/{id}/enable`, `PATCH /strategies/{id}/disable`
  - `GET /features/{featureId}/strategies`
- **Review** : Well-organized. The dual path structure (`/strategies` and `/features/{featureId}/strategies`) provides both global and scoped access.

---

### 2.8 Exception Layer

| Exception | HTTP Status | Trigger |
|---|---|---|
| `ResourceNotFoundException` | 404 | Entity not found |
| `BadRequestException` | 400 | Invalid request |
| `DuplicateResourceException` | 409 | Duplicate name/key |
| `InvalidStrategyConfigException` | 400 | Malformed strategy JSON |

#### `GlobalExceptionHandler`
- `@RestControllerAdvice` handling all custom exceptions + framework exceptions:
  - `MethodArgumentNotValidException` → 400 with field-level error map
  - `HttpMessageNotReadableException` → 400 "Malformed JSON"
  - `MethodArgumentTypeMismatchException` → 400
  - `MissingServletRequestParameterException` → 400
  - `HttpRequestMethodNotSupportedException` → 405
  - `DataIntegrityViolationException` → 409
  - Generic `Exception` → 500
- **Error Response Format** : `{ status, error, message, path, timestamp }`
- **Review** : Comprehensive, consistent error format throughout the API.

---

### 2.9 Configuration

#### `application.properties`
```properties
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5432/feature_flag_db
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

- H2 is also available as a runtime dependency for testing.

#### `FeatureFlagPlatformApplication.java`
- `@SpringBootApplication` + `@EnableCaching`
- Enables Spring's cache abstraction (backed by `ConcurrentMapCacheManager` by default)

---

## 3. Design Patterns & Principles

### 3.1 Strategy Pattern (Evaluation)
```
StrategyEvaluator (interface)
    ├── PercentageStrategyEvaluator
    ├── AllowListStrategyEvaluator
    └── EnvironmentStrategyEvaluator
```
- **Context** : `StrategyRegistry` collects all implementations
- **Client** : `EvaluationEngine` uses registry to find the right evaluator
- **New strategy** : Simply add a new `@Component implements StrategyEvaluator`

### 3.2 Open/Closed Principle
- `EvaluationEngine` is **closed** for modification (no switches on type)
- The evaluation package is **open** for extension (new evaluators auto-register)

### 3.3 Single Responsibility Principle
- Each controller handles one entity/concern
- `EvaluationController` separated from `FeaturController`
- Each evaluator handles exactly one strategy type

### 3.4 Dependency Injection
- Constructor injection throughout
- No field injection (`@Autowired` on fields)
- `StrategyRegistry` receives `List<StrategyEvaluator>` — Spring auto-wires all beans

### 3.5 Immutability
- All DTOs are Java records
- `EvaluationContext` is a record
- `StrategyRegistry.evaluators` is `Collections.unmodifiableMap()`

---

## 4. Cache Strategy

- **Cache name** : `featureEvaluation`
- **Cache key** : `featureKey + "::" + userId + "::" + environment`
- **Annotation** : `@Cacheable` on `FeatureEvaluationService.evaluate()`
- **Eviction** : `@CacheEvict(allEntries = true)` called from `FeatureService` and `StrategyService` on any mutation
- **Current backend** : Spring's default `ConcurrentMapCacheManager` (in-memory, suitable for single-instance)
- **Review** : The cache key is well-chosen — it covers the three evaluation dimensions. The `allEntries = true` eviction is slightly aggressive but safe (could be optimized later with key-specific eviction for large-scale deployments).

---

## 5. Request Flow

```
Client
  │
  │ GET /api/v1/features/payment/evaluate?userId=u123&environment=PROD
  ▼
EvaluationController.evaluateFeature(key="payment", userId="u123", env="PROD")
  │
  ▼
FeatureEvaluationService.evaluate("payment", "u123", "PROD")
  │
  ├── [CACHE CHECK] Cache key = "payment::u123::PROD"
  │   └── If hit → return cached EvaluationResponse
  │
  ├── FeatureRepository.findByKey("payment")
  │   └── Not found → throw 404
  │
  ├── feature.enabled == false → return OFF
  │
  ├── ContextProvider.provide("payment", "u123", "PROD")
  │   └── DefaultContextProvider → EvaluationContext(featureKey, userId, env, now)
  │
  ├── StrategyRepository.findByFeatureIdAndActiveTrue(featureId)
  │   └── Empty list → return ON
  │
  ├── EvaluationEngine.evaluate(activeStrategies, context)
  │   │
  │   ├── StrategyRegistry.getEvaluator(PERCENTAGE)
  │   │   └── PercentageStrategyEvaluator.evaluate(config, context)
  │   │       ├── Parse JSON → {"percentage": 25}
  │   │       ├── RolloutService.isInRollout("u123", "payment", 25)
  │   │       └── hash("payment::u123") % 100 < 25 → true/false
  │   │
  │   ├── StrategyRegistry.getEvaluator(ALLOWLIST)
  │   │   └── AllowListStrategyEvaluator.evaluate(config, context)
  │   │       ├── Parse JSON → {"userIds": ["u1", "u2"]}
  │   │       └── "u123" in ["u1", "u2"] → false
  │   │
  │   │   └── [SHORT CIRCUIT] false → return false
  │   │
  │   └── Result: false (OFF)
  │
  └── EvaluationResponse(featureKey="payment", userId="u123", enabled=false)
      │
      ▼
  Response 200 OK: {"featureKey": "payment", "userId": "u123", "enabled": false}
```

---

## 6. Security & Resilience

### Resilience
- **Malformed JSON** : Every `StrategyEvaluator` wraps parsing in try-catch → logs error → returns `false` (never throws)
- **Missing evaluator** : `EvaluationEngine` returns `false` if no evaluator is registered for a strategy type
- **Null userId/environment** : Each evaluator handles missing values gracefully
- **Cache** : Prevents repeated DB hits under load

### Security Considerations
- No authentication/authorization layer (out of scope for current version)
- No JWT token parsing
- Controller endpoints are unauthenticated
- **Recommendation** : Add Spring Security + JWT validation for production

---

## 7. Recommendations

### High Priority
1. **Unique constraint on Feature.key** : Add `@Column(unique = true)` to avoid duplicate keys at DB level
2. **DATE StrategyEvaluator** : Since `DATE` is declared in `StrategyType`, implement `DateStrategyEvaluator` or remove the enum value

### Medium Priority
3. **Cache eviction granularity** : Switch from `allEntries = true` to key-specific eviction when feature/strategy changes (to avoid flushing unrelated evaluations)
4. **Package name typo**: Rename `MappedStructer` → `Mapper` or `Mapping`
5. **Controller name typo**: Rename `FeaturController` → `FeatureController`

### Low Priority
6. **AuditEntry integration** : Wire up `AuditEntry` to log evaluation requests
7. **Redis/Caffeine cache** : Replace `ConcurrentMapCacheManager` with a distributed cache for multi-instance deployments
8. **API versioning** : Add version prefix (e.g., `/api/v2/...`) for future changes
9. **OpenAPI/Swagger documentation** : Add `@Operation` annotations to controllers for better API docs
10. **Integration tests** : Add tests for the evaluation flow

---

## 8. Conclusion

The platform is well-architected with clear separation of concerns. The evaluation system correctly implements the Strategy Pattern with full adherence to SOLID principles. The code is production-ready for a single-instance deployment with caching.

**Strengths** :
- Clean Strategy Pattern implementation
- Open/Closed Principle respected
- Immutable DTOs and context
- Proper error handling (no silent failures)
- Cache layer for performance

**Areas for improvement** :
- Missing `DateStrategyEvaluator`
- Cache eviction is broad (`allEntries`)
- Minor naming inconsistencies
- No security layer

Overall quality : **8.5/10** — Well-structured, maintainable, and extensible.
