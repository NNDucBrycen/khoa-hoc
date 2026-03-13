# Security Standards
_Living doc — updated: 2026-03-13_

---

## 1. Authentication

- **Mechanism**: Server-side session, session id in `JSESSIONID` cookie.
- **Session idle timeout**: dev=8h, staging=2h, prod=30m.
- **Cookie attributes**: `HttpOnly=true`, `SameSite=None`, `Secure=true` (staging/prod), `Path=/`.
- Session store: in-memory only (no Redis for now). Do **not** add Redis without a spec change.

Evidence: [docs/changes/auth/spec-pack.md:139-208](../changes/auth/spec-pack.md#L139)

---

## 2. CSRF Protection

- All mutating requests (POST, PUT, PATCH, DELETE) to protected endpoints must carry `X-CSRF-TOKEN` header.
- CSRF token is obtained via `GET /api/v1/auth/csrf` after login.
- Missing or invalid token → 403 RFC 7807 response; never silently ignore.
- Frontend must store the token in JS memory (not localStorage, not a cookie).

Evidence: [docs/changes/auth/spec-pack.md:177-184](../changes/auth/spec-pack.md#L177)

---

## 3. Password Handling

- Algorithm: **BCrypt**, cost factor **12**.
- Never log passwords or hashes at any log level.
- Never store plain-text passwords anywhere (code, config, DB, logs).

Evidence: [docs/changes/auth/spec-pack.md:266-270](../changes/auth/spec-pack.md#L266)

---

## 4. Input Validation

- Validate all user-supplied input at the Controller boundary (Bean Validation or explicit checks).
- Reject requests with missing required fields before reaching the Service layer.
- Never trust client-supplied role claims — derive roles from the DB/session only.

---

## 5. CORS

- `allowCredentials: true` — required for cross-site cookie flow.
- `allowedOrigins`: explicit whitelist only — never use `*` with credentials.
  - dev: `http://localhost:5173`
  - staging: configurable via environment property
  - prod: to be decided (OI-A')
- `allowedHeaders`: `Content-Type`, `X-CSRF-TOKEN` only.

Evidence: [docs/changes/auth/spec-pack.md:212-220](../changes/auth/spec-pack.md#L212)

---

## 6. Secrets & Configuration

- No credentials, API keys, or DB passwords in checked-in config files.
- Environment-specific secrets are injected via environment variables or external config.
- `.env` and `*.key`/`*.pem` files are in `.gitignore` and blocked by `.claude/settings.json`.

Evidence: [.claude/settings.json](../../.claude/settings.json)

---

## 7. PII & Logging

- Do not log usernames, emails, IP addresses, or any PII at INFO level or above in prod.
- Use opaque identifiers (session id fragments, user id) in structured logs.
- Springdoc (OpenAPI) must be disabled in prod to avoid leaking API details.

Evidence: [docs/changes/auth/spec-pack.md:274-281](../changes/auth/spec-pack.md#L274)

---

## 8. Error Responses

All error responses must use **RFC 7807 Problem Details**:
```json
{
  "type": "https://errors.example.com/<category>/<code>",
  "title": "<human-readable title>",
  "status": <http-status-code>,
  "detail": "<specific message>",
  "instance": "<request-path>"
}
```
Never expose stack traces or internal exception messages to clients.

Evidence: [docs/changes/auth/spec-pack.md:128-136](../changes/auth/spec-pack.md#L128)
