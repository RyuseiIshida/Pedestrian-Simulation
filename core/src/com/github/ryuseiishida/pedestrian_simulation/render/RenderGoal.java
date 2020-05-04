package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.goal.Goal;

public class RenderGoal {
    public RenderGoal(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        goalRegion(shapeRenderer, camera, environment);
    }

    public static void goalRegion(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        for (Goal goal : environment.getGoals()) {
            shapeRenderer.rect(goal.getPositionX(), goal.getPositionY(), goal.getWidth(), goal.getHeight());
        }
        shapeRenderer.end();
    }

}
