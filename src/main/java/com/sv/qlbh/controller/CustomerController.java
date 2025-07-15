package com.sv.qlbh.controller;

import com.sv.qlbh.dao.CustomerDAO;
import com.sv.qlbh.dao.CustomerDAOImpl;
import com.sv.qlbh.models.Customer;
import com.sv.qlbh.utils.AlertUtils;
import com.sv.qlbh.utils.ValidationUtils;
import com.sv.qlbh.utils.DatabaseExceptionHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
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
    @FXML private TableColumn<Customer, String> colTotalSpent;
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
        customerDAO = new CustomerDAOImpl();
        customerList = FXCollections.observableArrayList();
        
        setupTableColumns();
        setupComboBox();
        setupEventHandlers();
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
        colTotalSpent.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0");
            String formattedAmount = df.format(customer.getTotalSpent());
            return new javafx.beans.property.SimpleStringProperty(formattedAmount);
        });
        colStatus.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            String status = customer.isStatus() ? "Hoạt động" : "Ngưng hoạt động";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        
        tableCustomer.setItems(customerList);
    }

    private void setupComboBox() {
        txtGroup.setEditable(false);
        txtGroup.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #6c757d;");
    }

    private void setupEventHandlers() {
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
            AlertUtils.showDatabaseError("Không thể kết nối database để tải danh sách khách hàng");
        }
    }

    private void fillForm(Customer customer) {
        txtName.setText(customer.getName());
        txtPhone.setText(customer.getPhone());
        txtEmail.setText(customer.getEmail());
        txtPoints.setText(String.valueOf(customer.getPoints()));
        
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0");
        txtTotalSpent.setText(df.format(customer.getTotalSpent()));
        
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
        if (ValidationUtils.isEmpty(txtName.getText())) {
            AlertUtils.showValidationError("Vui lòng nhập tên khách hàng");
            return false;
        }
        
        if (ValidationUtils.isEmpty(txtPhone.getText())) {
            AlertUtils.showValidationError("Vui lòng nhập số điện thoại");
            return false;
        }
        
        if (!ValidationUtils.isValidPhone(txtPhone.getText().trim())) {
            AlertUtils.showValidationError("Số điện thoại phải có 10-11 chữ số");
            return false;
        }
        
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
            AlertUtils.showValidationError("Email không hợp lệ");
            return false;
        }
        
        return true;
    }

    private Customer createCustomerFromForm() {
        Customer customer = new Customer();
        customer.setName(txtName.getText().trim());
        customer.setPhone(txtPhone.getText().trim());
        customer.setEmail(txtEmail.getText().trim());
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
            loadData();
        } else {
            try {
                List<Customer> results = customerDAO.searchByName(searchText);
                customerList.clear();
                customerList.addAll(results);
                AlertUtils.showInfo("Tìm kiếm", "Tìm thấy " + results.size() + " khách hàng");
            } catch (RuntimeException e) {
                System.err.println("Lỗi khi tìm kiếm: " + e.getMessage());
                AlertUtils.showError("Lỗi", "Không thể tìm kiếm khách hàng");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
        clearForm();
        AlertUtils.showInfo("Làm mới", "Đã tải lại dữ liệu thành công");
    }

    @FXML
    private void handleAdd() {
        if (!validateInput()) return;
        
        try {
            Customer customer = createCustomerFromForm();
            
            Customer existing = customerDAO.getByPhone(customer.getPhone());
            if (existing != null) {
                AlertUtils.showWarning("Cảnh báo", "Số điện thoại đã tồn tại trong hệ thống");
                return;
            }
            
            if (customerDAO.insert(customer)) {
                loadData();
                clearForm();
                AlertUtils.showSuccess("Thêm khách hàng thành công");
            } else {
                AlertUtils.showError("Lỗi", "Không thể thêm khách hàng");
            }
        } catch (RuntimeException e) {
            System.err.println("RuntimeException khi thêm khách hàng: " + e.getMessage());
            AlertUtils.showDatabaseError("Không thể thêm khách hàng vào database");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedCustomer == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn khách hàng cần sửa");
            return;
        }
        
        if (!validateInput()) return;
        
        try {
            Customer customer = createCustomerFromForm();
            customer.setId(selectedCustomer.getId());
            
            Customer existingByPhone = customerDAO.getByPhone(customer.getPhone());
            if (existingByPhone != null && existingByPhone.getId() != selectedCustomer.getId()) {
                AlertUtils.showWarning("Cảnh báo", "Số điện thoại đã tồn tại trong hệ thống");
                return;
            }
            
            customer.setPoints(selectedCustomer.getPoints());
            customer.setTotalSpent(selectedCustomer.getTotalSpent());
            
            if (customerDAO.update(customer)) {
                loadData();
                clearForm();
                AlertUtils.showSuccess("Cập nhật khách hàng thành công");
            } else {
                AlertUtils.showError("Lỗi", "Không thể cập nhật khách hàng");
            }
        } catch (RuntimeException e) {
            System.err.println("Lỗi khi cập nhật khách hàng: " + e.getMessage());
            AlertUtils.showError("Lỗi", "Không thể cập nhật khách hàng: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    @FXML
    private void handleViewDetails() {
        if (selectedCustomer == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn khách hàng để xem chi tiết");
            return;
        }
        
        AlertUtils.showInfo("Chi tiết khách hàng", 
            "Tên: " + selectedCustomer.getName() + "\n" +
            "SĐT: " + selectedCustomer.getPhone() + "\n" +
            "Email: " + selectedCustomer.getEmail() + "\n" +
            "Điểm: " + selectedCustomer.getPoints() + "\n" +
            "Tổng chi tiêu: " + selectedCustomer.getTotalSpent());
    }

    @FXML
    private void handleExportExcel() {
        AlertUtils.showInfo("Xuất Excel", "Chức năng xuất Excel sẽ được phát triển sau");
    }

    private void recalculateCustomerPoints(int customerId) {
        System.out.println("Tính toán lại điểm cho khách hàng ID: " + customerId);
    }
    
    private void recalculateTotalSpent(int customerId) {
        System.out.println("Tính toán lại tổng chi tiêu cho khách hàng ID: " + customerId);
    }
} 