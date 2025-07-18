package com.sv.qlbh.utils;

import com.sv.qlbh.models.Order;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * VNPay Service for JavaFX Application
 * @author nghip
 */
public class VNPayService {
    
    /**
     * Tạo URL thanh toán VNPay
     */
    public static String createPaymentUrl(Order order, String clientIpAddress) {
        try {
            Map<String, String> vnpParams = new HashMap<>();
            
            // Thông tin cơ bản
            vnpParams.put("vnp_Version", VNPayConfig.VNP_VERSION);
            vnpParams.put("vnp_Command", VNPayConfig.VNP_COMMAND);
            vnpParams.put("vnp_TmnCode", VNPayConfig.VNP_TMN_CODE);
            vnpParams.put("vnp_Amount", String.valueOf((long)(order.getFinalAmount() * 100))); // VNPay yêu cầu số tiền * 100
            vnpParams.put("vnp_CurrCode", "VND");
            // Tạo unique transaction reference để tránh lỗi "Giao dịch đã tồn tại"
            String uniqueTxnRef = order.getId() + "_" + System.currentTimeMillis();
            vnpParams.put("vnp_TxnRef", uniqueTxnRef);
            vnpParams.put("vnp_OrderInfo", "Order " + order.getId());
            vnpParams.put("vnp_OrderType", VNPayConfig.VNP_ORDER_TYPE);
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", VNPayConfig.getReturnUrl());
            // vnpParams.put("vnp_IpnUrl", VNPayConfig.getReturnUrl()); // Remove IPN URL for now
            vnpParams.put("vnp_IpAddr", clientIpAddress != null ? clientIpAddress : "127.0.0.1");
            
            // Thời gian tạo và hết hạn
            String vnpCreateDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            vnpParams.put("vnp_CreateDate", vnpCreateDate);
            
            LocalDateTime expireTime = LocalDateTime.now().plusHours(1); // Increase to 1 hour
            String vnpExpireDate = expireTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            vnpParams.put("vnp_ExpireDate", vnpExpireDate);
            
            // Tạo hash 
            String signValue = VNPayConfig.hashAllFields(vnpParams);
            vnpParams.put("vnp_SecureHash", signValue);
            // vnpParams.put("vnp_SecureHashType", "SHA512"); // Remove this - may cause conflict
            
            // Tạo URL query string như code mẫu ajaxServlet.java
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);
            StringBuilder query = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnpParams.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Build query với URL encoding US_ASCII như code mẫu
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                    }
                }
            }
            
            String finalUrl = VNPayConfig.VNP_PAY_URL + "?" + query.toString();
            return finalUrl;
            
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error creating VNPay URL", e);
        }
    }
    
    /**
     * Xác thực và xử lý response từ VNPay
     */
    public static VNPayResult processPaymentReturn(Map<String, String> params) {
        VNPayResult result = new VNPayResult();
        
        try {
            // Lấy secure hash từ response
            String vnpSecureHash = params.get("vnp_SecureHash");
            
            // Tạo map để hash (loại bỏ secure hash)
            Map<String, String> fields = new HashMap<>(params);
            fields.remove("vnp_SecureHashType");
            fields.remove("vnp_SecureHash");
            
            // Tính toán hash
            String signValue = VNPayConfig.hashAllFields(fields);
            
            // Kiểm tra chữ ký
            if (signValue.equals(vnpSecureHash)) {
                result.setSignatureValid(true);
                
                // Lấy thông tin giao dịch
                result.setTransactionNo(params.get("vnp_TransactionNo"));
                result.setOrderId(params.get("vnp_TxnRef"));
                result.setAmount(Long.parseLong(params.get("vnp_Amount")) / 100); // Chia 100 để về số tiền gốc
                result.setOrderInfo(params.get("vnp_OrderInfo"));
                result.setPayDate(params.get("vnp_PayDate"));
                result.setTransactionStatus(params.get("vnp_TransactionStatus"));
                result.setResponseCode(params.get("vnp_ResponseCode"));
                
                // Kiểm tra trạng thái giao dịch
                if ("00".equals(params.get("vnp_TransactionStatus"))) {
                    result.setTransactionSuccess(true);
                    result.setMessage("Giao dịch thành công");
                } else {
                    result.setTransactionSuccess(false);
                    result.setMessage("Giao dịch thất bại");
                }
                
            } else {
                result.setSignatureValid(false);
                result.setMessage("Chữ ký không hợp lệ");
            }
            
        } catch (NumberFormatException e) {
            result.setSignatureValid(false);
            result.setMessage("Lỗi định dạng số: " + e.getMessage());
        } catch (RuntimeException e) {
            result.setSignatureValid(false);
            result.setMessage("Lỗi xử lý phản hồi: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Class chứa kết quả xử lý VNPay
     */
    public static class VNPayResult {
        private boolean signatureValid;
        private boolean transactionSuccess;
        private String transactionNo;
        private String orderId;
        private double amount;
        private String orderInfo;
        private String payDate;
        private String transactionStatus;
        private String responseCode;
        private String message;
        
        // Getters and Setters
        public boolean isSignatureValid() { return signatureValid; }
        public void setSignatureValid(boolean signatureValid) { this.signatureValid = signatureValid; }
        
        public boolean isTransactionSuccess() { return transactionSuccess; }
        public void setTransactionSuccess(boolean transactionSuccess) { this.transactionSuccess = transactionSuccess; }
        
        public String getTransactionNo() { return transactionNo; }
        public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        
        public String getOrderInfo() { return orderInfo; }
        public void setOrderInfo(String orderInfo) { this.orderInfo = orderInfo; }
        
        public String getPayDate() { return payDate; }
        public void setPayDate(String payDate) { this.payDate = payDate; }
        
        public String getTransactionStatus() { return transactionStatus; }
        public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }
        
        public String getResponseCode() { return responseCode; }
        public void setResponseCode(String responseCode) { this.responseCode = responseCode; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
} 