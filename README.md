# E-Library — Hệ thống quản lý thư viện

Ứng dụng web quản lý danh mục sách và quy trình mượn/trả, thực hiện cho môn **72ITSE41704 – Application Programming Project**, lớp **261_72ITSE41704_01**. Đề tài theo **Topic 6 – E-Library and Resource Circulation System**.

## Tổng quan

E-Library hỗ trợ hai vai trò:

- **Member:** tìm sách, xem tình trạng bản sao, đăng ký tài khoản, gửi yêu cầu mượn, đặt trước sách chưa có bản sao khả dụng và xem hoạt động của chính mình.
- **Librarian:** quản lý sách và bản sao, duyệt yêu cầu mượn, ghi nhận trả sách, quản lý thành viên, xử lý tiền phạt và xem báo cáo lưu thông.

Đăng ký công khai chỉ tạo tài khoản Member. Giao diện có nút chuyển đổi tiếng Anh/tiếng Việt theo yêu cầu bổ sung của nhóm. Tài khoản Librarian không thể được tạo từ biểu mẫu đăng ký thành viên.

## Thành viên

| Họ và tên | Mã sinh viên |
| --- | --- |
| Nguyễn Thành Trí | 2474802010400 |
| Phạm Thị Khánh Ngân | 2474802016786 |

## Use case

Các use case dưới đây được mô tả chi tiết trong thư mục [docs](docs/).

| ID | Use case | Vai trò chính |
| --- | --- | --- |
| UC-01 | Đăng ký, đăng nhập và đăng xuất tài khoản | Member, Librarian |
| UC-02 | Quản lý đầu sách và bản sao vật lý | Librarian |
| UC-03 | Tìm sách và xem tình trạng bản sao | Member, Librarian |
| UC-04 | Gửi yêu cầu mượn sách | Member |
| UC-05 | Duyệt yêu cầu và lập lượt mượn | Librarian |
| UC-06 | Ghi nhận trả sách và tính tiền phạt | Librarian |
| UC-07 | Xem yêu cầu, lượt mượn và lịch sử của mình | Member |
| UC-08 | Đặt trước đầu sách chưa có bản sao khả dụng | Member |
| UC-09 | Cập nhật thông tin và hạng thành viên | Librarian |
| UC-10 | Nhận sách đặt trước hoặc hủy yêu cầu đặt trước | Member |
| UC-11 | Lọc lịch sử lưu thông và ghi nhận thanh toán/miễn phạt | Librarian |
| UC-12 | Xem báo cáo đầu sách được mượn nhiều | Librarian |

## Business rules

| ID | Quy tắc nghiệp vụ |
| --- | --- |
| BR-01 | Hạng Standard được mượn tối đa 5 sách trong 14 ngày; Student tối đa 3 sách trong 21 ngày; Premium tối đa 8 sách trong 21 ngày. |
| BR-02 | Chỉ tài khoản Member đang hoạt động, không có lượt mượn quá hạn và còn hạn mức mới được tạo lượt mượn. |
| BR-03 | Phạt trễ 5.000 VND mỗi ngày tính phí, không tính ngày nghỉ trong `LIBRARY_HOLIDAYS`, tối đa 100.000 VND cho một lượt mượn. |
| BR-04 | Chỉ được đặt trước khi không còn bản sao khả dụng. Bản sao trả về được cấp theo FIFO và giữ 48 giờ cho Member đủ điều kiện; nếu hết hạn hoặc Member không đủ điều kiện, bản sao được chuyển cho người tiếp theo. |
| BR-05 | Một Member chỉ được có một đặt trước đang hoạt động (`WAITING` hoặc `READY`) cho cùng một đầu sách. |

## Đối chiếu yêu cầu nhóm hai thành viên

Tài liệu đề tài gợi ý 9–12 use case, 12–16 domain class có ý nghĩa, 4–5 business rule và ít nhất 15 test case được ghi nhận. Bài hiện có 12 use case, 12 domain type có ý nghĩa, 5 business rule và 77 test case trong các bảng theo tuần. Bộ kiểm thử tự động gần nhất đạt 89/89 vào ngày 8/11/2026. Nghiệm thu trực tiếp trên trình duyệt và xác nhận dữ liệu production Supabase vẫn cần thực hiện riêng.

## Công nghệ

- Java 17, Spring Boot 3.3, Spring MVC và Thymeleaf.
- Spring Security, Spring Data JPA và Bean Validation.
- MySQL cho môi trường local; PostgreSQL trên Supabase cho cấu hình production.
- Maven, JUnit 5, Mockito và Docker.
- Render dùng để chạy web service; cấu hình triển khai nằm trong `render.yaml`.

## Cấu trúc dự án

### Mã nguồn ứng dụng — `src/main`

| Thư mục / file | Nội dung |
| --- | --- |
| `src/main/java/com/elibrary/config/` | Cấu hình bảo mật, khởi tạo dữ liệu, tài khoản quản trị production và ngôn ngữ giao diện. |
| `src/main/java/com/elibrary/domain/` | Các đối tượng nghiệp vụ như sách, bản sao, tài khoản, lượt mượn, đặt trước và quy tắc tính phạt. |
| `src/main/java/com/elibrary/repository/` | Spring Data repository dùng để truy vấn và lưu dữ liệu. |
| `src/main/java/com/elibrary/service/` | Nghiệp vụ đăng ký thành viên, quản lý sách và xử lý lưu thông. |
| `src/main/java/com/elibrary/web/` | Controller nhận yêu cầu web, xử lý trang lỗi và thông báo giao diện. |
| `src/main/java/com/elibrary/web/form/` | Đối tượng nhận dữ liệu biểu mẫu và kiểm tra dữ liệu nhập. |
| `src/main/resources/templates/auth/` | Trang đăng nhập và đăng ký thành viên. |
| `src/main/resources/templates/books/` | Danh sách, chi tiết và biểu mẫu đầu sách. |
| `src/main/resources/templates/copies/` | Biểu mẫu quản lý bản sao vật lý. |
| `src/main/resources/templates/circulation/` | Trang yêu cầu mượn, lượt mượn, đặt trước và hoạt động thành viên. |
| `src/main/resources/templates/members/` | Trang thủ thư quản lý tài khoản thành viên. |
| `src/main/resources/templates/error/` | Trang lỗi truy cập và lỗi ứng dụng. |
| `src/main/resources/templates/fragments/` | Thành phần giao diện dùng chung, gồm thanh điều hướng. |
| `src/main/resources/static/css/` | CSS của ứng dụng. |
| `src/main/resources/messages.properties`, `messages_vi.properties` | Nhãn giao diện và thông báo tiếng Anh/tiếng Việt. |
| `src/main/resources/application*.properties` | Cấu hình Spring mặc định và profile cơ sở dữ liệu. |

### Kiểm thử — `src/test`

| Thư mục | Nội dung |
| --- | --- |
| `src/test/java/com/elibrary/config/` | Kiểm thử cấu hình và khởi tạo tài khoản. |
| `src/test/java/com/elibrary/domain/` | Kiểm thử quy tắc nghiệp vụ trong domain. |
| `src/test/java/com/elibrary/repository/` | Kiểm thử truy vấn và tìm kiếm dữ liệu. |
| `src/test/java/com/elibrary/service/` | Kiểm thử dịch vụ và luồng nghiệp vụ. |
| `src/test/java/com/elibrary/web/` | Kiểm thử tích hợp controller, phân quyền, biểu mẫu và giao diện. |
| `src/test/resources/` | Cấu hình dùng riêng khi chạy test. |

### Tài liệu — `docs`

| Thư mục / file | Nội dung |
| --- | --- |
| `docs/design/` | Kiến trúc, ERD, class diagram, use-case diagram, sequence diagram và wireframe. |
| `docs/use-cases-week*.md` | Use case được chia theo các mốc tuần. |
| `docs/test-cases-week*.md` | Test case và kết quả kiểm tra theo từng mốc. |
| `docs/requirements-week2.md` | Functional requirement, non-functional requirement và BR-01–BR-05. |
| `docs/week*-status.md`, `weekly-progress.md` | Tiến độ và các điểm đã kiểm tra theo tuần. |
| `docs/week9-acceptance.md` | Checklist nghiệm thu trên trình duyệt, database và môi trường triển khai. |
| `docs/deployment-render-supabase.md` | Ghi chú cấu hình Render và Supabase. |
| `docs/sample-catalogue-data.md` | Nguồn và ghi chú về dữ liệu danh mục mẫu. |
| `docs/work-plan-week1-10.md` | Kế hoạch các mốc tuần. |

### File cấu hình ở thư mục gốc

| File | Nội dung |
| --- | --- |
| `pom.xml` | Dependency và cấu hình build/test Maven. |
| `Dockerfile`, `.dockerignore` | Đóng gói ứng dụng và loại file local khỏi Docker build context. |
| `compose.yaml` | Chạy ứng dụng cùng MySQL local có volume lưu dữ liệu. |
| `render.yaml` | Cấu hình Render Blueprint và biến môi trường production. |
| `.env.example` | Mẫu biến môi trường cho local; không chứa thông tin production. |
| `RUN_GUIDE.md` | Hướng dẫn chạy và kiểm tra dự án. |

Thư mục `data/` chứa database H2 local nếu sử dụng profile H2; dữ liệu này không được commit. Thư mục `target/` là kết quả build Maven được tạo lại khi build.

## Chạy local

Cần JDK 17 và Docker Desktop. Tại thư mục dự án, chạy:

```powershell
if (-not (Test-Path .env)) { Copy-Item .env.example .env }
docker compose up --build
```

Mở [http://localhost:8080](http://localhost:8080). Dừng ứng dụng bằng:

```powershell
docker compose down
```

Docker Compose giữ dữ liệu MySQL trong volume để lần chạy sau tiếp tục sử dụng. Hướng dẫn đầy đủ, bao gồm cách chạy không dùng Docker, nằm trong [RUN_GUIDE.md](RUN_GUIDE.md).

## Kiểm thử và trạng thái

Chạy test bằng `mvn test`; chạy cả bước đóng gói bằng `mvn verify`. Bộ test tự động gần nhất được ghi nhận đạt **89/89 vào ngày 8/11/2026**. Các bước nghiệm thu bằng trình duyệt và xác nhận dữ liệu production trên Supabase vẫn cần kiểm tra riêng; xem [checklist nghiệm thu](docs/week9-acceptance.md).

## Website và repository

| Nội dung | Liên kết |
| --- | --- |
| Website Render | [elibrary-web-gy9e.onrender.com](https://elibrary-web-gy9e.onrender.com) |
| GitHub repository | [261_72ITSE41704_2474802010400_E-Library](https://github.com/trithanh1577/261_72ITSE41704_2474802010400_E-Library) |
| Pull requests | [Danh sách Pull Requests](https://github.com/trithanh1577/261_72ITSE41704_2474802010400_E-Library/pulls) |
| Render Dashboard | [dashboard.render.com](https://dashboard.render.com/) |
| Supabase Dashboard | [supabase.com/dashboard](https://supabase.com/dashboard) |

Trạng thái website và kết nối Supabase cần được xác nhận trên Render Dashboard. Không commit file `.env`, mật khẩu production, chuỗi kết nối có thông tin xác thực hoặc dữ liệu database local lên GitHub.
