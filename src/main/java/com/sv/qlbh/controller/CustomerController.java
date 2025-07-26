package com.sv.qlbh.controller;

import com.sv.qlbh.dao.CustomerDAO;
import com.sv.qlbh.models.Customer;
import com.sv.qlbh.utils.AlertUtils;
import com.sv.qlbh.utils.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.SQLException;

import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    @FXML private TextField txtSearch, txtName, txtPhone, txtEmail, txtGroup, txtPoints, txtTotalSpent;
    @FXML private TableView<Customer> tableCustomer;
    @FXML private TableColumn<Customer, Integer> colId, colPoints;
    @FXML private TableColumn<Customer, String> colName, colPhone, colEmail, colGroup, colTotalSpent, colStatus;
    @FXML private CheckBox chkStatus;
    private CustomerDAO customerDAO;
    private ObservableList<Customer> customerList;
    private Customer selectedCustomer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerDAO = new CustomerDAO();
        customerList = FXCollections.observableArrayList();
        setupTableColumns();
        txtGroup.setEditable(false);
        txtGroup.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #6c757d;");
        tableCustomer.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedCustomer = n;
            if (n != null) fillForm(n);
        });
        loadData();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colGroup.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getGroupDisplayName()));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        colTotalSpent.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(new java.text.DecimalFormat("#,##0").format(c.getValue().getTotalSpent())));
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().isStatus() ? "Hoạt động" : "Ngưng hoạt động"));
        tableCustomer.setItems(customerList);
    }

    private void loadData() {
        try {
            customerList.setAll(customerDAO.getAll());
        } catch (SQLException e) {
            AlertUtils.showDatabaseError("Không thể kết nối database để tải danh sách khách hàng");
        }
    }

    private void fillForm(Customer c) {
        txtName.setText(c.getName());
        txtPhone.setText(c.getPhone());
        txtEmail.setText(c.getEmail());
        txtPoints.setText(String.valueOf(c.getPoints()));
        txtTotalSpent.setText(new java.text.DecimalFormat("#,##0").format(c.getTotalSpent()));
        txtGroup.setText(c.getGroupDisplayName());
        chkStatus.setSelected(c.isStatus());
    }

    private void clearForm() {
        txtName.clear(); txtPhone.clear(); txtEmail.clear();
        txtPoints.setText("0"); txtTotalSpent.setText("0");
        chkStatus.setSelected(true); txtGroup.setText("🥉 Khách hàng thường");
        selectedCustomer = null;
    }

    private String validateInput() {
        if (ValidationUtils.isEmpty(txtName.getText())) return "Vui lòng nhập tên khách hàng";
        if (ValidationUtils.isEmpty(txtPhone.getText())) return "Vui lòng nhập số điện thoại";
        if (!ValidationUtils.isValidPhone(txtPhone.getText().trim())) return "Số điện thoại phải có 10-11 chữ số";
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) return "Email không hợp lệ";
        return null;
    }

    private Customer customerFromForm() {
        Customer c = new Customer();
        c.setName(txtName.getText().trim());
        c.setPhone(txtPhone.getText().trim());
        c.setEmail(txtEmail.getText().trim());
        c.setPoints(selectedCustomer != null ? selectedCustomer.getPoints() : 0);
        c.setTotalSpent(selectedCustomer != null ? selectedCustomer.getTotalSpent() : 0.0);
        c.setStatus(chkStatus.isSelected());
        if (selectedCustomer != null) c.setId(selectedCustomer.getId());
        return c;
    }

    @FXML
    private void handleSearch() {
        String s = txtSearch.getText().trim();
        if (s.isEmpty()) loadData();
        else {
            try {
                customerList.setAll(customerDAO.searchByName(s));
                AlertUtils.showInfo("Tìm kiếm", "Tìm thấy " + customerList.size() + " khách hàng");
            } catch (RuntimeException e) {
                AlertUtils.showError("Lỗi", "Không thể tìm kiếm khách hàng");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadData(); clearForm();
        AlertUtils.showInfo("Làm mới", "Đã tải lại dữ liệu thành công");
    }

    @FXML
    private void handleAdd() {
        String err = validateInput();
        if (err != null) { AlertUtils.showValidationError(err); return; }
        try {
            Customer c = customerFromForm();
            if (customerDAO.getByPhone(c.getPhone()) != null) {
                AlertUtils.showWarning("Cảnh báo", "Số điện thoại đã tồn tại trong hệ thống");
                return;
            }
            if (customerDAO.insert(c)) {
                loadData(); clearForm();
                AlertUtils.showSuccess("Thêm khách hàng thành công");
            } else AlertUtils.showError("Lỗi", "Không thể thêm khách hàng");
        } catch (RuntimeException e) {
            AlertUtils.showDatabaseError("Không thể thêm khách hàng vào database");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedCustomer == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn khách hàng cần sửa");
            return;
        }
        String err = validateInput();
        if (err != null) { AlertUtils.showValidationError(err); return; }
        try {
            Customer c = customerFromForm();
            Customer existing = customerDAO.getByPhone(c.getPhone());
            if (existing != null && existing.getId() != selectedCustomer.getId()) {
                AlertUtils.showWarning("Cảnh báo", "Số điện thoại đã tồn tại trong hệ thống");
                return;
            }
            if (customerDAO.update(c)) {
                loadData(); clearForm();
                AlertUtils.showSuccess("Cập nhật khách hàng thành công");
            } else AlertUtils.showError("Lỗi", "Không thể cập nhật khách hàng");
        } catch (RuntimeException e) {
            AlertUtils.showError("Lỗi", "Không thể cập nhật khách hàng: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() { clearForm(); }

    @FXML
    private void handleViewDetails() {
        if (selectedCustomer == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn khách hàng để xem chi tiết");
            return;
        }
        String tongChiTieu = new java.text.DecimalFormat("#,##0").format(selectedCustomer.getTotalSpent());
        AlertUtils.showInfo("Chi tiết khách hàng",
            "Tên: " + selectedCustomer.getName() + "\n" +
            "SĐT: " + selectedCustomer.getPhone() + "\n" +
            "Email: " + selectedCustomer.getEmail() + "\n" +
            "Điểm: " + selectedCustomer.getPoints() + "\n" +
            "Tổng chi tiêu: " + tongChiTieu);
    }

} 