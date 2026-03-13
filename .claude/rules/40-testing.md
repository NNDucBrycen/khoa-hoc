# 40-testing.md — Testing Policy Rules

| Rule | Detail |
|---|---|
| **TST-1** | Every Acceptance Criterion in a spec-pack must have ≥ 1 automated test. AC coverage is tracked in the test-plan. |
| **TST-2** | BE integration tests must use a real PostgreSQL instance (Testcontainers or local). No DB mocking in `@SpringBootTest`. |
| **TST-3** | BE unit tests (`@ExtendWith(MockitoExtension.class)`) may mock Repository and Service dependencies. |
| **TST-4** | BE slice tests (`@WebMvcTest`) may mock the Service layer to test Controller + Security filters in isolation. |
| **TST-5** | BE test method naming: `methodName_stateUnderTest_expectedBehaviour` (e.g., `login_withValidCredentials_returns200`). |
| **TST-6** | FE unit tests use Vitest + React Testing Library. Test behaviour (what user sees), not implementation details. |
| **TST-7** | FE unit tests mock only the API client module (`src/api/`) — not internal hooks or component state. |
| **TST-8** | FE E2E tests use Playwright against a real running stack. No API mocking in E2E. |
| **TST-9** | `cd demo && ./gradlew test` must pass before any BE PR. |
| **TST-10** | `cd my-react-app && npm run lint` must pass before any FE PR. `npm run build` must also succeed. |

**Evidence**:
- Existing BE test pattern: [demo/src/test/java/com/example/demo/DemoApplicationTests.java:1-14](../../demo/src/test/java/com/example/demo/DemoApplicationTests.java#L1)
- Build scripts: [demo/build.gradle:29-31](../../demo/build.gradle#L29) · [my-react-app/package.json:6-11](../../my-react-app/package.json#L6)
- AC list requiring test coverage: [docs/changes/auth/spec-pack.md:405-432](../changes/auth/spec-pack.md#L405)

**Detail**: See [docs/standards/testing.md](../../docs/standards/testing.md)
