package com.sv.qlbh.controller;

import com.sv.qlbh.dao.CustomerDAO;
import com.sv.qlbh.dao.CustomerDAOImpl;
import com.sv.qlbh.models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller cho Customer Management
 * @author nghip
 */
public class CustomerController implements Initializable {

    // Search Controls
    @FXML private TextField txtSearch;

    // Table Controls
    @FXML private TableView<Customer> tableCustomer;
    @FXML private TableColumn<Customer, Integer> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colGroup;
    @FXML private TableColumn<Customer, Integer> colPoints;
    @FXML private TableColumn<Customer, Double> colTotalSpent;
    @FXML private TableColumn<Customer, String> colStatus;

    // Form Controls
    @FXML private TextField txtName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtGroup;
    @FXML private TextField txtPoints;
    @FXML private TextField txtTotalSpent;
    @FXML private CheckBox chkStatus;

    // DAO và Data
    private CustomerDAO customerDAO;
    private ObservableList<Customer> customerList;
    private Customer selectedCustomer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize DAO
        customerDAO = new CustomerDAOImpl();
        
        // Initialize list
        customerList = FXCollections.observableArrayList();
        
        // Setup table
        setupTableColumns();
        
        // Setup ComboBox
        setupComboBox();
        
        // Setup event handlers
        setupEventHandlers();
        
        // Load data
        loadData();
        
        System.out.println("Customer Management loaded successfully!");
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colGroup.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(customer.getGroupDisplayName());
        });
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        colTotalSpent.setCellValueFactory(new PropertyValueFactory<>("totalSpent"));
        colStatus.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            String status = customer.isStatus() ? "Hoạt động" : "Ngưng hoạt động";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        
        // Set table data
        tableCustomer.setItems(customerList);
    }

    private void setupComboBox() {
        txtGroup.setEditable(false);
        txtGroup.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #6c757d;");
    }

    private void setupEventHandlers() {
        // Table selection handler
        tableCustomer.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                selectedCustomer = newSelection;
                if (selectedCustomer != null) {
                    fillForm(selectedCustomer);
                }
            }
        );
    }

    private void loadData() {
        try {
            List<Customer> customers = customerDAO.getAll();
            customerList.clear();
            customerList.addAll(customers);
            System.out.println("Đã load " + customers.size() + " khách hàng");
        } catch (RuntimeException e) {
            System.err.println("RuntimeException khi load khách hàng: " + e.getMessage());
            showError("Lỗi cơ sở dữ liệu", "Không thể kết nối database để tải danh sách khách hàng");
        }
    }

    private void fillForm(Customer customer) {
        txtName.setText(customer.getName());
        txtPhone.setText(customer.getPhone());
        txtEmail.setText(customer.getEmail());
        txtPoints.setText(String.valueOf(customer.getPoints()));
        txtTotalSpent.setText(String.valueOf(customer.getTotalSpent()));
        txtGroup.setText(customer.getGroupDisplayName());
        chkStatus.setSelected(customer.isStatus());
    }

    private void clearForm() {
        txtName.clear();
        txtPhone.clear();
        txtEmail.clear();
        txtPoints.setText("0");
        txtTotalSpent.setText("0");
        chkStatus.setSelected(true);
        txtGroup.setText("🥉 Khách hàng thường");
        selectedCustomer = null;
    }

    private boolean validateInput() {
        if (txtName.getText().trim().isEmpty()) {
            showWarning("Cảnh báo", "Vui lòng nhập tên khách hàng");
            return false;
        }
        
        if (txtPhone.getText().trim().isEmpty()) {
            showWarning("Cảnh báo", "Vui lòng nhập số điện thoại");
            return false;
        }
        
        // Validate phone number format
        String phone = txtPhone.getText().trim();
        if (!phone.matches("^[0-9]{10,11}$")) {
            showWarning("Cảnh báo", "Số điện thoại phải có 10-11 chữ số");
            return false;
        }
        
        // Validate email if provided
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showWarning("Cảnh báo", "Email không hợp lệ");
            return false;
        }
        
        return true;
    }

    private Customer createCustomerFromForm() throws NumberFormatException {
        Customer customer = new Customer();
        customer.setName(txtName.getText().trim());
        customer.setPhone(txtPhone.getText().trim());
        customer.setEmail(txtEmail.getText().trim());
        customer.setGroupId(0);
        
        // Mặc định = 0 cho khách hàng mới
        customer.setPoints(0);
        customer.setTotalSpent(0.0);
        
        customer.setStatus(chkStatus.isSelected());
        return customer;
    }

    // ================== EVENT HANDLERS ==================

    @FXML
    private void handleSearch() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            loadData(); // Reload all data
        } else {
            try {
                List<Customer> results = customerDAO.searchByName(searchText);
                customerList.clear();
                customerList.addAll(results);
                showInfo("Tìm kiếm", "Tìm thấy " + results.size() + " khách hàng");
            } catch (Exception e) {
                System.err.println("Lỗi khi tìm kiếm: " + e.getMessage());
                showError("Lỗi", "Không thể tìm kiếm khách hàng");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
        clearForm();
        showInfo("Làm mới", "Đã tải lại dữ liệu thành công");
    }

    @FXML
    private void handleAdd() {
        if (!validateInput()) return;
        
        try {
            Customer customer = createCustomerFromForm();
            
            // Check if phone already exists
            Customer existing = customerDAO.getByPhone(customer.getPhone());
            if (existing != null) {
                showWarning("Cảnh báo", "Số điện thoại đã tồn tại trong hệ thống");
                return;
            }
            
            if (customerDAO.insert(customer)) {
                loadData();
                clearForm();
                showInfo("Thành công", "Thêm khách hàng thành công");
            } else {
                showError("Lỗi", "Không thể thêm khách hàng");
            }
        } catch (NumberFormatException e) {
            System.err.println("NumberFormatException khi thêm khách hàng: " + e.getMessage());
            showWarning("Dữ liệu không hợp lệ", e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("RuntimeException khi thêm khách hàng: " + e.getMessage());
            showError("Lỗi cơ sở dữ liệu", "Không thể thêm khách hàng vào database");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedCustomer == null) {
            showWarning("Cảnh báo", "Vui lòng chọn khách hàng cần sửa");
            return;
        }
        
        if (!validateInput()) return;
        
        try {
            Customer customer = createCustomerFromForm();
            customer.setId(selectedCustomer.getId());
            
            // Giữ nguyên điểm và tổng chi tiêu
            customer.setPoints(selectedCustomer.getPoints());
            customer.setTotalSpent(selectedCustomer.getTotalSpent());
            
            if (customerDAO.update(customer)) {
                loadData();
                clearForm();
                showInfo("Thành công", "Cập nhật khách hàng thành công");
            } else {
                showError("Lỗi", "Không thể cập nhật khách hàng");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật khách hàng: " + e.getMessage());
            showError("Lỗi", "Không thể cập nhật khách hàng: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedCustomer == null) {
            showWarning("Cảnh báo", "Vui lòng chọn khách hàng cần xóa");
            return;
        }
        
        Optional<ButtonType> result = showConfirmation("Xác nhận", 
            "Bạn có chắc chắn muốn vô hiệu hóa khách hàng: " + selectedCustomer.getName() + "?");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (customerDAO.delete(selectedCustomer.getId())) {
                    loadData();
                    clearForm();
                    showInfo("Thành công", "Vô hiệu hóa khách hàng thành công");
                } else {
                    showError("Lỗi", "Không thể vô hiệu hóa khách hàng");
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi xóa khách hàng: " + e.getMessage());
                showError("Lỗi", "Không thể vô hiệu hóa khách hàng: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    @FXML
    private void handleViewDetails() {
        if (selectedCustomer == null) {
            showWarning("Cảnh báo", "Vui lòng chọn khách hàng để xem chi tiết");
            return;
        }
        
        // TODO: Open detail dialog
        showInfo("Chi tiết khách hàng", 
            "Tên: " + selectedCustomer.getName() + "\n" +
            "SĐT: " + selectedCustomer.getPhone() + "\n" +
            "Email: " + selectedCustomer.getEmail() + "\n" +
            "Điểm: " + selectedCustomer.getPoints() + "\n" +
            "Tổng chi tiêu: " + selectedCustomer.getTotalSpent());
    }

    @FXML
    private void handleExportExcel() {
        // TODO: Export to Excel
        showInfo("Xuất Excel", "Chức năng xuất Excel sẽ được phát triển sau");
    }

    // TODO: Implement when order module ready
    private void recalculateCustomerPoints(int customerId) {
        System.out.println("Tính toán lại điểm cho khách hàng ID: " + customerId);
    }
    
    private void recalculateTotalSpent(int customerId) {
        System.out.println("Tính toán lại tổng chi tiêu cho khách hàng ID: " + customerId);
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
} 