package com.sv.qlbh.dao;

import com.sv.qlbh.models.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nghip
 */
public class SupplierDAOImpl implements SupplierDAO {
    
    @Override
    public boolean add(Supplier supplier) throws SQLException {
     
        String sql = "INSERT INTO suppliers (name, phone, email, address, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getPhone());
            stmt.setString(3, supplier.getEmail());
            stmt.setString(4, supplier.getAddress());
  
            stmt.setBoolean(5, supplier.isStatus()); // Đổi từ index 6 thành 5
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm nhà cung cấp '" + supplier.getName() + "': " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            
            if (e.getErrorCode() == 1062) {
                System.err.println("Thông tin nhà cung cấp có thể đã tồn tại trong hệ thống");
            }
            
            throw e;
        }
    }
    
    @Override
    public boolean update(Supplier supplier) throws SQLException {

        String sql = "UPDATE suppliers SET name=?, phone=?, email=?, address=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getPhone());
            stmt.setString(3, supplier.getEmail());
            stmt.setString(4, supplier.getAddress());

            stmt.setBoolean(5, supplier.isStatus());
            stmt.setInt(6, supplier.getId()); 
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public Supplier getById(int id) throws SQLException {
        String sql = "SELECT * FROM suppliers WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSupplier(rs);
            }
        }
        return null;
    }
    
    @Override
    public List<Supplier> getAll() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        }
        return suppliers;
    }
    
    @Override
    public List<Supplier> searchByName(String name) throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers WHERE name LIKE ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        }
        return suppliers;
    }
    
    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setId(rs.getInt("id"));
        supplier.setName(rs.getString("name"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setEmail(rs.getString("email"));
        supplier.setAddress(rs.getString("address"));
        supplier.setStatus(rs.getBoolean("status"));
        return supplier;
    }
} 