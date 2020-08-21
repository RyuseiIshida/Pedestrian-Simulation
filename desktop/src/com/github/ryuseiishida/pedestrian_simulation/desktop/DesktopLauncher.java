package com.github.ryuseiishida.pedestrian_simulation.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.ryuseiishida.pedestrian_simulation.analysis.AnalyzeLogLDA;
import com.github.ryuseiishida.pedestrian_simulation.controller.GdxController;
import com.github.ryuseiishida.pedestrian_simulation.controller.FXApplication;
import javafx.application.Application;

import java.awt.*;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double desktopWidth = screenSize.getWidth();
        double desktopHeight = screenSize.getHeight();
        config.title = "Pedestrian Simulation";
        config.width = (int)desktopHeight;
        config.height = (int)desktopHeight;
        config.x = 350;
        config.y = 0;
        GdxController gdxController = new GdxController();
        new LwjglApplication(gdxController, config);
        new AnalyzeLogLDA();
        Application.launch(FXApplication.class);
    }
}