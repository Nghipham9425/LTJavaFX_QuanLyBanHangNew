package com.sv.qlbh.dao;

import com.sv.qlbh.models.OrderDetail;
import com.sv.qlbh.models.CartItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for OrderDetail management
 * @author nghip
 */
public class OrderDetailDAO {
    
    /**
     * Thêm chi tiết đơn hàng
     */
    public void createOrderDetail(OrderDetail orderDetail) throws SQLException {
        String sql = "INSERT INTO order_details (order_id, product_id, quantity, price, discount_amount) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderDetail.getOrderId());
            stmt.setInt(2, orderDetail.getProductId());
            stmt.setInt(3, orderDetail.getQuantity());
            stmt.setDouble(4, orderDetail.getPrice());
            stmt.setDouble(5, orderDetail.getDiscountAmount());
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Thêm nhiều chi tiết đơn hàng từ CartItem list
     */
    public void createOrderDetailsFromCart(int orderId, List<CartItem> cartItems) throws SQLException {
        String sql = "INSERT INTO order_details (order_id, product_id, quantity, price, discount_amount) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (CartItem item : cartItems) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, item.getProduct().getId());
                stmt.setInt(3, item.getQuantity());
                stmt.setDouble(4, item.getPrice());
                stmt.setDouble(5, item.getDiscountAmount());
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
    
    /**
     * Lấy chi tiết đơn hàng theo order ID
     */
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) throws SQLException {
        String sql = "SELECT od.*, p.name as product_name FROM order_details od " +
                    "JOIN products p ON od.product_id = p.id WHERE od.order_id = ?";
        List<OrderDetail> orderDetails = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orderDetails.add(mapResultSetToOrderDetail(rs));
                }
            }
        }
        return orderDetails;
    }
    
    /**
     * Xóa chi tiết đơn hàng theo order ID
     */
    public boolean deleteOrderDetailsByOrderId(int orderId) throws SQLException {
        String sql = "DELETE FROM order_details WHERE order_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Cập nhật số lượng trong chi tiết đơn hàng
     */
    public boolean updateOrderDetailQuantity(int orderDetailId, int newQuantity) throws SQLException {
        String sql = "UPDATE order_details SET quantity = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, orderDetailId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Tính tổng tiền của đơn hàng từ order details
     */
    public double calculateOrderTotal(int orderId) throws SQLException {
        String sql = "SELECT SUM((price * quantity) - discount_amount) as total FROM order_details WHERE order_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }
    
    /**
     * Map ResultSet thành OrderDetail object
     */
    private OrderDetail mapResultSetToOrderDetail(ResultSet rs) throws SQLException {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(rs.getInt("id"));
        orderDetail.setOrderId(rs.getInt("order_id"));
        orderDetail.setProductId(rs.getInt("product_id"));
        orderDetail.setQuantity(rs.getInt("quantity"));
        orderDetail.setPrice(rs.getDouble("price"));
        orderDetail.setDiscountAmount(rs.getDouble("discount_amount"));
        
        // Thêm thông tin product nếu có join
        if (rs.getMetaData().getColumnCount() > 6) {
            try {
                orderDetail.setProductName(rs.getString("product_name"));
            } catch (SQLException e) {
                // Ignore if column doesn't exist
            }
        }
        
        return orderDetail;
    }
} 