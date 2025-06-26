package com.sv.qlbh.controller;

import com.sv.qlbh.models.User;
import com.sv.qlbh.utils.SessionManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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

public class DashboardController implements Initializable {
    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private VBox contentArea;
    
    private HostServices hostServices;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Hiển thị thông tin user đã đăng nhập
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            String roleName = getRoleName(currentUser.getRole());
            welcomeLabel.setText("Chào mừng " + roleName + " " + currentUser.getFullName());
        } else {
            welcomeLabel.setText("Chào mừng đến với hệ thống bán hàng");
        }
        
        // Load default home content
        loadHomeContent();
        
        System.out.println("Dashboard loaded successfully!");
    }
    
    private void loadHomeContent() {
        // Tạo nội dung dashboard mặc định
        createDefaultDashboardContent();
    }
    
    private void createDefaultDashboardContent() {
        contentArea.getChildren().clear();
        
        // Tạo nội dung dashboard mặc định bằng code JavaFX
        try {
            javafx.scene.layout.VBox mainContainer = new javafx.scene.layout.VBox(20);
            mainContainer.setPadding(new javafx.geometry.Insets(20));
            
            // Tạo 4 stat cards
            javafx.scene.layout.HBox statsRow = new javafx.scene.layout.HBox(18);
            statsRow.setAlignment(javafx.geometry.Pos.CENTER);
            
            statsRow.getChildren().addAll(
                createStatCard("Sản phẩm", "1200", "stat-card stat-blue"),
                createStatCard("Khách hàng", "350", "stat-card stat-green"),
                createStatCard("Đơn hôm nay", "45", "stat-card stat-orange"),
                createStatCard("Doanh thu hôm nay", "₫ 12,500,000", "stat-card stat-purple")
            );
            
            // Tạo info blocks
            javafx.scene.layout.HBox infoRow1 = new javafx.scene.layout.HBox(18);
            infoRow1.setAlignment(javafx.geometry.Pos.CENTER);
            infoRow1.getChildren().addAll(
                createInfoCard("Lịch sử bán hàng", "", 400, 180),
                createInfoCard("Biểu đồ doanh thu", "", 400, 180)
            );
            
            javafx.scene.layout.HBox infoRow2 = new javafx.scene.layout.HBox(18);
            infoRow2.setAlignment(javafx.geometry.Pos.CENTER);
            infoRow2.getChildren().addAll(
                createInfoCard("Sản phẩm sắp hết hàng", "Sản phẩm A - Còn 5\nSản phẩm B - Còn 3", 400, 120),
                createInfoCard("Hoạt động gần đây", "Đơn hàng mới #1234\nNhập kho 50 sản phẩm", 400, 120)
            );
            
            // Tạo quick tools
            javafx.scene.layout.HBox toolsRow = new javafx.scene.layout.HBox(18);
            toolsRow.setAlignment(javafx.geometry.Pos.CENTER);
            toolsRow.getStyleClass().add("quick-tools");
            
            toolsRow.getChildren().addAll(
                createQuickButton("Tạo đơn hàng mới"),
                createQuickButton("Nhập kho"),
                createQuickButton("Kiểm kê"),
                createQuickButton("Báo cáo")
            );
            
            mainContainer.getChildren().addAll(statsRow, infoRow1, infoRow2, toolsRow);
            contentArea.getChildren().add(mainContainer);
            
        } catch (Exception e) {
            // Fallback: tạo nội dung đơn giản
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
        return button;
    }
    

    
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
    
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            
                         // If loading POS, inject HostServices
             if (fxmlPath.equals("/fxml/POS.fxml")) {
                 com.sv.qlbh.controller.POSController posController = loader.getController();
                 if (posController != null && hostServices != null) {
                     posController.setHostServices(hostServices);
                 }
             }
            
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
    
    // Xử lý sự kiện cho các nút
    @FXML
    private void handleCreateOrder() {
        // Chuyển đến màn hình bán hàng
        welcomeLabel.setText("💰 Bán Hàng - POS");
        loadContent("/fxml/Sales.fxml");
    }
    
    @FXML
    private void handleImportStock() {
        // TODO: Xử lý nhập kho
    }
    
    @FXML
    private void handleInventoryCheck() {
        // TODO: Xử lý kiểm kê
    }
    
    @FXML
    private void handleGenerateReport() {
        // TODO: Xử lý tạo báo cáo
    }
    
    // Navigation handlers
    @FXML
    private void handleHomeManagement(javafx.scene.input.MouseEvent event) {
        // Reset welcome message
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
        // TODO: Load Order.fxml
        System.out.println("Order Management clicked");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleInventoryManagement(javafx.scene.input.MouseEvent event) {
        welcomeLabel.setText("Quản lý kho");
        // TODO: Tạo Inventory.fxml
        System.out.println("Inventory Management clicked");
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
        // TODO: Tạo Report.fxml
        System.out.println("Report Management clicked");
        updateActiveMenuItem(event);
    }
    
    @FXML
    private void handleSettingsManagement(javafx.scene.input.MouseEvent event) {
        welcomeLabel.setText("Cài đặt hệ thống");
        // TODO: Tạo Settings.fxml
        System.out.println("Settings Management clicked");
        updateActiveMenuItem(event);
    }
    
    private void updateActiveMenuItem(javafx.scene.input.MouseEvent event) {
        // Remove active class from all menu items
        Node sidebar = ((Node) event.getSource()).getParent().getParent();
        sidebar.lookupAll(".menu-item").forEach(node -> {
            node.getStyleClass().remove("active");
        });
        
        // Add active class to clicked item
        Node clickedItem = (Node) event.getSource();
        clickedItem.getStyleClass().add("active");
    }

    @FXML
    private void handleLogout(javafx.scene.input.MouseEvent event) {
        try {
            // Xóa thông tin user đã đăng nhập
            SessionManager.logout();
            
            // Chuyển về màn hình login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            // Lấy stage hiện tại và thay đổi scene
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Đăng nhập - Quản Lý Bán Hàng");
            stage.setResizable(false);
            
            // Đặt lại kích thước cho màn hình login
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