package com.github.ryuseiishida.pedestrian_simulation.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class SimControlApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/control_panel.fxml"));
        FXMLController fxmlController = new FXMLController();
        loader.setController(fxmlController);

        Parent root = loader.load();
        stage.setX(1);
        stage.setY(1);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double desktopWidth = screenSize.getWidth();
        double desktopHeight = screenSize.getHeight();
        stage.setScene(new Scene(root,350,desktopHeight));
        stage.show();
    }

}
