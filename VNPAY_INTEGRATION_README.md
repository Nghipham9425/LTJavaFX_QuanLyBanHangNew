# VNPay Integration cho JavaFX POS System

## Tổng quan

Project này đã được tích hợp VNPay để hỗ trợ thanh toán trực tuyến qua ngân hàng. Hệ thống hỗ trợ cả thanh toán tiền mặt và thanh toán VNPay.

## Các file đã được tạo/cập nhật

### 1. Database Changes

- `database_fix.sql`: Cập nhật database schema để hỗ trợ VNPay
  - Payment method: `ENUM('CASH', 'VNPAY')`
  - Status: `ENUM('PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED')`

### 2. VNPay Core Classes

- `VNPayConfig.java`: Cấu hình VNPay (TMN Code, Secret Key, URLs)
- `VNPayService.java`: Service chính để tạo payment URL và xử lý response
- `VNPayReturnHandler.java`: HTTP server đơn giản để nhận callback từ VNPay

### 3. DAO Updates

- `OrderDAO.java`: Tạo lại với hỗ trợ VNPay
- `OrderDetailDAO.java`: Tạo lại để quản lý chi tiết đơn hàng

### 4. Models Updates

- `OrderDetail.java`: Thêm trường productName và phương thức getSubtotal()

### 5. POS System

- `POSController.java`: Controller chính cho hệ thống POS với VNPay
- `POS.fxml`: Giao diện POS bán hàng

### 6. Application Updates

- `Main.java`: Khởi động VNPay Return Handler khi ứng dụng chạy

## Cách setup VNPay

### 1. Đăng ký VNPay Sandbox

1. Truy cập: https://sandbox.vnpayment.vn/
2. Đăng ký tài khoản merchant test
3. Lấy thông tin:
   - `TMN Code` (Mã merchant)
   - `Secret Key` (Khóa bí mật)

### 2. Cập nhật cấu hình

Mở file `src/main/java/com/sv/qlbh/utils/VNPayConfig.java` và cập nhật:

```java
public static final String VNP_TMN_CODE = "YOUR_TMN_CODE"; // Thay bằng mã của bạn
public static final String SECRET_KEY = "YOUR_SECRET_KEY"; // Thay bằng secret key của bạn
```

### 3. Cập nhật database

Chạy script `database_fix.sql` để cập nhật database:

```sql
mysql -u your_username -p your_database < database_fix.sql
```

## Cách sử dụng

### 1. Chạy ứng dụng

```bash
mvn clean javafx:run
```

Ứng dụng sẽ tự động khởi động VNPay Return Handler trên port 8080.

### 2. Sử dụng POS

1. Đăng nhập vào hệ thống
2. Mở màn hình POS
3. Thêm sản phẩm vào giỏ hàng
4. Chọn phương thức thanh toán:
   - **Tiền mặt**: Đơn hàng sẽ hoàn thành ngay lập tức
   - **VNPay**: Hệ thống sẽ mở trình duyệt để thanh toán

### 3. Quy trình VNPay Payment

1. Khách hàng chọn VNPay làm phương thức thanh toán
2. Hệ thống tạo đơn hàng với status "PROCESSING"
3. Trình duyệt mở trang VNPay để thanh toán
4. Khách hàng thực hiện thanh toán trên VNPay
5. VNPay redirect về `http://localhost:8080/vnpay-return`
6. Hệ thống tự động cập nhật trạng thái đơn hàng:
   - Thành công: "COMPLETED"
   - Thất bại: "FAILED"

## Test VNPay Sandbox

### Thông tin test cho VNPay Sandbox:

- **Ngân hàng**: NCB
- **Số thẻ**: 9704198526191432198
- **Tên chủ thẻ**: NGUYEN VAN A
- **Ngày hết hạn**: 07/15
- **Mật khẩu OTP**: 123456

## Cấu trúc trạng thái đơn hàng

| Trạng thái | Mô tả                                      |
| ---------- | ------------------------------------------ |
| PROCESSING | Đơn hàng đang xử lý (VNPay chờ thanh toán) |
| COMPLETED  | Đơn hàng hoàn thành                        |
| FAILED     | Thanh toán thất bại                        |
| CANCELLED  | Đơn hàng bị hủy                            |

## Troubleshooting

### 1. VNPay không callback về

- Kiểm tra firewall, đảm bảo port 8080 mở
- Kiểm tra `VNP_RETURN_URL` trong VNPayConfig.java
- Đảm bảo máy có thể truy cập từ internet (nếu test production)

### 2. Lỗi chữ ký không hợp lệ

- Kiểm tra `SECRET_KEY` có đúng không
- Kiểm tra `TMN_CODE` có đúng không

### 3. Đơn hàng không cập nhật trạng thái

- Kiểm tra database connection
- Xem log trong console để debug

## API Reference

### VNPayService.createPaymentUrl()

Tạo URL thanh toán VNPay từ đơn hàng.

### VNPayService.processPaymentReturn()

Xử lý và xác thực response từ VNPay.

### VNPayReturnHandler

HTTP server đơn giản chạy trên port 8080 để nhận callback từ VNPay.

## Security Notes

- **Chỉ sử dụng Sandbox cho development**
- **Không commit SECRET_KEY vào git**
- **Sử dụng HTTPS cho production**
- **Validate tất cả input từ VNPay**

## Support

Nếu gặp vấn đề, vui lòng:

1. Kiểm tra log trong console
2. Xem VNPay documentation: https://sandbox.vnpayment.vn/apis/
3. Kiểm tra network connectivity và firewall

---

**Lưu ý quan trọng**: Đây là integration cho môi trường development/test. Để deploy production, cần:

- Đăng ký VNPay production account
- Cập nhật URLs thành production URLs
- Implement proper security measures
- Sử dụng HTTPS
