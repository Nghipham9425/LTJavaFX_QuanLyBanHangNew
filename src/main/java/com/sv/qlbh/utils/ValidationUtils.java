package com.sv.qlbh.utils;

/**
 * Utility class for common validation patterns
 * Eliminates duplicate validation code across controllers
 */
public class ValidationUtils {
    
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEX = "^[0-9]{10,11}$";
    
    public static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches(PHONE_REGEX);
    }
    
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }
    
    public static boolean isPositiveNumber(String text) {
        try {
            double value = Double.parseDouble(text);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isPositiveInteger(String text) {
        try {
            int value = Integer.parseInt(text);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static String safeTrim(String text) {
        return text != null ? text.trim() : "";
    }
} 