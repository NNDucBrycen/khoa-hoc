# Self-Review — AUTH-001
_Template: Claude điền sau khi hoàn thành implementation_
_Tạo: 2026-03-13 · Trạng thái: **[ ] Chưa điền**_

> Hướng dẫn: Đánh dấu `[x]` khi mục đã xác nhận pass. Để `[ ]` nếu chưa check hoặc fail.
> Mọi Blocker phải là `[x]` trước khi tạo PR.

---

## 1. Spec / AC Coverage

- [ ] 1.1 — Tất cả 32 AC + NFR có ít nhất 1 automated test
- [ ] 1.2 — NFR-1→NFR-8 được cover bởi test hoặc config review
- [ ] 1.3 — Không có feature ngoài scope spec-pack §2
- [ ] 1.4 — Open Issues (OI-A', OI-B, OI-C) ghi chú rõ, không implement ngầm
- [ ] 1.5 — Out-of-scope items (Social login, MFA, JWT, Redis…) không xuất hiện trong code

---

## 2. Design & Dependencies

- [ ] 2.1 — `build.gradle` chỉ thêm đúng 10 dependencies spec (S-01)
- [ ] 2.2 — Springdoc version `2.8.9`
- [ ] 2.3 — Spring Boot managed versions — không hardcode (ST-B4)
- [ ] 2.4 — Layer: Controller → Service → Repository (không bypass)
- [ ] 2.5 — Không có `HttpServletRequest`/`HttpServletResponse` trong Service
- [ ] 2.6 — `@Configuration` classes trong package `config/`
- [ ] 2.7 — Exception handling qua `@ControllerAdvice`, không catch-and-swallow
- [ ] 2.8 — FE: API calls chỉ từ `src/api/authApi.ts`
- [ ] 2.9 — FE: Auth state trong 1 React context duy nhất
- [ ] 2.10 — FE: Protected routes qua `ProtectedRoute` wrapper

---

## 3. Security

- [ ] 3.1 — Cookie `HttpOnly=true` mọi môi trường
- [ ] 3.2 — Cookie `Secure=true` staging và prod
- [ ] 3.3 — Cookie `SameSite=None` mọi môi trường
- [ ] 3.4 — BCrypt cost factor = **12**
- [ ] 3.5 — Không log password / hash / CSRF token ở bất kỳ level
- [ ] 3.6 — CORS whitelist tường minh, không dùng `"*"`
- [ ] 3.7 — CSRF validation bật cho POST/PUT/PATCH/DELETE
- [ ] 3.8 — `formLogin().disable()` và `httpBasic().disable()`
- [ ] 3.9 — Role derive từ DB/session, không trust client
- [ ] 3.10 — Springdoc tắt hoàn toàn ở prod
- [ ] 3.11 — Stack trace không xuất hiện trong response body
- [ ] 3.12 — Không có credentials trong file committed
- [ ] 3.13 — BCrypt hash trong V3 seed được tính offline
- [ ] 3.14 — Input validation tại Controller boundary

---

## 4. Performance

- [ ] 4.1 — Session timeout: dev=8h, staging=2h, prod=30m
- [ ] 4.2 — In-memory session limitation được document (NFR-8)
- [ ] 4.3 — `UserDetailsServiceImpl` không gọi DB nhiều lần / request
- [ ] 4.4 — Không có N+1 query khi load user + roles

---

## 5. Compatibility

- [ ] 5.1 — `SameSite=None` + `Secure=false` ở dev được ghi chú limitation (Chrome)
- [ ] 5.2 — Flyway V3 không xuất hiện trong `flyway_schema_history` ở staging/prod
- [ ] 5.3 — Spring 6 built-in `ProblemDetail` được dùng, không có custom duplicate class
- [ ] 5.4 — Vite proxy config đúng, không ảnh hưởng production build
- [ ] 5.5 — `fetchCsrf` được gọi khi app init nếu session còn hợp lệ

---

## 6. Logging / Audit

- [ ] 6.1 — Không log username / session id / CSRF token ở INFO+ trong prod
- [ ] 6.2 — Không log password hoặc hash ở bất kỳ level
- [ ] 6.3 — Login failure được log ở WARN với opaque identifier
- [ ] 6.4 — Logout event được log

---

## 7. Error Handling

- [ ] 7.1 — Tất cả error có đủ 5 RFC 7807 fields: `type`, `title`, `status`, `detail`, `instance`
- [ ] 7.2 — Login thất bại → 401 RFC 7807
- [ ] 7.3 — User disabled → 401 RFC 7807
- [ ] 7.4 — CSRF endpoint không có session → 401 RFC 7807
- [ ] 7.5 — Thiếu CSRF header → 403 RFC 7807
- [ ] 7.6 — Sai CSRF token → 403 RFC 7807
- [ ] 7.7 — Unauthenticated → 401 RFC 7807 (custom `AuthenticationEntryPoint`)
- [ ] 7.8 — Forbidden → 403 RFC 7807 (custom `AccessDeniedHandler`)
- [ ] 7.9 — Bean Validation failure → 400 RFC 7807
- [ ] 7.10 — `GlobalExceptionHandler` không swallow im lặng

---

## 8. Tests

- [ ] 8.1 — `cd demo && ./gradlew test` → zero failures
- [ ] 8.2 — `cd my-react-app && npm run lint` → zero errors
- [ ] 8.3 — `cd my-react-app && npm run build` → success
- [ ] 8.4 — BE integration tests dùng Testcontainers PostgreSQL thật
- [ ] 8.5 — BE unit tests naming: `method_state_expectedBehaviour`
- [ ] 8.6 — BE `@WebMvcTest` slice tests cho AuthController + SecurityConfig
- [ ] 8.7 — FE Vitest + RTL, chỉ mock `src/api/`
- [ ] 8.8 — Test cases cover tất cả happy path + failure path quan trọng
- [ ] 8.9 — Không có `@ts-ignore` / `any` không documented

---

## 9. Operations

- [ ] 9.1 — App khởi động OK với profile `dev`
- [ ] 9.2 — App khởi động OK với profile `prod`
- [ ] 9.3 — Flyway V1, V2 chạy thành công mọi môi trường
- [ ] 9.4 — Flyway V3 chỉ chạy dev/test qua `spring.flyway.locations`
- [ ] 9.5 — Rollback plan trong impl-plan §5 còn hợp lệ
- [ ] 9.6 — Không có `.env`, `*.key`, `*.pem` bị commit
- [ ] 9.7 — `application-prod.properties` không chứa credentials thật
- [ ] 9.8 — DB connection dùng environment variables

---

## 10. Commands Run

> Điền sau khi chạy. Ghi rõ thời điểm, môi trường, và kết quả.

| Lệnh | Môi trường | Kết quả | Ghi chú |
|------|-----------|---------|---------|
| `cd demo && ./gradlew test` | local / dev | | |
| `cd my-react-app && npm run lint` | local | | |
| `cd my-react-app && npm run build` | local | | |
| `cd demo && ./gradlew dependencies --configuration compileClasspath` | local | | |
| Manual smoke: login `user01/User@123` | dev profile | | |
| Manual smoke: `GET /api/v1/auth/csrf` sau login | dev profile | | |
| Manual smoke: POST protected + CSRF → 200 | dev profile | | |
| Manual smoke: POST protected không CSRF → 403 | dev profile | | |
| Manual smoke: `GET /v3/api-docs` | dev profile | | |
| Manual smoke: `GET /swagger-ui/index.html` | prod profile | | |
| Manual smoke: kiểm tra `flyway_schema_history` V3 không có | prod profile | | |
| Manual smoke: logout → session cleared | dev profile | | |

---

## 11. Known Risks

> Copy từ impl-plan §4 và cập nhật trạng thái sau implementation.

| # | Rủi ro | Trạng thái | Ghi chú |
|---|--------|-----------|---------|
| R-1 | `SameSite=None` + `Secure=false` dev — Chrome có thể block | [ ] Confirmed / [ ] Mitigated | |
| R-2 | Flyway V3 accidentally chạy ở prod | [ ] Confirmed / [ ] Mitigated | |
| R-3 | Spring 6 `ProblemDetail` vs custom record conflict | [ ] Confirmed / [ ] Mitigated | |
| R-4 | CSRF token mất khi FE reload | [ ] Confirmed / [ ] Mitigated | |
| R-5 | `SecurityConfig` chặn nhầm `/api/v1/auth/login` | [ ] Confirmed / [ ] Mitigated | |

---

## 12. Not Covered / Gaps

> Điền những gì chưa được test hoặc chưa implement (ngoài Open Issues).

| # | Gap | Lý do không cover | Action |
|---|-----|-------------------|--------|
| | | | |

---

## 13. Remaining Work / Follow-up

> Open Issues và công việc cần làm sau merge.

| # | Item | Priority | Ticket |
|---|------|----------|--------|
| OI-A' | CORS `allowedOrigins` cho prod | Block deploy prod | Cần ticket mới |
| OI-B | RFC 7807 `type` URI namespace — placeholder `https://errors.example.com/...` | Low | |
| OI-C | HTTPS local setup guide cho staging/prod `Secure=true` | Low | |
| | | | |
