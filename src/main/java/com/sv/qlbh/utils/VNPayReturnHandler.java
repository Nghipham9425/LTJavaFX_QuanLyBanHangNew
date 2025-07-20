package com.sv.qlbh.utils;

import com.sv.qlbh.dao.OrderDAO;
import com.sv.qlbh.models.Order;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import com.sv.qlbh.dao.CustomerDAO;

/**
 * VNPay Return Handler for JavaFX Application
 * Tạo một HTTP server đơn giản để nhận callback từ VNPay
 * @author nghip
 */
public class VNPayReturnHandler {
    
    private static final int[] PORTS = {8090, 8091, 8092, 8093, 8094}; // Skip 8080-8084 to avoid XAMPP
    private static ServerSocket serverSocket;
    private static boolean isRunning = false;
    private static int activePort = -1;
    private static OrderDAO orderDAO = new OrderDAO();
    
    /**
     * Khởi động HTTP server để lắng nghe VNPay return
     */
    public static void startServer() {
        if (isRunning) {
            return;
        }
        
        Thread serverThread = new Thread(() -> {
            ServerSocket tempSocket = null;
            int portToUse = -1;
            
            // Thử các port khác nhau để tránh conflict
            for (int port : PORTS) {
                try {
                    tempSocket = new ServerSocket(port);
                    portToUse = port;
                    break;
                } catch (IOException e) {
                    System.out.println("Port " + port + " is busy, trying next...");
                }
            }
            
            if (tempSocket == null || portToUse == -1) {
                System.err.println("Could not find available port for VNPay server");
                return;
            }
            
            serverSocket = tempSocket;
            activePort = portToUse;
            isRunning = true;
            System.out.println("VNPay Return Handler started on port " + activePort);
            
            while (isRunning && !serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleRequest(clientSocket);
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error handling client request: " + e.getMessage());
                    }
                }
            }
        });
        
        serverThread.setDaemon(true);
        serverThread.start();
    }
    
    /**
     * Dừng HTTP server
     */
    public static void stopServer() {
        isRunning = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                System.out.println("VNPay Return Handler stopped");
            } catch (IOException e) {
                System.err.println("Error stopping VNPay server: " + e.getMessage());
            }
        }
        activePort = -1;
    }
    
    /**
     * Lấy port đang được sử dụng
     */
    public static int getActivePort() {
        return activePort;
    }
    
    /**
     * Xử lý HTTP request từ VNPay
     */
    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             var out = clientSocket.getOutputStream()) {
            
            String requestLine = in.readLine();
            if (requestLine == null || !requestLine.startsWith("GET")) {
                return;
            }
            
            // Parse URL và parameters
            String url = requestLine.split(" ")[1];
            if (url.startsWith("/vnpay-return")) {
                Map<String, String> params = parseQueryParams(url);
                
                // Xử lý VNPay response
                VNPayService.VNPayResult result = VNPayService.processPaymentReturn(params);
                
                // Cập nhật database
                if (result.isSignatureValid()) {
                    updateOrderStatus(result);
                }
                
                // Trả về response HTML
                String response = generateResponseHtml(result);
                sendHttpResponse(out, response);
                
                // Hiển thị thông báo trên JavaFX UI
                Platform.runLater(() -> showPaymentResult(result));
            }
            
        } catch (java.io.IOException e) {
            System.err.println("IO error processing VNPay return: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error processing VNPay return: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
    
    /**
     * Parse query parameters từ URL
     */
    private static Map<String, String> parseQueryParams(String url) {
        Map<String, String> params = new HashMap<>();
        
        if (url.contains("?")) {
            String queryString = url.substring(url.indexOf("?") + 1);
            String[] pairs = queryString.split("&");
            
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    try {
                        String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.toString());
                        String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.toString());
                        params.put(key, value);
                    } catch (java.io.UnsupportedEncodingException e) {
                        System.err.println("Error decoding parameter: " + pair);
                    }
                }
            }
        }
        
        return params;
    }
    
    /**
     * Cập nhật trạng thái đơn hàng trong database
     */
    private static void updateOrderStatus(VNPayService.VNPayResult result) {
        try {
            String txnRef = result.getOrderId();
            int orderId;
            if (txnRef.contains("_")) {
                orderId = Integer.parseInt(txnRef.split("_")[0]);
            } else {
                orderId = Integer.parseInt(txnRef);
            }
            
            String status = result.isTransactionSuccess() ? "COMPLETED" : "FAILED";
            
            boolean updated = orderDAO.updateOrderWithVNPayInfo(orderId, status, result.getTransactionNo());
            
            if (updated) {
                System.out.println("Order " + orderId + " status updated to: " + status);
                
                if (result.isTransactionSuccess()) {
                    try {
                        Order order = orderDAO.getOrderById(orderId);
                        if (order != null && order.getCustomerId() != null && order.getCustomerId() > 0) {
                            CustomerDAO customerDAO = new CustomerDAO();
                            customerDAO.updateTotalSpent(order.getCustomerId(), order.getFinalAmount());
                            
                            int pointsToAdd = (int)(order.getFinalAmount() / 1000);
                            if (pointsToAdd > 0) {
                                customerDAO.updatePoints(order.getCustomerId(), pointsToAdd);
                            }
                            
                            System.out.println("Đã cập nhật điểm và tổng chi tiêu cho khách hàng ID: " + order.getCustomerId());
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi cập nhật điểm khách hàng VNPay: " + e.getMessage());
                    }
                }
            } else {
                System.err.println("Failed to update order " + orderId);
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid order ID: " + result.getOrderId());
        } catch (SQLException e) {
            System.err.println("Database error updating order: " + e.getMessage());
        }
    }
    
    /**
     * Tạo HTML response
     */
    private static String generateResponseHtml(VNPayService.VNPayResult result) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Kết quả thanh toán VNPay</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 50px; text-align: center; }");
        html.append(".success { color: green; }");
        html.append(".error { color: red; }");
        html.append("</style>");
        html.append("</head><body>");
        
        if (result.isSignatureValid()) {
            // Extract order ID để hiển thị
            String displayOrderId = result.getOrderId();
            if (displayOrderId.contains("_")) {
                displayOrderId = displayOrderId.split("_")[0];
            }
            
            if (result.isTransactionSuccess()) {
                html.append("<h1 class='success'>✓ Thanh toán thành công!</h1>");
                html.append("<p>Mã giao dịch: ").append(result.getTransactionNo()).append("</p>");
                html.append("<p>Đơn hàng: #").append(displayOrderId).append("</p>");
                html.append("<p>Số tiền: ").append(String.format("%,.0f", result.getAmount())).append(" VND</p>");
            } else {
                html.append("<h1 class='error'>✗ Thanh toán thất bại!</h1>");
                html.append("<p>").append(result.getMessage()).append("</p>");
                html.append("<p>Đơn hàng: #").append(displayOrderId).append("</p>");
            }
        } else {
            html.append("<h1 class='error'>✗ Lỗi xác thực!</h1>");
            html.append("<p>Chữ ký không hợp lệ</p>");
        }
        
        html.append("<p><button onclick='window.close()'>Đóng cửa sổ</button></p>");
        html.append("</body></html>");
        
        return html.toString();
    }
    
    /**
     * Gửi HTTP response
     */
    private static void sendHttpResponse(java.io.OutputStream out, String content) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                         "Content-Type: text/html; charset=UTF-8\r\n" +
                         "Content-Length: " + content.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                         "Connection: close\r\n" +
                         "\r\n" +
                         content;
        
        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
    
    /**
     * Hiển thị kết quả thanh toán trên JavaFX UI
     */
    private static void showPaymentResult(VNPayService.VNPayResult result) {
        Alert alert;
        
        // Extract order ID để hiển thị
        String displayOrderId = result.getOrderId();
        if (displayOrderId.contains("_")) {
            displayOrderId = displayOrderId.split("_")[0];
        }
        
        if (result.isSignatureValid() && result.isTransactionSuccess()) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thanh toán thành công");
            alert.setHeaderText("VNPay - Thanh toán thành công!");
            alert.setContentText("Đơn hàng #" + displayOrderId + 
                                " đã được thanh toán thành công.\n" +
                                "Mã giao dịch: " + result.getTransactionNo());
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Thanh toán thất bại");
            alert.setHeaderText("VNPay - Thanh toán thất bại!");
            alert.setContentText(result.getMessage() + "\n" +
                                "Đơn hàng #" + displayOrderId);
        }
        
        alert.showAndWait();
    }
} 