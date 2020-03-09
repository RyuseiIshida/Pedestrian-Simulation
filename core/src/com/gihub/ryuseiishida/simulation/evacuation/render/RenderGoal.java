package com.gihub.ryuseiishida.simulation.evacuation.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gihub.ryuseiishida.simulation.evacuation.environment.Environment;
import com.gihub.ryuseiishida.simulation.evacuation.goal.Goal;

public class RenderGoal {
    public RenderGoal(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        renderGoalRegion(shapeRenderer, camera, environment);
    }

    public static void renderGoalRegion(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        for (Goal goal : environment.getGoals()) {
            shapeRenderer.rect(goal.getPositionX(), goal.getPositionY(), goal.getWidth(), goal.getHeight());
        }
        shapeRenderer.end();
    }

}
