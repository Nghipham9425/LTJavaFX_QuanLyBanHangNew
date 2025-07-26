# Quản Lý Bán Hàng JavaFX

## Tổng quan

Đây là dự án đồ án môn học xây dựng ứng dụng quản lý bán hàng sử dụng JavaFX. Ứng dụng hỗ trợ quản lý ca làm việc, nhân viên, và các chức năng cơ bản cho một hệ thống bán hàng nhỏ. Giao diện thân thiện, dễ sử dụng, phù hợp cho sinh viên học tập và mở rộng.

## Chức năng chính

- **Quản lý ca làm việc:**
  - Thêm, sửa, xóa ca làm việc
  - Kết thúc ca (chuyển trạng thái sang COMPLETED)
  - Hiển thị danh sách ca, lọc và chọn ca để thao tác
- **Quản lý nhân viên:**
  - Thêm, sửa, xóa nhân viên
  - Đăng nhập, phân quyền (nếu có)
- **Giao diện trực quan:**
  - Sử dụng JavaFX TableView, ComboBox, Spinner, DatePicker
  - Thông báo, xác nhận thao tác bằng AlertUtils
- **Kết nối cơ sở dữ liệu MySQL:**
  - Lưu trữ thông tin ca làm việc, nhân viên, ...

## Cấu trúc thư mục

- `src/main/java/com/sv/qlbh/controller/` - Controller cho các màn hình giao diện
- `src/main/java/com/sv/qlbh/dao/` - Data Access Object (DAO) truy xuất dữ liệu
- `src/main/java/com/sv/qlbh/models/` - Các class model (Shift, User...)
- `src/main/resources/fxml/` - File FXML giao diện JavaFX
- `src/main/resources/styles/` - File CSS giao diện
- `src/main/java/com/sv/qlbh/utils/` - Các class tiện ích (AlertUtils...)

## Hướng dẫn cài đặt & chạy

1. **Clone dự án:**
   ```
   git clone <repo-url>
   ```
2. **Cấu hình database:**
   - Tạo database MySQL, ví dụ tên `qlbh`.
   - Tạo các bảng cần thiết. Ví dụ bảng `users` và `shifts`:

```sql
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  full_name VARCHAR(100),
  role VARCHAR(20),
  status BOOLEAN DEFAULT TRUE
);

CREATE TABLE shifts (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME,
  status ENUM('ACTIVE', 'COMPLETED') DEFAULT 'ACTIVE',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

- Sửa thông tin kết nối trong file `DatabaseConnection.java` cho phù hợp với user/password/database của bạn.

3. **Chạy ứng dụng:**
   - Mở project bằng NetBeans/IntelliJ/Eclipse
   - Build project (nếu cần)
   - Chạy file `Main.java` hoặc cấu hình launcher JavaFX

## Hướng dẫn sử dụng

- **Thêm ca làm việc:**
  - Chọn nhân viên, ngày giờ bắt đầu/kết thúc, nhấn "Thêm"
- **Cập nhật ca:**
  - Chọn ca trong bảng, chỉnh sửa thông tin, nhấn "Cập nhật"
- **Xóa ca:**
  - Chọn ca trong bảng, nhấn "Xóa"
- **Kết thúc ca:**
  - Chọn ca trong bảng, nhấn "Kết thúc ca"
- **Làm mới:**
  - Xóa dữ liệu trên form và reload lại danh sách ca

## Công nghệ sử dụng

- Java 8+
- JavaFX
- MySQL
- JDBC
- Maven/Gradle (nếu có)

## Đóng góp

- Fork repo, tạo branch mới, commit và gửi pull request.
- Mọi ý kiến đóng góp, báo lỗi hoặc đề xuất chức năng mới đều được hoan nghênh!

## Tác giả

- Họ tên: [Điền tên bạn]
- Trường: [Tên trường]
- Email: [Email liên hệ]

---

Chúc bạn sử dụng và phát triển dự án hiệu quả!
