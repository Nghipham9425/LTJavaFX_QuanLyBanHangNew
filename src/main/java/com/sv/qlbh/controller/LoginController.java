/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sv.qlbh.controller;

import com.sv.qlbh.dao.UserDAO;
import com.sv.qlbh.dao.UserDAOImpl;
import com.sv.qlbh.models.User;
import com.sv.qlbh.utils.SessionManager;
import java.io.IOException;
import java.sql.SQLException;
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
                  // Gọi UserDAO để kiểm tra đăng nhập
                  UserDAO userDAO = new UserDAOImpl();
                  User user = userDAO.login(username, password);
                  
                  Platform.runLater(() -> {
                      progressLogin.setVisible(false);
                      btnLogin.setDisable(false);
                      
                      if (user != null) {
                          // Lưu thông tin user đã đăng nhập
                          SessionManager.setCurrentUser(user);
                          
                          // Chuyển đến Dashboard
                          try {
                              FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
                              Parent root = loader.load();
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
                  Platform.runLater(() -> {
                      progressLogin.setVisible(false);
                      btnLogin.setDisable(false);
                      lblMessage.setText("Lỗi kết nối database: " + e.getMessage());
                  });
              }
          }).start();
      }
    }
