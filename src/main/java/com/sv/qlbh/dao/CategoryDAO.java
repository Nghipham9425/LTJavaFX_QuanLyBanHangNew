/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sv.qlbh.dao;

import com.sv.qlbh.models.Category;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author nghip
 */
public interface CategoryDAO {
    // Thêm category mới
    boolean add(Category category) throws SQLException;
    
    // Cập nhật category
    boolean update(Category category) throws SQLException;
    
    // Xóa category
    boolean delete(int id) throws SQLException;
    
    // Lấy category theo ID
    Category getById(int id) throws SQLException;
    
    // Lấy tất cả categories
    List<Category> getAll() throws SQLException;
    
    // Tìm category theo tên
    List<Category> getByName(String name) throws SQLException;
} 