# Dự án Quản lý Bán hàng JavaFX

Dự án ứng dụng desktop quản lý bán hàng được xây dựng bằng JavaFX và MySQL.

## Yêu cầu hệ thống

### Phần mềm cần thiết

- **Java Development Kit (JDK) 11 hoặc cao hơn**
- **Apache Maven 3.6+**
- **MySQL Server 8.0+**
- **IDE** (khuyến nghị: IntelliJ IDEA, Eclipse, hoặc NetBeans)

### Cài đặt cho Windows

#### 1. Cài đặt JDK

- Tải JDK từ [Oracle](https://www.oracle.com/java/technologies/downloads/) hoặc [OpenJDK](https://adoptium.net/)
- Chạy file installer và làm theo hướng dẫn
- Thiết lập biến môi trường:
  ```cmd
  # Mở Command Prompt với quyền Administrator
  setx JAVA_HOME "C:\Program Files\Java\jdk-11.0.xx"
  setx PATH "%PATH%;%JAVA_HOME%\bin"
  ```

#### 2. Cài đặt Maven

- Tải Maven từ [Apache Maven](https://maven.apache.org/download.cgi)
- Giải nén vào thư mục `C:\Program Files\Apache\maven`
- Thiết lập biến môi trường:
  ```cmd
  setx MAVEN_HOME "C:\Program Files\Apache\maven"
  setx PATH "%PATH%;%MAVEN_HOME%\bin"
  ```

#### 3. Cài đặt MySQL

- Tải MySQL từ [MySQL Downloads](https://dev.mysql.com/downloads/mysql/)
- Chạy installer và thiết lập:
  - Port: 3306 (mặc định)
  - Root password: để trống hoặc thiết lập password tùy ý

### Cài đặt cho macOS

#### 1. Cài đặt JDK

```bash
# Sử dụng Homebrew
brew install openjdk@11

# Thiết lập JAVA_HOME
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v11)' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc
```

#### 2. Cài đặt Maven

```bash
brew install maven
```

#### 3. Cài đặt MySQL

```bash
# Cài đặt MySQL
brew install mysql

# Khởi động MySQL
brew services start mysql

# Thiết lập MySQL (tùy chọn)
mysql_secure_installation
```

## Thiết lập dự án

### 1. Clone hoặc tải dự án

```bash
# Nếu có Git repository
git clone <repository-url>
cd qlbh-javafx

# Hoặc tải file ZIP và giải nén
```

### 2. Thiết lập Database

#### Tạo database

```sql
-- Kết nối MySQL
mysql -u root -p

-- Tạo database
CREATE DATABASE qlbh CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qlbh;

-- Tạo các bảng cần thiết
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category_id INT,
    stock_quantity INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(50) DEFAULT 'employee',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Thêm user mặc định
INSERT INTO users (username, password, full_name, role) VALUES
('admin', 'admin123', 'Administrator', 'admin');
```

### 3. Cấu hình kết nối Database

Chỉnh sửa file `src/main/java/com/sv/qlbh/dao/DatabaseConnection.java` nếu cần:

```java
private static final String URL = "jdbc:mysql://localhost:3306/qlbh?useUnicode=true&characterEncoding=UTF-8";
private static final String USER = "root";
private static final String PASSWORD = ""; // Thay đổi nếu có password
```

### 4. Build và chạy dự án

#### Sử dụng Maven (khuyến nghị)

```bash
# Di chuyển đến thư mục dự án
cd qlbh-javafx

# Compile và download dependencies
mvn clean compile

# Chạy ứng dụng
mvn javafx:run
```

#### Sử dụng IDE

**Với IntelliJ IDEA:**

1. File → Open → Chọn thư mục dự án
2. IDE sẽ tự động nhận diện Maven project
3. Đợi download dependencies
4. Right-click vào `Main.java` → Run

**Với Eclipse:**

1. File → Import → Existing Maven Projects
2. Chọn thư mục dự án
3. Right-click vào project → Run As → Java Application
4. Chọn main class: `com.sv.qlbh.Main`

**Với NetBeans:**

1. File → Open Project → Chọn thư mục dự án
2. Right-click vào project → Run

## Cấu trúc dự án

```
qlbh-javafx/
├── src/main/java/com/sv/qlbh/
│   ├── controller/          # Controllers cho các màn hình
│   │   ├── DashboardController.java
│   │   ├── LoginController.java
│   │   └── ProductCategoryController.java
│   ├── dao/                 # Data Access Objects
│   │   ├── CategoryDAO.java
│   │   ├── ProductDAO.java
│   │   ├── UserDAO.java
│   │   └── DatabaseConnection.java
│   ├── models/              # Model classes
│   │   ├── Category.java
│   │   ├── Product.java
│   │   ├── User.java
│   │   └── ...
│   ├── utils/               # Utility classes
│   │   └── SessionManager.java
│   └── Main.java            # Entry point
├── src/main/resources/
│   ├── fxml/               # FXML layout files
│   ├── styles/             # CSS stylesheets
│   └── images/             # Image resources
└── pom.xml                 # Maven configuration
```

## Tính năng chính

- 🔐 **Đăng nhập/Xác thực người dùng**
- 📊 **Dashboard tổng quan**
- 📦 **Quản lý sản phẩm**
- 🏷️ **Quản lý danh mục**
- 👥 **Quản lý khách hàng**
- 💰 **Quản lý bán hàng**
- 📈 **Báo cáo**

## Xử lý sự cố

### Lỗi "Module not found"

```bash
# Xóa target directory và build lại
mvn clean
mvn compile
mvn javafx:run
```

### Lỗi kết nối Database

1. Kiểm tra MySQL đã chạy chưa
2. Xác nhận database `qlbh` đã được tạo
3. Kiểm tra username/password trong `DatabaseConnection.java`
4. Đảm bảo port 3306 không bị block

### Lỗi JavaFX Runtime

```bash
# Nếu gặp lỗi JavaFX runtime, thử:
mvn clean javafx:run
```

### Lỗi encoding

- Đảm bảo IDE sử dụng UTF-8 encoding
- Trên Windows, có thể cần set:
  ```cmd
  set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
  ```

## Phát triển thêm

### Thêm dependencies mới

Chỉnh sửa file `pom.xml` và thêm dependency:

```xml
<dependency>
    <groupId>group.id</groupId>
    <artifactId>artifact-id</artifactId>
    <version>version</version>
</dependency>
```

### Tạo màn hình mới

1. Tạo file FXML trong `src/main/resources/fxml/`
2. Tạo Controller trong `src/main/java/com/sv/qlbh/controller/`
3. Thêm CSS styling trong `src/main/resources/styles/`

## Liên hệ và hỗ trợ

Nếu gặp vấn đề trong quá trình setup, vui lòng:

1. Kiểm tra lại các bước cài đặt
2. Xem log lỗi trong console
3. Tham khảo tài liệu JavaFX và Maven

---

**Chú ý:** Đảm bảo tất cả phần mềm đều được cài đặt với quyền Administrator (Windows) hoặc sudo (macOS) khi cần thiết.
