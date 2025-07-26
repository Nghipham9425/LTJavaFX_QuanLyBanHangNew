package com.sv.qlbh.controller;

import com.sv.qlbh.dao.InventoryDAO;
import com.sv.qlbh.dao.ProductDAO;
import com.sv.qlbh.models.Statistic;
import com.sv.qlbh.models.Inventory;
import com.sv.qlbh.models.Product;
import java.sql.SQLException;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import java.time.LocalDate;
import java.util.List;

public class StatisticController {
    @FXML private Label lblTotalAmount;
    @FXML private ComboBox<String> cbxReportType;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;

    @FXML private TableView<Statistic> tblReport;
    @FXML private TableColumn<Statistic, Integer> colId;
    @FXML private TableColumn<Statistic, String> colName;
    @FXML private TableColumn<Statistic, String> colCategory;
    @FXML private TableColumn<Statistic, Integer> colQuantity;
    @FXML private TableColumn<Statistic, Double> colAmount;
    @FXML private TableColumn<Statistic, LocalDate> colDate;

    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @FXML
    public void initialize() {
        cbxReportType.getItems().addAll("Tất cả", "Nhập kho", "Xuất kho");
        cbxReportType.setValue("Tất cả");

        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        colQuantity.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colAmount.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()).asObject());
        colDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));

        loadReportData(); // load toàn bộ dữ liệu ban đầu
    }

    private void loadReportData() {
        ObservableList<Statistic> data = FXCollections.observableArrayList();

        try {
            List<Inventory> inventoryList = inventoryDAO.getAllInventoryEntries();
            for (Inventory inv : inventoryList) {
                Product product = productDAO.getById(inv.getProductId());
                if (product == null) continue;

                String type = inv.getType().equalsIgnoreCase("IN") ? "Nhập kho" : "Xuất kho";
                double amount = product.getPrice() * inv.getQuantity();
                LocalDate date = inv.getCreatedAt().toLocalDate();

                data.add(new Statistic(
                        inv.getId(),
                        product.getName(),
                        type,
                        inv.getQuantity(),
                        amount,
                        date
                ));
            }
            tblReport.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải báo cáo: " + e.getMessage());
        }
    }

    @FXML
    private void handleFilterReport() {
        LocalDate start = dpStartDate.getValue();
        LocalDate end = dpEndDate.getValue();
        String selectedType = cbxReportType.getValue();

        if (start != null && end != null && start.isAfter(end)) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Từ ngày không thể sau Đến ngày.");
            return;
        }

        ObservableList<Statistic> filteredData = FXCollections.observableArrayList();

        try {
            List<Inventory> inventoryList = inventoryDAO.getAllInventoryEntries();
            for (Inventory inv : inventoryList) {
                Product product = productDAO.getById(inv.getProductId());
                if (product == null) continue;

                LocalDate entryDate = inv.getCreatedAt().toLocalDate();
                String type = inv.getType().equalsIgnoreCase("IN") ? "Nhập kho" : "Xuất kho";

                // Lọc theo ngày
                if (start != null && entryDate.isBefore(start)) continue;
                if (end != null && entryDate.isAfter(end)) continue;

                // Lọc theo loại
                if (!"Tất cả".equals(selectedType) && !selectedType.equals(type)) continue;

                double amount = product.getPrice() * inv.getQuantity();

                filteredData.add(new Statistic(
                        inv.getId(),
                        product.getName(),
                        type,
                        inv.getQuantity(),
                        amount,
                        entryDate
                ));
            }

            tblReport.setItems(filteredData);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lọc dữ liệu: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearFilter() {
        cbxReportType.setValue("Tất cả");
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
        loadReportData();
    }
    @FXML
private void handleCalculateTotal() {
    double total = 0;
    for (Statistic row : tblReport.getItems()) {
        total += row.getAmount();
    }
    lblTotalAmount.setText(String.format("Tổng doanh thu: %, .0f₫", total));
}


    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
