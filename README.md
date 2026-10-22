# E-Library – Hệ thống thư viện điện tử

Ứng dụng web quản lý danh mục sách và quy trình mượn/trả tài liệu, được thực hiện cho môn **72ITSE41704 – Application Programming Project**, lớp **261_72ITSE41704_01**.

## Tổng quan dự án

E-Library hỗ trợ thủ thư quản lý sách và bản sao vật lý; thành viên có thể tìm sách, gửi yêu cầu mượn và xem hoạt động của mình. Hệ thống phân quyền giữa **Librarian** và **Member**.

Các chức năng đã có trong mốc Week 6:

- Tìm danh mục theo tên sách, tác giả, ISBN hoặc thể loại.
- Thủ thư thêm, sửa, ngừng kinh doanh sách; quản lý bản sao và trạng thái bản sao.
- Thành viên đăng ký tài khoản, đăng nhập và gửi yêu cầu mượn sách trong phiên bản mã nguồn hiện tại.
- Thủ thư duyệt hoặc từ chối yêu cầu; ghi nhận trả sách.
- Thời hạn mượn 14 ngày; tính khoản phạt trễ hạn 5.000 VND/ngày.
- Lưu dữ liệu bằng MySQL khi chạy local; hỗ trợ PostgreSQL trên Supabase khi chạy production.
- Triển khai web service Docker trên Render.

> Đăng ký công khai chỉ tạo tài khoản `MEMBER`; người dùng không thể tự tạo tài khoản thủ thư. Tính năng đăng ký trong nhánh hiện tại cần được push và deploy để xuất hiện trên website. Một số bước kiểm tra thủ công như đăng nhập production và kiểm tra dữ liệu sau khi restart vẫn cần được xác nhận.

## Thành viên

| Họ và tên | Mã sinh viên | Vai trò dự kiến |
| --- | --- | --- |
| Nguyễn Thành Trí | 2474802010400 | Backend và cơ sở dữ liệu |
| Phạm Thị Khánh Ngân | 2474802016786 | Frontend và tích hợp tính năng |

## Website và liên kết

| Nội dung | Liên kết |
| --- | --- |
| Website Render | [elibrary-web-gy9e.onrender.com](https://elibrary-web-gy9e.onrender.com) |
| Mã nguồn GitHub | [261_72ITSE41704_2474802010400_E-Library](https://github.com/trithanh1577/261_72ITSE41704_2474802010400_E-Library) |
| Pull Requests | [GitHub Pull Requests](https://github.com/trithanh1577/261_72ITSE41704_2474802010400_E-Library/pulls) |
| Render Dashboard | [dashboard.render.com](https://dashboard.render.com/) |
| Supabase Dashboard | [supabase.com/dashboard](https://supabase.com/dashboard) |
| Website local | [localhost:8080](http://localhost:8080) |

Render dùng gói miễn phí nên service có thể tạm dừng khi không có truy cập; lượt mở đầu tiên sau đó có thể mất thêm thời gian để khởi động.

## Công nghệ sử dụng

- Java 17 và Spring Boot 3.3.
- Spring MVC, Thymeleaf, HTML và CSS.
- Spring Security để đăng nhập và phân quyền.
- Spring Data JPA và Bean Validation.
- MySQL cho môi trường local; PostgreSQL trên Supabase cho production.
- Maven, JUnit 5 và Mockito.
- Docker và Render.

## Chạy dự án trên Visual Studio Code

### Yêu cầu

- Visual Studio Code.
- JDK 17 trở lên.
- Maven 3.9 trở lên.
- MySQL 8 trở lên nếu chạy theo cấu hình môn học.

Mở thư mục dự án trong VS Code. Tạo database một lần trong MySQL:

```sql
CREATE DATABASE elibrary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Từ terminal tại thư mục dự án, chạy:

```powershell
mvn test
mvn spring-boot:run
```

Mở [http://localhost:8080](http://localhost:8080). Tài khoản demo `admin / admin123` và `member / member123` chỉ dành cho local, không dùng trên website production.

Nếu máy chưa có MySQL, có thể chạy profile H2 dạng file để xem thử giao diện:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=h2"
```

H2 lưu dữ liệu tại `data/elibrary.mv.db` và giữ dữ liệu sau khi tắt/mở lại ứng dụng. Thư mục `data/` không được đẩy lên GitHub. H2 là phương án local thay thế, không thay cho kiểm tra với MySQL.

Trên Windows, nếu Maven gặp lỗi khi chạy từ đường dẫn thư mục có dấu tiếng Việt, xem hướng dẫn ánh xạ ổ đĩa tạm trong [`RUN_GUIDE.md`](RUN_GUIDE.md).

## Tài khoản production

- Username thủ thư ban đầu được cấu hình bằng `BOOTSTRAP_ADMIN_USERNAME` trên Render (hiện đặt là `librarian`).
- Mật khẩu nằm trong biến bí mật `BOOTSTRAP_ADMIN_PASSWORD` trên Render; README không lưu mật khẩu production.
- Đổi biến bootstrap sau khi tài khoản đã được tạo không tự đổi mật khẩu của tài khoản hiện có.
- Thành viên tự đăng ký trên website; biểu mẫu đăng ký không cho chọn vai trò quản trị.

Không commit file `.env`, mật khẩu, chuỗi kết nối có thông tin xác thực, API key hoặc dữ liệu production lên GitHub. Nếu cần cập nhật mật khẩu quản trị, thực hiện qua quy trình bảo mật của môi trường triển khai; không gửi mật khẩu qua chat.

## Biến môi trường

| Biến | Mục đích |
| --- | --- |
| `DB_URL` | URL JDBC đến MySQL local hoặc PostgreSQL Supabase |
| `DB_USERNAME` | Username kết nối database |
| `DB_PASSWORD` | Mật khẩu database; không ghi vào Git |
| `DB_DRIVER` | Driver database, ví dụ `org.postgresql.Driver` trên production |
| `SPRING_PROFILES_ACTIVE` | Profile Spring, đặt `prod` trên Render |
| `BOOTSTRAP_ADMIN_USERNAME` | Username thủ thư đầu tiên trên database production trống |
| `BOOTSTRAP_ADMIN_PASSWORD` | Mật khẩu ban đầu của thủ thư; nhập trực tiếp trong Render |

## Cấu trúc dự án

```text
src/main/java/com/elibrary/       Mã nguồn Java: controller, service, domain, repository
src/main/resources/templates/     Giao diện Thymeleaf
src/main/resources/static/        CSS và tài nguyên giao diện
src/test/java/com/elibrary/       Unit test và repository test
docs/                             Tiến độ tuần, yêu cầu, thiết kế và test case
Dockerfile                        Đóng gói ứng dụng để chạy trên Render
render.yaml                       Cấu hình Render Blueprint
pom.xml                           Dependency và cấu hình Maven
RUN_GUIDE.md                      Hướng dẫn chạy local và bàn giao Git
```

## Quy trình cập nhật website

Render hiện kết nối với branch `feature/week-6-circulation`. Khi thay đổi đã được review, push lên branch được cấu hình sẽ kích hoạt quá trình build/deploy tự động trên Render. Push mã nguồn lên GitHub không đồng nghĩa với việc website đã deploy thành công; hãy kiểm tra trạng thái và log trên Render sau mỗi lần cập nhật.

Theo bàn giao của nhóm, **Ngan3112** thực hiện thao tác push GitHub. Không force-push và không đưa thông tin xác thực vào commit.

## Kiểm thử và trạng thái

Chạy bộ kiểm thử bằng:

```powershell
mvn test
```

Lần kiểm tra local gần nhất: **19 test đạt, 0 lỗi**. Kiểm tra thủ công với MySQL, luồng trình duyệt đầy đủ và khả năng giữ dữ liệu trên Supabase sau khi restart cần được ghi nhận riêng trong [`docs/week6-status.md`](docs/week6-status.md).

Các phần như đăng nhập bằng email, đặt lại mật khẩu, hàng chờ đặt trước, thanh toán/miễn phạt, báo cáo và quản trị tài khoản thành viên chưa nằm trong mốc hiện tại.
