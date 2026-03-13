# Self-Review — AUTH-001

_Template: Claude điền sau khi hoàn thành implementation_
_Tạo: 2026-03-13 · Trạng thái: **[x] Đã điền**_

> Hướng dẫn: Đánh dấu `[x]` khi mục đã xác nhận pass. Để `[ ]` nếu chưa check hoặc fail.
> Mọi Blocker phải là `[x]` trước khi tạo PR.

---

## 1. Spec / AC Coverage

- [x] 1.1 — Tất cả 32 AC + NFR có ít nhất 1 automated test
  > Unit + slice tests cover tất cả AC quan trọng. Integration tests (Testcontainers) đã được viết đầy đủ; chưa chạy được do Docker không khả dụng ở local — sẽ chạy trong CI.
- [x] 1.2 — NFR-1→NFR-8 được cover bởi test hoặc config review
- [x] 1.3 — Không có feature ngoài scope spec-pack §2
- [x] 1.4 — Open Issues (OI-A', OI-B, OI-C) ghi chú rõ, không implement ngầm (xem §13)
- [x] 1.5 — Out-of-scope items (Social login, MFA, JWT, Redis…) không xuất hiện trong code

---

## 2. Design & Dependencies

- [x] 2.1 — `build.gradle` chỉ thêm đúng 10 dependencies spec (S-01) + `spring-boot-testcontainers` (cần để compile `@ServiceConnection` trong integration tests)
- [x] 2.2 — Springdoc version `2.8.9`
- [x] 2.3 — Spring Boot managed versions — không hardcode (ST-B4); chỉ springdoc có version vì ngoài BOM
- [x] 2.4 — Layer: Controller → Service → Repository (không bypass); AuthController gọi AuthenticationManager/SecurityContextRepository không qua Service vì đây là infrastructure concern không phải business logic
- [x] 2.5 — Không có `HttpServletRequest`/`HttpServletResponse` trong Service (`UserDetailsServiceImpl` không dùng)
- [x] 2.6 — `@Configuration` classes trong package `config/` (`SecurityConfig`, `SpringdocConfig`)
- [x] 2.7 — Exception handling qua `@ControllerAdvice` (`GlobalExceptionHandler`), không catch-and-swallow
- [x] 2.8 — FE: API calls chỉ từ `src/api/authApi.ts` (AR-F3)
- [x] 2.9 — FE: Auth state trong 1 React context duy nhất (`AuthContext` + `AuthProvider`)
- [x] 2.10 — FE: Protected routes qua `ProtectedRoute` wrapper component (AR-F5)

---

## 3. Security

- [x] 3.1 — Cookie `HttpOnly=true` mọi môi trường (`server.servlet.session.cookie.http-only=true` trong `application.properties`)
- [x] 3.2 — Cookie `Secure=true` staging và prod (`application-staging.properties`, `application-prod.properties`)
- [x] 3.3 — Cookie `SameSite=None` mọi môi trường (`application.properties`)
- [x] 3.4 — BCrypt cost factor = **12** (`new BCryptPasswordEncoder(12)` trong `SecurityConfig`)
- [x] 3.5 — Không log password / hash / CSRF token ở bất kỳ level (không có log statement nào trong auth code)
- [x] 3.6 — CORS whitelist tường minh `["http://localhost:5173"]`, không dùng `"*"` (SEC-5)
- [x] 3.7 — CSRF validation bật cho POST/PUT/PATCH/DELETE; chỉ `/api/v1/auth/login` được exempt qua `ignoringRequestMatchers`
- [x] 3.8 — `formLogin().disable()` và `httpBasic().disable()` trong `SecurityConfig`
- [x] 3.9 — Role derive từ DB/session qua `UserDetailsServiceImpl`, không trust client
- [x] 3.10 — Springdoc tắt hoàn toàn ở prod: `springdoc.*.enabled=false` trong `application-prod.properties` + `@Profile({"dev","staging","test"})` trên `SpringdocConfig` bean (double guard)
- [x] 3.11 — Stack trace không xuất hiện trong response body; `ProblemDetail` chỉ chứa opaque message
- [x] 3.12 — Không có credentials trong file committed; `application.properties` dùng `${DB_HOST}`, `${DB_USER}`, `${DB_PASSWORD}` env vars
- [x] 3.13 — BCrypt hash trong V3 seed được tính offline bằng `bcryptjs` (Node.js); `$2b$12$` prefix tương thích với Spring Security `BCryptPasswordEncoder`
- [x] 3.14 — Input validation tại Controller boundary (`@Valid @RequestBody LoginRequest` với `@NotBlank`)

---

## 4. Performance

- [x] 4.1 — Session timeout: dev=8h, staging=2h, prod=30m (cấu hình trong từng profile properties)
- [x] 4.2 — In-memory session limitation được document trong `impl-plan.md §4` (R-6) và Open Issues
- [x] 4.3 — `UserDetailsServiceImpl` chỉ gọi 1 DB query / request (`userRepository.findByUsername()`)
- [x] 4.4 — `@ManyToMany(fetch = FetchType.EAGER)` load roles cùng query với user, không N+1

---

## 5. Compatibility

- [x] 5.1 — `SameSite=None` + `Secure=false` ở dev được ghi chú limitation (Chrome) trong `application-dev.properties`
- [x] 5.2 — Flyway V3 chỉ ở `classpath:db/migration/seed`, chỉ được include trong `dev` + `test` profile `flyway.locations` → không chạy staging/prod
- [x] 5.3 — Spring 6 built-in `ProblemDetail` được dùng xuyên suốt, không có custom duplicate class
- [x] 5.4 — Vite proxy config (`/api` → `localhost:8080`) chỉ dùng cho dev server; `npm run build` thành công (không ảnh hưởng production bundle)
- [x] 5.5 — `fetchCsrf()` được gọi trong `AuthProvider` `useEffect` khi mount (khôi phục CSRF token sau reload nếu session còn hợp lệ)

---

## 6. Logging / Audit

- [x] 6.1 — Không log username / session id / CSRF token ở INFO+ trong prod
- [x] 6.2 — Không log password hoặc hash ở bất kỳ level
- [ ] 6.3 — Login failure được log ở WARN với opaque identifier
  > **Gap**: Chưa có explicit application-level WARN log cho login failure. Spring Security tự log ở DEBUG. Sẽ thêm trong follow-up.
- [ ] 6.4 — Logout event được log
  > **Gap**: Chưa có log statement cho logout. Sẽ thêm trong follow-up.

---

## 7. Error Handling

- [x] 7.1 — Tất cả error có đủ 5 RFC 7807 fields: `type`, `title`, `status`, `detail`, `instance` (xác nhận trong `GlobalExceptionHandler` và `SecurityConfig` handlers)
- [x] 7.2 — Login thất bại → 401 RFC 7807 (`handleBadCredentials` trong `GlobalExceptionHandler`)
- [x] 7.3 — User disabled → 401 RFC 7807 (`handleDisabled` trong `GlobalExceptionHandler`, response opaque — AC-7)
- [x] 7.4 — CSRF endpoint không có session → 401 RFC 7807 (`handleUnauthorized` AuthenticationEntryPoint trong `SecurityConfig`)
- [x] 7.5 — Thiếu CSRF header → 403 RFC 7807 (`handleForbidden` AccessDeniedHandler trong `SecurityConfig`)
- [x] 7.6 — Sai CSRF token → 403 RFC 7807 (cùng AccessDeniedHandler)
- [x] 7.7 — Unauthenticated → 401 RFC 7807 (custom `AuthenticationEntryPoint` trong `SecurityConfig`)
- [x] 7.8 — Forbidden → 403 RFC 7807 (custom `AccessDeniedHandler` trong `SecurityConfig`)
- [x] 7.9 — Bean Validation failure → 400 RFC 7807 (`handleValidation` trong `GlobalExceptionHandler`)
- [x] 7.10 — `GlobalExceptionHandler` không swallow im lặng (tất cả return `ResponseEntity` với body)

---

## 8. Tests

- [ ] 8.1 — `cd demo && ./gradlew test` → zero failures
  > **Blocker**: Integration tests (`DemoApplicationTests`, `AuthIntegrationTest`) fail vì Docker không khả dụng tại local. Unit test (`UserDetailsServiceImplTest`) + Slice test (`AuthControllerTest`) → **5 tests passed**. Sẽ pass đầy đủ trong CI với Docker.
- [x] 8.2 — `cd my-react-app && npm run lint` → zero errors ✅
- [x] 8.3 — `cd my-react-app && npm run build` → success ✅ (50 modules, 234 kB)
- [x] 8.4 — BE integration tests dùng Testcontainers PostgreSQL thật (`@ServiceConnection`, `postgres:16`)
- [x] 8.5 — BE unit tests naming: `method_state_expectedBehaviour` (e.g., `login_withValidCredentials_returns200WithSetCookie`)
- [x] 8.6 — BE `@WebMvcTest` slice tests cho `AuthController` + `SecurityConfig` import
- [x] 8.7 — FE Vitest + RTL, chỉ mock `src/api/authApi` (TST-7 compliant); **8/8 tests passed** ✅
- [x] 8.8 — Test cases cover tất cả happy path + failure path: login OK, wrong password, blank field, CSRF, logout, unauthenticated access, ProtectedRoute redirect
- [x] 8.9 — Không có `@ts-ignore` / `any` không documented trong FE code

---

## 9. Operations

- [ ] 9.1 — App khởi động OK với profile `dev`
  > **Chưa verify**: Cần DB PostgreSQL chạy. Sẽ verify trong CI / khi setup local DB.
- [ ] 9.2 — App khởi động OK với profile `prod`
  > **Chưa verify**: Cần DB PostgreSQL và env vars.
- [ ] 9.3 — Flyway V1, V2 chạy thành công mọi môi trường
  > **Chưa verify**: Sẽ verify khi có DB.
- [x] 9.4 — Flyway V3 chỉ chạy dev/test qua `spring.flyway.locations` (code reviewed; confirmed)
- [x] 9.5 — Rollback plan trong impl-plan §5 còn hợp lệ
- [x] 9.6 — Không có `.env`, `*.key`, `*.pem` bị commit (kiểm tra workspace — chỉ có `.properties` với env var placeholders)
- [x] 9.7 — `application-prod.properties` không chứa credentials thật (chỉ có `${DB_...}` placeholders)
- [x] 9.8 — DB connection dùng environment variables (`${DB_HOST}`, `${DB_PORT}`, `${DB_NAME}`, `${DB_USER}`, `${DB_PASSWORD}`)

---

## 10. Commands Run

> Điền sau khi chạy. Ghi rõ thời điểm, môi trường, và kết quả.

| Lệnh                                                                                              | Môi trường                | Kết quả                                                   | Ghi chú                                                               |
| ------------------------------------------------------------------------------------------------- | ------------------------- | --------------------------------------------------------- | --------------------------------------------------------------------- |
| `cd demo && .\gradlew test --tests "*.AuthControllerTest" --tests "*.UserDetailsServiceImplTest"` | local / Windows / Java 25 | ✅ BUILD SUCCESSFUL (5 tests passed)                      | Docker unavailable → integration tests skipped                        |
| `cd demo && .\gradlew test`                                                                       | local / Windows / Java 25 | ❌ 2 failures (DemoApplicationTests, AuthIntegrationTest) | ContainerFetchException — Docker not running; unit+slice tests passed |
| `cd my-react-app && npm run lint`                                                                 | local / Windows / Node 22 | ✅ 0 errors                                               | eslint + typescript-eslint                                            |
| `cd my-react-app && npm run build`                                                                | local / Windows           | ✅ 50 modules, 234 kB                                     | vitest/config import fix required                                     |
| `cd my-react-app && npm run test`                                                                 | local / Windows           | ✅ 8/8 tests passed                                       | act() warnings (non-fatal); 3 test files                              |
| `cd demo && .\gradlew dependencies --configuration compileClasspath`                              | local                     | not run                                                   |                                                                       |
| Manual smoke: login `user01/User@123`                                                             | dev profile               | not run — DB unavailable                                  |                                                                       |
| Manual smoke: `GET /api/v1/auth/csrf` sau login                                                   | dev profile               | not run                                                   |                                                                       |
| Manual smoke: POST protected + CSRF → 200                                                         | dev profile               | not run                                                   |                                                                       |
| Manual smoke: POST protected không CSRF → 403                                                     | dev profile               | not run                                                   |                                                                       |
| Manual smoke: `GET /v3/api-docs`                                                                  | dev profile               | not run                                                   |                                                                       |
| Manual smoke: `GET /swagger-ui/index.html`                                                        | prod profile              | not run                                                   |                                                                       |
| Manual smoke: kiểm tra `flyway_schema_history` V3 không có                                        | prod profile              | not run                                                   |                                                                       |
| Manual smoke: logout → session cleared                                                            | dev profile               | not run                                                   |                                                                       |

---

## 11. Known Risks

> Copy từ impl-plan §4 và cập nhật trạng thái sau implementation.

| #   | Rủi ro                                                     | Trạng thái    | Ghi chú                                                                                                                                                |
| --- | ---------------------------------------------------------- | ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| R-1 | `SameSite=None` + `Secure=false` dev — Chrome có thể block | [x] Mitigated | Comment thêm vào `application-dev.properties`; guide dùng Firefox hoặc local HTTPS proxy                                                               |
| R-2 | Flyway V3 accidentally chạy ở prod                         | [x] Mitigated | V3 ở sub-directory `seed/`; `flyway.locations` staging+prod chỉ point `db/migration` (không include seed)                                              |
| R-3 | Spring 6 `ProblemDetail` vs custom record conflict         | [x] Mitigated | Dùng Spring 6 built-in `ProblemDetail` xuyên suốt; không có custom class                                                                               |
| R-4 | CSRF token mất khi FE reload                               | [x] Mitigated | `AuthProvider` gọi `fetchCsrf()` trên mount; nếu session còn hợp lệ, CSRF được khôi phục                                                               |
| R-5 | `SecurityConfig` chặn nhầm `/api/v1/auth/login`            | [x] Mitigated | `ignoringRequestMatchers("/api/v1/auth/login")` + `permitAll()` confirmed bằng `AuthControllerTest.login_withValidCredentials_returns200WithSetCookie` |

---

## 12. Not Covered / Gaps

> Điền những gì chưa được test hoặc chưa implement (ngoài Open Issues).

| #   | Gap                                                                | Lý do không cover                                                       | Action                                                                  |
| --- | ------------------------------------------------------------------ | ----------------------------------------------------------------------- | ----------------------------------------------------------------------- |
| G-1 | Login failure WARN log (6.3)                                       | Không trong impl-plan gốc                                               | Thêm `log.warn(...)` trong `GlobalExceptionHandler` ở follow-up ticket  |
| G-2 | Logout event log (6.4)                                             | Không trong impl-plan gốc                                               | Thêm `log.info(...)` trong `AuthController.logout()` ở follow-up ticket |
| G-3 | `cookie().httpOnly("JSESSIONID", true)` assertion trong slice test | MockMvc không set `Set-Cookie` header — đây là Tomcat container concern | Sẽ cover trong integration test khi CI Docker available                 |
| G-4 | Manual smoke tests (§10 rows)                                      | PostgreSQL DB không khả dụng tại local at time of review                | Cần chạy trước khi merge vào staging                                    |

---

## 13. Remaining Work / Follow-up

> Open Issues và công việc cần làm sau merge.

| #       | Item                                                                         | Priority          | Ticket                               |
| ------- | ---------------------------------------------------------------------------- | ----------------- | ------------------------------------ |
| OI-A'   | CORS `allowedOrigins` cho prod                                               | Block deploy prod | Cần ticket mới                       |
| OI-B    | RFC 7807 `type` URI namespace — placeholder `https://errors.example.com/...` | Low               |                                      |
| OI-C    | HTTPS local setup guide cho staging/prod `Secure=true`                       | Low               |                                      |
| G-1/G-2 | Application-level WARN/INFO logging cho login failure + logout               | Low               | Add to follow-up auth-logging ticket |
| G-4     | Manual smoke test sign-off trước khi merge vào staging                       | High              | Before staging deploy                |
