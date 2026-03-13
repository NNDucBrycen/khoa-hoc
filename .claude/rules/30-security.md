# 30-security.md — Security Rules

| Rule | Detail |
|---|---|
| **SEC-1** | All mutating endpoints (POST/PUT/PATCH/DELETE) must validate `X-CSRF-TOKEN`. Missing/invalid → 403 RFC 7807. |
| **SEC-2** | Session cookie must be `HttpOnly=true` in all environments. `Secure=true` in staging and prod. |
| **SEC-3** | BCrypt cost factor = **12**. Never change without a spec update. Never store plain-text passwords. |
| **SEC-4** | Never log passwords, password hashes, or CSRF tokens at any log level. |
| **SEC-5** | CORS: `allowCredentials=true` requires an explicit origin whitelist — never `allowedOrigins("*")`. |
| **SEC-6** | All user input must be validated at the Controller boundary before reaching Service. |
| **SEC-7** | Derive user roles from session/DB only — never trust client-supplied role values. |
| **SEC-8** | Springdoc endpoints (`/v3/api-docs`, `/swagger-ui/**`) must be disabled in prod via Spring profile. |
| **SEC-9** | Error responses must use RFC 7807 format. Never expose stack traces or internal messages to clients. |
| **SEC-10** | No credentials, keys, or DB passwords in any checked-in file. Use environment variables or external config. |

**Evidence**:
- Cookie policy: [docs/changes/auth/spec-pack.md:139-147](../changes/auth/spec-pack.md#L139)
- CSRF requirement: [docs/changes/auth/spec-pack.md:177-184](../changes/auth/spec-pack.md#L177)
- BCrypt: [docs/changes/auth/spec-pack.md:266-270](../changes/auth/spec-pack.md#L266)
- CORS: [docs/changes/auth/spec-pack.md:212-220](../changes/auth/spec-pack.md#L212)
- Springdoc toggle: [docs/changes/auth/spec-pack.md:274-281](../changes/auth/spec-pack.md#L274)
- Secrets deny list: [.claude/settings.json](../../.claude/settings.json)
