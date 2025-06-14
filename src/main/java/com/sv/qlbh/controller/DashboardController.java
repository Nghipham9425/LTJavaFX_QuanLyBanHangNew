package com.sv.qlbh.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;

public class DashboardController implements Initializable {
    
    @FXML
    private BarChart<String, Number> barChart;
    
    @FXML
    private PieChart pieChart;
    
    @FXML
    private ListView<String> lowStockList;
    
    @FXML
    private ListView<String> recentActivityList;
    
    @FXML
    private Label totalProductsLabel;
    
    @FXML
    private Label totalCustomersLabel;
    
    @FXML
    private Label todayOrdersLabel;
    
    @FXML
    private Label todayRevenueLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Khởi tạo dữ liệu cho biểu đồ doanh thu
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("T1", 1000000));
        series.getData().add(new XYChart.Data<>("T2", 2000000));
        series.getData().add(new XYChart.Data<>("T3", 1500000));
        series.getData().add(new XYChart.Data<>("T4", 3000000));
        series.getData().add(new XYChart.Data<>("T5", 2500000));
        series.getData().add(new XYChart.Data<>("T6", 4000000));
        barChart.getData().add(series);
        
        // Khởi tạo dữ liệu cho biểu đồ tròn
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Sản phẩm A", 30),
            new PieChart.Data("Sản phẩm B", 25),
            new PieChart.Data("Sản phẩm C", 20),
            new PieChart.Data("Sản phẩm D", 15),
            new PieChart.Data("Sản phẩm E", 10)
        );
        pieChart.setData(pieChartData);
        
        // Cập nhật số liệu thống kê
        updateStatistics();
        
        // Cập nhật danh sách cảnh báo và hoạt động
        updateAlerts();
    }
    
    private void updateStatistics() {
        // TODO: Lấy dữ liệu từ database
        totalProductsLabel.setText("1200");
        totalCustomersLabel.setText("350");
        todayOrdersLabel.setText("45");
        todayRevenueLabel.setText("₫ 12,500,000");
    }
    
    private void updateAlerts() {
        // TODO: Lấy dữ liệu từ database
        ObservableList<String> lowStockItems = FXCollections.observableArrayList(
            "Sản phẩm A - Còn 5 sản phẩm",
            "Sản phẩm B - Còn 3 sản phẩm"
        );
        lowStockList.setItems(lowStockItems);
        
        ObservableList<String> recentActivities = FXCollections.observableArrayList(
            "Đơn hàng mới #1234",
            "Nhập kho 50 sản phẩm"
        );
        recentActivityList.setItems(recentActivities);
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
    private void handleLogout() {
        // TODO: Xử lý đăng xuất
    }
}