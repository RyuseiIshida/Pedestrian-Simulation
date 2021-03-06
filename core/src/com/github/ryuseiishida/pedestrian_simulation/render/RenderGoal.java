package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.controller.GdxController;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.Goal;

public class RenderGoal {
    private static Camera camera = GdxController.getCamera();
    private static ShapeRenderer shapeRenderer = GdxController.getShapeRenderer();

    private static boolean renderFlag = true;

    public RenderGoal() {
        if (renderFlag) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            for (Goal goal : Environment.getInstance().getGoals()) {
                shapeRenderer.rect(goal.getPositionX(), goal.getPositionY(), goal.getWidth(), goal.getHeight());
            }
            shapeRenderer.end();
        }
    }

    public RenderGoal(Goal goal) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(goal.getPositionX(), goal.getPositionY(), goal.getWidth(), goal.getHeight());
        shapeRenderer.end();
    }

    public static void switchRenderFlag() {
        RenderGoal.renderFlag = !RenderGoal.renderFlag;
    }
}
