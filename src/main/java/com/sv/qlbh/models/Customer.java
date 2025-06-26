/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sv.qlbh.models;

/**
 *
 * @author nghip
 */
public class Customer {
    private int id;
    private String name;
    private String phone;
    private String email;
    private int groupId;
    private int points;
    private double totalSpent;
    private boolean status;

    public Customer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Customer(int id, String name, String phone, String email, int groupId, int points, double totalSpent, boolean status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.groupId = groupId;
        this.points = points;
        this.totalSpent = totalSpent;
        this.status = status;
    }
    
    public CustomerGroup getCustomerGroup() {
        return CustomerGroup.getGroupBySpent(this.totalSpent);
    }
    
    public String getGroupDisplayName() {
        return getCustomerGroup().getDisplayNameWithIcon();
    }
    
    public double getAmountToNextLevel() {
        return getCustomerGroup().getAmountToNextLevel(this.totalSpent);
    }
    
    public boolean canLevelUp() {
        return getCustomerGroup().getNextLevel() != null;
    }
    
}
