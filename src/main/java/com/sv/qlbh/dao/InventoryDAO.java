package com.sv.qlbh.dao;

import com.sv.qlbh.models.Inventory;
import java.sql.SQLException;
import java.util.List;

public interface InventoryDAO {
    boolean addInventoryEntry(Inventory entry) throws SQLException;
    List<Inventory> getAllInventoryEntries() throws SQLException;
    List<Inventory> getInventoryEntriesByProductId(int productId) throws SQLException;

    // Khai báo phương thức giao dịch mới
    boolean addInventoryEntryAndUpdateProductStock(Inventory inventoryEntry, int productId, int newStock) throws SQLException;
} 