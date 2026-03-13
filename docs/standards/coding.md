# Coding Standards
_Living doc — updated: 2026-03-13_

---

## 1. Backend (Java · Spring Boot)

### 1.1 Package structure
```
com.example.demo.
  controller/    ← REST controllers (@RestController)
  service/       ← Business logic (@Service)
  repository/    ← Spring Data JPA interfaces (@Repository)
  entity/        ← JPA entity classes (@Entity)
  dto/           ← Request/response DTOs (records preferred)
  config/        ← @Configuration classes (Security, CORS, Flyway, etc.)
  exception/     ← Custom exceptions + @ControllerAdvice
```

### 1.2 Naming
| Element | Convention | Example |
|---|---|---|
| Class | PascalCase | `AuthController`, `UserService` |
| Method | camelCase | `findByUsername`, `buildErrorResponse` |
| Constant | UPPER_SNAKE_CASE | `MAX_SESSION_SECONDS` |
| DTO record | PascalCase + suffix | `LoginRequest`, `LoginResponse` |
| Exception | PascalCase + `Exception` | `SessionExpiredException` |

### 1.3 Gradle dependencies
Use Spring Boot managed versions (no explicit version) unless unavoidable:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'  // no version
```
Evidence: [demo/build.gradle:22-26](../../demo/build.gradle#L22)

### 1.4 Error responses
All HTTP error responses must use RFC 7807 Problem Details format:
```json
{ "type": "...", "title": "...", "status": 4xx, "detail": "...", "instance": "/api/..." }
```
Evidence: [docs/changes/auth/spec-pack.md:128-136](../changes/auth/spec-pack.md#L128)

---

## 2. Frontend (TypeScript · React · Vite)

### 2.1 File naming
| Element | Convention | Example |
|---|---|---|
| Component file | PascalCase `.tsx` | `LoginForm.tsx` |
| Hook file | camelCase `.ts` | `useAuth.ts` |
| API client file | camelCase `.ts` | `authApi.ts` |
| Style file | camelCase `.css` | `loginForm.css` |

### 2.2 TypeScript rules (enforced by tsconfig.app.json)
- `strict: true` — no implicit any, no loose null checks.
- `noUnusedLocals: true` — remove dead variables before committing.
- `noUnusedParameters: true` — prefix unused param with `_` if unavoidable.
- `noFallthroughCasesInSwitch: true` — every case must break/return.

Evidence: [my-react-app/tsconfig.app.json:20-25](../../my-react-app/tsconfig.app.json#L20)

### 2.3 ESLint (must pass before build)
```bash
cd my-react-app && npm run lint
```
Active rule sets: ESLint recommended, TypeScript ESLint recommended, react-hooks, react-refresh.
Evidence: [my-react-app/eslint.config.js:11-16](../../my-react-app/eslint.config.js#L11)

### 2.4 Exports
- Prefer **named exports** for components and hooks.
- Default export only for top-level page/route components.

### 2.5 HTTP calls
- All fetch calls must include `credentials: 'include'`.
- Attach `X-CSRF-TOKEN` header on every mutating request (POST/PUT/PATCH/DELETE).
- Centralise in `src/api/` — never call `fetch` directly from a component.
