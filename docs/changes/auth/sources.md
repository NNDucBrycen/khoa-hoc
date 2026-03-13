# Sources — TICKET: auth
_Cập nhật: 2026-03-13_

## 1. Danh sách tài liệu nguồn

| # | Tài liệu | Đường dẫn | Loại | Thẩm quyền |
|---|----------|-----------|------|------------|
| S-1 | User Story: Authentication bằng Session Cookie + CSRF API riêng, tích hợp Springdoc, PostgreSQL, Flyway | `docs/changes/auth/raw/spec.md` | Raw spec (text extract) | **Primary** |

> Không có tài liệu nguồn thứ hai. Mọi quyết định thiết kế đều dẫn chiếu về S-1.

---

## 2. Trạng thái các Open Issues trong S-1

Tất cả 5 open issues ban đầu trong raw spec đã được chốt:

| OI | Nội dung | Trạng thái | Quyết định |
|----|----------|-----------|-----------|
| OI-1 | Nhu cầu tích hợp cross-site | ✅ Đã chốt | Có; `SameSite=None`, `allowCredentials=true` |
| OI-2 | Session TTL theo môi trường | ✅ Đã chốt | dev=8h, staging=2h, prod=30m |
| OI-3 | Seed user mặc định | ✅ Đã chốt | Chỉ dev/test; `admin/Admin@123`, `user01/User@123` |
| OI-4 | Swagger | ✅ Đã chốt | Bật dev/staging, tắt prod |
| OI-5 | Session store | ✅ Đã chốt | In-memory mặc định, không Redis |

Open Issues bổ sung được chốt trong quá trình tạo Spec Pack (2026-03-13):

| OI | Câu hỏi | Trạng thái | Quyết định |
|----|---------|-----------|-----------|
| OI-6 | CORS whitelist frontend origin | ✅ Đã chốt | `http://localhost:5173` (dev), `http://staging:5173` (staging) |
| OI-7 | Cơ chế seed dev/test | ✅ Đã chốt | Flyway V3 conditional theo Spring profile |
| OI-8 | Cookie `Secure=true` ở local dev | ✅ Đã chốt | Không bắt buộc ở local dev |
| OI-9 | BCrypt cost factor | ✅ Đã chốt | **Cost factor 12** (cân bằng security/performance, Spring Security default) |
| OI-10 | Response format lỗi | ✅ Đã chốt | Có dùng RFC 7807 Problem Details |

---

## 3. Xung đột giữa các nguồn

**Không có xung đột.** Chỉ có một tài liệu nguồn duy nhất (S-1) và tất cả open issues đã được chốt.

---

## 4. Điểm cần lưu ý về thẩm quyền

- S-1 là nguồn duy nhất và đầy đủ; `spec-pack.md` là tài liệu phái sinh từ S-1.
- Nếu có thay đổi yêu cầu, phải cập nhật cả S-1 (hoặc tạo S-2) và spec-pack.md đồng thời.
- CORS origins cho staging/prod chưa được xác định — xem Open Issues trong spec-pack.md.
