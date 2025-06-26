package com.sv.qlbh;

import com.sv.qlbh.utils.VNPayReturnHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Khởi động VNPay Return Handler
        VNPayReturnHandler.startServer();
        System.out.println("VNPay integration enabled - listening on port 8080");
        
        // Load FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        
        // Inject HostServices into LoginController
        com.sv.qlbh.controller.LoginController loginController = loader.getController();
        if (loginController != null) {
            loginController.setHostServices(getHostServices());
        }
        
        // Create scene
        Scene scene = new Scene(root);
        
        // Set stage properties for Login
        primaryStage.setTitle("Đăng nhập - Quản Lý Bán Hàng");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setWidth(700);
        primaryStage.setHeight(400);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(400);
        primaryStage.setMaxWidth(700);
        primaryStage.setMaxHeight(400);
        primaryStage.centerOnScreen();
        
        // Handle application shutdown to stop VNPay server
        primaryStage.setOnCloseRequest(event -> {
            VNPayReturnHandler.stopServer();
        });
        
        // Show stage
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        // Dừng VNPay Return Handler khi ứng dụng tắt
        VNPayReturnHandler.stopServer();
        super.stop();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}