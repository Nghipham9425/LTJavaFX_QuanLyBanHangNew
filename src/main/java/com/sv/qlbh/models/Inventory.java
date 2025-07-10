/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sv.qlbh.models;

import java.time.LocalDateTime;

/**
 *
 * @author nghip
 */
public class Inventory {
    private int id;
    private int productId;
    private int quantity;
    private String type;
    private Integer referenceId;
    private String referenceType;
    private String note;
    private LocalDateTime createdAt;
    private String productName; // Để lưu trữ tên sản phẩm khi hiển thị trên UI

    public Inventory() {
    }

    public Inventory(int id, int productId, int quantity, String type, Integer referenceId, String referenceType, String note, LocalDateTime createdAt, String productName) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.type = type;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.note = note;
        this.createdAt = createdAt;
        this.productName = productName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getter và Setter cho productName
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    
}
