/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sv.qlbh.controller;

import com.sv.qlbh.utils.AlertUtils;
import java.net.URL;
import com.sv.qlbh.models.Shift;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.sv.qlbh.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.sv.qlbh.dao.ShiftDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import com.sv.qlbh.dao.UserDAO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
/**
 * FXML Controller class
 *
 * @author nghip
 */
public class ShiftController implements Initializable {

    /**
     * Initializes the controller class.
     */
    // DAO và Data
    @FXML private ComboBox<User> userComboBox;
    @FXML private DatePicker startDatePicker, endDatePicker;
    @FXML private Spinner<Integer> startHourSpinner, startMinuteSpinner, endHourSpinner, endMinuteSpinner;
    @FXML private TableColumn<Shift, Integer> idColumn;
    @FXML private TableColumn<Shift, String> userColumn;
    @FXML private TableColumn<Shift, LocalDateTime> startTimeColumn, endTimeColumn;
    @FXML private TableColumn<Shift, String> statusColumn;
    @FXML private TableView<Shift> shiftTable;

    private final ShiftDAO shiftDAO = new ShiftDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<Shift> shiftList = FXCollections.observableArrayList();
    private Shift selectedShift = null;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        try
        {
            userComboBox.setItems(FXCollections.observableArrayList(userDAO.getAll()));
        }
        catch(SQLException e)
        {
            AlertUtils.showDatabaseError("Không thể tải danh sách nhân viên: " + e.getMessage());
        }
        userComboBox.setConverter(new javafx.util.StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user == null ? "" : user.getFullName();
            }
            @Override
            public User fromString(String s) { return null; }
        });
        startHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        startMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        endMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        // TableColumn binding
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        userColumn.setCellValueFactory(cell -> {
            User u = userComboBox.getItems().stream()
                .filter(user -> user.getId() == cell.getValue().getUserId())
                .findFirst().orElse(null);
            return new javafx.beans.property.SimpleStringProperty(u != null ? u.getFullName() : "");
        });
        startTimeColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getStartTime()));
        endTimeColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getEndTime()));
        statusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
       
        //load danh saach ca lam
        shiftTable.setItems(shiftList);
        loadShiftList();

        // Khi chọn ca trên bảng, điền dữ liệu lên form
        shiftTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selectedShift = newSel;
            fillFormWithSelectedShift();
        });
    }
    private void fillFormWithSelectedShift() {
        if (selectedShift == null) return;
        // Chọn user
        for (User u : userComboBox.getItems()) {
            if (u.getId() == selectedShift.getUserId()) {
                userComboBox.setValue(u);
                break;
            }
        }
        // Set start
        if (selectedShift.getStartTime() != null) {
            startDatePicker.setValue(selectedShift.getStartTime().toLocalDate());
            startHourSpinner.getValueFactory().setValue(selectedShift.getStartTime().getHour());
            startMinuteSpinner.getValueFactory().setValue(selectedShift.getStartTime().getMinute());
        }
        // Set end
        if (selectedShift.getEndTime() != null) {
            endDatePicker.setValue(selectedShift.getEndTime().toLocalDate());
            endHourSpinner.getValueFactory().setValue(selectedShift.getEndTime().getHour());
            endMinuteSpinner.getValueFactory().setValue(selectedShift.getEndTime().getMinute());
        }
    }
        @FXML
        private void handleAddShift() throws SQLException {
            Shift shift = getShiftFromForm();
            if (shift == null) return;
            shift.setStatus("ACTIVE");
            shift.setCreatedAt(LocalDateTime.now());
            if (shiftDAO.add(shift)) {
                AlertUtils.showSuccess("Thêm ca thành công!");
                loadShiftList();
                clearForm();
            } else {
                AlertUtils.showDatabaseError("Không thể thêm ca.");
            }
        }

        @FXML
        private void handleUpdateShift() throws SQLException {
            if (selectedShift == null) {
                AlertUtils.showValidationError("Chọn ca để cập nhật!");
                return;
            }
            Shift shift = getShiftFromForm();
            if (shift == null) return;
            shift.setId(selectedShift.getId());
            shift.setStatus(selectedShift.getStatus());
            shift.setCreatedAt(selectedShift.getCreatedAt());
            if (shiftDAO.update(shift)) {
                AlertUtils.showSuccess("Cập nhật ca thành công!");
                loadShiftList();
                clearForm();
            } else {
                AlertUtils.showDatabaseError("Không thể cập nhật ca.");
            }
        }

        @FXML
        private void handleDeleteShift() throws SQLException {
            if (selectedShift == null) {
                AlertUtils.showValidationError("Chọn ca để xóa!");
                return;
            }
            if (shiftDAO.delete(selectedShift.getId())) {
                AlertUtils.showSuccess("Xóa ca thành công!");
                loadShiftList();
                clearForm();
            } else {
                AlertUtils.showDatabaseError("Không thể xóa ca.");
            }
        }
        @FXML
        private void handleRefresh() {
            clearForm();
            loadShiftList();
        }

        private Shift getShiftFromForm() {
            User user = userComboBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            if (user == null || startDate == null || endDate == null) {
                AlertUtils.showValidationError("Nhập đủ thông tin!");
                return null;
            }
            LocalDateTime startTime = LocalDateTime.of(startDate, LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue()));
            LocalDateTime endTime = LocalDateTime.of(endDate, LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue()));
            Shift shift = new Shift();
            shift.setUserId(user.getId());
            shift.setStartTime(startTime);
            shift.setEndTime(endTime);
            return shift;
        }
        @FXML
        private void handleEndShift() throws SQLException {
            if (selectedShift == null) {
                AlertUtils.showValidationError("Chọn ca để kết thúc!");
                return;
            }
            selectedShift.setStatus("COMPLETED");
            if (shiftDAO.update(selectedShift)) {
                AlertUtils.showSuccess("Đã kết thúc ca!");
                loadShiftList();
                clearForm();
            } else {
                AlertUtils.showDatabaseError("Không thể kết thúc ca.");
            }
        }

        private void clearForm() {
            userComboBox.getSelectionModel().clearSelection();
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            startHourSpinner.getValueFactory().setValue(0);
            startMinuteSpinner.getValueFactory().setValue(0);
            endHourSpinner.getValueFactory().setValue(0);
            endMinuteSpinner.getValueFactory().setValue(0);
            shiftTable.getSelectionModel().clearSelection();
            selectedShift = null;
        }

            
    

     private void loadShiftList()
     {
         shiftList.clear();
         shiftList.addAll(shiftDAO.getAll());
     }
    
}
