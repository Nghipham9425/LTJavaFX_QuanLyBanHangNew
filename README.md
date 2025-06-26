# 🏪 Hệ thống Quản lý Bán hàng JavaFX

Ứng dụng desktop quản lý bán hàng toàn diện được xây dựng bằng JavaFX và MySQL, áp dụng kiến trúc MVC và pattern DAO.

## 📋 Tổng quan

Hệ thống quản lý bán hàng với giao diện hiện đại, hỗ trợ:

- Quản lý khách hàng với hệ thống phân hạng tự động
- Quản lý sản phẩm và danh mục
- Quản lý nhà cung cấp
- Hệ thống POS/Bán hàng (đang phát triển)
- Dashboard thống kê
- Xác thực người dùng theo vai trò

## 🚀 Tính năng đã hoàn thành

### ✅ **Quản lý khách hàng với phân hạng tự động**

- **Hệ thống phân hạng khách hàng:**
  - 🥉 **Normal** (< 1M VND)
  - 🥈 **Silver** (1-5M VND) - Giảm giá 5%
  - 🥇 **Gold** (5-20M VND) - Giảm giá 10%
  - 💎 **Diamond** (> 20M VND) - Giảm giá 15%
- Tự động tính điểm và tổng chi tiêu
- CRUD hoàn chình với validation

### ✅ **Quản lý sản phẩm & danh mục**

- Quản lý danh mục sản phẩm
- Quản lý thông tin sản phẩm
- Liên kết sản phẩm với danh mục

### ✅ **Quản lý nhà cung cấp**

- Thông tin chi tiết nhà cung cấp
- Người liên hệ và thông tin hợp đồng
- CRUD hoàn chỉnh

### ✅ **Dashboard tổng quan**

- Thống kê tổng quan
- Menu điều hướng hoàn chỉnh
- Cards thông tin nhanh

### ✅ **Hệ thống xác thực**

- Đăng nhập theo vai trò
- Quản lý session
- Bảo mật mật khẩu

## 🔧 Công nghệ sử dụng

- **Java**: 21+ (JDK 21)
- **JavaFX**: 21.0.2
- **Build Tool**: Maven 3.6+
- **Database**: MySQL 8.0+
- **Architecture**: MVC với DAO Pattern
- **Exception Handling**: Specific SQLException handling
- **UI Framework**: JavaFX với FXML

## 📦 Yêu cầu hệ thống

### Phần mềm cần thiết

- **JDK 21** hoặc cao hơn
- **Apache Maven 3.6+**
- **XAMPP** (bao gồm Apache + MySQL + phpMyAdmin)
- **IDE**: NetBeans, IntelliJ IDEA, hoặc Eclipse

### Cài đặt nhanh (Windows)

#### 1. Cài đặt JDK 21

```cmd
# Tải từ Oracle hoặc OpenJDK
# Thiết lập biến môi trường
setx JAVA_HOME "C:\Program Files\Java\jdk-21"
setx PATH "%PATH%;%JAVA_HOME%\bin"
```

#### 2. Cài đặt Maven

```cmd
# Tải Maven từ Apache
setx MAVEN_HOME "C:\Program Files\Apache\maven"
setx PATH "%PATH%;%MAVEN_HOME%\bin"
```

#### 3. Cài đặt XAMPP

```cmd
# Tải XAMPP từ https://www.apachefriends.org/
# Chạy installer và cài đặt
# Khởi động Apache và MySQL từ XAMPP Control Panel
```

## 🗄️ Thiết lập Database

### Tạo database và schema

#### Cách 1: Import file SQL (Khuyến nghị)

```bash
# Sử dụng MySQL Command Line (XAMPP)
mysql -u root < database_schema.sql
```

#### Cách 2: Sử dụng phpMyAdmin

1. Mở trình duyệt → `http://localhost/phpmyadmin`
2. Tạo database mới: `qlbh`
3. Chọn database `qlbh` → tab **Import**
4. Chọn file `database_schema.sql` → Click **Go**

#### Cách 3: MySQL Command Line thủ công

```sql
-- Kết nối MySQL (XAMPP thường không có password)
mysql -u root

-- Copy toàn bộ nội dung file database_schema.sql và paste vào
```

#### ✅ Database bao gồm:

- **15 tables** với relationships hoàn chỉnh
- **Sample data** sẵn sàng để test
- **4 user accounts** với roles khác nhau
- **22 products** trong 7 categories
- **8 customers** với group assignments
- **Promotions & Vouchers** để test discount system

### Cấu hình kết nối

File: `src/main/java/com/sv/qlbh/dao/DatabaseConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/qlbh?useUnicode=true&characterEncoding=UTF-8";
private static final String USER = "root";
private static final String PASSWORD = ""; // Thay đổi nếu có password
```

## 🚀 Chạy ứng dụng

### Sử dụng Maven

```bash
# Clone project
git clone <repository-url>
cd qlbh-javafx

# Build và chạy
mvn clean compile
mvn javafx:run
```

### Sử dụng NetBeans

1. File → Open Project → Chọn thư mục dự án
2. Right-click project → Clean and Build
3. Right-click project → Run

### Tài khoản mặc định

| Username     | Password        | Vai trò    | Mô tả                             |
| ------------ | --------------- | ---------- | --------------------------------- |
| `admin`      | `admin123`      | ADMIN      | Administrator - Quản trị hệ thống |
| `staff`      | `staff123`      | STAFF      | Nhân viên bán hàng                |
| `accountant` | `accountant123` | ACCOUNTANT | Kế toán                           |
| `warehouse`  | `warehouse123`  | WAREHOUSE  | Thủ kho                           |

## 📁 Cấu trúc dự án

```
qlbh-javafx/
├── src/main/java/com/sv/qlbh/
│   ├── controller/              # Controllers
│   │   ├── CustomerController.java      # ✅ Hoàn thành
│   │   ├── DashboardController.java     # ✅ Hoàn thành
│   │   ├── LoginController.java         # ✅ Hoàn thành
│   │   ├── ProductCategoryController.java # ✅ Hoàn thành
│   │   ├── SupplierController.java      # ✅ Hoàn thành
│   │   └── SalesController.java         # 🔄 Đang phát triển
│   ├── dao/                     # Data Access Objects
│   │   ├── CustomerDAO.java & CustomerDAOImpl.java  # ✅
│   │   ├── SupplierDAO.java & SupplierDAOImpl.java  # ✅
│   │   ├── CategoryDAO.java & CategoryDAOImpl.java  # ✅
│   │   ├── ProductDAO.java & ProductDAOImpl.java    # ✅
│   │   ├── UserDAO.java & UserDAOImpl.java          # ✅
│   │   ├── OrderDAO.java & OrderDetailDAO.java     # 🔄 Interface sẵn sàng
│   │   └── DatabaseConnection.java      # ✅ Hoàn thành
│   ├── models/                  # Model classes
│   │   ├── Customer.java        # ✅ Với CustomerGroup enum
│   │   ├── Supplier.java        # ✅ Với contact_person
│   │   ├── Category.java        # ✅ Hoàn thành
│   │   ├── Product.java         # ✅ Hoàn thành
│   │   ├── User.java           # ✅ Hoàn thành
│   │   ├── Order.java & OrderDetail.java # 🔄 Sẵn sàng cho POS
│   │   └── ... (Promotion, Voucher, etc.)
│   ├── utils/
│   │   └── SessionManager.java  # ✅ Quản lý phiên đăng nhập
│   └── Main.java               # ✅ Entry point
├── src/main/resources/
│   ├── fxml/                   # FXML layouts
│   │   ├── Dashboard.fxml      # ✅ Menu hoàn chỉnh
│   │   ├── Customer.fxml       # ✅ Table view + forms
│   │   ├── Supplier.fxml       # ✅ CRUD hoàn chỉnh
│   │   ├── ProductCategory.fxml # ✅ Hoàn thành
│   │   ├── Login.fxml          # ✅ Hoàn thành
│   │   └── Sales.fxml          # 🔄 POS interface đã tạo
│   ├── styles/                 # CSS files
│   │   ├── dashboard.css       # ✅ Modern UI
│   │   ├── customer.css        # ✅ Responsive design
│   │   ├── supplier.css        # ✅ Professional styling
│   │   └── ...
│   └── images/                 # Icons & logos
└── pom.xml                     # ✅ JavaFX 21.0.2, Java 21
```

## 🎯 Lộ trình phát triển

### 🔄 Đang phát triển

1. **💰 Hệ thống POS/Bán hàng** (Ưu tiên cao)
   - Giao diện Sales.fxml đã sẵn sàng
   - Cần implement SalesController.java
   - Tích hợp với OrderDAO & OrderDetailDAO

### 📅 Kế hoạch tiếp theo

2. **📋 Quản lý đơn hàng**
3. **📦 Quản lý kho**
4. **🎫 Hệ thống khuyến mãi & voucher**
5. **📊 Báo cáo thống kê**
6. **⏰ Quản lý ca làm việc**

## 🛠️ Tính năng nổi bật

### 🎯 **Hệ thống phân hạng khách hàng tự động**

- Tự động phân loại dựa trên tổng chi tiêu
- Chiết khấu theo từng hạng
- Tích lũy điểm thưởng

### 🔒 **Exception Handling chuyên nghiệp**

- Xử lý SQLException cụ thể (error codes 1062, 1452, 1451)
- Thông báo lỗi tiếng Việt thân thiện
- Logging chi tiết cho debug

### 🎨 **UI/UX hiện đại**

- Responsive design với CSS
- Icons đẹp mắt
- Navigation menu trực quan

## 🐛 Xử lý sự cố

### Lỗi build Maven

```bash
mvn clean
mvn compile
mvn javafx:run
```

### Lỗi database connection

1. **XAMPP**: Kiểm tra Apache và MySQL đã start trong XAMPP Control Panel
2. Xác nhận database `qlbh` đã tạo trong phpMyAdmin
3. Kiểm tra username/password trong DatabaseConnection.java
   - XAMPP default: user=`root`, password=`""` (empty)
4. Đảm bảo port 3306 available và không conflict

### Lỗi JavaFX module

```bash
# Đảm bảo Java 21+ và JavaFX 21.0.2
mvn clean install
```

## 📊 Thống kê dự án

- **📁 Total Files**: 50+ files
- **💻 Lines of Code**: 5000+ lines
- **🗄️ Database Tables**: 15+ tables
- **✅ Completion**: ~70% core features
- **🔄 Active Development**: POS System

## 🤝 Đóng góp

Dự án được phát triển theo chuẩn:

- **Clean Code** principles
- **MVC Architecture**
- **DAO Pattern**
- **Exception Handling** best practices
- **Vietnamese UI/UX**

## 📝 Ghi chú phiên bản

### v1.0 (Current)

- ✅ Customer Management với auto-grouping
- ✅ Product & Category Management
- ✅ Supplier Management
- ✅ Dashboard với navigation hoàn chỉnh
- ✅ Authentication system
- 🔄 POS System (đang phát triển)

---

**🎯 Mục tiêu**: Xây dựng hệ thống quản lý bán hàng hoàn chỉnh, chuyên nghiệp cho doanh nghiệp Việt Nam
