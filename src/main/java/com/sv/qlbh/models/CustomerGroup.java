/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sv.qlbh.models;

/**
 *
 * @author nghip
 */
public class CustomerGroup {
    private int id;
    private String name;
    private double discountPercent;
    private String description;

    public CustomerGroup() {
    }

    public CustomerGroup(int id, String name, double discountPercent, String description) {
        this.id = id;
        this.name = name;
        this.discountPercent = discountPercent;
        this.description = description;
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

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
