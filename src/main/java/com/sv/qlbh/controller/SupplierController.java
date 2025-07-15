package com.sv.qlbh.controller;

import com.sv.qlbh.dao.SupplierDAO;
import com.sv.qlbh.dao.SupplierDAOImpl;
import com.sv.qlbh.models.Supplier;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class SupplierController implements Initializable {

    @FXML private TextField txtSearch;

    @FXML private TableView<Supplier> tableSupplier;
    @FXML private TableColumn<Supplier, Integer> colId;
    @FXML private TableColumn<Supplier, String> colName;
    @FXML private TableColumn<Supplier, String> colPhone;
    @FXML private TableColumn<Supplier, String> colEmail;
    @FXML private TableColumn<Supplier, String> colAddress;
    @FXML private TableColumn<Supplier, String> colStatus;

    @FXML private TextField txtName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAddress;
    @FXML private CheckBox chkStatus;

    private SupplierDAO supplierDAO;
    private ObservableList<Supplier> supplierList;
    private Supplier selectedSupplier;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        supplierDAO = new SupplierDAOImpl();
        supplierList = FXCollections.observableArrayList();
        
        setupTableColumns();
        setupEventHandlers();
        loadData();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colStatus.setCellValueFactory(cellData -> {
            Supplier supplier = cellData.getValue();
            String status = supplier.isStatus() ? "Hoạt động" : "Ngưng hoạt động";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        
        tableSupplier.setItems(supplierList);
    }

    private void setupEventHandlers() {
        tableSupplier.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                selectedSupplier = newSelection;
                if (selectedSupplier != null) {
                    fillForm(selectedSupplier);
                }
            }
        );
    }

    private void loadData() {
        try {
            List<Supplier> suppliers = supplierDAO.getAll();
            supplierList.clear();
            supplierList.addAll(suppliers);
            System.out.println("Đã load " + suppliers.size() + " nhà cung cấp");
        } catch (SQLException e) {
            System.err.println("SQLException khi load nhà cung cấp: " + e.getMessage());
            AlertUtils.showDatabaseError("Không thể tải danh sách nhà cung cấp");
        } catch (RuntimeException e) {
            System.err.println("RuntimeException khi load nhà cung cấp: " + e.getMessage());
            AlertUtils.showDatabaseError("Không thể kết nối cơ sở dữ liệu");
        }
    }

    private void fillForm(Supplier supplier) {
        txtName.setText(supplier.getName());
        txtPhone.setText(supplier.getPhone());
        txtEmail.setText(supplier.getEmail());
        txtAddress.setText(supplier.getAddress());
        chkStatus.setSelected(supplier.isStatus());
    }

    private void clearForm() {
        txtName.clear();
        txtPhone.clear();
        txtEmail.clear();
        txtAddress.clear();
        chkStatus.setSelected(true);
        selectedSupplier = null;
    }

    private boolean validateInput() {
        if (ValidationUtils.isEmpty(txtName.getText())) {
            AlertUtils.showValidationError("Vui lòng nhập tên nhà cung cấp");
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

    private Supplier createSupplierFromForm() {
        Supplier supplier = new Supplier();
        supplier.setName(txtName.getText().trim());
        supplier.setPhone(txtPhone.getText().trim());
        supplier.setEmail(txtEmail.getText().trim());
        supplier.setAddress(txtAddress.getText().trim());
        supplier.setStatus(chkStatus.isSelected());
        return supplier;
    }

    @FXML
    private void handleSearch() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            loadData();
        } else {
            try {
                List<Supplier> results = supplierDAO.searchByName(searchText);
                supplierList.clear();
                supplierList.addAll(results);
                AlertUtils.showInfo("Tìm kiếm", "Tìm thấy " + results.size() + " nhà cung cấp");
            } catch (SQLException e) {
                System.err.println("SQLException khi tìm kiếm: " + e.getMessage());
                AlertUtils.showDatabaseError("Không thể tìm kiếm nhà cung cấp");
            } catch (RuntimeException e) {
                System.err.println("RuntimeException khi tìm kiếm: " + e.getMessage());
                AlertUtils.showDatabaseError("Không thể kết nối cơ sở dữ liệu");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
        clearForm();
        AlertUtils.showSuccess("Đã tải lại dữ liệu thành công");
    }

    @FXML
    private void handleAdd() {
        if (!validateInput()) return;
        
        try {
            Supplier supplier = createSupplierFromForm();
            
            if (supplierDAO.add(supplier)) {
                loadData();
                clearForm();
                AlertUtils.showSuccess("Thêm nhà cung cấp thành công");
            } else {
                AlertUtils.showError("Lỗi", "Không thể thêm nhà cung cấp");
            }
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleSQLException(e, "thêm nhà cung cấp");
        } catch (RuntimeException e) {
            System.err.println("RuntimeException khi thêm nhà cung cấp: " + e.getMessage());
            AlertUtils.showDatabaseError("Không thể kết nối cơ sở dữ liệu");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedSupplier == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn nhà cung cấp cần sửa");
            return;
        }
        
        if (!validateInput()) return;
        
        try {
            Supplier supplier = createSupplierFromForm();
            supplier.setId(selectedSupplier.getId());
            
            if (supplierDAO.update(supplier)) {
                loadData();
                clearForm();
                AlertUtils.showSuccess("Cập nhật nhà cung cấp thành công");
            } else {
                AlertUtils.showError("Lỗi", "Không thể cập nhật nhà cung cấp");
            }
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleSQLException(e, "cập nhật nhà cung cấp");
        } catch (RuntimeException e) {
            System.err.println("RuntimeException khi cập nhật nhà cung cấp: " + e.getMessage());
            AlertUtils.showDatabaseError("Không thể kết nối cơ sở dữ liệu");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedSupplier == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn nhà cung cấp cần xóa");
            return;
        }
        
        Optional<ButtonType> result = AlertUtils.showConfirmation("Xác nhận", 
            "Bạn có chắc chắn muốn xóa nhà cung cấp: " + selectedSupplier.getName() + "?");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (supplierDAO.delete(selectedSupplier.getId())) {
                    loadData();
                    clearForm();
                    AlertUtils.showSuccess("Xóa nhà cung cấp thành công");
                } else {
                    AlertUtils.showError("Lỗi", "Không thể xóa nhà cung cấp");
                }
            } catch (SQLException e) {
                DatabaseExceptionHandler.handleSQLException(e, "xóa nhà cung cấp");
            } catch (RuntimeException e) {
                System.err.println("RuntimeException khi xóa nhà cung cấp: " + e.getMessage());
                AlertUtils.showDatabaseError("Không thể kết nối cơ sở dữ liệu");
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    @FXML
    private void handleViewDetails() {
        if (selectedSupplier == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn nhà cung cấp để xem chi tiết");
            return;
        }
        
        AlertUtils.showInfo("Chi tiết nhà cung cấp", 
            "Tên: " + selectedSupplier.getName() + "\n" +
            "SĐT: " + selectedSupplier.getPhone() + "\n" +
            "Email: " + selectedSupplier.getEmail() + "\n" +
            "Địa chỉ: " + selectedSupplier.getAddress());
    }

    @FXML
    private void handleExportExcel() {
        AlertUtils.showInfo("Xuất Excel", "Chức năng xuất Excel sẽ được phát triển sau");
    }
} 