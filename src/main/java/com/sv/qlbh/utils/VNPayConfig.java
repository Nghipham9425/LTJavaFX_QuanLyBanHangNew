package com.sv.qlbh.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * VNPay Configuration for JavaFX Application
 * @author nghip
 */
public class VNPayConfig {
    
    // VNPay Sandbox Configuration
    public static final String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    // Your VNPay credentials (từ email VNPay)
    public static final String VNP_TMN_CODE = "SDQ9JWEK"; // Your real credentials
    public static final String SECRET_KEY = "ADEM3LCZ9EXKBW35Z23UMZLYLEV9B1VG"; // Your real credentials
    public static final String VNP_API_URL = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    
    /**
     * Return URL như code mẫu VNPay
     */
    public static String getReturnUrl() {
        // Theo code mẫu ajaxServlet.java sử dụng localhost:8080
        int port = VNPayReturnHandler.getActivePort();
        if (port > 0) {
            return "http://localhost:" + port + "/vnpay-return";
        }
        return "http://localhost:8090/vnpay-return"; // fallback
    }
    
    // VNPay Version and Command
    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND = "pay";
    public static final String VNP_ORDER_TYPE = "other";
    
    /**
     * Tạo MD5 hash
     */
    public static String md5(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }

    /**
     * Tạo SHA256 hash
     */
    public static String sha256(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }

    /**
     * Hash tất cả các fields cho VNPay (copy từ code mẫu ajaxServlet.java)
     */
    public static String hashAllFields(Map<String, String> fields) {
        // Copy exact logic từ ajaxServlet.java
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data với URL encoding US_ASCII như code mẫu
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(java.net.URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (Exception e) {
                    hashData.append(fieldValue);
                }
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }
        
        String queryString = hashData.toString();
        System.out.println("DEBUG Hash input: " + queryString);
        String hash = hmacSHA512(SECRET_KEY, queryString);
        System.out.println("DEBUG Generated hash: " + hash);
        return hash;
    }
    
    /**
     * Tạo HMAC SHA512
     */
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Tạo số ngẫu nhiên
     */
    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
} 