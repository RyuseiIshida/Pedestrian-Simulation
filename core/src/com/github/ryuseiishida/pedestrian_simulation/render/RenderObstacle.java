package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.GDXController;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.Obstacle;

public class RenderObstacle {
    private static Camera camera = GDXController.getCamera();
    private static ShapeRenderer shapeRenderer = GDXController.getShapeRenderer();

    public RenderObstacle() {
        obstacleRegion();
    }

    public static void obstacleRegion() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (Obstacle obstacle : GDXController.getEnvironment().getObstacles()) {
            shapeRenderer.line(obstacle.getStartPoint(),obstacle.getEndPoint());
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
