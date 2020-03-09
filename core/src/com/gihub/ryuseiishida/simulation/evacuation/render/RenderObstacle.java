package com.gihub.ryuseiishida.simulation.evacuation.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gihub.ryuseiishida.simulation.evacuation.cell.Cell;
import com.gihub.ryuseiishida.simulation.evacuation.environment.Environment;
import com.gihub.ryuseiishida.simulation.evacuation.obstacle.Obstacle;

public class RenderObstacle {
    public RenderObstacle(ShapeRenderer shapeRenderer, Camera camera, Environment environment){
        renderObstacleRegion(shapeRenderer, camera, environment);
    }

    public static void renderObstacleRegion(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        for (Obstacle obstacle : environment.getObstacles()) {
            for (Cell obstacleCell : obstacle.getObstacleCells()) {
                shapeRenderer.rect(obstacleCell.getLeftBottomPoint().x,
                        obstacleCell.getLeftBottomPoint().y,
                        obstacleCell.getCellInterval(),
                        obstacleCell.getCellInterval());
                shapeRenderer.rect(obstacleCell.getLeftBottomPoint().x,
                        obstacleCell.getLeftBottomPoint().y,
                        obstacleCell.getCellInterval(),
                        obstacleCell.getCellInterval());
            }
        }
        shapeRenderer.end();
    }
}
