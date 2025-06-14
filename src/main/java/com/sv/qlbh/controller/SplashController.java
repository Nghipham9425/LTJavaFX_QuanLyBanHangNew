package com.sv.qlbh.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {
    
    @FXML
    private ProgressIndicator progressIndicator;
    
    @FXML
    private Label loadingLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bắt đầu quá trình loading
        startLoadingProcess();
    }
    
    private void startLoadingProcess() {
        // Tạo các bước loading với delay
        PauseTransition step1 = new PauseTransition(Duration.seconds(1));
        step1.setOnFinished(e -> {
            loadingLabel.setText("Đang tải cấu hình...");
        });
        
        PauseTransition step2 = new PauseTransition(Duration.seconds(2));
        step2.setOnFinished(e -> {
            loadingLabel.setText("Đang kết nối database...");
        });
        
        PauseTransition step3 = new PauseTransition(Duration.seconds(3));
        step3.setOnFinished(e -> {
            loadingLabel.setText("Hoàn tất!");
        });
        
        PauseTransition step4 = new PauseTransition(Duration.seconds(3.5));
        step4.setOnFinished(e -> {
            // Chuyển đến màn hình login
            loadLoginScreen();
        });
        
        // Chạy các bước loading
        step1.play();
        step2.play();
        step3.play();
        step4.play();
    }
    
    private void loadLoginScreen() {
        try {
            // Load màn hình login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            // Lấy stage hiện tại
            Stage stage = (Stage) progressIndicator.getScene().getWindow();
            
            // Đặt lại kích thước cho màn hình login
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