package com.simulation.pedestrian.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class JavaFX extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Controller.fxml"));
        stage.setX(0);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void init() {
        new DesktopLauncher();
    }
}


