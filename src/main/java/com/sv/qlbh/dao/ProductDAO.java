/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sv.qlbh.dao;

import com.sv.qlbh.models.Product;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author nghip
 */
public interface ProductDAO {
    // Thêm sản phẩm mới
    boolean add(Product product) throws SQLException;
    
    // Cập nhật sản phẩm
    boolean update(Product product) throws SQLException;
    
    // Xóa sản phẩm
    boolean delete(int id) throws SQLException;
    
    // Lấy sản phẩm theo ID
    Product getById(int id) throws SQLException;
    
    // Lấy tất cả sản phẩm
    List<Product> getAll() throws SQLException;
    
    // Lấy sản phẩm theo category
    List<Product> getByCategory(int categoryId) throws SQLException;
    
    // Lấy sản phẩm theo supplier
    List<Product> getBySupplier(int supplierId) throws SQLException;
    
    // Tìm sản phẩm theo tên
    List<Product> getByName(String name) throws SQLException;
    
    // Tìm sản phẩm theo barcode
    Product getByBarcode(String barcode) throws SQLException;
    
    // Lấy sản phẩm theo trạng thái
    List<Product> getByStatus(boolean status) throws SQLException;
    
    // Cập nhật stock
    boolean updateStock(int productId, int newStock) throws SQLException;
    
    // Lấy sản phẩm có stock thấp
    List<Product> getLowStockProducts(int threshold) throws SQLException;
    
    // Tìm kiếm sản phẩm theo tên hoặc barcode
    List<Product> searchByNameOrBarcode(String keyword) throws SQLException;
} 