package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.obstacle.Fire;

public class RenderFire {
    private static boolean renderFireFlag = true;

    public RenderFire(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        isRegion(shapeRenderer, camera, environment);
    }

    public static void isRegion(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        if (renderFireFlag) {
            renderRegion(shapeRenderer, camera, environment);
        }
    }

    public static void renderRegion(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.FIREBRICK);
        shapeRenderer.setColor(Color.RED);
        Fire fire = environment.getFire();
        shapeRenderer.circle(fire.getFirePoint().x, fire.getFirePoint().y, fire.getSpreadFireRange());
        shapeRenderer.end();
    }

    public static void setRenderFlag() {
        renderFireFlag = !renderFireFlag;
    }
}
