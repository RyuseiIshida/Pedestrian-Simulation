package com.gihutb.ryuseiishida.simulation.evacuation.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gihutb.ryuseiishida.simulation.evacuation.environment.Environment;
import com.gihutb.ryuseiishida.simulation.evacuation.goal.Goal;

public class RenderGoal {
    public RenderGoal(ShapeRenderer shapeRenderer, Camera camera, Environment environment){
        goal(shapeRenderer, camera, environment);
    }

    public static void goal(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Goal goal : environment.getGoals()) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(goal.getPositionX(), goal.getPositionY(), goal.getWidth(), goal.getHeight());
        }
        shapeRenderer.end();
    }

}
