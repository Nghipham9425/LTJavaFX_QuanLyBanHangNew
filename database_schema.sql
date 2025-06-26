-- Tạo database
CREATE DATABASE qlbh;
USE qlbh;

-- Bảng users
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'STAFF', 'ACCOUNTANT', 'WAREHOUSE') NOT NULL,
    status BOOLEAN DEFAULT TRUE
);

-- Bảng suppliers (nhà cung cấp)
CREATE TABLE suppliers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    status BOOLEAN DEFAULT TRUE
);

-- Bảng categories
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Bảng products
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category_id INT,
    supplier_id INT,
    barcode VARCHAR(50) UNIQUE,
    price DECIMAL(15,2) NOT NULL,
    cost_price DECIMAL(15,2) NOT NULL,
    stock INT DEFAULT 0,
    status BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

-- Bảng customer_groups (nhóm khách hàng)
CREATE TABLE customer_groups (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    discount_percent DECIMAL(5,2) DEFAULT 0,
    description TEXT
);

-- Bảng customers
CREATE TABLE customers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    group_id INT,
    points INT DEFAULT 0,
    total_spent DECIMAL(15,2) DEFAULT 0,
    status BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (group_id) REFERENCES customer_groups(id)
);

-- Bảng promotions (khuyến mãi)
CREATE TABLE promotions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    discount_type ENUM('PERCENT', 'AMOUNT') NOT NULL,
    discount_value DECIMAL(15,2) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    status BOOLEAN DEFAULT TRUE
);

-- Bảng vouchers (mã giảm giá)
CREATE TABLE vouchers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_type ENUM('PERCENT', 'AMOUNT') NOT NULL,
    discount_value DECIMAL(15,2) NOT NULL,
    min_order_value DECIMAL(15,2) DEFAULT 0,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    status BOOLEAN DEFAULT TRUE
);

-- Bảng orders (đã cập nhật cho VNPay)
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    user_id INT NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    discount_amount DECIMAL(15,2) DEFAULT 0,
    final_amount DECIMAL(15,2) NOT NULL,
    payment_method ENUM('CASH', 'VNPAY') NOT NULL, -- Updated: Chỉ giữ CASH và VNPAY
    status ENUM('PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED') DEFAULT 'PROCESSING', -- Updated: Thêm PROCESSING, FAILED cho VNPay
    note TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Bảng order_details
CREATE TABLE order_details (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    discount_amount DECIMAL(15,2) DEFAULT 0,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Bảng inventory (tồn kho)
CREATE TABLE inventory (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    type ENUM('IN', 'OUT') NOT NULL,
    reference_id INT,
    reference_type VARCHAR(50),
    note TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Bảng shifts (ca làm việc)
CREATE TABLE shifts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    status ENUM('ACTIVE', 'COMPLETED') DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Bảng reports (báo cáo)
CREATE TABLE reports (
    id INT PRIMARY KEY AUTO_INCREMENT,
    report_type ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY') NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    total_revenue DECIMAL(15,2) NOT NULL,
    total_orders INT NOT NULL,
    total_customers INT NOT NULL,
    total_products_sold INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Bảng report_details (chi tiết báo cáo)
CREATE TABLE report_details (
    id INT PRIMARY KEY AUTO_INCREMENT,
    report_id INT NOT NULL,
    category_id INT,
    product_id INT,
    quantity INT NOT NULL,
    revenue DECIMAL(15,2) NOT NULL,
    profit DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (report_id) REFERENCES reports(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Tạo index cho performance (VNPay optimization)
CREATE INDEX idx_orders_payment_method ON orders(payment_method);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- Tạo tài khoản mặc định
INSERT INTO users (username, password, full_name, role) VALUES 
('admin', 'admin123', 'Administrator', 'ADMIN'),
('staff', 'staff123', 'Nhân viên bán hàng', 'STAFF'),
('accountant', 'accountant123', 'Kế toán', 'ACCOUNTANT'),
('warehouse', 'warehouse123', 'Thủ kho', 'WAREHOUSE');

-- Thêm nhà cung cấp
INSERT INTO suppliers (name, phone, email) VALUES 
('Công ty TNHH ABC', '0123456789', 'abc@email.com'),
('Công ty XYZ', '0987654321', 'xyz@email.com'),
('Công ty DEF', '0912345678', 'def@email.com');

-- Thêm danh mục
INSERT INTO categories (name, description) VALUES 
('Thực phẩm tươi sống', 'Rau củ, thịt cá, trứng sữa'),
('Đồ uống', 'Nước ngọt, nước trái cây, trà sữa'),
('Bánh kẹo', 'Bánh, kẹo, snack'),
('Đồ gia dụng', 'Dụng cụ nhà bếp, đồ dùng gia đình'),
('Vệ sinh cá nhân', 'Sữa tắm, dầu gội, kem đánh răng'),
('Đồ đông lạnh', 'Thực phẩm đông lạnh, kem'),
('Đồ khô', 'Mì gói, gia vị, đồ hộp');

-- Thêm nhóm khách hàng
INSERT INTO customer_groups (name, discount_percent, description) VALUES 
('Thường', 0, 'Khách hàng thường'),
('Bạc', 5, 'Khách hàng bạc'),
('Vàng', 10, 'Khách hàng vàng'),
('Kim cương', 15, 'Khách hàng kim cương');

-- Thêm sản phẩm
INSERT INTO products (name, category_id, supplier_id, barcode, price, cost_price, stock) VALUES 
-- Thực phẩm tươi sống
('Cá hồi', 1, 1, 'CH001', 350000, 300000, 20),
('Cá basa', 1, 1, 'CB001', 120000, 100000, 30),
('Rau cải', 1, 2, 'RC001', 25000, 20000, 100),
('Trứng gà', 1, 2, 'TG001', 35000, 30000, 200),

-- Đồ uống
('Coca Cola', 2, 3, 'CC001', 12000, 10000, 100),
('Pepsi', 2, 3, 'PS001', 12000, 10000, 100),
('Trà sữa trân châu', 2, 1, 'TS001', 35000, 25000, 50),
('Nước cam', 2, 2, 'NC001', 15000, 12000, 80),
('Nước ép dưa hấu', 2, 2, 'ND001', 25000, 20000, 40),
('Trà đào', 2, 1, 'TD001', 20000, 15000, 60),

-- Bánh kẹo
('Bánh Oreo', 3, 3, 'BO001', 25000, 20000, 50),
('Kẹo mút', 3, 3, 'KM001', 5000, 3000, 200),
('Snack khoai tây', 3, 3, 'SK001', 15000, 12000, 100),

-- Đồ gia dụng
('Bát đĩa', 4, 1, 'BD001', 45000, 35000, 30),
('Nồi cơm điện', 4, 1, 'NCD001', 850000, 700000, 10),
('Bình giữ nhiệt', 4, 1, 'BN001', 250000, 200000, 20),

-- Vệ sinh cá nhân
('Dầu gội đầu', 5, 2, 'DG001', 85000, 70000, 40),
('Sữa tắm', 5, 2, 'ST001', 95000, 80000, 40),
('Kem đánh răng', 5, 2, 'KR001', 45000, 35000, 50),

-- Đồ đông lạnh
('Kem que', 6, 3, 'KQ001', 5000, 3000, 200),
('Thịt đông lạnh', 6, 1, 'TL001', 120000, 100000, 30),
('Cá đông lạnh', 6, 1, 'CL001', 100000, 80000, 25),

-- Đồ khô
('Mì gói', 7, 3, 'MG001', 5000, 3000, 300),
('Nước mắm', 7, 2, 'NM001', 45000, 35000, 40),
('Đường', 7, 2, 'DG002', 15000, 12000, 100);

-- Thêm khách hàng
INSERT INTO customers (name, phone, email, group_id) VALUES 
('Chị Hằng', '0123456789', 'hang@email.com', 1),
('Anh Minh', '0987654321', 'minh@email.com', 2),
('Chị Linh', '0912345678', 'linh@email.com', 3),
('Anh Tuấn', '0898765432', 'tuan@email.com', 4),
('Chị Mai', '0777777777', 'mai@email.com', 1),
('Anh Dũng', '0666666666', 'dung@email.com', 2),
('Chị Thảo', '0555555555', 'thao@email.com', 3),
('Anh Phúc', '0444444444', 'phuc@email.com', 4),
('Anh Minh - 0987654321', '0987654321', NULL, 1), -- Updated: Thêm format tương tự trong app
('Chị Linh - 0666666666', '0666666666', NULL, 2);

-- Thêm khuyến mãi
INSERT INTO promotions (name, discount_type, discount_value, start_date, end_date) VALUES 
('Khuyến mãi tháng 5', 'PERCENT', 10, '2025-05-01 00:00:00', '2025-05-31 23:59:59'),
('Giảm giá sốc', 'AMOUNT', 50000, '2025-05-15 00:00:00', '2025-05-20 23:59:59');

-- Thêm mã giảm giá
INSERT INTO vouchers (code, discount_type, discount_value, min_order_value, start_date, end_date) VALUES
('SUMMER2025', 'PERCENT', 15, 200000, '2025-05-01 00:00:00', '2025-05-31 23:59:59'),
('WELCOME50K', 'AMOUNT', 50000, 100000, '2025-05-01 00:00:00', '2025-12-31 23:59:59');

-- Sample orders for testing (Optional)
INSERT INTO orders (customer_id, user_id, total_amount, discount_amount, final_amount, payment_method, status, note) VALUES
(1, 1, 100000, 0, 100000, 'CASH', 'COMPLETED', 'Test order 1'),
(2, 1, 200000, 20000, 180000, 'VNPAY', 'PROCESSING', 'Test VNPay order'); 