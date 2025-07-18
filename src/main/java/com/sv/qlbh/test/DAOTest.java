//package com.sv.qlbh.test;
//
//import com.sv.qlbh.dao.CategoryDAO;
//import com.sv.qlbh.dao.CategoryDAOImpl;
//import com.sv.qlbh.dao.ProductDAO;
//import com.sv.qlbh.dao.ProductDAOImpl;
//import com.sv.qlbh.models.Category;
//import com.sv.qlbh.models.Product;
//import java.sql.SQLException;
//import java.util.List;
//
//public class DAOTest {
//    
//    public static void main(String[] args) {
//        try {
//            // Test CategoryDAO
//            System.out.println("=== Testing CategoryDAO ===");
//            testCategoryDAO();
//            
//            // Test ProductDAO
//            System.out.println("\n=== Testing ProductDAO ===");
//            testProductDAO();
//            
//        } catch (SQLException e) {
//            System.err.println("Error during testing: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//    
//    private static void testCategoryDAO() throws SQLException {
//        CategoryDAO categoryDAO = new CategoryDAOImpl();
//        
//        // Test getAll
//        System.out.println("\n1. Testing getAll()");
//        List<Category> categories = categoryDAO.getAll();
//        System.out.println("Total categories: " + categories.size());
//        for (Category cat : categories) {
//            System.out.println("- " + cat.getName() + " (ID: " + cat.getId() + ")");
//        }
//        
//        // Test getById
//        System.out.println("\n2. Testing getById()");
//        Category category = categoryDAO.getById(1);
//        if (category != null) {
//            System.out.println("Found category: " + category.getName());
//        } else {
//            System.out.println("Category not found");
//        }
//        
//        // Test getByName
//        System.out.println("\n3. Testing getByName()");
//        List<Category> searchResults = categoryDAO.getByName("Thực phẩm");
//        System.out.println("Search results for 'Thực phẩm': " + searchResults.size());
//        for (Category cat : searchResults) {
//            System.out.println("- " + cat.getName());
//        }
//    }
//    
//    private static void testProductDAO() throws SQLException {
//        ProductDAO productDAO = new ProductDAOImpl();
//        
//        // Test getAll
//        System.out.println("\n1. Testing getAll()");
//        List<Product> products = productDAO.getAll();
//        System.out.println("Total products: " + products.size());
//        for (Product prod : products) {
//            System.out.println("- " + prod.getName() + " (ID: " + prod.getId() + ", Stock: " + prod.getStock() + ")");
//        }
//        
//        // Test getById
//        System.out.println("\n2. Testing getById()");
//        Product product = productDAO.getById(1);
//        if (product != null) {
//            System.out.println("Found product: " + product.getName());
//            System.out.println("Price: " + product.getPrice());
//            System.out.println("Stock: " + product.getStock());
//        } else {
//            System.out.println("Product not found");
//        }
//        
//        // Test getByBarcode
//        System.out.println("\n3. Testing getByBarcode()");
//        Product productByBarcode = productDAO.getByBarcode("CH001");
//        if (productByBarcode != null) {
//            System.out.println("Found product by barcode: " + productByBarcode.getName());
//        } else {
//            System.out.println("Product not found");
//        }
//        
//        // Test getByCategory
//        System.out.println("\n4. Testing getByCategory()");
//        List<Product> productsByCategory = productDAO.getByCategory(1);
//        System.out.println("Products in category 1: " + productsByCategory.size());
//        for (Product prod : productsByCategory) {
//            System.out.println("- " + prod.getName());
//        }
//        
//        // Test getLowStockProducts
//        System.out.println("\n5. Testing getLowStockProducts()");
//        List<Product> lowStockProducts = productDAO.getLowStockProducts(50);
//        System.out.println("Products with stock <= 50: " + lowStockProducts.size());
//        for (Product prod : lowStockProducts) {
//            System.out.println("- " + prod.getName() + " (Stock: " + prod.getStock() + ")");
//        }
//    }
//}