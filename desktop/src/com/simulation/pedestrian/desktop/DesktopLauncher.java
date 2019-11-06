package com.simulation.pedestrian.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.simulation.pedestrian.Parameter;
import com.simulation.pedestrian.mode.CreateMap;
import com.simulation.pedestrian.mode.DefaultSimulation;

public class DesktopLauncher {
    public void startGDX() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Pedestrian Simulation";
        config.width = 1000;
        config.height = 1000;
        config.x = -1;
        config.y = -1;
        switch (Parameter.MODE) {
            case "DefaultSimulation":
                new LwjglApplication(new DefaultSimulation(), config);
                break;
            case "CreateMap":
                new LwjglApplication(new CreateMap(), config);
                break;
        }
    }

    public static void main(String[] arg) {
        DesktopLauncher desktopLauncher = new DesktopLauncher();
        desktopLauncher.startGDX();
    }
}
