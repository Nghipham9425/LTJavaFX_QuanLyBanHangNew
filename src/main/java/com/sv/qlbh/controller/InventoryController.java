// src/main/java/com/sv/qlbh/controller/InventoryController.java
package com.sv.qlbh.controller;

import com.sv.qlbh.dao.InventoryDAO;
import com.sv.qlbh.dao.InventoryDAOImpl;
import com.sv.qlbh.dao.ProductDAO;
import com.sv.qlbh.dao.ProductDAOImpl;
import com.sv.qlbh.models.Inventory;
import com.sv.qlbh.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {

    // Các thành phần UI được ánh xạ từ FXML
    @FXML private ComboBox<Product> cbxProduct;
    @FXML private TextField txtQuantity;
    @FXML private ComboBox<String> cbxType;
    @FXML private ComboBox<String> cbxReason;
    @FXML private TextField txtReferenceId;
    @FXML private TextArea txtNote;
    @FXML private Button btnAddEntry;
    @FXML private ComboBox<Product> cbxSearchProduct;
    @FXML private TableView<Inventory> tblInventory;
    @FXML private TableColumn<Inventory, Integer> colId;
    @FXML private TableColumn<Inventory, String> colProductName; // Để hiển thị tên sản phẩm
    @FXML private TableColumn<Inventory, Integer> colQuantity;
    @FXML private TableColumn<Inventory, String> colType;
    @FXML private TableColumn<Inventory, String> colReason;
    @FXML private TableColumn<Inventory, Integer> colReferenceId;
    @FXML private TableColumn<Inventory, String> colNote;
    @FXML private TableColumn<Inventory, String> colCreatedAt;

    // Các đối tượng DAO để tương tác với cơ sở dữ liệu
    private InventoryDAO inventoryDAO;
    private ProductDAO productDAO;

    // Danh sách dữ liệu cho TableView và ComboBox
    private ObservableList<Inventory> inventoryList;
    private ObservableList<Product> productList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Khởi tạo các đối tượng DAO
        inventoryDAO = new InventoryDAOImpl();
        productDAO = new ProductDAOImpl();

        // Khởi tạo ObservableList để chứa dữ liệu
        inventoryList = FXCollections.observableArrayList();
        productList = FXCollections.observableArrayList();

        // Khởi tạo ComboBox loại giao dịch (IN/OUT)
        cbxType.setItems(FXCollections.observableArrayList("IN", "OUT"));
        
        // Khởi tạo ComboBox lý do giao dịch (setup listener trước)
        setupReasonComboBox();
        
        // Sau đó mới set default value để trigger listener
        cbxType.getSelectionModel().selectFirst(); // Chọn mặc định là "IN"

        // --- Cấu hình các cột cho TableView ---
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("referenceType"));
        colReferenceId.setCellValueFactory(new PropertyValueFactory<>("referenceId"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        // Định dạng và hiển thị cột ngày tạo (createdAt)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        colCreatedAt.setCellValueFactory(cellData -> {
            LocalDateTime createdAt = cellData.getValue().getCreatedAt();
            // Trả về StringProperty để hiển thị chuỗi ngày đã định dạng
            return new javafx.beans.property.SimpleStringProperty(createdAt.format(formatter));
        });

        // Hiển thị tên sản phẩm
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));

        // Cấu hình ComboBox hiển thị tên sản phẩm
        setupProductComboBoxes();

        // Tải dữ liệu ban đầu
        loadProducts();         // Tải danh sách sản phẩm cho ComboBox
        loadInventoryEntries(); // Tải tất cả các giao dịch kho lên TableView

        // Thêm Listener cho ComboBox tìm kiếm sản phẩm: khi người dùng chọn một sản phẩm,
        // bảng sẽ tự động được lọc mà không cần bấm nút "Tìm kiếm".
        cbxSearchProduct.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                handleSearchByProduct(null); // Gọi hàm tìm kiếm
            }
        });
    }

    // Cấu hình ComboBox để hiển thị tên sản phẩm
    private void setupProductComboBoxes() {
        StringConverter<Product> converter = new StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                return product != null ? product.getName() + " (Stock: " + product.getStock() + ")" : "";
            }

            @Override
            public Product fromString(String string) {
                return null;
            }
        };

        cbxProduct.setConverter(converter);
        cbxSearchProduct.setConverter(converter);
    }
    
    // Cấu hình ComboBox lý do giao dịch
    private void setupReasonComboBox() {
        // Thiết lập listener để thay đổi options dựa trên loại giao dịch
        cbxType.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("IN".equals(newVal)) {
                cbxReason.setItems(FXCollections.observableArrayList(
                    "PURCHASE",     // Nhập từ mua hàng
                    "RETURN",       // Nhập từ trả hàng
                    "ADJUSTMENT",   // Điều chỉnh tăng
                    "TRANSFER_IN",  // Chuyển kho vào
                    "OTHER"         // Khác
                ));
            } else if ("OUT".equals(newVal)) {
                cbxReason.setItems(FXCollections.observableArrayList(
                    "SALE",         // Xuất bán hàng
                    "DAMAGE",       // Xuất do hỏng
                    "EXPIRED",      // Xuất do hết hạn
                    "ADJUSTMENT",   // Điều chỉnh giảm
                    "TRANSFER_OUT", // Chuyển kho ra
                    "OTHER"         // Khác
                ));
            }
            cbxReason.getSelectionModel().selectFirst();
        });
    }

    // Phương thức để tải danh sách sản phẩm từ DB và điền vào ComboBox
    private void loadProducts() {
        try {
            // Sử dụng getAll() của ProductDAO để lấy tất cả sản phẩm
            List<Product> products = productDAO.getAll();
            productList.setAll(products); // Cập nhật ObservableList
            cbxProduct.setItems(productList); // Thiết lập cho ComboBox thêm mới
            cbxSearchProduct.setItems(productList); // Thiết lập cho ComboBox tìm kiếm
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi tải sản phẩm", "Không thể tải danh sách sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức để tải tất cả các giao dịch kho từ DB và điền vào TableView
    private void loadInventoryEntries() {
        try {
            List<Inventory> entries = inventoryDAO.getAllInventoryEntries();
            inventoryList.setAll(entries); // Cập nhật ObservableList
            tblInventory.setItems(inventoryList); // Thiết lập cho TableView
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi tải dữ liệu kho", "Không thể tải danh sách giao dịch kho: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Xử lý sự kiện khi nhấn nút "Thêm giao dịch"
    @FXML
    private void handleAddInventoryEntry(ActionEvent event) {
        // Lấy dữ liệu từ các trường nhập liệu
        Product selectedProduct = cbxProduct.getSelectionModel().getSelectedItem();
        String quantityText = txtQuantity.getText();
        String type = cbxType.getSelectionModel().getSelectedItem(); // "IN" hoặc "OUT"
        String reason = cbxReason.getSelectionModel().getSelectedItem();
        String referenceIdText = txtReferenceId.getText().trim();
        String note = txtNote.getText().trim();

        // --- Kiểm tra dữ liệu đầu vào ---
        if (selectedProduct == null || quantityText.isEmpty() || type == null || reason == null) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền đầy đủ Sản phẩm, Số lượng, Loại giao dịch và Lý do.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                showAlert(Alert.AlertType.WARNING, "Số lượng không hợp lệ", "Số lượng phải là một số nguyên dương.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Số lượng không hợp lệ", "Số lượng phải là một số nguyên.");
            return;
        }

        // Tạo đối tượng Inventory mới
        Inventory newEntry = new Inventory();
        newEntry.setProductId(selectedProduct.getId());
        newEntry.setQuantity(quantity);
        newEntry.setType(type);
        newEntry.setReferenceType(reason);
        
        // Xử lý reference_id
        if (!referenceIdText.isEmpty()) {
            try {
                int referenceId = Integer.parseInt(referenceIdText);
                newEntry.setReferenceId(referenceId);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Mã tham chiếu không hợp lệ", "Mã tham chiếu phải là số nguyên.");
                return;
            }
        }
        
        newEntry.setNote(note);
        newEntry.setCreatedAt(LocalDateTime.now());

        try {
            // --- Tính toán stock mới ---
            int currentStock = selectedProduct.getStock(); // Lấy tồn kho hiện tại của sản phẩm
            int newStock;

            if ("IN".equals(type)) { // Nếu là nhập kho
                newStock = currentStock + quantity;
            } else { // Nếu là xuất kho ("OUT")
                if (currentStock < quantity) { // Kiểm tra số lượng tồn kho
                    showAlert(Alert.AlertType.ERROR, "Lỗi tồn kho", "Số lượng xuất kho (" + quantity + ") vượt quá số lượng tồn hiện có (" + currentStock + ").");
                    return;
                }
                newStock = currentStock - quantity;
            }

            // Sử dụng transaction để đảm bảo tính toàn vẹn dữ liệu
            boolean success = inventoryDAO.addInventoryEntryAndUpdateProductStock(newEntry, selectedProduct.getId(), newStock);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm giao dịch kho và cập nhật tồn kho thành công.");
                clearForm();             // Xóa dữ liệu trên form
                loadInventoryEntries();  // Tải lại dữ liệu bảng giao dịch kho
                loadProducts();          // Tải lại danh sách sản phẩm để cập nhật stock
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm giao dịch kho.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi cơ sở dữ liệu", "Lỗi khi thêm giao dịch kho: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Xử lý sự kiện khi nhấn nút "Tìm kiếm" (theo sản phẩm)
    @FXML
    private void handleSearchByProduct(ActionEvent event) {
        Product selectedProduct = cbxSearchProduct.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                // Lọc các giao dịch theo Product ID đã chọn
                List<Inventory> filteredEntries = inventoryDAO.getInventoryEntriesByProductId(selectedProduct.getId());
                inventoryList.setAll(filteredEntries); // Cập nhật ObservableList hiển thị
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi tìm kiếm", "Không thể tìm kiếm giao dịch theo sản phẩm: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Nếu không có sản phẩm nào được chọn trong ComboBox tìm kiếm, tải lại tất cả
            loadInventoryEntries();
        }
    }

    // Xử lý sự kiện khi nhấn nút "Xóa lọc"
    @FXML
    private void handleClearSearch(ActionEvent event) {
        cbxSearchProduct.getSelectionModel().clearSelection(); // Xóa lựa chọn trong ComboBox tìm kiếm
        loadInventoryEntries(); // Tải lại tất cả các giao dịch vào bảng
    }

    // Phương thức trợ giúp để xóa dữ liệu trên form nhập liệu
    private void clearForm() {
        cbxProduct.getSelectionModel().clearSelection();
        txtQuantity.clear();
        cbxType.getSelectionModel().selectFirst(); // Đặt lại mặc định "IN"
        cbxReason.getSelectionModel().selectFirst();
        txtReferenceId.clear();
        txtNote.clear();
    }

    // Phương thức trợ giúp để hiển thị hộp thoại thông báo
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 