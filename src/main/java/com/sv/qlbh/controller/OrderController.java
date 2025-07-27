/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sv.qlbh.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.sv.qlbh.models.Order;
import com.sv.qlbh.models.OrderDetail;
import com.sv.qlbh.dao.OrderDAO;
import com.sv.qlbh.dao.OrderDetailDAO;
import com.sv.qlbh.utils.AlertUtils;
import com.sv.qlbh.utils.DatabaseExceptionHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.util.List;

public class OrderController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> colOrderId;
    @FXML private TableColumn<Order, String> colOrderCus;
    @FXML private TableColumn<Order, String> colOrderDate;
    @FXML private TableColumn<Order, String> colOrderStatus;
    @FXML private TableColumn<Order, String> colOrderTotal;

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    private final ObservableList<Order> orderList = FXCollections.observableArrayList();
    private final DecimalFormat df = new DecimalFormat("#,##0");
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupFilter();
        loadData();
    }
    
    private void setupTable() {
        colOrderId.setCellValueFactory(data -> new SimpleStringProperty("#" + data.getValue().getId()));
        colOrderCus.setCellValueFactory(data -> {
            Order order = data.getValue();
            String customer = (order.getCustomerName() != null) ? order.getCustomerName() : "Khách vãng lai";
            return new SimpleStringProperty(customer);
        });
        colOrderDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCreatedAt().format(dateFormat)));
        colOrderStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        colOrderTotal.setCellValueFactory(data -> new SimpleStringProperty(df.format(data.getValue().getFinalAmount()) + " VNĐ"));
        orderTable.setItems(orderList);
    }
    
    private void setupFilter() {
        statusFilter.setItems(FXCollections.observableArrayList("ALL", "PROCESSING", "COMPLETED", "FAILED", "CANCELLED"));
        statusFilter.setValue("ALL");
        statusFilter.setOnAction(e -> handleSearch());
    }
    
    private void loadData() {
        try {
            List<Order> orders = orderDAO.getAllOrders();
            orderList.setAll(orders);
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleSQLException(e, "tải danh sách đơn hàng");
        }
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        String status = statusFilter.getValue();
        
        ObservableList<Order> filtered = orderList.filtered(order -> {
            boolean matchKeyword = keyword.isEmpty() || 
                String.valueOf(order.getId()).contains(keyword) ||
                (order.getCustomerName() != null && order.getCustomerName().toLowerCase().contains(keyword));
            
            boolean matchStatus = status.equals("ALL") || order.getStatus().equals(status);
            
            return matchKeyword && matchStatus;
        });
        
        orderTable.setItems(filtered);
    }

    @FXML
    public void handleDetail() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn đơn hàng!");
            return;
        }
        
        try {
            List<OrderDetail> details = orderDetailDAO.getOrderDetailsByOrderId(selected.getId());
            showDetailDialog(selected, details);
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleSQLException(e, "lấy chi tiết đơn hàng");
        }
    }

    @FXML
    public void handleCancel() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null || !selected.getStatus().equals("PROCESSING")) {
            AlertUtils.showWarning("Cảnh báo", "Chỉ có thể hủy đơn hàng PROCESSING!");
            return;
        }
        
        if (AlertUtils.showConfirmation("Xác nhận", "Hủy đơn hàng #" + selected.getId() + "?").orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (orderDAO.cancelOrder(selected.getId())) {
                    AlertUtils.showSuccess("Đã hủy đơn hàng!");
                    loadData();
                }
            } catch (SQLException e) {
                DatabaseExceptionHandler.handleSQLException(e, "hủy đơn hàng");
            }
        }
    }

    @FXML
    public void handleRefresh() {
        loadData();
        searchField.clear();
        statusFilter.setValue("ALL");
        orderTable.setItems(orderList);
    }
    
    private void showDetailDialog(Order order, List<OrderDetail> details) {
        StringBuilder content = new StringBuilder();
        content.append("Khách hàng: ").append(order.getCustomerName() != null ? order.getCustomerName() : "Khách vãng lai").append("\n");
        content.append("Ngày: ").append(order.getCreatedAt().format(dateFormat)).append("\n");
        content.append("Thanh toán: ").append(order.getPaymentMethod()).append("\n");
        content.append("Trạng thái: ").append(order.getStatus()).append("\n\n");
        
        content.append("Sản phẩm:\n");
        for (OrderDetail detail : details) {
            content.append("- ").append(detail.getProductName() != null ? detail.getProductName() : "SP #" + detail.getProductId());
            content.append(" x").append(detail.getQuantity());
            content.append(" = ").append(df.format(detail.getSubtotal())).append(" VNĐ\n");
        }
        
        content.append("\nTổng: ").append(df.format(order.getFinalAmount())).append(" VNĐ");
        
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Chi tiết đơn hàng #" + order.getId());
        dialog.setContentText(content.toString());
        dialog.getDialogPane().setMinWidth(500);
        dialog.showAndWait();
    }
}
