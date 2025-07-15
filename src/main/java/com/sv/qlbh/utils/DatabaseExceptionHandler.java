package com.sv.qlbh.utils;

import java.sql.SQLException;

/**
 * Utility class for handling common database exceptions
 * Provides user-friendly error messages for common SQL error codes
 */
public class DatabaseExceptionHandler {
    
    // Common MySQL error codes
    public static final int DUPLICATE_ENTRY = 1062;
    public static final int FOREIGN_KEY_CONSTRAINT = 1451;
    public static final int FOREIGN_KEY_CONSTRAINT_FAILS = 1452;
    public static final int ACCESS_DENIED = 1045;
    public static final int CONNECTION_REFUSED = 2003;
    
    public static void handleSQLException(SQLException e, String operation) {
        System.err.println("SQLException during " + operation + ": " + e.getMessage());
        System.err.println("Error Code: " + e.getErrorCode());
        
        switch (e.getErrorCode()) {
            case DUPLICATE_ENTRY:
                AlertUtils.showWarning("Thông tin trùng lặp", 
                    "Thông tin đã tồn tại trong hệ thống. Vui lòng kiểm tra lại.");
                break;
                
            case FOREIGN_KEY_CONSTRAINT:
                AlertUtils.showWarning("Không thể xóa", 
                    "Dữ liệu đang được sử dụng trong hệ thống, không thể xóa.\n" +
                    "Bạn có thể đặt trạng thái 'Không hoạt động' thay vì xóa.");
                break;
                
            case FOREIGN_KEY_CONSTRAINT_FAILS:
                AlertUtils.showError("Lỗi ràng buộc", 
                    "Dữ liệu tham chiếu không hợp lệ hoặc không tồn tại.");
                break;
                
            case ACCESS_DENIED:
                AlertUtils.showError("Lỗi xác thực", 
                    "Không thể kết nối database - lỗi xác thực.");
                break;
                
            case CONNECTION_REFUSED:
                AlertUtils.showError("Lỗi kết nối", 
                    "Không thể kết nối đến database server.");
                break;
                
            default:
                AlertUtils.showDatabaseError("Lỗi " + operation + ": " + e.getMessage());
                break;
        }
    }
    
    public static void handleRuntimeException(RuntimeException e, String operation) {
        System.err.println("RuntimeException during " + operation + ": " + e.getMessage());
        AlertUtils.showError("Lỗi hệ thống", 
            "Không thể thực hiện " + operation + ". Vui lòng thử lại sau.");
    }
} 