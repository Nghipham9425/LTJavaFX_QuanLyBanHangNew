/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sv.qlbh.controller;

import com.sv.qlbh.dao.CategoryDAO;
import com.sv.qlbh.dao.CategoryDAOImpl;
import com.sv.qlbh.dao.ProductDAO;
import com.sv.qlbh.dao.ProductDAOImpl;
import com.sv.qlbh.dao.SupplierDAO;
import com.sv.qlbh.dao.SupplierDAOImpl;
import com.sv.qlbh.models.Category;
import com.sv.qlbh.models.Product;
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

/**
 * FXML Controller class
 *
 * @author nghip
 */
public class ProductCategoryController implements Initializable {

    // Top Search Controls
    @FXML private TextField txtSearch;
    @FXML private ComboBox<Category> cbCategoryFilter;

    // Category Panel Controls
    @FXML private TableView<Category> tableCategory;
    @FXML private TableColumn<Category, Integer> colCatId;
    @FXML private TableColumn<Category, String> colCatName;
    @FXML private TableColumn<Category, String> colCatDesc;
    
    @FXML private TextField txtCatName;
    @FXML private TextField txtCatDesc;

    // Product Panel Controls
    @FXML private TableView<Product> tableProduct;
    @FXML private TableColumn<Product, Integer> colProdId;
    @FXML private TableColumn<Product, String> colProdName;
    @FXML private TableColumn<Product, String> colProdBarcode;
    @FXML private TableColumn<Product, Double> colProdPrice;
    @FXML private TableColumn<Product, Double> colProdCostPrice;
    @FXML private TableColumn<Product, Integer> colProdStock;
    @FXML private TableColumn<Product, String> colProdCategory;
    @FXML private TableColumn<Product, String> colProdSupplier;
    @FXML private TableColumn<Product, String> colProdStatus;
    
    @FXML private TextField txtProdName;
    @FXML private TextField txtProdBarcode;
    @FXML private TextField txtProdPrice;
    @FXML private TextField txtProdCostPrice;
    @FXML private TextField txtProdStock;
    @FXML private ComboBox<Category> cbProdCategory;
    @FXML private ComboBox<Supplier> cbProdSupplier;
    @FXML private CheckBox chkProdStatus;

    // DAOs
    private CategoryDAO categoryDAO;
    private ProductDAO productDAO;
    private SupplierDAO supplierDAO;
    
    // Observable Lists
    private ObservableList<Category> categoryList;
    private ObservableList<Product> productList;
    private ObservableList<Supplier> supplierList;
    
    // Selected items
    private Category selectedCategory;
    private Product selectedProduct;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoryDAO = new CategoryDAOImpl();
        productDAO = new ProductDAOImpl();
        supplierDAO = new SupplierDAOImpl();
        
        categoryList = FXCollections.observableArrayList();
        productList = FXCollections.observableArrayList();
        supplierList = FXCollections.observableArrayList();
        
        setupTableColumns();
        setupCategoryComboBoxes();
        setupEventHandlers();
        loadData();
    }

    private void setupTableColumns() {
        // Category table columns
        colCatId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCatName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCatDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Product table columns
        colProdId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProdName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProdBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colProdPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colProdCostPrice.setCellValueFactory(new PropertyValueFactory<>("costPrice"));
        colProdStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colProdCategory.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            if (product != null && product.getCategoryId() != 0) {
                try {
                    Category category = categoryDAO.getById(product.getCategoryId());
                    return new javafx.beans.property.SimpleStringProperty(
                        category != null ? category.getName() : "N/A"
                    );
                } catch (SQLException e) {
                    return new javafx.beans.property.SimpleStringProperty("N/A");
                }
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        colProdSupplier.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            if (product != null && product.getSupplierId() != 0) {
                try {
                    Supplier supplier = supplierDAO.getById(product.getSupplierId());
                    return new javafx.beans.property.SimpleStringProperty(
                        supplier != null ? supplier.getName() : "N/A"
                    );
                } catch (SQLException e) {
                    return new javafx.beans.property.SimpleStringProperty("N/A");
                }
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        colProdStatus.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            String status = product.isStatus() ? "Hoạt động" : "Không hoạt động";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        
        tableCategory.setItems(categoryList);
        tableProduct.setItems(productList);
    }

    private void setupEventHandlers() {
        tableCategory.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                selectedCategory = newSelection;
                if (selectedCategory != null) {
                    fillCategoryForm(selectedCategory);
                }
            }
        );
        
        tableProduct.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                selectedProduct = newSelection;
                if (selectedProduct != null) {
                    fillProductForm(selectedProduct);
                }
            }
        );
        
        cbCategoryFilter.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                filterProducts();
            }
        );
    }

    private void loadData() {
        loadCategories();
        loadSuppliers();
        loadProducts();
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryDAO.getAll();
            categoryList.clear();
            categoryList.addAll(categories);
            
            cbCategoryFilter.getItems().clear();
            Category allCategories = new Category();
            allCategories.setId(0);
            allCategories.setName("Tất cả danh mục");
            cbCategoryFilter.getItems().add(allCategories);
            cbCategoryFilter.getItems().addAll(categories);
            cbCategoryFilter.setValue(allCategories);
            
            cbProdCategory.getItems().clear();
            cbProdCategory.getItems().addAll(categories);
            
        } catch (SQLException e) {
            System.err.println("SQLException khi load danh mục: " + e.getMessage());
            AlertUtils.showDatabaseError("Không thể kết nối database để tải danh mục");
        }
    }
    
    private void setupCategoryComboBoxes() {
        cbCategoryFilter.setCellFactory(listView -> new javafx.scene.control.ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                } else {
                    setText(category.getName());
                }
            }
        });
        
        cbCategoryFilter.setButtonCell(new javafx.scene.control.ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                } else {
                    setText(category.getName());
                }
            }
        });
        
        cbProdCategory.setCellFactory(listView -> new javafx.scene.control.ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                } else {
                    setText(category.getName());
                }
            }
        });
        
        cbProdCategory.setButtonCell(new javafx.scene.control.ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                } else {
                    setText(category.getName());
                }
            }
        });
        
        cbProdSupplier.setCellFactory(listView -> new javafx.scene.control.ListCell<Supplier>() {
            @Override
            protected void updateItem(Supplier supplier, boolean empty) {
                super.updateItem(supplier, empty);
                if (empty || supplier == null) {
                    setText(null);
                } else {
                    setText(supplier.getName());
                }
            }
        });
        
        cbProdSupplier.setButtonCell(new javafx.scene.control.ListCell<Supplier>() {
            @Override
            protected void updateItem(Supplier supplier, boolean empty) {
                super.updateItem(supplier, empty);
                if (empty || supplier == null) {
                    setText(null);
                } else {
                    setText(supplier.getName());
                }
            }
        });
    }

    private void loadProducts() {
        try {
            List<Product> products = productDAO.getAll();
            productList.clear();
            productList.addAll(products);
        } catch (SQLException e) {
            System.err.println("SQLException khi load sản phẩm: " + e.getMessage());
            AlertUtils.showDatabaseError("Không thể kết nối database để tải sản phẩm");
        }
    }

    private void loadSuppliers() {
        try {
            List<Supplier> suppliers = supplierDAO.getAll();
            supplierList.clear();
            supplierList.addAll(suppliers);
            
            cbProdSupplier.getItems().clear();
            cbProdSupplier.getItems().addAll(suppliers);
            
        } catch (SQLException e) {
            System.err.println("SQLException khi load nhà cung cấp: " + e.getMessage());
            AlertUtils.showDatabaseError("Không thể kết nối database để tải nhà cung cấp");
        }
    }

    private void fillCategoryForm(Category category) {
        txtCatName.setText(category.getName());
        txtCatDesc.setText(category.getDescription());
    }

    private void fillProductForm(Product product) {
        txtProdName.setText(product.getName());
        txtProdBarcode.setText(product.getBarcode());
        txtProdPrice.setText(String.valueOf(product.getPrice()));
        txtProdCostPrice.setText(String.valueOf(product.getCostPrice()));
        txtProdStock.setText(String.valueOf(product.getStock()));
        chkProdStatus.setSelected(product.isStatus());
        
        if (product.getCategoryId() != 0) {
            for (Category category : cbProdCategory.getItems()) {
                if (category.getId() == product.getCategoryId()) {
                    cbProdCategory.setValue(category);
                    break;
                }
            }
        } else {
            cbProdCategory.setValue(null);
        }
        
        if (product.getSupplierId() != 0) {
            for (Supplier supplier : cbProdSupplier.getItems()) {
                if (supplier.getId() == product.getSupplierId()) {
                    cbProdSupplier.setValue(supplier);
                    break;
                }
            }
        } else {
            cbProdSupplier.setValue(null);
        }
    }

    private void clearCategoryForm() {
        txtCatName.clear();
        txtCatDesc.clear();
        selectedCategory = null;
    }

    private void clearProductForm() {
        txtProdName.clear();
        txtProdBarcode.clear();
        txtProdPrice.clear();
        txtProdCostPrice.clear();
        txtProdStock.clear();
        cbProdCategory.setValue(null);
        cbProdSupplier.setValue(null);
        chkProdStatus.setSelected(false);
        selectedProduct = null;
    }

    // FXML Event Handlers

    @FXML
    private void handleSearch() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            loadProducts();
            return;
        }
        
        try {
            List<Product> searchResults = productDAO.searchByNameOrBarcode(searchText);
            productList.clear();
            productList.addAll(searchResults);
        } catch (SQLException e) {
            System.err.println("SQLException khi tìm kiếm sản phẩm: " + e.getMessage());
            AlertUtils.showDatabaseError("Không thể tìm kiếm sản phẩm");
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
        clearCategoryForm();
        clearProductForm();
        txtSearch.clear();
        AlertUtils.showSuccess("Đã làm mới dữ liệu");
    }

    private void filterProducts() {
        Category selectedFilter = cbCategoryFilter.getValue();
        if (selectedFilter == null || selectedFilter.getId() == 0) {
            loadProducts();
            return;
        }
        
        try {
            List<Product> filteredProducts = productDAO.getByCategory(selectedFilter.getId());
            productList.clear();
            productList.addAll(filteredProducts);
        } catch (SQLException e) {
            AlertUtils.showError("Lỗi lọc sản phẩm", e.getMessage());
        }
    }

    // Category Management

    @FXML
    private void handleAddCategory() {
        if (!validateCategoryInput()) return;
        
        try {
            Category category = new Category();
            category.setName(txtCatName.getText().trim());
            category.setDescription(txtCatDesc.getText().trim());
            
            categoryDAO.add(category);
            loadCategories();
            clearCategoryForm();
            AlertUtils.showSuccess("Đã thêm danh mục mới: " + category.getName());
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleSQLException(e, "thêm danh mục");
        }
    }

    @FXML
    private void handleUpdateCategory() {
        if (selectedCategory == null) {
            AlertUtils.showWarning("Chưa chọn danh mục", "Vui lòng chọn danh mục cần sửa");
            return;
        }
        
        if (!validateCategoryInput()) return;
        
        try {
            String oldName = selectedCategory.getName();
            String newName = txtCatName.getText().trim();
            selectedCategory.setName(newName);
            selectedCategory.setDescription(txtCatDesc.getText().trim());
            
            categoryDAO.update(selectedCategory);
            loadCategories();
            loadProducts();
            clearCategoryForm();
            AlertUtils.showSuccess("Đã cập nhật danh mục: " + oldName + " → " + newName);
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleSQLException(e, "cập nhật danh mục");
        }
    }

    @FXML
    private void handleDeleteCategory() {
        if (selectedCategory == null) {
            AlertUtils.showWarning("Chưa chọn danh mục", "Vui lòng chọn danh mục cần xóa");
            return;
        }
        
        try {
            List<Product> productsInCategory = productDAO.getByCategory(selectedCategory.getId());
            if (!productsInCategory.isEmpty()) {
                AlertUtils.showWarning("Không thể xóa danh mục", 
                    "Danh mục '" + selectedCategory.getName() + "' đang được sử dụng bởi " + 
                    productsInCategory.size() + " sản phẩm.\n\n" +
                    "Vui lòng xóa hoặc chuyển các sản phẩm sang danh mục khác trước khi xóa danh mục này.");
                return;
            }
        } catch (SQLException e) {
            AlertUtils.showError("Lỗi kiểm tra danh mục", "Không thể kiểm tra sản phẩm trong danh mục: " + e.getMessage());
            return;
        }
        
        Optional<ButtonType> result = AlertUtils.showConfirmation("Xác nhận xóa", 
            "Bạn có chắc chắn muốn xóa danh mục '" + selectedCategory.getName() + "'?\n\n" +
            "Hành động này không thể hoàn tác.");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String categoryName = selectedCategory.getName();
                categoryDAO.delete(selectedCategory.getId());
                loadCategories();
                loadProducts();
                clearCategoryForm();
                AlertUtils.showSuccess("Đã xóa danh mục: " + categoryName);
            } catch (SQLException e) {
                AlertUtils.showError("Lỗi xóa danh mục", e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleClearCategory() {
        clearCategoryForm();
        tableCategory.getSelectionModel().clearSelection();
    }

    // Product Management

    @FXML
    private void handleAddProduct() {
        if (!validateProductInput()) return;
        
        try {
            Product product = new Product();
            fillProductFromForm(product);
            
            productDAO.add(product);
            loadProducts();
            clearProductForm();
            AlertUtils.showSuccess("Đã thêm sản phẩm mới: " + product.getName());
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleSQLException(e, "thêm sản phẩm");
        }
    }

    @FXML
    private void handleUpdateProduct() {
        if (selectedProduct == null) {
            AlertUtils.showWarning("Chưa chọn sản phẩm", "Vui lòng chọn sản phẩm cần sửa");
            return;
        }
        
        if (!validateProductInput()) return;
        
        try {
            String oldName = selectedProduct.getName();
            String newName = txtProdName.getText().trim();
            fillProductFromForm(selectedProduct);
            
            productDAO.update(selectedProduct);
            loadProducts();
            clearProductForm();
            AlertUtils.showSuccess("Đã cập nhật sản phẩm: " + oldName + " → " + newName);
        } catch (SQLException e) {
            AlertUtils.showError("Lỗi cập nhật sản phẩm", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteProduct() {
        if (selectedProduct == null) {
            AlertUtils.showWarning("Chưa chọn sản phẩm", "Vui lòng chọn sản phẩm cần xóa");
            return;
        }
        
        Optional<ButtonType> result = AlertUtils.showConfirmation("Xác nhận xóa", 
            "Bạn có chắc chắn muốn xóa sản phẩm '" + selectedProduct.getName() + "'?\n\n" +
            "Lưu ý: Không thể xóa sản phẩm đã có giao dịch bán hàng hoặc nhập kho.");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String productName = selectedProduct.getName();
                productDAO.delete(selectedProduct.getId());
                loadProducts();
                clearProductForm();
                AlertUtils.showSuccess("Đã xóa sản phẩm: " + productName);
            } catch (SQLException e) {
                System.err.println("SQLException khi xóa sản phẩm: " + e.getMessage());
                System.err.println("Error Code: " + e.getErrorCode());
                
                if (e.getErrorCode() == 1451) {
                    Alert choice = new Alert(Alert.AlertType.CONFIRMATION);
                    choice.setTitle("Không thể xóa - Chọn hành động khác");
                    choice.setHeaderText("Sản phẩm đang được sử dụng trong hệ thống");
                    choice.setContentText("Sản phẩm '" + selectedProduct.getName() + "' đang có giao dịch liên quan.\n\n" +
                        "Bạn muốn đặt sản phẩm về trạng thái 'Không hoạt động' thay vì xóa?");
                    
                    ButtonType deactivateBtn = new ButtonType("Đặt không hoạt động");
                    ButtonType cancelBtn = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
                    choice.getButtonTypes().setAll(deactivateBtn, cancelBtn);
                    
                    Optional<ButtonType> choiceResult = choice.showAndWait();
                    if (choiceResult.isPresent() && choiceResult.get() == deactivateBtn) {
                        handleDeactivateProduct();
                    }
                } else {
                    AlertUtils.showError("Lỗi xóa sản phẩm", "Không thể xóa: " + e.getMessage());
                }
            }
        }
    }
    
    private void handleDeactivateProduct() {
        try {
            selectedProduct.setStatus(false);
            productDAO.update(selectedProduct);
            loadProducts();
            clearProductForm();
            AlertUtils.showSuccess("Đã đặt sản phẩm '" + selectedProduct.getName() + "' về trạng thái không hoạt động");
        } catch (SQLException e) {
            AlertUtils.showError("Lỗi", "Không thể cập nhật trạng thái sản phẩm: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClearProduct() {
        clearProductForm();
        tableProduct.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleViewProduct() {
        if (selectedProduct == null) {
            AlertUtils.showWarning("Chưa chọn sản phẩm", "Vui lòng chọn sản phẩm để xem chi tiết");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(selectedProduct.getId()).append("\n");
        details.append("Tên sản phẩm: ").append(selectedProduct.getName()).append("\n");
        details.append("Mã vạch: ").append(selectedProduct.getBarcode()).append("\n");
        details.append("Giá bán: ").append(selectedProduct.getPrice()).append("\n");
        details.append("Giá nhập: ").append(selectedProduct.getCostPrice()).append("\n");
        details.append("Tồn kho: ").append(selectedProduct.getStock()).append("\n");
        
        if (selectedProduct.getCategoryId() != 0) {
            try {
                Category category = categoryDAO.getById(selectedProduct.getCategoryId());
                details.append("Danh mục: ").append(category != null ? category.getName() : "N/A").append("\n");
            } catch (SQLException e) {
                details.append("Danh mục: N/A\n");
            }
        }
        
        if (selectedProduct.getSupplierId() != 0) {
            try {
                Supplier supplier = supplierDAO.getById(selectedProduct.getSupplierId());
                details.append("Nhà cung cấp: ").append(supplier != null ? supplier.getName() : "N/A").append("\n");
            } catch (SQLException e) {
                details.append("Nhà cung cấp: N/A\n");
            }
        }
        
        details.append("Trạng thái: ").append(selectedProduct.isStatus() ? "Hoạt động" : "Không hoạt động");
        
        AlertUtils.showInfo("Chi tiết sản phẩm", details.toString());
    }

    @FXML
    private void handleExportExcel() {
        AlertUtils.showInfo("Xuất Excel", "Tính năng xuất Excel sẽ được triển khai sau");
    }

    // Validation Methods

    private boolean validateCategoryInput() {
        if (ValidationUtils.isEmpty(txtCatName.getText())) {
            AlertUtils.showValidationError("Vui lòng nhập tên danh mục");
            txtCatName.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateProductInput() {
        if (ValidationUtils.isEmpty(txtProdName.getText())) {
            AlertUtils.showValidationError("Vui lòng nhập tên sản phẩm");
            txtProdName.requestFocus();
            return false;
        }
        
        if (ValidationUtils.isEmpty(txtProdBarcode.getText())) {
            AlertUtils.showValidationError("Vui lòng nhập mã vạch");
            txtProdBarcode.requestFocus();
            return false;
        }
        
        if (!ValidationUtils.isPositiveNumber(txtProdPrice.getText())) {
            AlertUtils.showValidationError("Giá bán phải là số dương");
            txtProdPrice.requestFocus();
            return false;
        }
        
        if (!ValidationUtils.isPositiveNumber(txtProdCostPrice.getText())) {
            AlertUtils.showValidationError("Giá nhập phải là số dương");
            txtProdCostPrice.requestFocus();
            return false;
        }
        
        try {
            Integer.parseInt(txtProdStock.getText().trim());
        } catch (NumberFormatException e) {
            AlertUtils.showValidationError("Tồn kho phải là số nguyên");
            txtProdStock.requestFocus();
            return false;
        }
        
        if (cbProdCategory.getValue() == null) {
            AlertUtils.showValidationError("Vui lòng chọn danh mục");
            cbProdCategory.requestFocus();
            return false;
        }
        
        if (cbProdSupplier.getValue() == null) {
            AlertUtils.showValidationError("Vui lòng chọn nhà cung cấp");
            cbProdSupplier.requestFocus();
            return false;
        }
        
        return true;
    }

    private void fillProductFromForm(Product product) {
        product.setName(txtProdName.getText().trim());
        product.setBarcode(txtProdBarcode.getText().trim());
        product.setPrice(Double.parseDouble(txtProdPrice.getText().trim()));
        product.setCostPrice(Double.parseDouble(txtProdCostPrice.getText().trim()));
        product.setStock(Integer.parseInt(txtProdStock.getText().trim()));
        product.setCategoryId(cbProdCategory.getValue().getId());
        
        if (cbProdSupplier.getValue() != null) {
            product.setSupplierId(cbProdSupplier.getValue().getId());
        } else {
            product.setSupplierId(0);
        }
        
        product.setStatus(chkProdStatus.isSelected());
    }
}
