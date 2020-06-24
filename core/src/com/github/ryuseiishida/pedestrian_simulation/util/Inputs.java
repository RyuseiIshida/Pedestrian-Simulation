package com.github.ryuseiishida.pedestrian_simulation.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.environment.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.goal.Goal;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.CreateLineFromMouse;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.Line;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.Obstacle;
import com.github.ryuseiishida.pedestrian_simulation.render.*;

import java.util.ArrayList;

public class Inputs {
    private Camera camera;
    private ShapeRenderer shapeRenderer;
    private Environment environment;

    // javafx flag
    public static String fxCreateProperty = "";

    public Inputs(Camera camera, ShapeRenderer shapeRenderer, Environment environment) {
        this.camera = camera;
        this.shapeRenderer = shapeRenderer;
        this.environment = environment;
        keyInput();
        mouseInput();
    }

    private void keyInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            environment.switchUpdateFlag();
        }
    }

    public static void setFxCreateProperty(String propertyName) {
        fxCreateProperty = propertyName;
    }

    //TODO refactor
    private void mouseInput() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        shapeRenderer.setProjectionMatrix(camera.combined);

        if (fxCreateProperty.contains("eraser")) {
            new RenderEraser(touchPos.x, touchPos.y);
            if (Gdx.input.isTouched()) {
                Vector2 eraserStartPoint = new Vector2(touchPos.x - RenderEraser.width / 2, touchPos.y - RenderEraser.height / 2);
                Vector2 eraserEndPoint = new Vector2(eraserStartPoint.x + RenderEraser.width, eraserStartPoint.y + RenderEraser.height);
                // delete objects
                // obstacle
                Obstacle deleteObstacle;
                for (Obstacle obstacle : environment.getObstacles()) {
                    if (UtilVector.judgeIntersected(eraserStartPoint, eraserEndPoint, obstacle.getStartPoint(), obstacle.getEndPoint())) {
                        environment.getObstacles().remove(obstacle);
                        break;
                    }
                }
                // goal
                // TODO 更新
                Goal deleteGoal;
                for (Goal goal : environment.getGoals()) {
                    if (eraserStartPoint.x <= goal.getPositionX() && eraserStartPoint.y <= goal.getPositionY()
                            && eraserEndPoint.x >= goal.getPositionX() && eraserEndPoint.y <= goal.getPositionY()) {
                        environment.getGoals().remove(goal);
                        break;
                    }
                    if (eraserStartPoint.x <= goal.getRightTop().x && eraserStartPoint.y <= goal.getRightTop().y
                            && eraserEndPoint.x >= goal.getRightTop().x && eraserEndPoint.y <= goal.getRightTop().y) {
                        environment.getGoals().remove(goal);
                        break;
                    }
                }
                // agent
                Agent deleteAgent;
                for (Agent agent : environment.getAgentList()) {
                    if (eraserStartPoint.x <= agent.getPosition().x && eraserStartPoint.y <= agent.getPosition().y
                            && eraserEndPoint.x >= agent.getPosition().x && eraserEndPoint.y >= agent.getPosition().y) {
                        environment.getAgentList().remove(agent);
                        break;
                    }
                }
            }
            shapeRenderer.end();
        }

        if (fxCreateProperty.contains("agentN")) {
            String[] element = fxCreateProperty.split("-");
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(RenderAgent.nonGoalAgentColor);
            shapeRenderer.circle(touchPos.x, touchPos.y, Parameter.AGENT_RADIUS);
            if (Gdx.input.justTouched()) {
                if (element.length == 2) {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), Float.parseFloat(element[1]));
                } else {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y));
                }
            }
            shapeRenderer.end();
        }

        if (fxCreateProperty.contains("agentG")) {
            String[] element = fxCreateProperty.split("-");
            String goalID = fxCreateProperty.split("-")[1];
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(RenderAgent.goalAgentColor);
            shapeRenderer.circle(touchPos.x, touchPos.y, Parameter.AGENT_RADIUS);
            if (Gdx.input.justTouched()) {
                if (element.length == 3) {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), goalID, Float.parseFloat(element[2]));
                } else {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), goalID);
                }
            }
            shapeRenderer.end();
        }

        if (fxCreateProperty.contains("Goal")) {
            String[] goalProperty = fxCreateProperty.split("-");
            touchPos.x -= Float.parseFloat(goalProperty[2]) / 2;
            touchPos.y -= Float.parseFloat(goalProperty[3]) / 2;
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(touchPos.x, touchPos.y, Float.parseFloat(goalProperty[2]), Float.parseFloat(goalProperty[3]));
            if (Gdx.input.justTouched()) {
                environment.addGoal(new Goal(goalProperty[1], touchPos.x, touchPos.y, Float.parseFloat(goalProperty[2]), Float.parseFloat(goalProperty[3])));
            }
            shapeRenderer.end();
        }

        if (fxCreateProperty.equals("ObstacleLine")) {
            if (Gdx.input.justTouched()) {
                CreateLineFromMouse.setPoint(touchPos);
            }
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);
            if (CreateLineFromMouse.getStartPoint() != null) {
                shapeRenderer.line(CreateLineFromMouse.getStartPoint().x, CreateLineFromMouse.getStartPoint().y, touchPos.x, touchPos.y);
            }
            if (CreateLineFromMouse.isCompleteLine()) {
                environment.addObstacle(CreateLineFromMouse.getCompleteLine(Parameter.ENV_CELLS_MAP));
            }
            shapeRenderer.end();
        }
    }
}
