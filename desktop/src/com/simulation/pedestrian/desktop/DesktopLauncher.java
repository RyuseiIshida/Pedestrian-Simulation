package com.simulation.pedestrian.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.simulation.pedestrian.ControllerGDX;

public class DesktopLauncher {
	public DesktopLauncher() {
		startGDX();
	}

	public void startGDX () {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Pedestrian Simulation";
		config.width = 1090;
		config.height = 900;
		config.x = 500;
		config.y = 0;
		new LwjglApplication(new ControllerGDX(), config);
	}

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Pedestrian Simulation";
		config.width = 1000;
        config.height = 1000;
        config.x = -1;
        config.y = -1;
		new LwjglApplication(new ControllerGDX(), config);
	}
}
