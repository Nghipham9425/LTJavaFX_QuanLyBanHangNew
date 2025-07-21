package com.sv.qlbh.models;

import java.time.LocalDate;

public class BaoCao {
    private int id;
    private String name;
    private String category;
    private int quantity;
    private double amount;
    private LocalDate date;

    public BaoCao(int id, String name, String category, int quantity, double amount, LocalDate date) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.amount = amount;
        this.date = date;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDate(LocalDate date) { this.date = date; }
}
