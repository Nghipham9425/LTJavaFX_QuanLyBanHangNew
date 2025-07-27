package com.sv.qlbh;

import com.sv.qlbh.utils.VNPayReturnHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        VNPayReturnHandler.startServer();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        com.sv.qlbh.controller.LoginController loginController = loader.getController();
        if (loginController != null) loginController.setHostServices(getHostServices());
        Scene scene = new Scene(root);
        primaryStage.setTitle("Đăng nhập - Quản Lý Bán Hàng");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setWidth(700);
        primaryStage.setHeight(400);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(400);
        primaryStage.setMaxWidth(700);
        primaryStage.setMaxHeight(400);
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(event -> VNPayReturnHandler.stopServer());
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        VNPayReturnHandler.stopServer();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}