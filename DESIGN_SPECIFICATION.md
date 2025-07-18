# THIẾT KẾ HỆ THỐNG QUẢN LÝ BÁN HÀNG

## 2. THIẾT KẾ DỮ LIỆU

### 2.1 Sơ đồ Logic Dữ Liệu (ERD)

**File:** `ERD_logical_design.puml`

Hệ thống được thiết kế với 10 thực thể chính:

#### 2.1.1 Thực thể chính:

- **User**: Quản lý người dùng hệ thống (Admin, Staff, Accountant, Warehouse)
- **Customer**: Khách hàng với hệ thống điểm thưởng và phân loại
- **Product**: Sản phẩm với barcode, giá bán, giá nhập, tồn kho
- **Category**: Danh mục sản phẩm
- **Supplier**: Nhà cung cấp
- **Order**: Đơn hàng hỗ trợ VNPay và tiền mặt
- **OrderDetail**: Chi tiết đơn hàng
- **Inventory**: Quản lý nhập/xuất kho
- **Promotion**: Khuyến mãi theo phần trăm hoặc số tiền
- **Shift**: Ca làm việc của nhân viên

#### 2.1.2 Mối quan hệ chính:

- Customer (0..n) → Order: Khách hàng có thể đặt nhiều đơn hàng (hoặc NULL cho khách vãng lai)
- User (1..n) → Order: Nhân viên tạo đơn hàng
- Order (1..n) → OrderDetail: Đơn hàng chứa nhiều chi tiết
- Product (n..1) → Category: Sản phẩm thuộc danh mục
- Product (n..1) → Supplier: Sản phẩm được cung cấp bởi nhà cung cấp
- Product (1..n) → Inventory: Theo dõi nhập/xuất kho

#### 2.1.3 Ràng buộc dữ liệu:

- `price >= cost_price`: Giá bán không được thấp hơn giá nhập
- `stock >= 0`: Tồn kho không được âm
- `quantity > 0`: Số lượng trong đơn hàng phải dương
- Barcode là duy nhất trong hệ thống

### 2.2 Thiết kế Database

**Schema File:** `database_schema.sql`

#### 2.2.1 Đặc điểm kỹ thuật:

- **Database Engine**: MySQL 8.0+
- **Character Set**: UTF-8
- **Storage Engine**: InnoDB (hỗ trợ transactions)
- **Indexing**: Tối ưu cho các truy vấn thường xuyên

#### 2.2.2 Indexes được tạo:

```sql
-- Performance indexes
CREATE INDEX idx_customers_phone ON customers(phone);
CREATE INDEX idx_products_barcode ON products(barcode);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_payment_method ON orders(payment_method);
```

## 3. THIẾT KẾ GIAO DIỆN

### 3.1 Kiến trúc giao diện

Hệ thống sử dụng **JavaFX** với kiến trúc **MVC (Model-View-Controller)**:

#### 3.1.1 Cấu trúc thư mục giao diện:

```
src/main/resources/
├── fxml/           # File layout giao diện
│   ├── Login.fxml
│   ├── Dashboard.fxml
│   ├── Customer.fxml
│   ├── ProductCategory.fxml
│   ├── Supplier.fxml
│   ├── POS.fxml
│   └── Order.fxml
├── styles/         # CSS styling
│   ├── login.css
│   ├── dashboard.css
│   ├── customer.css
│   ├── pos.css
│   └── order.css
└── images/         # Icons và hình ảnh
    ├── logo_ic.png
    ├── icon_*.png
    └── ...
```

### 3.2 Thiết kế các màn hình chính

#### 3.2.1 Màn hình Đăng nhập (Login.fxml)

**Chức năng:**

- Xác thực người dùng với username/password
- Kiểm tra role và quyền truy cập
- Chuyển hướng đến Dashboard sau khi đăng nhập thành công

**Thành phần giao diện:**

- TextField: Username input
- PasswordField: Password input (ẩn ký tự)
- Button: Đăng nhập
- Label: Hiển thị thông báo lỗi
- Logo: Branding hệ thống

**Validation:**

- Kiểm tra trống username/password
- Hiển thị thông báo lỗi rõ ràng
- Tự động focus vào field tiếp theo

#### 3.2.2 Màn hình Dashboard (Dashboard.fxml)

**Layout:** Sidebar + Content Area

**Sidebar Navigation:**

- 🏠 Dashboard
- 📦 Quản lý sản phẩm
- 👥 Quản lý khách hàng
- 🏢 Quản lý nhà cung cấp
- 📋 Quản lý đơn hàng
- 📊 Quản lý kho
- 💰 Bán hàng (POS)
- 📈 Báo cáo
- ⚙️ Cài đặt

**Content Area:**

- Welcome message với role người dùng
- Dashboard statistics (hiện tại dùng static data)
- Quick access buttons
- Recent activities

**Features:**

- Responsive layout
- Role-based menu visibility
- Dynamic content loading
- Smooth transitions

#### 3.2.3 Màn hình Quản lý Khách hàng (Customer.fxml)

**Layout:** Table + Form

**Thành phần chính:**

- **Search Bar**: Tìm kiếm theo tên
- **TableView**: Hiển thị danh sách khách hàng
  - Columns: ID, Tên, SĐT, Email, Nhóm, Điểm, Tổng chi tiêu, Trạng thái
- **Form Panel**: Thêm/sửa khách hàng
  - TextFields: Tên, SĐT, Email
  - CheckBox: Trạng thái hoạt động
  - Buttons: Thêm, Sửa, Xóa, Làm mới

**Business Logic:**

- Auto-grouping khách hàng theo total_spent:
  - 🥉 Khách hàng thường: < 1M
  - 🥈 Khách hàng bạc: 1M - 10M
  - 🥇 Khách hàng vàng: 10M - 50M
  - 💎 Khách hàng kim cương: > 50M
- Validation số điện thoại và email
- Soft delete (deactivate) thay vì hard delete

#### 3.2.4 Màn hình Quản lý Sản phẩm (ProductCategory.fxml)

**Layout:** Split view (Category + Product)

**Category Section:**

- TableView với columns: ID, Tên, Mô tả
- Form: Tên danh mục, Mô tả
- CRUD operations

**Product Section:**

- TableView với columns: ID, Tên, Barcode, Giá, Tồn kho, Danh mục, NCC, Trạng thái
- Form với:
  - TextFields: Tên, Barcode, Giá bán, Giá nhập, Tồn kho
  - ComboBox: Danh mục, Nhà cung cấp
  - CheckBox: Trạng thái
- Advanced features:
  - Barcode validation (unique)
  - Price validation (bán >= nhập)
  - Stock management
  - Smart delete (deactivate khi có ràng buộc)

#### 3.2.5 Màn hình POS (POS.fxml)

**Layout:** Product List + Cart + Checkout

**Product Section:**

- Search bar với real-time filtering
- TableView hiển thị: Tên, Giá, Tồn kho
- Double-click để thêm vào giỏ hàng

**Cart Section:**

- TableView: Sản phẩm, Số lượng, Giá, Thành tiền
- Editable quantity inline
- Remove item functionality
- Auto-calculate totals

**Checkout Panel:**

- Customer selection (ComboBox):
  - "Khách vãng lai" (default)
  - Active customers only
- Payment method (RadioButton):
  - 💵 Tiền mặt
  - 🏦 VNPay
- Total display: Subtotal, Discount, Final
- Checkout button

**Business Features:**

- Real-time inventory checking
- Customer status validation
- VNPay integration với browser redirect
- Automatic inventory deduction
- Customer points calculation

#### 3.2.6 Màn hình Quản lý Đơn hàng (Order.fxml)

**Layout:** Filter + Table + Details

**Filter Section:**

- Search field: Theo Order ID hoặc tên khách hàng
- Status filter: ALL, PROCESSING, COMPLETED, FAILED, CANCELLED

**Orders Table:**

- Columns: #ID, Khách hàng, Ngày tạo, Trạng thái, Tổng tiền
- Real-time status display
- Sort by date (newest first)

**Actions:**

- View Details: Popup với order details và product list
- Cancel Order: Chỉ cho orders PROCESSING
- Refresh: Reload data từ database

### 3.3 Thiết kế UX/UI

#### 3.3.1 Color Scheme:

- **Primary**: Blue (#007bff) - Professional, trustworthy
- **Success**: Green (#28a745) - Positive actions
- **Warning**: Orange (#ffc107) - Attention needed
- **Danger**: Red (#dc3545) - Critical actions
- **Background**: Light gray (#f8f9fa) - Clean, minimal

#### 3.3.2 Typography:

- **Font Family**: System default (San Francisco, Segoe UI, Arial)
- **Font Sizes**:
  - Headers: 18-24px
  - Body text: 14px
  - Small text: 12px

#### 3.3.3 Icons:

- Material Design style icons
- Consistent sizing (16px, 24px, 32px)
- Semantic colors (green for success, red for delete, etc.)

#### 3.3.4 Responsive Design:

- Minimum window size: 1000x700
- Flexible layouts với VBox/HBox
- TableView auto-resize columns
- Scrollable content areas

### 3.4 Validation & Error Handling

#### 3.4.1 Client-side Validation:

- **Real-time validation** cho form inputs
- **Visual feedback** với border colors
- **Tooltip messages** cho validation errors
- **Disable buttons** khi form invalid

#### 3.4.2 Error Messages:

- **User-friendly language** (tiếng Việt)
- **Specific error descriptions** thay vì generic
- **Actionable suggestions** (ví dụ: "Vui lòng nhập số điện thoại 10-11 chữ số")

#### 3.4.3 Success Feedback:

- **Toast notifications** cho actions thành công
- **Progress indicators** cho long-running operations
- **Confirmation dialogs** cho destructive actions

### 3.5 Accessibility & Usability

#### 3.5.1 Keyboard Navigation:

- Tab order logic
- Enter key để submit forms
- Escape key để cancel dialogs
- Arrow keys cho table navigation

#### 3.5.2 User Experience:

- **Auto-focus** field đầu tiên trong forms
- **Remember last selections** trong ComboBoxes
- **Undo functionality** cho accidental deletes
- **Bulk operations** cho efficiency

#### 3.5.3 Performance:

- **Lazy loading** cho large datasets
- **Caching** frequently accessed data
- **Background tasks** cho database operations
- **Responsive UI** với progress indicators

## 4. KIẾN TRÚC PHẦN MỀM

### 4.1 Pattern sử dụng:

- **MVC (Model-View-Controller)**
- **DAO (Data Access Object)**
- **Singleton** cho DatabaseConnection
- **Factory** cho Alert creation
- **Observer** cho real-time updates

### 4.2 Utility Classes:

- **AlertUtils**: Centralized alert dialogs
- **ValidationUtils**: Common validation patterns
- **DatabaseExceptionHandler**: SQL exception handling
- **SessionManager**: User session management
- **VNPayService**: Payment integration

### 4.3 Code Quality:

- **DRY Principle**: Loại bỏ 450+ dòng code duplicate
- **Single Responsibility**: Mỗi class có một nhiệm vụ rõ ràng
- **Error Handling**: Comprehensive exception handling
- **Documentation**: JavaDoc cho public methods
- **Testing**: Unit tests cho business logic
