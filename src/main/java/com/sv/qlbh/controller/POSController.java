package com.sv.qlbh.controller;

import com.sv.qlbh.dao.*;
import com.sv.qlbh.models.*;
import com.sv.qlbh.utils.SessionManager;
import com.sv.qlbh.utils.VNPayService;
import javafx.application.HostServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * POS Controller with VNPay Integration
 * @author nghip
 */
public class POSController implements Initializable {
    
    // FXML Controls
    // Header controls
    @FXML private Label lblCurrentUser;
    @FXML private Label lblCurrentTime;
    
    // Product controls
    @FXML private TextField txtProductSearch;
    @FXML private TableView<Product> tblProducts;
    @FXML private TableColumn<Product, String> colProductName;
    @FXML private TableColumn<Product, Double> colProductPrice;
    @FXML private TableColumn<Product, Integer> colProductStock;
    
    // Cart controls
    @FXML private TableView<CartItem> tblCart;
    @FXML private TableColumn<CartItem, String> colCartProduct;
    @FXML private TableColumn<CartItem, Integer> colCartQuantity;
    @FXML private TableColumn<CartItem, Double> colCartPrice;
    @FXML private TableColumn<CartItem, Double> colCartTotal;
    
    // Form controls
    @FXML private ComboBox<Customer> cmbCustomer;
    @FXML private Label lblSubtotal;
    @FXML private Label lblDiscount;
    @FXML private Label lblTotal;
    @FXML private RadioButton rbCash;
    @FXML private RadioButton rbVNPay;
    @FXML private Button btnCheckout;
    @FXML private Button btnClearCart;
    
    // Data
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    
    // DAOs
    private ProductDAO productDAO = new ProductDAOImpl();
    private CustomerDAO customerDAO = new CustomerDAOImpl();
    private OrderDAO orderDAO = new OrderDAO();
    private OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    private InventoryDAO inventoryDAO = new InventoryDAOImpl();
    
    // Other services
    private HostServices hostServices;
    private DecimalFormat df = new DecimalFormat("#,##0.00");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupCustomerComboBox();
        setupPaymentMethods();
        loadProducts();
        loadCustomers();
        setupEventHandlers();
        updateTotals();
    }
    
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
    
    private void setupTableColumns() {
        // Product table
        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProductPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colProductStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        // Cart table
        colCartProduct.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getProductName()));
        colCartQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCartPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colCartTotal.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getTotalAfterDiscount()).asObject());
        
        // Set tables data
        tblProducts.setItems(productList);
        tblCart.setItems(cartItems);
    }
    
    private void setupCustomerComboBox() {
        cmbCustomer.setItems(customerList);
        cmbCustomer.setConverter(new StringConverter<Customer>() {
            @Override
            public String toString(Customer customer) {
                if (customer == null) return "";
                if (customer.getId() == -1) return customer.getName(); // Walk-in customer
                return customer.getName() + " - " + customer.getPhone();
            }
            
            @Override
            public Customer fromString(String string) {
                return null;
            }
        });
        
        // Make it look nice
        cmbCustomer.setPromptText("Chọn khách hàng...");
    }
    
    private void setupPaymentMethods() {
        ToggleGroup paymentGroup = new ToggleGroup();
        rbCash.setToggleGroup(paymentGroup);
        rbVNPay.setToggleGroup(paymentGroup);
        rbCash.setSelected(true); // Default
    }
    
    private void setupEventHandlers() {
        // Product search
        txtProductSearch.textProperty().addListener((obs, oldText, newText) -> {
            searchProducts(newText);
        });
        
        // Double click to add product to cart
        tblProducts.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    addToCart(row.getItem());
                }
            });
            return row;
        });
        
        // Cart quantity editing
        colCartQuantity.setCellFactory(TextFieldTableCell.forTableColumn(
            new javafx.util.converter.IntegerStringConverter()));
        colCartQuantity.setOnEditCommit(event -> {
            CartItem item = event.getRowValue();
            item.setQuantity(event.getNewValue());
            updateTotals();
        });
        
        // Cart total updates when quantity changes
        cartItems.addListener((javafx.collections.ListChangeListener<CartItem>) change -> {
            updateTotals();
        });
    }
    
    private void loadProducts() {
        try {
            List<Product> products = productDAO.getAll(); // Use existing method
            productList.setAll(products);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách sản phẩm: " + e.getMessage());
        }
    }
    
    private void loadCustomers() {
        List<Customer> customers = customerDAO.getAll(); // Use existing method - doesn't throw SQLException
        
        // Add "Walk-in Customer" option
        Customer walkInCustomer = new Customer();
        walkInCustomer.setId(-1); // Special ID for walk-in
        walkInCustomer.setName("Khách vãng lai");
        walkInCustomer.setPhone("N/A");
        
        customerList.clear();
        customerList.add(walkInCustomer); // Add walk-in option first
        customerList.addAll(customers);
        
        // Set walk-in as default selection
        cmbCustomer.setValue(walkInCustomer);
    }
    
    private void searchProducts(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadProducts();
            return;
        }
        
        try {
            List<Product> products = productDAO.getByName(searchText); // Use existing method
            productList.setAll(products);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi tìm kiếm sản phẩm: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch() {
        searchProducts(txtProductSearch.getText());
    }
    
    @FXML
    private void handleRefresh() {
        loadProducts();
        txtProductSearch.clear();
    }
    
    @FXML
    private void handleAddNewCustomer() {
        // TODO: Implement add new customer functionality
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Chức năng thêm khách hàng mới sẽ được phát triển sau.");
    }
    
    @FXML
    private void addToCart(Product product) {
        if (product == null || product.getStock() <= 0) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Sản phẩm không có trong kho!");
            return;
        }
        
        // Check if product already in cart
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                if (item.getQuantity() < product.getStock()) {
                    item.setQuantity(item.getQuantity() + 1);
                    tblCart.refresh();
                    return;
                } else {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Không đủ hàng trong kho!");
                    return;
                }
            }
        }
        
        // Add new item to cart
        CartItem newItem = new CartItem(product, 1);
        cartItems.add(newItem);
    }
    
    @FXML
    private void removeFromCart() {
        CartItem selectedItem = tblCart.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            cartItems.remove(selectedItem);
        }
    }
    
    @FXML
    private void clearCart() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Xác nhận");
        confirmation.setHeaderText("Xóa toàn bộ giỏ hàng?");
        confirmation.setContentText("Bạn có chắc chắn muốn xóa tất cả sản phẩm trong giỏ hàng?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cartItems.clear();
        }
    }
    
    private void updateTotals() {
        double subtotal = cartItems.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
        
        double discount = cartItems.stream()
                .mapToDouble(CartItem::getDiscountAmount)
                .sum();
        
        double total = subtotal - discount;
        
        lblSubtotal.setText(df.format(subtotal) + " VND");
        lblDiscount.setText(df.format(discount) + " VND");
        lblTotal.setText(df.format(total) + " VND");
    }
    
    @FXML
    private void checkout() {
        if (cartItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Giỏ hàng trống!");
            return;
        }
        
        try {
            // Create order
            Order order = createOrderFromCart();
            int orderId = orderDAO.createOrder(order);
            
            // Create order details and update inventory
            orderDetailDAO.createOrderDetailsFromCart(orderId, new ArrayList<>(cartItems));
            
            // Update stock and create inventory records for sale
            for (CartItem item : cartItems) {
                Product product = item.getProduct();
                int newStock = product.getStock() - item.getQuantity();
                
                // Create inventory record for sale
                Inventory inventoryEntry = new Inventory();
                inventoryEntry.setProductId(product.getId());
                inventoryEntry.setQuantity(item.getQuantity());
                inventoryEntry.setType("OUT");
                inventoryEntry.setReferenceId(orderId);
                inventoryEntry.setReferenceType("SALE");
                inventoryEntry.setNote("Bán hàng - Đơn #" + orderId + " - " + product.getName());
                
                // Use the combined method to update stock and inventory atomically
                inventoryDAO.addInventoryEntryAndUpdateProductStock(inventoryEntry, product.getId(), newStock);
            }
            
            // Get customer info for display
            Customer selectedCustomer = cmbCustomer.getSelectionModel().getSelectedItem();
            String customerInfo = (selectedCustomer != null && selectedCustomer.getId() > 0) 
                ? "Khách hàng: " + selectedCustomer.getName()
                : "Khách vãng lai";
            
            // Process payment
            if (rbVNPay.isSelected()) {
                order.setId(orderId); // Set the generated ID
                processVNPayPayment(order, customerInfo);
            } else {
                // Cash payment - complete immediately
                orderDAO.updateOrderStatus(orderId, "COMPLETED");
                showAlert(Alert.AlertType.INFORMATION, "✅ Thanh toán thành công!", 
                         "Đơn hàng #" + orderId + " đã được tạo thành công!\n" +
                         customerInfo + "\n" +
                         "Phương thức: Tiền mặt\n" +
                         "Tổng tiền: " + lblTotal.getText());
                clearCart();
            }
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo đơn hàng: " + e.getMessage());
        }
    }
    
    private Order createOrderFromCart() {
        Order order = new Order();
        
        Customer selectedCustomer = cmbCustomer.getSelectionModel().getSelectedItem();
        // Only set customer ID if it's a real customer (not walk-in)
        if (selectedCustomer != null && selectedCustomer.getId() > 0) {
            order.setCustomerId(selectedCustomer.getId());
        }
        // If walk-in customer (ID = -1) or no selection, leave customer_id as null
        
        order.setUserId(SessionManager.getCurrentUser().getId());
        
        double subtotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        double discount = cartItems.stream().mapToDouble(CartItem::getDiscountAmount).sum();
        
        order.setTotalAmount(subtotal);
        order.setDiscountAmount(discount);
        order.setFinalAmount(subtotal - discount);
        order.setPaymentMethod(rbVNPay.isSelected() ? "VNPAY" : "CASH");
        order.setStatus("PROCESSING");
        
        // Set note based on customer type
        if (selectedCustomer != null && selectedCustomer.getId() == -1) {
            order.setNote("POS Order - Khách vãng lai");
        } else if (selectedCustomer != null) {
            order.setNote("POS Order - Khách hàng: " + selectedCustomer.getName());
        } else {
            order.setNote("POS Order - Khách vãng lai");
        }
        
        order.setCreatedAt(LocalDateTime.now());
        
        return order;
    }
    
    private void processVNPayPayment(Order order, String customerInfo) {
        try {
            String paymentUrl = VNPayService.createPaymentUrl(order, "127.0.0.1");
            
            if (hostServices != null) {
                hostServices.showDocument(paymentUrl);
                showAlert(Alert.AlertType.INFORMATION, "🏦 VNPay - Chờ thanh toán", 
                         "Đơn hàng #" + order.getId() + " đã được tạo\n" +
                         customerInfo + "\n" +
                         "Tổng tiền: " + lblTotal.getText() + "\n\n" +
                         "Vui lòng hoàn tất thanh toán trên trình duyệt.\n" +
                         "Trạng thái đơn hàng sẽ được cập nhật tự động.");
                clearCart();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở trình duyệt để thanh toán VNPay");
            }
            
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi VNPay", "Không thể tạo URL thanh toán: " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 