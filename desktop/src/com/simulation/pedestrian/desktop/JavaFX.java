package com.simulation.pedestrian.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;

public class JavaFX extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String path = "desktop/src/com/simulation/pedestrian/desktop/Controller.fxml";
        Parent root = FXMLLoader.load(Paths.get(path).toUri().toURL());
        stage.setX(0);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void init() {
        new DesktopLauncher();
    }
}


