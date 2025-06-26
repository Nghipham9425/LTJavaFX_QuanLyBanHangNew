package com.sv.qlbh.models;

/**
 * Model cho item trong giỏ hàng POS
 * @author nghip
 */
public class CartItem {
    private Product product;
    private int quantity;
    private double price;
    private double discountAmount;
    
    public CartItem() {
    }
    
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.price = product.getPrice();
        this.discountAmount = 0.0;
    }
    
    public CartItem(Product product, int quantity, double price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.discountAmount = 0.0;
    }

    // Getters and Setters
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    // Calculated properties
    public double getSubtotal() {
        return price * quantity;
    }
    
    public double getTotalAfterDiscount() {
        return getSubtotal() - discountAmount;
    }
    
    // Convenience methods
    public String getProductName() {
        return product != null ? product.getName() : "";
    }
    
    public double getProductPrice() {
        return product != null ? product.getPrice() : 0.0;
    }
    
    public int getProductStock() {
        return product != null ? product.getStock() : 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CartItem cartItem = (CartItem) obj;
        return product != null && product.equals(cartItem.product);
    }
    
    @Override
    public int hashCode() {
        return product != null ? product.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return String.format("CartItem{product=%s, quantity=%d, price=%.2f, total=%.2f}", 
                           getProductName(), quantity, price, getTotalAfterDiscount());
    }
} 