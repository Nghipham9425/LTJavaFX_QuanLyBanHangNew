package com.sv.qlbh.utils;

import com.sv.qlbh.models.Order;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class VNPayService {
    public static String createPaymentUrl(Order order, String clientIp) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("vnp_Version", VNPayConfig.VNP_VERSION);
            params.put("vnp_Command", VNPayConfig.VNP_COMMAND);
            params.put("vnp_TmnCode", VNPayConfig.VNP_TMN_CODE);
            params.put("vnp_Amount", String.valueOf((long)(order.getFinalAmount() * 100)));
            params.put("vnp_CurrCode", "VND");
            params.put("vnp_TxnRef", order.getId() + "_" + System.currentTimeMillis());
            params.put("vnp_OrderInfo", "Order " + order.getId());
            params.put("vnp_OrderType", VNPayConfig.VNP_ORDER_TYPE);
            params.put("vnp_Locale", "vn");
            params.put("vnp_ReturnUrl", VNPayConfig.getReturnUrl());
            params.put("vnp_IpAddr", clientIp != null ? clientIp : "127.0.0.1");
            params.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            params.put("vnp_ExpireDate", LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            params.put("vnp_SecureHash", VNPayConfig.hashAllFields(params));
            List<String> keys = new ArrayList<>(params.keySet());
            Collections.sort(keys);
            StringBuilder query = new StringBuilder();
            for (int i = 0; i < keys.size(); i++) {
                String k = keys.get(i), v = params.get(k);
                if (v != null && !v.isEmpty()) {
                    query.append(URLEncoder.encode(k, StandardCharsets.US_ASCII)).append("=")
                         .append(URLEncoder.encode(v, StandardCharsets.US_ASCII));
                    if (i < keys.size() - 1) query.append('&');
                }
            }
            return VNPayConfig.VNP_PAY_URL + "?" + query;
        } catch (Exception e) {
            throw new RuntimeException("Error creating VNPay URL", e);
        }
    }

    public static VNPayResult processPaymentReturn(Map<String, String> params) {
        VNPayResult r = new VNPayResult();
        try {
            String hash = params.get("vnp_SecureHash");
            Map<String, String> fields = new HashMap<>(params);
            fields.remove("vnp_SecureHashType");
            fields.remove("vnp_SecureHash");
            if (VNPayConfig.hashAllFields(fields).equals(hash)) {
                r.signatureValid = true;
                r.transactionNo = params.get("vnp_TransactionNo");
                r.orderId = params.get("vnp_TxnRef");
                r.amount = Long.parseLong(params.get("vnp_Amount")) / 100.0;
                r.orderInfo = params.get("vnp_OrderInfo");
                r.payDate = params.get("vnp_PayDate");
                r.transactionStatus = params.get("vnp_TransactionStatus");
                r.responseCode = params.get("vnp_ResponseCode");
                if ("00".equals(r.transactionStatus)) {
                    r.transactionSuccess = true;
                    r.message = "Giao dịch thành công";
                } else {
                    r.transactionSuccess = false;
                    r.message = "Giao dịch thất bại";
                }
            } else {
                r.signatureValid = false;
                r.message = "Chữ ký không hợp lệ";
            }
        } catch (Exception e) {
            r.signatureValid = false;
            r.message = "Lỗi xử lý: " + e.getMessage();
        }
        return r;
    }

    public static class VNPayResult {
        public boolean signatureValid;
        public boolean transactionSuccess;
        public String transactionNo, orderId, orderInfo, payDate, transactionStatus, responseCode, message;
        public double amount;
        public boolean isSignatureValid() { return signatureValid; }
        public boolean isTransactionSuccess() { return transactionSuccess; }
        public String getTransactionNo() { return transactionNo; }
        public String getOrderId() { return orderId; }
        public double getAmount() { return amount; }
        public String getOrderInfo() { return orderInfo; }
        public String getPayDate() { return payDate; }
        public String getTransactionStatus() { return transactionStatus; }
        public String getResponseCode() { return responseCode; }
        public String getMessage() { return message; }
    }
} 