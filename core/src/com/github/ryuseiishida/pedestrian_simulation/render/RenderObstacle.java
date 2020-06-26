package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.controller.GdxController;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.Obstacle;

public class RenderObstacle {
    private static Camera camera = GdxController.getCamera();
    private static ShapeRenderer shapeRenderer = GdxController.getShapeRenderer();

    private static boolean renderFlag = true;

    public RenderObstacle() {
        obstacleRegion();
    }

    public static void obstacleRegion() {
        if (renderFlag) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);
            for (Obstacle obstacle : GdxController.getEnvironment().getObstacles()) {
                shapeRenderer.line(obstacle.getStartPoint(), obstacle.getEndPoint());
            }

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.BLACK);
//        for (Obstacle obstacle : environment.getObstacles()) {
//            for (Cell obstacleCell : obstacle.getObstacleCells()) {
//                shapeRenderer.rect(obstacleCell.getLeftBottomPoint().x,
//                        obstacleCell.getLeftBottomPoint().y,
//                        obstacleCell.getCellInterval(),
//                        obstacleCell.getCellInterval());
//                shapeRenderer.rect(obstacleCell.getLeftBottomPoint().x,
//                        obstacleCell.getLeftBottomPoint().y,
//                        obstacleCell.getCellInterval(),
//                        obstacleCell.getCellInterval());
//            }
//        }
            shapeRenderer.end();
        }
    }

    public static void switchRenderFlag() {
        RenderObstacle.renderFlag = !RenderObstacle.renderFlag;
    }
}
