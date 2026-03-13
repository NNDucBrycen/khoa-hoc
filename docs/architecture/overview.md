# Architecture Overview
_Living doc — updated: 2026-03-13 · Status: scaffold-stage (pre-AUTH-001)_

---

## 1. System Components

```
┌─────────────────────────┐       HTTP / JSON        ┌──────────────────────────────┐
│   my-react-app (FE)     │ ──────────────────────► │   demo (BE)                  │
│   React 19 · Vite 7     │ ◄────────────────────── │   Spring Boot 3.5 · Java 25  │
│   TypeScript 5.9        │   session cookie + CSRF  │   Gradle 8                   │
└─────────────────────────┘                          └──────────────┬───────────────┘
                                                                     │ JPA / Flyway
                                                          ┌──────────▼───────────────┐
                                                          │   PostgreSQL              │
                                                          │   tables: users, roles,   │
                                                          │           user_roles      │
                                                          └───────────────────────────┘
```

| Sub-project | Root | Dev port |
|---|---|---|
| Frontend (Vite) | `my-react-app/` | `http://localhost:5173` |
| Backend (Spring Boot) | `demo/` | `http://localhost:8080` |
| Database | external | `localhost:5432` |

---

## 2. Backend Layers (planned for AUTH-001)

```
Controller   (demo/src/main/java/com/example/demo/controller/)
    ↓ calls
Service      (demo/src/main/java/com/example/demo/service/)
    ↓ calls
Repository   (demo/src/main/java/com/example/demo/repository/)  ← Spring Data JPA
    ↓ queries
PostgreSQL   (schema managed by Flyway migrations)
```

**Layer rules**:
- Controllers handle HTTP only — no business logic.
- Services own business logic — no `HttpServletRequest`/`HttpServletResponse`.
- Repositories are interfaces extending `JpaRepository` — no query logic in Service unless unavoidable.
- Cross-layer bypass (Controller → Repository directly) is prohibited.

**Current state**: Only `DemoApplication.java` exists.
Evidence: [demo/src/main/java/com/example/demo/DemoApplication.java](../../demo/src/main/java/com/example/demo/DemoApplication.java)

---

## 3. Frontend Layers (planned for AUTH-001)

```
Page / Route   (src/pages/)          ← top-level route component
    ↓ uses
Component      (src/components/)     ← reusable UI (LoginForm, ProtectedRoute, etc.)
    ↓ calls
Hook           (src/hooks/)          ← useAuth, useCsrf, etc.
    ↓ calls
API client     (src/api/)            ← fetch wrapper with credentials: 'include'
```

**Layer rules**:
- Pages compose components; no direct API calls from Page components.
- Hooks encapsulate all async / state logic — components stay declarative.
- The API client module is the only place that sets `credentials: 'include'` and `X-CSRF-TOKEN` header.

**Current state**: Only scaffold `App.tsx` and `main.tsx`.
Evidence: [my-react-app/src/App.tsx](../../my-react-app/src/App.tsx), [my-react-app/src/main.tsx](../../my-react-app/src/main.tsx)

---

## 4. Auth Data Flow

### 4.1 Login
```
Browser → POST /api/v1/auth/login {username, password}
        ← 200 OK + Set-Cookie: JSESSIONID=... + {authenticated, username, roles}
Browser → GET /api/v1/auth/csrf  (cookie sent automatically)
        ← 200 OK + {csrfToken, headerName}
```
Evidence: [docs/changes/auth/spec-pack.md:109-173](../changes/auth/spec-pack.md)

### 4.2 Mutating Request
```
Browser → POST /api/v1/... + Cookie: JSESSIONID + X-CSRF-TOKEN: <token>
        ← 200 OK  |  401 (no session)  |  403 (bad CSRF)
```
Evidence: [docs/changes/auth/spec-pack.md:177-184](../changes/auth/spec-pack.md)

### 4.3 Logout
```
Browser → POST /api/v1/auth/logout + X-CSRF-TOKEN
        ← 200 OK + Set-Cookie: JSESSIONID=; Max-Age=0
```
Evidence: [docs/changes/auth/spec-pack.md:190-198](../changes/auth/spec-pack.md)

---

## 5. Key External Integrations

| Integration | Purpose | Config location |
|---|---|---|
| PostgreSQL | Primary data store | `application.properties` (per profile) |
| Flyway | Schema migration on startup | `build.gradle` dependency + migration files |
| Springdoc | OpenAPI 3 / Swagger UI | Enabled dev/staging, disabled prod |
| Vite dev proxy | Forward `/api/**` to `:8080` | `vite.config.ts` (to be added) |

---

## 6. Environment Matrix

| Env | Session idle | Secure cookie | Springdoc | Seed data |
|---|---|---|---|---|
| dev | 8h | false | ✅ | ✅ (V3 migration) |
| staging | 2h | true | ✅ | ❌ |
| prod | 30m | true | ❌ | ❌ |

Evidence: [docs/changes/auth/spec-pack.md:202-282](../changes/auth/spec-pack.md)

---

## 7. Open Issues (carry-over from spec-pack)

- **OI-A'**: CORS allowed origins for prod not yet decided.
- **OI-B**: RFC 7807 `type` URI namespace convention (placeholder OK for now).
- **OI-C**: HTTPS local dev setup guide.
