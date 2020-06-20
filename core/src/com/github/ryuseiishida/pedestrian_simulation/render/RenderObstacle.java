package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.Obstacle;

public class RenderObstacle {
    public static Color color = Color.BLACK;

    public RenderObstacle(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        obstacleRegion(shapeRenderer, camera, environment);
    }

    public static void obstacleRegion(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(RenderObstacle.color);
        for (Obstacle obstacle : environment.getObstacles()) {
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
