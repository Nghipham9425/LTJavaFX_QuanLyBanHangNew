/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sv.qlbh.dao;
import com.sv.qlbh.models.User;
import java.sql.SQLException;
/**
 *
 * @author nghip
 */
import java.util.List;
public interface UserDAO {
    //Add user moi
    boolean add(User user) throws SQLException;
    
    boolean update(User user) throws SQLException;
    
    boolean deactivate(User user) throws SQLException;
    
    boolean activate(User user) throws SQLException;
    
    User getById(int id) throws SQLException;
    
    User getByUsername(String username) throws SQLException;
    
    List<User> getAll() throws SQLException;
    
    User login(String username, String password) throws SQLException;
}
