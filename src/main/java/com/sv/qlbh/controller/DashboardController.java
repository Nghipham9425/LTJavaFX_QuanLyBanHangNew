package com.sv.qlbh.controller;

import com.sv.qlbh.models.User;
import com.sv.qlbh.utils.SessionManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DashboardController implements Initializable {
    
    @FXML
    private Label welcomeLabel;
    
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
        
        System.out.println("Dashboard loaded successfully!");
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
        // TODO: Xử lý tạo đơn hàng mới
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