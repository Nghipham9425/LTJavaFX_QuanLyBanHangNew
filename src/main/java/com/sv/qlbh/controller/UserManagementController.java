/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sv.qlbh.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


import com.sv.qlbh.dao.UserDAO;
import com.sv.qlbh.models.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
/**
 * FXML Controller class
 *
 * @author nghip
 */
public class UserManagementController implements Initializable {

    @FXML private TableView<User> tableUser;
    @FXML private TableColumn<User,String> colUsername;
    @FXML private TableColumn<User,String> colFullName;
    @FXML private TableColumn<User,String> colRole;
    @FXML private TableColumn<User,String> colStatus;

    @FXML private TextField txtUsername;
    @FXML private TextField txtFullName;
    @FXML private ComboBox<String> cbRole;
    @FXML private CheckBox chkActive;
    @FXML private TextField txtSearch;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;


    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<User> userList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
       colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
       colFullName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
       colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
       colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isStatus() ? "Hoạt động" : "Không hoạt động"));
       
       cbRole.setItems(FXCollections.observableArrayList("ADMIN", "STAFF","ACCOUNTANT","WAREHOUSE"));

       // load danh sach user
       LoadUserList();
    //    
    
    //hien thi user khi click
    tableUser.getSelectionModel().selectedItemProperty().addListener(
        (obs,oldsel,newSel) -> showUserDetails(newSel)
    );
    }
    private void LoadUserList(){
    try
    {
        //lay danh sach user tu db
        List<User> users=userDAO.getAll();

        // Dua vao observable List
        userList.setAll(users);
        tableUser.setItems(userList);
    }
    catch(SQLException e)
    {
        e.printStackTrace();
    }
    }
    private void clearForm()
    {
        txtUsername.clear();
        txtFullName.clear();
        cbRole.setValue(null);
        chkActive.setSelected(false);
        txtPassword.clear();
        txtConfirmPassword.clear();
    }
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void showUserDetails(User user)
    {
        if(user!=null)
        {
            txtUsername.setText(user.getUsername());
            txtFullName.setText(user.getFullName());
            cbRole.setValue(user.getRole());
            chkActive.setSelected(user.isStatus());
        }
        else{
            txtUsername.clear();
            txtFullName.clear();
            cbRole.setValue(null);
            chkActive.setSelected(false);
        }
    }
    @FXML private void handleAddUser() {
        String username = txtUsername.getText().trim();
        String fullName = txtFullName.getText().trim();
        String role = cbRole.getValue();
        boolean status = chkActive.isSelected();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (username.isEmpty() || fullName.isEmpty() || role == null || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin!", Alert.AlertType.WARNING);
            return;
        }
        if (!password.equals(confirmPassword)) {
            showAlert("Lỗi", "Mật khẩu và xác nhận mật khẩu không khớp!", Alert.AlertType.WARNING);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setRole(role);
        user.setStatus(status);
        user.setPassword(password);

        try {
            boolean ok = userDAO.add(user);
            if (ok) {
                LoadUserList();
                showUserDetails(null);
                showAlert("Thành công", "Đã thêm người dùng mới!", Alert.AlertType.INFORMATION);

            } else {
                showAlert("Lỗi", "Không thể thêm người dùng!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Lỗi", "Có lỗi xảy ra khi thêm người dùng!", Alert.AlertType.ERROR);
        }
    }
    @FXML private void handleUpdateUser() throws SQLException {
        // Lấy user đang select
        User user = tableUser.getSelectionModel().getSelectedItem();
        if (user == null) {
            showAlert("Thông báo", "Vui lòng chọn người dùng để cập nhật!", Alert.AlertType.WARNING);
            return;
        }
        
        // Lấy dữ liệu từ form
        String fullName = txtFullName.getText().trim();
        String role = cbRole.getValue();
        boolean status = chkActive.isSelected();
        String password = txtPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();

        // Validation
        if (fullName.isEmpty() || role == null) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin bắt buộc!", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Cập nhật thông tin cơ bản
            user.setFullName(fullName);
            user.setRole(role);
            user.setStatus(status);
            
            boolean updateSuccess = userDAO.update(user);
            
            // Cập nhật password nếu có
            if (!password.isEmpty() || !confirmPassword.isEmpty()) {
                if (!password.equals(confirmPassword)) {
                    showAlert("Lỗi", "Mật khẩu và xác nhận mật khẩu không khớp!", Alert.AlertType.WARNING);
                    return;
                }
                if (password.length() < 6) {
                    showAlert("Lỗi", "Mật khẩu phải có ít nhất 6 ký tự!", Alert.AlertType.WARNING);
                    return;
                }
                user.setPassword(password);
                updateSuccess = userDAO.updatePassword(user) && updateSuccess;
            }
            
            if (updateSuccess) {
                LoadUserList();
                clearForm();
                showUserDetails(null);
                showAlert("Thành công", "Đã cập nhật người dùng thành công!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Lỗi", "Không thể cập nhật người dùng!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Lỗi", "Có lỗi xảy ra khi cập nhật người dùng: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    // @FXML private void handleDeleteUser() {}
    @FXML private void handleClearUser() {
        clearForm();
    }
    @FXML private void handleResetPassword() {}
    @FXML private void handleSearch() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            LoadUserList();
            return;
        }
        
        try {
            List<User> allUsers = userDAO.getAll();
            List<User> filteredUsers = allUsers.stream()
                .filter(user -> 
                    user.getUsername().toLowerCase().contains(searchText) ||
                    user.getFullName().toLowerCase().contains(searchText) ||
                    user.getRole().toLowerCase().contains(searchText))
                .collect(java.util.stream.Collectors.toList());
            
            userList.setAll(filteredUsers);
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể tìm kiếm: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML private void handleRefresh() {
        clearForm();    
        showUserDetails(null);
        LoadUserList(); 
    }
}
