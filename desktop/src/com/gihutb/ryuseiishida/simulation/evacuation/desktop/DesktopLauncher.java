package com.gihutb.ryuseiishida.simulation.evacuation.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gihutb.ryuseiishida.simulation.evacuation.Parameter;
import com.gihutb.ryuseiishida.simulation.evacuation.mode.CreateMap;
import com.gihutb.ryuseiishida.simulation.evacuation.mode.DefaultSimulation;

public class DesktopLauncher {
    public void startGDX() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Pedestrian Simulation";
        //config.width = 1000;//600
        config.width = 900;
        //config.height = 1000;//900
        config.height = 600;
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
