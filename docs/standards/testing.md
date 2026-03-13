# Testing Standards
_Living doc — updated: 2026-03-13_

---

## 1. Coverage Policy

Every Acceptance Criterion in a spec-pack must be covered by ≥ 1 automated test.
The test-plan for each ticket documents the AC → test mapping.

---

## 2. Backend (JUnit 5 · Spring Boot Test)

### 2.1 Test types

| Type | Annotation | Scope | DB |
|---|---|---|---|
| Unit | `@ExtendWith(MockitoExtension.class)` | Single class (Service/util) | Mocked |
| Slice | `@WebMvcTest` | Controller + Security filter | Mocked service |
| Integration | `@SpringBootTest` | Full context | Real PostgreSQL (Testcontainers) |

**Never mock the database in integration tests** — use Testcontainers or a local PostgreSQL instance.
Evidence pattern: [demo/src/test/java/com/example/demo/DemoApplicationTests.java:1-14](../../demo/src/test/java/com/example/demo/DemoApplicationTests.java)

### 2.2 Test method naming
```
methodName_stateUnderTest_expectedBehaviour
```
Examples:
- `login_withValidCredentials_returns200WithCookie`
- `login_withWrongPassword_returns401ProblemDetail`
- `csrfEndpoint_withoutSession_returns401`

### 2.3 Run command
```bash
cd demo && ./gradlew test
```

---

## 3. Frontend (Vitest · React Testing Library)

> Vitest is not yet installed — add it before writing FE tests.

### 3.1 Test types

| Type | Tool | Scope |
|---|---|---|
| Unit | Vitest + RTL | Single component or hook (behaviour, not implementation) |
| E2E | Playwright | Full login → dashboard → logout flow |

### 3.2 Principles
- Test **behaviour** (what the user sees/does), not implementation details.
- Do not assert on internal state or private methods.
- Mock only external HTTP calls (use `msw` or Vitest `vi.fn()` for `fetch`).

### 3.3 E2E scenarios (minimum for AUTH-001)
1. Login with valid credentials → dashboard visible.
2. Login with wrong password → error message visible.
3. Protected route redirect → unauthenticated user goes to login.
4. Logout → session cleared, redirected to login.

### 3.4 Run commands
```bash
# Unit tests (once Vitest is added)
cd my-react-app && npm run test

# E2E (once Playwright is added)
cd my-react-app && npx playwright test
```

---

## 4. Mocking Policy

| Scenario | Allowed mock | Rationale |
|---|---|---|
| BE unit test (Service) | Mock Repository | Isolate business logic |
| BE slice test (Controller) | Mock Service | Test HTTP layer only |
| BE integration test | **No DB mock** | Catch real SQL/JPA issues |
| FE unit test (component) | Mock fetch / API module | Isolate UI from network |
| FE E2E | **No API mock** | Catch real integration issues |
