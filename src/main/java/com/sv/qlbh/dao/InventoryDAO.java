package com.sv.qlbh.dao;

import com.sv.qlbh.models.Inventory;
import com.sv.qlbh.models.Product; // Cần import Product để truy cập thông tin stock
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    public boolean addInventoryEntry(Inventory entry) throws SQLException {
        String sql = "INSERT INTO inventory (product_id, quantity, type, note, reference_id, reference_type) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, entry.getProductId());
            pstmt.setInt(2, entry.getQuantity());
            pstmt.setString(3, entry.getType());
            pstmt.setString(4, entry.getNote());
            
            if (entry.getReferenceId() != null) {
                pstmt.setInt(5, entry.getReferenceId());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }
            pstmt.setString(6, entry.getReferenceType());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Phương thức mới để thêm giao dịch kho và cập nhật tồn kho sản phẩm trong một TRANSACTION duy nhất.
     * Đảm bảo tính toàn vẹn dữ liệu: nếu một trong hai thao tác thất bại, toàn bộ sẽ được rollback.
     *
     * @param inventoryEntry Đối tượng Inventory chứa thông tin giao dịch cần thêm.
     * @param productId ID của sản phẩm cần cập nhật tồn kho.
     * @param newStock Giá trị tồn kho mới sau giao dịch.
     * @return true nếu cả hai thao tác thành công, false nếu không.
     * @throws SQLException Nếu có lỗi SQL trong quá trình thực hiện giao dịch.
     */
    public boolean addInventoryEntryAndUpdateProductStock(Inventory inventoryEntry, int productId, int newStock) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch: tắt chế độ auto-commit

            // 1. Thêm giao dịch kho
            String sqlInsertInventory = "INSERT INTO inventory (product_id, quantity, type, note, reference_id, reference_type) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsertInventory)) {
                pstmt.setInt(1, inventoryEntry.getProductId());
                pstmt.setInt(2, inventoryEntry.getQuantity());
                pstmt.setString(3, inventoryEntry.getType());
                pstmt.setString(4, inventoryEntry.getNote());

                if (inventoryEntry.getReferenceId() != null) {
                    pstmt.setInt(5, inventoryEntry.getReferenceId());
                } else {
                    pstmt.setNull(5, java.sql.Types.INTEGER);
                }
                pstmt.setString(6, inventoryEntry.getReferenceType());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback(); // Rollback nếu không thêm được giao dịch
                    return false;
                }
            }

            // 2. Cập nhật tồn kho sản phẩm
            String sqlUpdateProductStock = "UPDATE products SET stock = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateProductStock)) {
                pstmt.setInt(1, newStock);
                pstmt.setInt(2, productId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback(); // Rollback nếu không cập nhật được tồn kho
                    return false;
                }
            }

            conn.commit(); // Commit giao dịch nếu cả hai thao tác đều thành công
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu có bất kỳ lỗi nào xảy ra
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback giao dịch: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            throw e; // Ném lại ngoại lệ để Controller xử lý
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Đặt lại auto-commit về true
                    conn.close(); // Đóng kết nối
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Inventory> getAllInventoryEntries() throws SQLException {
        List<Inventory> entries = new ArrayList<>();
        String sql = "SELECT i.id, i.product_id, i.quantity, i.type, i.reference_id, i.reference_type, i.note, i.created_at, p.name AS product_name " +
                     "FROM inventory i JOIN products p ON i.product_id = p.id ORDER BY i.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                entries.add(mapResultSetToInventory(rs));
            }
        }
        return entries;
    }

    public List<Inventory> getInventoryEntriesByProductId(int productId) throws SQLException {
        List<Inventory> entries = new ArrayList<>();
        String sql = "SELECT i.id, i.product_id, i.quantity, i.type, i.reference_id, i.reference_type, i.note, i.created_at, p.name AS product_name " +
                     "FROM inventory i JOIN products p ON i.product_id = p.id WHERE i.product_id = ? ORDER BY i.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapResultSetToInventory(rs));
                }
            }
        }
        return entries;
    }

    private Inventory mapResultSetToInventory(ResultSet rs) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setId(rs.getInt("id"));
        inventory.setProductId(rs.getInt("product_id"));
        inventory.setQuantity(rs.getInt("quantity"));
        inventory.setType(rs.getString("type"));
        
        Object refId = rs.getObject("reference_id");
        inventory.setReferenceId(refId != null ? (Integer) refId : null);
        
        inventory.setReferenceType(rs.getString("reference_type"));
        inventory.setNote(rs.getString("note"));
        
        inventory.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        // Đảm bảo Product model có trường productName và setter cho nó.
        inventory.setProductName(rs.getString("product_name"));
        
        return inventory;
    }
} 