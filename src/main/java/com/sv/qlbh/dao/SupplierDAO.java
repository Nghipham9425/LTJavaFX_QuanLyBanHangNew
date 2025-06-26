package com.sv.qlbh.dao;

import com.sv.qlbh.models.Supplier;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author nghip
 */
public interface SupplierDAO {
    // Thêm supplier mới
    boolean add(Supplier supplier) throws SQLException;
    
    // Cập nhật supplier
    boolean update(Supplier supplier) throws SQLException;
    
    // Xóa supplier
    boolean delete(int id) throws SQLException;
    
    // Lấy supplier theo ID
    Supplier getById(int id) throws SQLException;
    
    // Lấy tất cả suppliers
    List<Supplier> getAll() throws SQLException;
    
    // Tìm supplier theo tên
    List<Supplier> searchByName(String name) throws SQLException;
} 