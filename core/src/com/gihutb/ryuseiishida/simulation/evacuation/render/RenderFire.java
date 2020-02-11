package com.gihutb.ryuseiishida.simulation.evacuation.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gihutb.ryuseiishida.simulation.evacuation.environment.Environment;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.Fire;

public class RenderFire {
    public RenderFire(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        fire(shapeRenderer, camera, environment);
    }

    public static void fire(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.FIREBRICK);
        shapeRenderer.setColor(Color.RED);
        Fire fire = environment.getFire();
        shapeRenderer.circle(fire.getFirePoint().x, fire.getFirePoint().y, fire.getSpreadFireRange());
        shapeRenderer.end();
    }
}
