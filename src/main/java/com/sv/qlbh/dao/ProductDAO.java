/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sv.qlbh.dao;

import com.sv.qlbh.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nghip
 */
public class ProductDAO {
    
    public boolean add(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, category_id, supplier_id, barcode, price, cost_price, stock, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getCategoryId());
            stmt.setInt(3, product.getSupplierId());
            stmt.setString(4, product.getBarcode());
            stmt.setDouble(5, product.getPrice());
            stmt.setDouble(6, product.getCostPrice());
            stmt.setInt(7, product.getStock());
            stmt.setBoolean(8, product.isStatus());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm sản phẩm '" + product.getName() + "': " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            
            if (e.getErrorCode() == 1062) {
                System.err.println("Mã vạch có thể đã tồn tại trong hệ thống");
            } else if (e.getErrorCode() == 1452) {
                System.err.println("Mã danh mục hoặc nhà cung cấp không hợp lệ");
            }
            
            throw e;
        }
    }
    
    public boolean update(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, category_id = ?, supplier_id = ?, barcode = ?, price = ?, cost_price = ?, stock = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getCategoryId());
            stmt.setInt(3, product.getSupplierId());
            stmt.setString(4, product.getBarcode());
            stmt.setDouble(5, product.getPrice());
            stmt.setDouble(6, product.getCostPrice());
            stmt.setInt(7, product.getStock());
            stmt.setBoolean(8, product.isStatus());
            stmt.setInt(9, product.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public Product getById(int id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
            return null;
        }
    }
    
    public List<Product> getAll() throws SQLException {
        String sql = "SELECT * FROM products ORDER BY name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            throw e;
        }
        return products;
    }
    
    public List<Product> getByCategory(int categoryId) throws SQLException {
        String sql = "SELECT * FROM products WHERE category_id = ? ORDER BY name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }
    
    public List<Product> getByName(String name) throws SQLException {
        String sql = "SELECT * FROM products WHERE name LIKE ? ORDER BY name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }
    
    public Product getByBarcode(String barcode) throws SQLException {
        String sql = "SELECT * FROM products WHERE barcode = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, barcode);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
            return null;
        }
    }
    
    public boolean updateStock(int productId, int newStock) throws SQLException {
        String sql = "UPDATE products SET stock = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newStock);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Product> searchByNameOrBarcode(String keyword) throws SQLException {
        String sql = "SELECT * FROM products WHERE name LIKE ? OR barcode LIKE ? ORDER BY name";
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }
    
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setSupplierId(rs.getInt("supplier_id"));
        product.setBarcode(rs.getString("barcode"));
        product.setPrice(rs.getDouble("price"));
        product.setCostPrice(rs.getDouble("cost_price"));
        product.setStock(rs.getInt("stock"));
        product.setStatus(rs.getBoolean("status"));
        return product;
    }
} 