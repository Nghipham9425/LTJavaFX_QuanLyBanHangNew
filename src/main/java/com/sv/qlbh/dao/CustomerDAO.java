/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sv.qlbh.dao;

import java.util.List;
import com.sv.qlbh.models.Customer;
/**
 *
 * @author nghip
 */
public interface CustomerDAO {
    List<Customer> getAll();
    Customer getById(int id);
    Customer getByName(String name);
    Customer getByPhone(String phone);
    boolean insert(Customer customer);
    boolean update(Customer customer);
    boolean delete(int id);
    List<Customer> searchByName(String name);
    List<Customer> getByGroupId(int groupId);
    boolean updatePoints(int customerId, int points);
    boolean updateTotalSpent(int customerId, double amount);
}
