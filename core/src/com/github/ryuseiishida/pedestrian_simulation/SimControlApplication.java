package com.github.ryuseiishida.pedestrian_simulation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;

public class SimControlApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        String path ="control_panel.fxml";
        String path ="desktop/build/libs/control_panel.fxml";
        FXMLLoader loader = new FXMLLoader(Paths.get(path).toUri().toURL());
        FXMLController fxmlController = new FXMLController();
        loader.setController(fxmlController);

        Parent root = loader.load();
        stage.setX(1);
        stage.setY(1);
        stage.setScene(new Scene(root));
        stage.show();
    }

}
