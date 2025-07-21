/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sv.qlbh.dao;


import com.sv.qlbh.models.Shift;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nghip
 */
public class ShiftDAO {

      public boolean add(Shift shift) throws SQLException
      {
        String sql= "INSERT INTO shifts (user_id, start_time, end_time, status) VALUES (?, ?, ?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt= conn.prepareStatement(sql))
            {
              stmt.setInt(1, shift.getUserId());
              stmt.setTimestamp(2, Timestamp.valueOf(shift.getStartTime()));
              stmt.setTimestamp(3, Timestamp.valueOf(shift.getEndTime()));
              stmt.setString(4, shift.getStatus());
              int rowsAffected = stmt.executeUpdate();
              return rowsAffected > 0;
            }
        catch (SQLException e)
        {
          System.err.println("Lỗi khi thêm ca làm việc: " + e.getMessage());
          return false;
        }
      }

      public boolean update(Shift shift) throws SQLException
      {
        String sql= "UPDATE shifts SET user_id=?, start_time=?, end_time=?, status=?  WHERE id= ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt= conn.prepareStatement(sql))
        {
            stmt.setInt(1, shift.getId());
            stmt.setTimestamp(2, Timestamp.valueOf(shift.getStartTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(shift.getEndTime()));
            stmt.setString(4, shift.getStatus());
            int rowAffected= stmt.executeUpdate();
            return rowAffected>0;
        }
        catch (SQLException e)
        {
            System.err.println("Lỗi khi Update ca làm việc: " + e.getMessage());
            return false;
        }
      }
      public boolean delete(int id) throws SQLException
      {
          String sql="DELETE FROM shifts WHERE id=?";
          try (Connection conn=DatabaseConnection.getConnection();
              PreparedStatement stmt= conn.prepareStatement(sql))
                  {
              stmt.setInt(1, id);
              int rowAffected= stmt.executeUpdate();
              return rowAffected>0;
          } 
          catch (SQLException e) {
              System.err.println("Lỗi khi xóa ca làm việc" + e.getMessage());
              return false;
          }
      }
      public Shift getById(int id) throws SQLException
      {
          String sql="SELECT * FROM shifts WHERE id=?";
          try (Connection conn=DatabaseConnection.getConnection();
                  PreparedStatement stmt= conn.prepareStatement(sql))
                  {
                      stmt.setInt(1,id);
                      try (ResultSet rs= stmt.executeQuery())
                      {
                          if(rs.next())
                              return mapResultSetToShift(rs);
                        }
                  }
          catch (SQLException e) 
          {
              System.err.println("Lỗi khi thêm ca làm việc: " + e.getMessage());
          }
          return null;
      }
      
      public List<Shift> getAll() 
      {
        List<Shift> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shifts ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                shifts.add(mapResultSetToShift(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách ca làm việc: " + e.getMessage());
        }
        return shifts;
    }

private Shift mapResultSetToShift(ResultSet rs) throws SQLException 
    {
      Shift shift = new Shift();
      shift.setId(rs.getInt("id"));
      shift.setUserId(rs.getInt("user_id"));
      shift.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
      Timestamp endTime = rs.getTimestamp("end_time");
      if (endTime != null) {
          shift.setEndTime(endTime.toLocalDateTime());
      }
      shift.setStatus(rs.getString("status"));
      shift.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
      return shift;
    }
}
