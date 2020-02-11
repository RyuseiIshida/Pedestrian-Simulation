package com.gihutb.ryuseiishida.simulation.evacuation.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.Cell;
import com.gihutb.ryuseiishida.simulation.evacuation.environment.Environment;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.Obstacle;

public class RenderCell {
    public RenderCell(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
     renderObstacle(shapeRenderer, camera, environment);
    }
    public static void renderObstacle(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.FIREBRICK);
        shapeRenderer.setColor(Color.BLUE);
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
