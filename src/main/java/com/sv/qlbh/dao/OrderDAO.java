package com.sv.qlbh.dao;

import com.sv.qlbh.models.Order;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO interface for Order management
 * @author nghip
 */
public interface OrderDAO {
    // Thêm đơn hàng mới
    boolean add(Order order) throws SQLException;
    
    // Lấy đơn hàng theo ID
    Order getById(int id) throws SQLException;
    
    // Lấy tất cả đơn hàng
    List<Order> getAll() throws SQLException;
    
    // Lấy đơn hàng theo khách hàng
    List<Order> getByCustomerId(int customerId) throws SQLException;
    
    // Lấy đơn hàng theo user (nhân viên)
    List<Order> getByUserId(int userId) throws SQLException;
    
    // Lấy đơn hàng theo trạng thái
    List<Order> getByStatus(String status) throws SQLException;
    
    // Lấy đơn hàng theo ngày
    List<Order> getByDate(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
    
    // Cập nhật trạng thái đơn hàng
    boolean updateStatus(int orderId, String status) throws SQLException;
    
    // Lấy tổng doanh thu theo ngày
    double getTotalRevenueByDate(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
    
    // Lấy số lượng đơn hàng theo ngày
    int getOrderCountByDate(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
    
    // Tìm kiếm đơn hàng theo customer name hoặc phone
    List<Order> searchByCustomer(String keyword) throws SQLException;
} 