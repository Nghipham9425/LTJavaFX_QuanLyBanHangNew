module com.sv.qlbh.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    opens com.sv.qlbh.javafx to javafx.fxml;
    exports com.sv.qlbh.javafx;
}
