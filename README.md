# E-Library – Hệ thống thư viện điện tử

Ứng dụng web quản lý danh mục sách và quy trình mượn/trả tài liệu, được thực hiện cho môn **72ITSE41704 – Application Programming Project**, lớp **261_72ITSE41704_01**.

## Tổng quan dự án

E-Library hỗ trợ thủ thư quản lý sách, thành viên và quy trình lưu thông; thành viên có thể tìm sách, đăng ký/đăng nhập, gửi yêu cầu mượn, xếp hàng đặt trước và theo dõi lịch sử cá nhân.

Mã nguồn hiện có các luồng sau:

- Tìm danh mục theo tên sách, tác giả, ISBN hoặc thể loại; xem trạng thái và số bản sao khả dụng.
- Thủ thư thêm, sửa, ngừng kinh doanh sách; quản lý bản sao vật lý.
- Thành viên đăng ký tài khoản Member, đăng nhập, gửi yêu cầu mượn và xem hoạt động cá nhân.
- Thủ thư duyệt hoặc từ chối yêu cầu, ghi nhận trả sách và khoản phạt.
- Thành viên xếp hàng đặt trước theo thứ tự; bản sao được giữ tối đa 48 giờ cho người đầu hàng.
- Thủ thư cập nhật tên hiển thị, hạng thành viên và trạng thái tài khoản; không thể cấp quyền Librarian từ trang quản lý thành viên.
- Đăng nhập bằng username giữ nguyên phân biệt hoa thường hoặc bằng email; mật khẩu được lưu bằng BCrypt.
- Khi đăng ký Member, mật khẩu không có độ dài tối thiểu; giới hạn tối đa là 72 byte UTF-8 theo BCrypt và được kiểm tra phía server.
- Thủ thư lọc lịch sử lưu thông theo thành viên, sách, mã bản sao và trạng thái; xem các đầu sách được mượn nhiều nhất; có thể ghi nhận thanh toán hoặc miễn phạt kèm ghi chú.
- Dữ liệu dùng MySQL khi chạy local; cấu hình production hỗ trợ PostgreSQL trên Supabase. Web service được đóng gói Docker và cấu hình trên Render.

Đăng ký công khai chỉ tạo tài khoản `MEMBER`; người dùng không thể tự tạo tài khoản thủ thư. Các thay đổi tiếp theo trên branch `feature/week-7-reservations` đang ở workspace cục bộ; chưa push, merge vào `main` hay xác nhận deploy trên Render. Các kiểm tra thủ công với MySQL/trình duyệt và kiểm tra dữ liệu cloud sau restart vẫn cần nhóm thực hiện.

## Thành viên

| Họ và tên | Mã sinh viên | Vai trò dự kiến |
| --- | --- | --- |
| Nguyễn Thành Trí | 2474802010400 | Backend và cơ sở dữ liệu |
| Phạm Thị Khánh Ngân | 2474802016786 | Frontend và tích hợp tính năng |

## Website và liên kết

| Nội dung | Liên kết |
| --- | --- |
| Render URL (mở trước đây; build hiện tại chưa xác minh) | [elibrary-web-gy9e.onrender.com](https://elibrary-web-gy9e.onrender.com) |
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

## Đối chiếu phạm vi theo tài liệu môn học

Đề tài là **Topic 6 – E-Library and Resource Circulation System**. Course Guidelines §4.1 nêu ngưỡng tối thiểu chung để qua môn; Suggested Project Titles §3 đưa ra mức hiệu chỉnh cho nhóm hai người. Các mức dưới đây là mục tiêu đối chiếu, không phải tuyên bố rằng mọi chức năng đã được nghiệm thu.

| Tiêu chí | Mục tiêu tham khảo cho nhóm 2 người | Trạng thái trong repository |
| --- | --- | --- |
| Use case end-to-end | 9–12 | Có 12 use case khác nhau, gồm báo cáo mức độ phổ biến theo lượt mượn; các luồng chính được kiểm tra tích hợp qua MockMvc, Spring Security và H2. Kiểm tra browser trên MySQL/production còn pending. |
| Domain class có ý nghĩa | 12–16 | Có 12 class trong `domain`: 7 entity và 5 class chính sách/giá trị/aggregate; 6 enum được tính riêng. |
| Vai trò | Ít nhất 2 với quyền khác nhau | Có `Librarian` và `Member`; kiểm tra đầy đủ trên MySQL/trình duyệt còn pending. |
| Business rule | 4–5 quy tắc không chỉ là CRUD | Có 5 quy tắc được đánh mã BR-01–BR-05 trong `docs/requirements-week2.md`, gồm giới hạn/kỳ hạn theo hạng, điều kiện mượn, tiền phạt, hàng chờ FIFO/48 giờ và chống đặt trùng; phần chạy tay trên MySQL còn pending. |
| Test case | Ít nhất 15 case được ghi nhận | Có 67 case được ghi nhận trong các bảng Week 5–9. Offline Maven test/package đạt 77/77 kiểm thử ngày 6/11. Browser/MySQL/cloud acceptance còn pending. |
| Architecture | Tách presentation / business logic / data access | Controller, service, domain và repository được tách lớp; trách nhiệm được mô tả trong [`docs/design/architecture.md`](docs/design/architecture.md). |
| Persistence | CSDL quan hệ hoặc dữ liệu có cấu trúc sống qua restart | Cấu hình MySQL local, H2 file demo và PostgreSQL production; H2 restart đã được kiểm tra, MySQL và Supabase cần nhóm xác nhận trên môi trường thật. |
| Input validation | Kiểm tra input phía server và thông báo lỗi rõ | Form validation và service checks có automated coverage; input parsing sai trả HTTP 400 ở TC-40. Rà soát thủ công từng trường còn pending. |
| Exception handling | Xử lý có cấu trúc lỗi input và truy cập dữ liệu | Recovery pages cho lỗi input, not-found, conflict và database; TC-40/TC-41/TC-56 kiểm tra phản hồi mà không lộ chi tiết nội bộ. |
| User interface | Giao diện đồ họa dùng được, nhất quán | Ứng dụng web Thymeleaf có luồng riêng cho Librarian và Member; kiểm tra thủ công Chrome/Edge và ảnh chụp nghiệm thu còn pending. |

Ngưỡng tối thiểu chung trong Course Guidelines §4.1 là ít nhất 6 use case, 8 domain class có ý nghĩa, 2 vai trò, 3 business rule và 10 test case được ghi nhận. Mô hình hiện có 12 domain class có trách nhiệm nghiệp vụ riêng, đạt mức tham khảo cho nhóm hai người; 6 enum không được dùng để tăng số lượng class. Chi tiết mô hình và trạng thái kiểm thử nằm trong [`docs/`](docs/).

Mục tự đánh giá rubric tuần 7 được lưu tại [`docs/week7-rubric-self-check.md`](docs/week7-rubric-self-check.md). Đây là bản dựng lại ngày 6/11 từ checkpoint `v0.2`, cần cả hai thành viên xem và xác nhận trước khi dùng.

## Chạy dự án trên Visual Studio Code

### Yêu cầu

- Visual Studio Code.
- JDK 17 trở lên.
- Maven 3.9 trở lên.
- MySQL 8 trở lên nếu chạy trực tiếp; hoặc Docker Desktop đang chạy nếu dùng Compose.

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

Nếu không muốn cài MySQL riêng, có thể dùng Docker Compose để chạy ứng dụng cùng MySQL local có volume lưu bền:

```powershell
Copy-Item .env.example .env
docker compose up --build
```

Mở `http://localhost:8080`. Dừng các container bằng `docker compose down`; volume database vẫn còn để lần chạy sau dùng tiếp. Chỉ xóa dữ liệu local khi thật sự muốn làm lại từ đầu bằng `docker compose down --volumes`. Mật khẩu trong `.env.example` chỉ dành cho demo local; không dùng trên production. Hướng dẫn nghiệm thu chi tiết nằm trong [`RUN_GUIDE.md`](RUN_GUIDE.md).

Nếu máy chưa có MySQL, có thể chạy profile H2 dạng file để xem thử giao diện:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=h2"
```

H2 lưu dữ liệu tại `data/elibrary.mv.db` và giữ dữ liệu sau khi tắt/mở lại ứng dụng. Thư mục `data/` không được đẩy lên GitHub. H2 là phương án local thay thế, không thay cho kiểm tra với MySQL.

Ở database local trống, ứng dụng tạo 50 đầu sách, 51 bản sao và hai tài khoản demo. Dữ liệu mẫu chỉ được seed khi bảng sách còn trống; database đã có sách sẽ không tự nhận thêm danh mục mẫu. Nguồn danh mục và giấy phép được ghi tại [`docs/sample-catalogue-data.md`](docs/sample-catalogue-data.md).

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
| `LIBRARY_HOLIDAYS` | Danh sách ngày nghỉ `yyyy-MM-dd`, phân cách bằng dấu phẩy; các ngày này không tính phạt |

## Cấu trúc dự án

```text
src/main/java/com/elibrary/       Mã nguồn Java: controller, service, domain, repository
src/main/resources/templates/     Giao diện Thymeleaf
src/main/resources/static/        CSS và tài nguyên giao diện
src/test/java/com/elibrary/       Unit test và repository test
docs/                             Tiến độ tuần, yêu cầu, thiết kế và test case
Dockerfile                        Đóng gói ứng dụng để chạy trên Render
render.yaml                       Cấu hình Render Blueprint
compose.yaml                      Ứng dụng và MySQL local với volume lưu bền
.env.example                      Biến môi trường mẫu, chỉ dùng local
pom.xml                           Dependency và cấu hình Maven
RUN_GUIDE.md                      Hướng dẫn chạy local và bàn giao Git
```

## Quy trình cập nhật website

Ảnh Render do nhóm cung cấp ngày 22/10/2026 cho thấy service khi đó dùng branch `feature/week-6-circulation`; cấu hình hiện tại chưa được xác minh. Branch local `feature/week-7-reservations` có thay đổi Week 6–8 và cập nhật kiểm chứng đến ngày 6/11 chưa commit/push/deploy. Trước khi cập nhật website, cần kiểm tra branch Render đang theo dõi, review thay đổi rồi xác nhận deployment thành công.

Theo bàn giao của nhóm, **Ngan3112** thực hiện thao tác push GitHub. Pull request và merge review cần giữ lại làm bằng chứng cộng tác. Hiện hai tag cục bộ `v0.1` (Week 5) và `v0.2` (Week 7) trỏ tới các commit checkpoint đã có; `v1.0` chưa tạo vì phần nghiệm thu cuối và review nhóm chưa hoàn tất. Các tag này chưa được push.

## Kiểm thử và trạng thái hiện tại

Chạy bộ kiểm thử bằng:

```powershell
mvn test
```

Kết quả kiểm thử chức năng và benchmark được ghi trong [`docs/week7-status.md`](docs/week7-status.md), [`docs/test-cases-week8.md`](docs/test-cases-week8.md) và [`docs/test-cases-week9.md`](docs/test-cases-week9.md). Offline Maven test/package đạt 77/77, gồm 76 kiểm thử chức năng và một benchmark H2; lần chạy sạch mới nhất ngày 6/11/2026 đo p95 11 ms trên 10.000 tiêu đề. JAR mới trả HTTP 200 ở `/login` và `/books`; tiêu đề mẫu còn sau khi khởi động lại với cùng H2 file riêng. Docker image, MySQL/PostgreSQL, browser acceptance và dữ liệu Supabase sau restart vẫn cần xác nhận trên môi trường tương ứng.

Checklist nghiệm thu trình duyệt và môi trường thật nằm trong [`docs/week9-acceptance.md`](docs/week9-acceptance.md). Các dòng vẫn để Pending cho tới khi thành viên chạy và ghi lại kết quả thực tế.

Thanh toán/miễn phạt, báo cáo lưu thông, đăng ký member, đăng nhập bằng email và benchmark tìm kiếm 10.000 đầu sách đã được triển khai trong branch hiện tại. Kiểm thử browser, hiệu năng trên cơ sở dữ liệu production và rà soát release vẫn chưa được nghiệm thu.

## Bổ sung quy trình mượn và quản lý thành viên

- Thành viên có thể xếp hàng đặt trước khi không còn bản sao khả dụng. Bản sao được trả sẽ dành cho người đầu hàng trong 48 giờ; hết hạn thì chuyển cho người tiếp theo.
- Hạng Standard: tối đa 5 sách trong 14 ngày; Student: tối đa 3 sách trong 21 ngày; Premium: tối đa 8 sách trong 21 ngày.
- Phạt trễ là 5.000 VND mỗi ngày tính phí, tối đa 100.000 VND cho một lượt mượn. Có thể khai báo ngày nghỉ bằng biến `LIBRARY_HOLIDAYS`, danh sách ngày `yyyy-MM-dd` cách nhau bằng dấu phẩy.
- Thủ thư có thể sửa tên hiển thị, hạng thành viên và trạng thái tài khoản tại trang **Members**. Quyền Librarian không thể được cấp từ trang này.
- Hạn giữ bản sao được xử lý khi có thao tác lưu thông và bởi tác vụ nền chạy định kỳ mỗi phút.
- Tiền phạt được ghi nhận thanh toán hoặc miễn toàn bộ số dư còn lại; lịch sử lưu khoản tiền, người xử lý, thời điểm và ghi chú.
