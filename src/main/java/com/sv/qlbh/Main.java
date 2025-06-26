package com.sv.qlbh;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        
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
        
        // Show stage
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}