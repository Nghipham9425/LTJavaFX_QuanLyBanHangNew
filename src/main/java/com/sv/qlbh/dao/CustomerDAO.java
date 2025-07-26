/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sv.qlbh.dao;

import com.sv.qlbh.models.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nghip
 */
public class CustomerDAO {

    public List<Customer> getAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
            System.out.println("Đã tải thành công " + customers.size() + " khách hàng");
        }
        
        return customers;
    }

    public Customer getById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                System.out.println("Tìm thấy khách hàng: " + customer.getName());
                return customer;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm khách hàng theo ID " + id + ": " + e.getMessage());
        }
        
        return null;
    }

    public Customer getByName(String name) {
        String sql = "SELECT * FROM customers WHERE name = ? LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                System.out.println("Tìm thấy khách hàng theo tên: " + customer.getName());
                return customer;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm khách hàng theo tên '" + name + "': " + e.getMessage());
        }
        
        return null;
    }

    public Customer getByPhone(String phone) {
        String sql = "SELECT * FROM customers WHERE phone = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                System.out.println("Tìm thấy khách hàng theo SĐT: " + customer.getName());
                return customer;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm khách hàng theo SĐT " + phone + ": " + e.getMessage());
        }
        
        return null;
    }

    public boolean insert(Customer customer) {
        String sql = "INSERT INTO customers (name, phone, email, points, total_spent, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getEmail());
            stmt.setInt(4, customer.getPoints());
            stmt.setDouble(5, customer.getTotalSpent());
            stmt.setBoolean(6, customer.isStatus());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Thêm khách hàng thành công: " + customer.getName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm khách hàng '" + customer.getName() + "': " + e.getMessage());
        }
        
        return false;
    }

    public boolean update(Customer customer) {
        String sql = "UPDATE customers SET name = ?, phone = ?, email = ?, points = ?, total_spent = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getEmail());
            stmt.setInt(4, customer.getPoints());
            stmt.setDouble(5, customer.getTotalSpent());
            stmt.setBoolean(6, customer.isStatus());
            stmt.setInt(7, customer.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cập nhật khách hàng thành công ID: " + customer.getId());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật khách hàng ID " + customer.getId() + ": " + e.getMessage());
        }
        
        return false;
    }

    public boolean deactivate(int id) {
        return updateStatus(id, false, "vô hiệu hóa");
    }

    public boolean activate(int id) {
        return updateStatus(id, true, "kích hoạt");
    }

    private boolean updateStatus(int id, boolean status, String action) {
        String sql = "UPDATE customers SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, status);
            stmt.setInt(2, id);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(action + " khách hàng thành công ID: " + id);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi " + action + " khách hàng ID " + id + ": " + e.getMessage());
        }
        
        return false;
    }

    public List<Customer> searchByName(String name) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE name LIKE ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
            System.out.println("Tìm thấy " + customers.size() + " khách hàng có tên chứa: " + name);
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm khách hàng theo tên '" + name + "': " + e.getMessage());
        }
        
        return customers;
    }

    public List<Customer> getByGroupId(int groupId) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE group_id = ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
            System.out.println("Tìm thấy " + customers.size() + " khách hàng trong nhóm ID: " + groupId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy khách hàng theo nhóm ID " + groupId + ": " + e.getMessage());
        }
        
        return customers;
    }

    public boolean updatePoints(int customerId, int points) {
        String sql = "UPDATE customers SET points = points + ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, points);
            stmt.setInt(2, customerId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cộng thành công " + points + " điểm cho khách hàng ID: " + customerId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi cộng điểm cho khách hàng ID " + customerId + ": " + e.getMessage());
        }
        
        return false;
    }

    public boolean updateTotalSpent(int customerId, double amount) {
        String sql = "UPDATE customers SET total_spent = total_spent + ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, amount);
            stmt.setInt(2, customerId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cập nhật thành công tổng chi tiêu +" + amount + " cho khách hàng ID: " + customerId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật tổng chi tiêu cho khách hàng ID " + customerId + ": " + e.getMessage());
        }
        
        return false;
    }

    // Helper method để chuyển đổi ResultSet thành Customer object
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setName(rs.getString("name"));
        customer.setPhone(rs.getString("phone"));
        customer.setEmail(rs.getString("email"));
        customer.setPoints(rs.getInt("points"));
        customer.setTotalSpent(rs.getDouble("total_spent"));
        customer.setStatus(rs.getBoolean("status"));
        return customer;
    }
}
