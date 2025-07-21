package com.sv.qlbh.test;

import com.sv.qlbh.dao.ShiftDAO;
import com.sv.qlbh.models.Shift;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ShiftDAOTest {
    public static void main(String[] args) throws SQLException {
        ShiftDAO dao = new ShiftDAO();

        // Test thêm ca làm việc
        Shift shift = new Shift();
        shift.setUserId(1); // ID user có thật trong DB
        shift.setStartTime(LocalDateTime.now());
        shift.setEndTime(LocalDateTime.now().plusHours(8));
        shift.setStatus("ACTIVE");

        boolean added = dao.add(shift);
        System.out.println("Thêm ca làm việc: " + (added ? "Thành công" : "Thất bại"));

        // Test lấy tất cả ca làm việc
        List<Shift> shifts = dao.getAll();
        System.out.println("Danh sách ca làm việc:");
        for (Shift s : shifts) {
            System.out.println("ID: " + s.getId() + ", UserID: " + s.getUserId() + ", Status: " + s.getStatus());
        }
    }
}