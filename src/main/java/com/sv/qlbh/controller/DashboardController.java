package com.sv.qlbh.controller;

import com.sv.qlbh.dao.ProductDAO;
import com.sv.qlbh.dao.CustomerDAO;
import com.sv.qlbh.dao.OrderDAO;
import com.sv.qlbh.models.User;
import com.sv.qlbh.utils.SessionManager;

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
    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private VBox contentArea;
    
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
            welcomeLabel.setText("Chào mừng " + roleName + " " + currentUser.getFullName());
        } else {
            welcomeLabel.setText("Chào mừng đến với hệ thống bán hàng");
        }
        
        loadHomeContent();
        System.out.println("Dashboard loaded successfully!");
    }
    
    private void loadHomeContent() {
        createDefaultDashboardContent();
    }
    
    private void createDefaultDashboardContent() {
        contentArea.getChildren().clear();
        
        try {
            javafx.scene.layout.VBox mainContainer = new javafx.scene.layout.VBox(20);
            mainContainer.setPadding(new javafx.geometry.Insets(20));
            
            javafx.scene.layout.HBox statsRow = new javafx.scene.layout.HBox(18);
            statsRow.setAlignment(javafx.geometry.Pos.CENTER);
            
            statsRow.getChildren().addAll(
                createStatCard("Sản phẩm", String.valueOf(getProductCount()), "stat-card stat-blue"),
                createStatCard("Khách hàng", String.valueOf(getCustomerCount()), "stat-card stat-green"),
                createStatCard("Tổng đơn hàng", String.valueOf(getOrderCount()), "stat-card stat-orange"),
                createStatCard("Doanh thu", "₫ " + formatCurrency(getTotalRevenue()), "stat-card stat-purple")
            );
            

            
            javafx.scene.layout.HBox toolsRow = new javafx.scene.layout.HBox(18);
            toolsRow.setAlignment(javafx.geometry.Pos.CENTER);
            toolsRow.getStyleClass().add("quick-tools");
            
            toolsRow.getChildren().addAll(
                createQuickButton("Tạo đơn hàng mới"),
                createQuickButton("Nhập kho"),
                createQuickButton("Báo cáo")
            );
            
            mainContainer.getChildren().addAll(statsRow, toolsRow);
            contentArea.getChildren().add(mainContainer);
            
        } catch (RuntimeException e) {
            javafx.scene.control.Label welcomeMsg = new javafx.scene.control.Label("Chào mừng đến với Dashboard!");
            welcomeMsg.setStyle("-fx-font-size: 18px; -fx-text-fill: #666; -fx-padding: 20px;");
            contentArea.getChildren().add(welcomeMsg);
        }
    }
    
    private javafx.scene.layout.VBox createStatCard(String label, String value, String styleClass) {
        javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(8);
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setPrefSize(180, 120);
        card.getStyleClass().addAll(styleClass.split(" "));
        
        javafx.scene.control.Label labelNode = new javafx.scene.control.Label(label);
        labelNode.getStyleClass().add("stat-label");
        
        javafx.scene.control.Label valueNode = new javafx.scene.control.Label(value);
        valueNode.getStyleClass().add("stat-value");
        
        card.getChildren().addAll(labelNode, valueNode);
        return card;
    }
    
    private javafx.scene.layout.VBox createInfoCard(String title, String content, double width, double height) {
        javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(12);
        card.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        card.setPrefSize(width, height);
        card.getStyleClass().add("block-card");
        
        javafx.scene.control.Label titleNode = new javafx.scene.control.Label(title);
        titleNode.getStyleClass().add("block-title");
        
        card.getChildren().add(titleNode);
        
        if (!content.isEmpty()) {
            String[] lines = content.split("\n");
            for (String line : lines) {
                javafx.scene.control.Label contentNode = new javafx.scene.control.Label(line);
                contentNode.getStyleClass().add("block-text");
                card.getChildren().add(contentNode);
            }
        }
        
        return card;
    }
    
    private javafx.scene.control.Button createQuickButton(String text) {
        javafx.scene.control.Button button = new javafx.scene.control.Button(text);
        button.getStyleClass().add("quick-tool-btn");
        
        // Thêm event handlers cho từng button
        if ("Tạo đơn hàng mới".equals(text)) {
            button.setOnAction(e -> handleCreateOrder());
        } else if ("Nhập kho".equals(text)) {
            button.setOnAction(e -> handleInventoryManagement(null));
        } else if ("Kiểm kê".equals(text)) {
            button.setOnAction(e -> handleInventoryManagement(null));
        } else if ("Báo cáo".equals(text)) {
            button.setOnAction(e -> handleReportManagement(null));
        }
        
        return button;
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
    
    private double getTotalRevenue() {
        try {
            double totalRevenue = 0.0;
            var orders = orderDAO.getAllOrders();
            for (var order : orders) {
            
                if (!"CANCELLED".equals(order.getStatus())) {
                    totalRevenue += order.getFinalAmount();
                }
            }
            return totalRevenue;
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
            return 0.0;
        }
    }
    
    private String formatCurrency(double amount) {
        return String.format("%,.0f", amount);
    }
    

    
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            

            
            contentArea.getChildren().clear();
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
        welcomeLabel.setText("💰 Bán Hàng - POS");
        loadContent("/fxml/POS.fxml");
    }
    

    
    @FXML
    private void handleHomeManagement(javafx.scene.input.MouseEvent event) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            String roleName = getRoleName(currentUser.getRole());
            welcomeLabel.setText("Chào mừng " + roleName + " " + currentUser.getFullName());
        } else {
            welcomeLabel.setText("Chào mừng đến với hệ thống bán hàng");
        }
        loadHomeContent();
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleProductManagement(javafx.scene.input.MouseEvent event) {
        welcomeLabel.setText("Quản lý sản phẩm và danh mục");
        loadContent("/fxml/ProductCategory.fxml");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleCustomerManagement(javafx.scene.input.MouseEvent event) {
        welcomeLabel.setText("Quản lý khách hàng");
        loadContent("/fxml/Customer.fxml");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleSupplierManagement(javafx.scene.input.MouseEvent event) {
        welcomeLabel.setText("Quản lý nhà cung cấp");
        loadContent("/fxml/Supplier.fxml");
        updateActiveMenuItem(event);
    }

    @FXML
    private void handleOrderManagement(javafx.scene.input.MouseEvent event) {
        welcomeLabel.setText("Quản lý đơn hàng");
        loadContent("/fxml/Order.fxml");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleInventoryManagement(javafx.scene.input.MouseEvent event) {
        welcomeLabel.setText("Quản lý kho");
        loadContent("/fxml/Inventory.fxml");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleSalesManagement(javafx.scene.input.MouseEvent event) {
        welcomeLabel.setText("💰 Bán Hàng - POS");
        loadContent("/fxml/POS.fxml");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleReportManagement(javafx.scene.input.MouseEvent event) {
        welcomeLabel.setText("Báo cáo");
        loadContent("/fxml/Report.fxml");
        updateActiveMenuItem(event);
    }
    @FXML
    private void handleUserManagement(javafx.scene.input.MouseEvent event) {
        welcomeLabel.setText("Quản lý người dùng");
        loadContent("/fxml/UserManagement.fxml");
        updateActiveMenuItem(event);
    }
    @FXML
    private void handleShiftManagement(javafx.scene.input.MouseEvent event) {
        welcomeLabel.setText("Quản lý ca làm");
        loadContent("/fxml/Shift.fxml");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleSettingsManagement(javafx.scene.input.MouseEvent event) {
        welcomeLabel.setText("Cài đặt hệ thống");
        System.out.println("Settings Management clicked");
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