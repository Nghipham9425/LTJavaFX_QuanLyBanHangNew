package com.sv.qlbh.dao;

import com.sv.qlbh.models.OrderDetail;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for OrderDetail management
 * @author nghip
 */
public interface OrderDetailDAO {
    // Thêm chi tiết đơn hàng
    boolean add(OrderDetail orderDetail) throws SQLException;
    
    // Thêm nhiều chi tiết đơn hàng cùng lúc
    boolean addMultiple(List<OrderDetail> orderDetails) throws SQLException;
    
    // Lấy chi tiết đơn hàng theo order ID
    List<OrderDetail> getByOrderId(int orderId) throws SQLException;
    
    // Lấy chi tiết theo ID
    OrderDetail getById(int id) throws SQLException;
    
    // Cập nhật chi tiết đơn hàng
    boolean update(OrderDetail orderDetail) throws SQLException;
    
    // Xóa chi tiết đơn hàng
    boolean delete(int id) throws SQLException;
    
    // Xóa tất cả chi tiết của đơn hàng
    boolean deleteByOrderId(int orderId) throws SQLException;
    
    // Lấy tổng số lượng sản phẩm đã bán theo product ID
    int getTotalQuantitySoldByProduct(int productId) throws SQLException;
    
    // Lấy doanh thu theo sản phẩm
    double getRevenueByProduct(int productId) throws SQLException;
} 