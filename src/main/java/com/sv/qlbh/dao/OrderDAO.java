package com.sv.qlbh.dao;

import com.sv.qlbh.models.Order;
import com.sv.qlbh.models.OrderDetail;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Order management with VNPay support
 * @author nghip
 */
public class OrderDAO {
    
    /**
     * Tạo đơn hàng mới
     */
    public int createOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, user_id, total_amount, discount_amount, final_amount, payment_method, status, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setObject(1, order.getCustomerId()); // nullable
            stmt.setInt(2, order.getUserId());
            stmt.setDouble(3, order.getTotalAmount());
            stmt.setDouble(4, order.getDiscountAmount());
            stmt.setDouble(5, order.getFinalAmount());
            stmt.setString(6, order.getPaymentMethod());
            stmt.setString(7, order.getStatus() != null ? order.getStatus() : "PROCESSING");
            stmt.setString(8, order.getNote());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Tạo đơn hàng thất bại, không có dòng nào được tạo.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    order.setId(orderId);
                    return orderId;
                } else {
                    throw new SQLException("Tạo đơn hàng thất bại, không lấy được ID.");
                }
            }
        }
    }
    
    /**
     * Cập nhật trạng thái đơn hàng (dùng cho VNPay callback)
     */
    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    
    public boolean updateOrderWithVNPayInfo(int orderId, String status, String transactionNo) throws SQLException {
        String sql = "UPDATE orders SET status = ?, note = CONCAT(IFNULL(note, ''), ' - VNPay Transaction: ', ?) WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setString(2, transactionNo);
            stmt.setInt(3, orderId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Lấy đơn hàng theo ID
     */
    public Order getOrderById(int orderId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Lấy tất cả đơn hàng theo trạng thái
     */
    public List<Order> getOrdersByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        }
        return orders;
    }
    
    /**
     * Xóa đơn hàng (chỉ cho phép với đơn hàng PROCESSING hoặc FAILED)
     */
    public boolean deleteOrder(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ? AND status IN ('PROCESSING', 'FAILED')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Lấy tất cả đơn hàng
     */
    public List<Order> getAllOrders() throws SQLException {
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        }
        return orders;
    }

    /**
     * Hủy đơn hàng (chỉ cho phép với đơn hàng PROCESSING)
     */
    public boolean cancelOrder(int orderId) throws SQLException {
        String sql = "UPDATE orders SET status = 'CANCELLED' WHERE id = ? AND status = 'PROCESSING'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Map ResultSet thành Order object
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setCustomerId(rs.getObject("customer_id", Integer.class));
        order.setUserId(rs.getInt("user_id"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setDiscountAmount(rs.getDouble("discount_amount"));
        order.setFinalAmount(rs.getDouble("final_amount"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setStatus(rs.getString("status"));
        order.setNote(rs.getString("note"));
        
        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            order.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        return order;
    }
} 