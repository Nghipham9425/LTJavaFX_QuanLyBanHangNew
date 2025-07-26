package com.sv.qlbh.controller;

import com.sv.qlbh.dao.CustomerDAO;
import com.sv.qlbh.dao.OrderDAO;
import com.sv.qlbh.dao.ProductDAO;
import com.sv.qlbh.models.User;
import com.sv.qlbh.utils.SessionManager;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    
    @FXML private Label dashboardWelcomeLabel;
    @FXML private VBox statCardProduct;
    @FXML private Label statLabelProduct;
    @FXML private Label statValueProduct;
    @FXML private VBox statCardCustomer;
    @FXML private Label statLabelCustomer;
    @FXML private Label statValueCustomer;
    @FXML private VBox statCardOrder;
    @FXML private Label statLabelOrder;
    @FXML private Label statValueOrder;
    @FXML private javafx.scene.control.Button btnQuickSale;
    @FXML private javafx.scene.control.Button btnQuickProduct;
    @FXML private javafx.scene.control.Button btnQuickReport;
    @FXML private VBox contentArea;
    
    private HostServices hostServices;
    

    private ProductDAO productDAO;
    private CustomerDAO customerDAO;
    private OrderDAO orderDAO;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
  
        productDAO = new ProductDAO();
        customerDAO = new CustomerDAO();
        orderDAO = new OrderDAO();

        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            String roleName = getRoleName(currentUser.getRole());
            dashboardWelcomeLabel.setText("Chào mừng " + roleName + " " + currentUser.getFullName());
        } else {
            dashboardWelcomeLabel.setText("Chào mừng đến với hệ thống bán hàng");
        }
        loadHomeContent();

    }
    
    private void loadHomeContent() {
        createDefaultDashboardContent();
    }
    
    private void createDefaultDashboardContent() {
        try {
            // Welcome message
            dashboardWelcomeLabel.setText("Chào mừng đến với hệ thống Quản Lý Bán Hàng");
            // Stat cards
            statLabelProduct.setText("Sản phẩm");
            statValueProduct.setText(String.valueOf(getProductCount()));
            statLabelCustomer.setText("Khách hàng");
            statValueCustomer.setText(String.valueOf(getCustomerCount()));
            statLabelOrder.setText("Tổng đơn hàng");
            statValueOrder.setText(String.valueOf(getOrderCount()));
            // Quick actions
            btnQuickSale.setOnAction(e -> handleSalesManagement(null));
            btnQuickProduct.setOnAction(e -> handleProductManagement(null));
            btnQuickReport.setOnAction(e -> handleReportManagement(null));
        } catch (RuntimeException e) {
            dashboardWelcomeLabel.setText("Chào mừng đến với Dashboard!");
        }
    }

    private int getProductCount() {
        try {
            return productDAO.getAll().size();
        } catch (SQLException e) {
            System.err.println("Error getting product count: " + e.getMessage());
            return 0;
        }
    }
    
    private int getCustomerCount() {
        try {
            return customerDAO.getAll().size();
        } catch (SQLException e) {
            System.err.println("Error getting customer count: " + e.getMessage());
            return 0;
        }
    }
    
    private int getOrderCount() {
        try {
            return orderDAO.getAllOrders().size();
        } catch (SQLException e) {
            System.err.println("Error getting order count: " + e.getMessage());
            return 0;
        }
    }
    
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
    
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            
            if (fxmlPath.equals("/fxml/POS.fxml")) {
                com.sv.qlbh.controller.POSController posController = loader.getController();
                if (posController != null && hostServices != null) {
                    posController.setHostServices(hostServices);
                }
            }
            
            contentArea.getChildren().add(content);
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể load nội dung từ: " + fxmlPath);
        }
    }
    
    private String getRoleName(String role) {
        switch (role) {
            case "ADMIN":
                return "Quản trị viên";
            case "STAFF":
                return "Nhân viên";
            case "ACCOUNTANT":
                return "Kế toán";
            case "WAREHOUSE":
                return "Thủ kho";
            default:
                return "";
        }
    }
    
    @FXML
    private void handleCreateOrder() {
        dashboardWelcomeLabel.setText("💰 Bán Hàng - POS");
        loadContent("/fxml/Sales.fxml");
    }
    
    @FXML
    private void handleHomeManagement(javafx.scene.input.MouseEvent event) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            String roleName = getRoleName(currentUser.getRole());
            dashboardWelcomeLabel.setText("Chào mừng " + roleName + " " + currentUser.getFullName());
        } else {
            dashboardWelcomeLabel.setText("Chào mừng đến với hệ thống bán hàng");
        }
        loadHomeContent();
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleProductManagement(javafx.scene.input.MouseEvent event) {
        dashboardWelcomeLabel.setText("Quản lý sản phẩm và danh mục");
        loadContent("/fxml/ProductCategory.fxml");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleCustomerManagement(javafx.scene.input.MouseEvent event) {
        dashboardWelcomeLabel.setText("Quản lý khách hàng");
        loadContent("/fxml/Customer.fxml");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleSupplierManagement(javafx.scene.input.MouseEvent event) {
        dashboardWelcomeLabel.setText("Quản lý nhà cung cấp");
        loadContent("/fxml/Supplier.fxml");
        updateActiveMenuItem(event);
    }

    @FXML
    private void handleOrderManagement(javafx.scene.input.MouseEvent event) {
        dashboardWelcomeLabel.setText("Quản lý đơn hàng");
        loadContent("/fxml/Order.fxml");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleInventoryManagement(javafx.scene.input.MouseEvent event) {
        dashboardWelcomeLabel.setText("Quản lý kho");
        loadContent("/fxml/Inventory.fxml");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleSalesManagement(javafx.scene.input.MouseEvent event) {
        dashboardWelcomeLabel.setText("💰 Bán Hàng - POS");
        loadContent("/fxml/POS.fxml");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleReportManagement(javafx.scene.input.MouseEvent event) {
        dashboardWelcomeLabel.setText("Báo cáo");
        loadContent("/fxml/Report.fxml");
        updateActiveMenuItem(event);
    }
    @FXML
    private void handleUserManagement(javafx.scene.input.MouseEvent event) {
        dashboardWelcomeLabel.setText("Quản lý người dùng");
        loadContent("/fxml/UserManagement.fxml");
        updateActiveMenuItem(event);
    }
    @FXML
    private void handleShiftManagement(javafx.scene.input.MouseEvent event) {
        dashboardWelcomeLabel.setText("Quản lý ca làm");
        loadContent("/fxml/Shift.fxml");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleSettingsManagement(javafx.scene.input.MouseEvent event) {
        dashboardWelcomeLabel.setText("Cài đặt hệ thống");
        updateActiveMenuItem(event);
    }
    
    private void updateActiveMenuItem(javafx.scene.input.MouseEvent event) {
        Node sidebar = ((Node) event.getSource()).getParent().getParent();
        sidebar.lookupAll(".menu-item").forEach(node -> {
            node.getStyleClass().remove("active");
        });
        
        Node clickedItem = (Node) event.getSource();
        clickedItem.getStyleClass().add("active");
    }

    @FXML
    private void handleLogout(javafx.scene.input.MouseEvent event) {
        try {
            SessionManager.logout();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Đăng nhập - Quản Lý Bán Hàng");
            stage.setResizable(false);
            
            stage.setWidth(700);
            stage.setHeight(400);
            stage.setMinWidth(700);
            stage.setMinHeight(400);
            stage.setMaxWidth(700);
            stage.setMaxHeight(400);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}