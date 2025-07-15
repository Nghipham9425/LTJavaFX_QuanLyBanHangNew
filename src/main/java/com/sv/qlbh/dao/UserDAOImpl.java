package com.sv.qlbh.dao;

import com.sv.qlbh.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    private Connection connection;

    public UserDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public boolean add(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, role, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole());
            ps.setBoolean(5, user.isStatus());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Thêm người dùng thành công: " + user.getUsername());
                return true;
            } else {
                System.out.println("Không có dòng nào bị ảnh hưởng khi thêm người dùng: " + user.getUsername());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm người dùng '" + user.getUsername() + "': " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            
            if (e.getErrorCode() == 1062) {
                System.err.println("Tên đăng nhập đã tồn tại trong hệ thống");
            }
            
            throw e;
        }
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET password=?, full_name=?, role=?, status=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getRole());
            ps.setBoolean(4, user.isStatus());
            ps.setInt(5, user.getId());
            int row = ps.executeUpdate();
            return row > 0;
        }
    }

    @Override
    public boolean deactivate(User user) throws SQLException {
        String sql = "UPDATE users SET status = false WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            int row = ps.executeUpdate();
            if (row > 0) {
                System.out.println("Vô hiệu hóa user thành công: " + user.getUsername());
                return true;
            } else {
                System.out.println("Không tìm thấy user để vô hiệu hóa ID: " + user.getId());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi vô hiệu hóa user ID " + user.getId() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean activate(User user) throws SQLException {
        String sql = "UPDATE users SET status = true WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            int row = ps.executeUpdate();
            if (row > 0) {
                System.out.println("Kích hoạt user thành công: " + user.getUsername());
                return true;
            } else {
                System.out.println("Không tìm thấy user để kích hoạt ID: " + user.getId());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kích hoạt user ID " + user.getId() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setFullName(rs.getString("full_name"));
                    u.setRole(rs.getString("role"));
                    u.setStatus(rs.getBoolean("status"));
                    return u;
                }
            }
        }
        return null;
    }

    @Override
    public User getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setFullName(rs.getString("full_name"));
                    u.setRole(rs.getString("role"));
                    u.setStatus(rs.getBoolean("status"));
                    return u;
                }
            }
        }
        return null;
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setFullName(rs.getString("full_name"));
                u.setRole(rs.getString("role"));
                u.setStatus(rs.getBoolean("status"));
                list.add(u);
            }
        }
        return list;
    }

    @Override
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? AND password=? AND status=true";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setFullName(rs.getString("full_name"));
                    u.setRole(rs.getString("role"));
                    u.setStatus(rs.getBoolean("status"));
                    System.out.println("Đăng nhập thành công: " + u.getFullName());
                    return u;
                } else {
                    System.out.println("Đăng nhập thất bại: Sai username/password hoặc tài khoản bị khóa");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đăng nhập với username '" + username + "': " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            throw e;
        }
        return null;
    }
}