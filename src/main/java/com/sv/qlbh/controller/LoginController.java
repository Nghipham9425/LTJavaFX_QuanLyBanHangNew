/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sv.qlbh.controller;

import com.sv.qlbh.dao.UserDAO;

import com.sv.qlbh.models.User;
import com.sv.qlbh.utils.SessionManager;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
/**
 *
 * @author nghip
 */
public class LoginController {
    
      @FXML
      private TextField txtUsername;
      
      @FXML
      private PasswordField txtPassword;
      
      @FXML
      private Button btnLogin;
      
      @FXML
      private ProgressIndicator progressLogin;
      
      @FXML
      private Label lblMessage;
      
      private HostServices hostServices;
      
      public void setHostServices(HostServices hostServices) {
          this.hostServices = hostServices;
      }
      
      @FXML
      private void initialize(){
          progressLogin.setVisible(false);
          lblMessage.setText("");
      }
      @FXML
      private void handleLogin(ActionEvent event) {
          String username = txtUsername.getText();
          String password = txtPassword.getText();
          
          if (username.isEmpty() || password.isEmpty()) {
              lblMessage.setText("Vui lòng nhập đầy đủ thông tin!");
              return;
          }
          
          btnLogin.setDisable(true);
          progressLogin.setVisible(true);
          lblMessage.setText("");
          
          new Thread(() -> {
              try {
                
                  UserDAO userDAO = new UserDAO();
                  User user = userDAO.login(username, password);
                  
                  Platform.runLater(() -> {
                      progressLogin.setVisible(false);
                      btnLogin.setDisable(false);
                      
                      if (user != null) {
                         
                          SessionManager.setCurrentUser(user);
                          
                          try {
                              FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
                              Parent root = loader.load();
                           
                              DashboardController dashboardController = loader.getController();
                              if (dashboardController != null && hostServices != null) {
                                  dashboardController.setHostServices(hostServices);
                              }
                              
                              Scene scene = new Scene(root);
                              Stage stage = (Stage) btnLogin.getScene().getWindow();
                              
                              // Đặt lại kích thước cho Dashboard
                              stage.setScene(scene);
                              stage.setTitle("Quản Lý Bán Hàng");
                              stage.setResizable(true);
                              stage.setMinWidth(1000);
                              stage.setMinHeight(700);
                              stage.setMaxWidth(Double.MAX_VALUE);
                              stage.setMaxHeight(Double.MAX_VALUE);
                              stage.setWidth(1200);
                              stage.setHeight(800);
                              stage.centerOnScreen();
                              stage.show();
                          } catch (IOException e) {
                              e.printStackTrace();
                              lblMessage.setText("Lỗi chuyển trang!");
                          }
                      } else {
                          lblMessage.setText("Tên đăng nhập hoặc mật khẩu không đúng!");
                      }
                  });
              } catch (SQLException e) {
                  System.err.println("SQLException trong login: " + e.getMessage());
                  Platform.runLater(() -> {
                      progressLogin.setVisible(false);
                      btnLogin.setDisable(false);
                      if (e.getErrorCode() == 1045) {
                          lblMessage.setText("Lỗi xác thực database!");
                      } else if (e.getErrorCode() == 2003) {
                          lblMessage.setText("Không thể kết nối đến database server!");
                      } else {
                          lblMessage.setText("Lỗi cơ sở dữ liệu: " + e.getMessage());
                      }
                  });
              }
          }).start();
      }
    }
