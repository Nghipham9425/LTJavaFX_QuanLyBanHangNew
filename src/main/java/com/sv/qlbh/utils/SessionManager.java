/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sv.qlbh.utils;
import com.sv.qlbh.models.User;

/**
 *
 * @author nghip
 */
public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
      currentUser = user;
  }
  
  public static User getCurrentUser() {
      return currentUser;
  }
  
  public static void logout() {
      currentUser = null;
  }
}
