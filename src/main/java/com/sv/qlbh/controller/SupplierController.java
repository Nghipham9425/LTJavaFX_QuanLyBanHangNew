package com.sv.qlbh.controller;

import com.sv.qlbh.dao.SupplierDAO;
import com.sv.qlbh.dao.SupplierDAOImpl;
import com.sv.qlbh.models.Supplier;
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
    @FXML private TableColumn<Supplier, String> colContact;
    @FXML private TableColumn<Supplier, String> colStatus;

    @FXML private TextField txtName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAddress;
    @FXML private TextField txtContact;
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
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
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
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            showError("Lỗi cơ sở dữ liệu", "Không thể tải danh sách nhà cung cấp");
        } catch (RuntimeException e) {
            System.err.println("RuntimeException khi load nhà cung cấp: " + e.getMessage());
            showError("Lỗi hệ thống", "Không thể kết nối cơ sở dữ liệu");
        }
    }

    private void fillForm(Supplier supplier) {
        txtName.setText(supplier.getName());
        txtPhone.setText(supplier.getPhone());
        txtEmail.setText(supplier.getEmail());
        txtAddress.setText(supplier.getAddress());
        txtContact.setText(supplier.getContactPerson());
        chkStatus.setSelected(supplier.isStatus());
    }

    private void clearForm() {
        txtName.clear();
        txtPhone.clear();
        txtEmail.clear();
        txtAddress.clear();
        txtContact.clear();
        chkStatus.setSelected(true);
        selectedSupplier = null;
    }

    private boolean validateInput() {
        if (txtName.getText().trim().isEmpty()) {
            showWarning("Cảnh báo", "Vui lòng nhập tên nhà cung cấp");
            return false;
        }
        
        if (txtPhone.getText().trim().isEmpty()) {
            showWarning("Cảnh báo", "Vui lòng nhập số điện thoại");
            return false;
        }
        
        String phone = txtPhone.getText().trim();
        if (!phone.matches("^[0-9]{10,11}$")) {
            showWarning("Cảnh báo", "Số điện thoại phải có 10-11 chữ số");
            return false;
        }
        
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showWarning("Cảnh báo", "Email không hợp lệ");
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
        supplier.setContactPerson(txtContact.getText().trim());
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
                showInfo("Tìm kiếm", "Tìm thấy " + results.size() + " nhà cung cấp");
            } catch (SQLException e) {
                System.err.println("SQLException khi tìm kiếm: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());
                showError("Lỗi cơ sở dữ liệu", "Không thể tìm kiếm nhà cung cấp");
            } catch (RuntimeException e) {
                System.err.println("RuntimeException khi tìm kiếm: " + e.getMessage());
                showError("Lỗi hệ thống", "Không thể kết nối cơ sở dữ liệu");
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
            Supplier supplier = createSupplierFromForm();
            
            if (supplierDAO.add(supplier)) {
                loadData();
                clearForm();
                showInfo("Thành công", "Thêm nhà cung cấp thành công");
            } else {
                showError("Lỗi", "Không thể thêm nhà cung cấp");
            }
        } catch (SQLException e) {
            System.err.println("SQLException khi thêm nhà cung cấp: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            
            if (e.getErrorCode() == 1062) {
                showWarning("Thông tin trùng lặp", "Thông tin nhà cung cấp đã tồn tại trong hệ thống");
            } else if (e.getErrorCode() == 1452) {
                showWarning("Lỗi ràng buộc", "Dữ liệu không hợp lệ hoặc vi phạm ràng buộc");
            } else {
                showError("Lỗi cơ sở dữ liệu", "Không thể thêm nhà cung cấp: " + e.getMessage());
            }
        } catch (RuntimeException e) {
            System.err.println("RuntimeException khi thêm nhà cung cấp: " + e.getMessage());
            showError("Lỗi hệ thống", "Không thể kết nối cơ sở dữ liệu");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedSupplier == null) {
            showWarning("Cảnh báo", "Vui lòng chọn nhà cung cấp cần sửa");
            return;
        }
        
        if (!validateInput()) return;
        
        try {
            Supplier supplier = createSupplierFromForm();
            supplier.setId(selectedSupplier.getId());
            
            if (supplierDAO.update(supplier)) {
                loadData();
                clearForm();
                showInfo("Thành công", "Cập nhật nhà cung cấp thành công");
            } else {
                showError("Lỗi", "Không thể cập nhật nhà cung cấp");
            }
        } catch (SQLException e) {
            System.err.println("SQLException khi cập nhật nhà cung cấp: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            
            if (e.getErrorCode() == 1062) {
                showWarning("Thông tin trùng lặp", "Thông tin nhà cung cấp đã tồn tại trong hệ thống");
            } else if (e.getErrorCode() == 1452) {
                showWarning("Lỗi ràng buộc", "Dữ liệu không hợp lệ hoặc vi phạm ràng buộc");
            } else {
                showError("Lỗi cơ sở dữ liệu", "Không thể cập nhật nhà cung cấp: " + e.getMessage());
            }
        } catch (RuntimeException e) {
            System.err.println("RuntimeException khi cập nhật nhà cung cấp: " + e.getMessage());
            showError("Lỗi hệ thống", "Không thể kết nối cơ sở dữ liệu");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedSupplier == null) {
            showWarning("Cảnh báo", "Vui lòng chọn nhà cung cấp cần xóa");
            return;
        }
        
        Optional<ButtonType> result = showConfirmation("Xác nhận", 
            "Bạn có chắc chắn muốn xóa nhà cung cấp: " + selectedSupplier.getName() + "?");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (supplierDAO.delete(selectedSupplier.getId())) {
                    loadData();
                    clearForm();
                    showInfo("Thành công", "Xóa nhà cung cấp thành công");
                } else {
                    showError("Lỗi", "Không thể xóa nhà cung cấp");
                }
            } catch (SQLException e) {
                System.err.println("SQLException khi xóa nhà cung cấp: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());
                
                if (e.getErrorCode() == 1451) {
                    showWarning("Không thể xóa", "Nhà cung cấp đang được sử dụng trong hệ thống, không thể xóa");
                } else {
                    showError("Lỗi cơ sở dữ liệu", "Không thể xóa nhà cung cấp: " + e.getMessage());
                }
            } catch (RuntimeException e) {
                System.err.println("RuntimeException khi xóa nhà cung cấp: " + e.getMessage());
                showError("Lỗi hệ thống", "Không thể kết nối cơ sở dữ liệu");
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
            showWarning("Cảnh báo", "Vui lòng chọn nhà cung cấp để xem chi tiết");
            return;
        }
        
        showInfo("Chi tiết nhà cung cấp", 
            "Tên: " + selectedSupplier.getName() + "\n" +
            "SĐT: " + selectedSupplier.getPhone() + "\n" +
            "Email: " + selectedSupplier.getEmail() + "\n" +
            "Địa chỉ: " + selectedSupplier.getAddress() + "\n" +
            "Liên hệ: " + selectedSupplier.getContactPerson());
    }

    @FXML
    private void handleExportExcel() {
        showInfo("Xuất Excel", "Chức năng xuất Excel sẽ được phát triển sau");
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