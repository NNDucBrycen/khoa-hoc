# 20-architecture.md — Layer, Dependency & Responsibility Rules

## Backend

| Rule | Detail |
|---|---|
| **AR-B1** | Dependency direction: Controller → Service → Repository. No cross-layer bypass (e.g., Controller → Repository directly). |
| **AR-B2** | Controllers handle HTTP only: parse request, call service, return response. No business logic. |
| **AR-B3** | Services own all business logic. No `HttpServletRequest`/`HttpServletResponse` in Service methods. |
| **AR-B4** | Repositories are Spring Data JPA interfaces only. Complex queries via JPQL/native in the interface — not in Service. |
| **AR-B5** | `@Configuration` classes live in `config/` package. Security, CORS, Flyway, Springdoc config each in its own class. |
| **AR-B6** | Exception handling via `@ControllerAdvice` in `exception/` package. Never catch-and-swallow in Service/Controller. |

**Evidence**: [demo/src/main/java/com/example/demo/DemoApplication.java](../../demo/src/main/java/com/example/demo/DemoApplication.java) (scaffold — layer structure to be built per this rule)

---

## Frontend

| Rule | Detail |
|---|---|
| **AR-F1** | Dependency direction: Page → Component → Hook → API client. No API calls from Page or Component directly. |
| **AR-F2** | Hooks (`src/hooks/`) own all async/state logic. Components must stay declarative. |
| **AR-F3** | The API client (`src/api/`) is the only place that sets `credentials: 'include'` and attaches `X-CSRF-TOKEN`. |
| **AR-F4** | Auth state (session, CSRF token) lives in a single React context — not duplicated across components. |
| **AR-F5** | Protected routes are enforced by a `ProtectedRoute` wrapper component — not checked ad-hoc in each page. |

**Evidence**: [my-react-app/src/main.tsx:6-10](../../my-react-app/src/main.tsx#L6) (mount point — Auth context provider wraps here) · [docs/changes/auth/spec-pack.md:83-101](../changes/auth/spec-pack.md#L83) (To-Be diagram)

---

## Cross-cutting

| Rule | Detail |
|---|---|
| **AR-X1** | FE and BE communicate only via the defined REST API — no shared code, no direct DB access from FE. |
| **AR-X2** | Environment-specific behaviour (timeouts, Springdoc toggle, seed data) is controlled by Spring profiles, not if/else in code. |

**Evidence**: [docs/changes/auth/spec-pack.md:202-282](../changes/auth/spec-pack.md#L202)
