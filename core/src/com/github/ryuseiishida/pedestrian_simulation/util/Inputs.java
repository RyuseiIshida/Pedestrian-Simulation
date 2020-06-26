package com.github.ryuseiishida.pedestrian_simulation.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.Goal;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.CreateLineFromMouse;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.Obstacle;
import com.github.ryuseiishida.pedestrian_simulation.render.*;

import java.util.ArrayList;

public class Inputs {
    private Camera camera;
    private ShapeRenderer shapeRenderer;
    private Environment environment;

    // javafx flag
    private static String fxCreateProperty = "";

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

    private void mouseInput() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        shapeRenderer.setProjectionMatrix(camera.combined);
        if (fxCreateProperty.contains("eraser")) eraser(touchPos);
        else if (fxCreateProperty.contains("agentN")) createNonGoalAgent(touchPos);
        else if (fxCreateProperty.contains("agentG")) createGoalAgent(touchPos);
        else if (fxCreateProperty.contains("RandomNonGoalSpawn")) createRandomNonGoalAgent(touchPos);
        else if (fxCreateProperty.contains("RandomGoalSpawn")) createRandomGoalAgent(touchPos);
        else if (fxCreateProperty.contains("Goal")) createGoal(touchPos);
        else if (fxCreateProperty.equals("ObstacleLine")) createObstacle(touchPos);

    }

    private void eraser(Vector3 touchPos) {
        new RenderEraser(touchPos.x, touchPos.y);
        if (Gdx.input.isTouched()) {
            Vector2 eraserStartPoint = new Vector2(touchPos.x - RenderEraser.width / 2, touchPos.y - RenderEraser.height / 2);
            Vector2 eraserEndPoint = new Vector2(eraserStartPoint.x + RenderEraser.width, eraserStartPoint.y + RenderEraser.height);

            // obstacle
            for (Obstacle obstacle : environment.getObstacles()) {
                if (UtilVector.judgeIntersected(eraserStartPoint, eraserEndPoint, obstacle.getStartPoint(), obstacle.getEndPoint())) {
                    environment.getObstacles().remove(obstacle);
                    break;
                }
            }
            environment.resetObstaclePosition();

            // goal
            ArrayList<Goal> removeGoalList = new ArrayList<>();
            for (Goal goal : environment.getGoals()) {
                if (touchPos.x > goal.getPositionX() && touchPos.y > goal.getPositionY()
                        && touchPos.x < goal.getRightTop().x && touchPos.y < goal.getRightTop().y) {
                    removeGoalList.add(goal);
                }
            }
            environment.removeGoalAll(removeGoalList);

            // agent
            ArrayList<Agent> removeAgentList = new ArrayList<>();
            for (Agent agent : environment.getAgentList()) {
                if (UtilVector.judgeInside(eraserStartPoint, eraserEndPoint, agent.getPosition())) {
                    removeAgentList.add(agent);
                }
            }
            environment.getAgentList().removeAll(removeAgentList);
        }
    }

    private void createNonGoalAgent(Vector3 touchPos) {
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

    private void createGoalAgent(Vector3 touchPos) {
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

    private void createRandomNonGoalAgent(Vector3 touchPos) {
        float random_range = Float.parseFloat(fxCreateProperty.split("-")[1]);
        int num = Integer.parseInt(fxCreateProperty.split("-")[2]);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(touchPos.x - random_range / 2, touchPos.y - random_range / 2, random_range, random_range);
        if (Gdx.input.justTouched()) {
            environment.spawnRandomNonGoalAgents(touchPos.x - random_range / 2, touchPos.y - random_range / 2,
                    touchPos.x + random_range / 2, touchPos.y + random_range / 2, num);
        }
        shapeRenderer.end();
    }

    private void createRandomGoalAgent(Vector3 touchPos) {
        float random_range = Float.parseFloat(fxCreateProperty.split("-")[1]);
        int num = Integer.parseInt(fxCreateProperty.split("-")[2]);
        String id = fxCreateProperty.split("-")[3];
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(touchPos.x - random_range / 2, touchPos.y - random_range / 2, random_range, random_range);
        if (Gdx.input.justTouched()) {
            environment.spawnRandomGoalAgents(touchPos.x - random_range / 2, touchPos.y - random_range / 2,
                    touchPos.x + random_range / 2, touchPos.y + random_range / 2, num, id);
        }
        shapeRenderer.end();
    }

    private void createObstacle(Vector3 touchPos) {
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

    private void createGoal(Vector3 touchPos) {
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
}
