package com.github.ryuseiishida.pedestrian_simulation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class SimControlApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        String path ="control_panel.fxml";
//        String path ="control_panel.fxml";
//        FXMLLoader loader = new FXMLLoader(Paths.get(path).toUri().toURL());
//        URL ul = getClass().getResource("￿/help.txt");
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
