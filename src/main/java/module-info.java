module com.sv.qlbh {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    
    exports com.sv.qlbh;
    exports com.sv.qlbh.controller;
    exports com.sv.qlbh.models;
    exports com.sv.qlbh.dao;
    exports com.sv.qlbh.utils;
    
    opens com.sv.qlbh.controller to javafx.fxml;
}
