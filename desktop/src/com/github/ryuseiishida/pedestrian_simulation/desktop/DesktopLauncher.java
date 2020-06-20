package com.github.ryuseiishida.pedestrian_simulation.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.ryuseiishida.pedestrian_simulation.GDXController;
import com.github.ryuseiishida.pedestrian_simulation.SimControlApplication;
import javafx.application.Application;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Pedestrian Simulation";
        config.width = 900;
        config.height = 600;
        config.x = 350;
        config.y = 0;
        GDXController gdxController = new GDXController();
        new LwjglApplication(gdxController, config);
        Application.launch(SimControlApplication.class);
    }
}