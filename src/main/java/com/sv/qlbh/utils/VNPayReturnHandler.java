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
import java.util.HashMap;
import java.util.Map;
import com.sv.qlbh.dao.CustomerDAO;
import java.sql.SQLException;

public class VNPayReturnHandler {
    private static final int PORT = 8094;
    private static ServerSocket serverSocket;
    private static boolean isRunning = false;
    private static final OrderDAO orderDAO = new OrderDAO();

    public static void startServer() {
        if (isRunning) return;
        Thread serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                isRunning = true;
                System.out.println("VNPay Return Handler started on port " + PORT);
                while (isRunning && !serverSocket.isClosed()) {
                    try (Socket clientSocket = serverSocket.accept()) {
                        handleRequest(clientSocket);
                    } catch (IOException e) {
                        if (isRunning) System.err.println("Error handling client: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Could not start VNPay server on port " + PORT);
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

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
    }

    public static int getActivePort() {
        return PORT;
    }

    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             var out = clientSocket.getOutputStream()) {
            String requestLine = in.readLine();
            if (requestLine == null || !requestLine.startsWith("GET")) return;
            String url = requestLine.split(" ")[1];
            if (url.startsWith("/vnpay-return")) {
                Map<String, String> params = parseQueryParams(url);
                VNPayService.VNPayResult result = VNPayService.processPaymentReturn(params);
                if (result.isSignatureValid()) updateOrderStatus(result);
                sendHttpResponse(out, generateResponseHtml(result));
                Platform.runLater(() -> showPaymentResult(result));
            }
        } catch (IOException e) {
            System.err.println("IO error processing VNPay return: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error processing VNPay return: " + e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }

    private static Map<String, String> parseQueryParams(String url) {
        Map<String, String> params = new HashMap<>();
        if (url.contains("?")) {
            String[] pairs = url.substring(url.indexOf("?") + 1).split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    try {
                        params.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                                   URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
                    } catch (Exception ignored) {}
                }
            }
        }
        return params;
    }

    private static void updateOrderStatus(VNPayService.VNPayResult result) {
        try {
            String txnRef = result.getOrderId();
            int orderId = txnRef.contains("_") ? Integer.parseInt(txnRef.split("_")[0]) : Integer.parseInt(txnRef);
            String status = result.isTransactionSuccess() ? "COMPLETED" : "FAILED";
            boolean updated = orderDAO.updateOrderWithVNPayInfo(orderId, status, result.getTransactionNo());
            if (updated && result.isTransactionSuccess()) {
                try {
                    Order order = orderDAO.getOrderById(orderId);
                    if (order != null && order.getCustomerId() != null && order.getCustomerId() > 0) {
                        CustomerDAO customerDAO = new CustomerDAO();
                        customerDAO.updateTotalSpent(order.getCustomerId(), order.getFinalAmount());
                        int pointsToAdd = (int)(order.getFinalAmount() / 1000);
                        if (pointsToAdd > 0) customerDAO.updatePoints(order.getCustomerId(), pointsToAdd);
                    }
                } catch (SQLException e) {
                    System.err.println("Lỗi SQL khi cập nhật điểm khách hàng VNPay: " + e.getMessage());
                } catch (RuntimeException e) {
                    System.err.println("Lỗi runtime khi cập nhật điểm khách hàng VNPay: " + e.getMessage());
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Lỗi chuyển đổi orderId: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật đơn hàng: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Lỗi runtime khi cập nhật đơn hàng: " + e.getMessage());
        }
    }

    private static String generateResponseHtml(VNPayService.VNPayResult result) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Kết quả thanh toán VNPay</title><style>body{font-family:Arial,sans-serif;margin:50px;text-align:center}.success{color:green}.error{color:red}</style></head><body>");
        String displayOrderId = result.getOrderId();
        if (displayOrderId != null && displayOrderId.contains("_")) displayOrderId = displayOrderId.split("_")[0];
        if (result.isSignatureValid()) {
            if (result.isTransactionSuccess()) {
                html.append("<h1 class='success'>✓ Thanh toán thành công!</h1><p>Mã giao dịch: ")
                    .append(result.getTransactionNo()).append("</p><p>Đơn hàng: #")
                    .append(displayOrderId).append("</p><p>Số tiền: ")
                    .append(String.format("%,.0f", result.getAmount())).append(" VND</p>");
            } else {
                html.append("<h1 class='error'>✗ Thanh toán thất bại!</h1><p>")
                    .append(result.getMessage()).append("</p><p>Đơn hàng: #")
                    .append(displayOrderId).append("</p>");
            }
        } else {
            html.append("<h1 class='error'>✗ Lỗi xác thực!</h1><p>Chữ ký không hợp lệ</p>");
        }
        html.append("<p><button onclick='window.close()'>Đóng cửa sổ</button></p></body></html>");
        return html.toString();
    }

    private static void sendHttpResponse(java.io.OutputStream out, String content) throws IOException {
        String response = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\nContent-Length: "
                + content.getBytes(StandardCharsets.UTF_8).length + "\r\nConnection: close\r\n\r\n" + content;
        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    private static void showPaymentResult(VNPayService.VNPayResult result) {
        Alert alert;
        String displayOrderId = result.getOrderId();
        if (displayOrderId != null && displayOrderId.contains("_")) displayOrderId = displayOrderId.split("_")[0];
        if (result.isSignatureValid() && result.isTransactionSuccess()) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thanh toán thành công");
            alert.setHeaderText("VNPay - Thanh toán thành công!");
            alert.setContentText("Đơn hàng #" + displayOrderId + " đã được thanh toán thành công.\nMã giao dịch: " + result.getTransactionNo());
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Thanh toán thất bại");
            alert.setHeaderText("VNPay - Thanh toán thất bại!");
            alert.setContentText(result.getMessage() + "\nĐơn hàng #" + displayOrderId);
        }
        alert.showAndWait();
    }
} 