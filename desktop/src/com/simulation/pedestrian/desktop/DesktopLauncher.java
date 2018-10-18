package com.simulation.pedestrian.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.simulation.pedestrian.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Pedestrian Simulation";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new Main(), config);
	}
}
