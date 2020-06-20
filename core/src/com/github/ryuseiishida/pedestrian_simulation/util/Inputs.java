package com.github.ryuseiishida.pedestrian_simulation.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.goal.Goal;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.CreateLineFromMouse;
import com.github.ryuseiishida.pedestrian_simulation.render.*;

public class Inputs {
    private static Camera camera;
    private static ShapeRenderer shapeRenderer;
    public static Environment environment;

    // javafx flag
    private static String fxCreateProperty = "";

    public Inputs(Camera camera, ShapeRenderer shapeRenderer, Environment environment) {
        this.camera = camera;
        this.shapeRenderer = shapeRenderer;
        this.environment = environment;
        systemController();
        setRenderFlag();
        createObject();
    }

    public static void systemController() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            environment.switchUpdateFlag();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
//            ldaController();
        }
    }

    public static void setFxCreateProperty(String propertyName) {
        fxCreateProperty = propertyName;
    }

    public static void createObject() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        shapeRenderer.setProjectionMatrix(camera.combined);

        if (fxCreateProperty.equals("agentN")) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(RenderAgent.nonGoalAgentColor);
            shapeRenderer.circle(touchPos.x, touchPos.y, Parameter.AGENT_RADIUS);
            if (Gdx.input.justTouched()) {
                environment.spawnAgent(new Vector2(touchPos.x, touchPos.y));
            }
            shapeRenderer.end();
        }


        if (fxCreateProperty.contains("agentG")) {
            String goalID = fxCreateProperty.split("-")[1];
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(RenderAgent.goalAgentColor);
            shapeRenderer.circle(touchPos.x, touchPos.y, Parameter.AGENT_RADIUS);
            if (Gdx.input.justTouched()) {
                environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), goalID);
            }
            shapeRenderer.end();
        }


        if (fxCreateProperty.contains("Goal")) {
            String[] goalProperty = fxCreateProperty.split("-");
            touchPos.x -= Float.parseFloat(goalProperty[2]) / 2;
            touchPos.y -= Float.parseFloat(goalProperty[3]) / 2;
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(RenderGoal.color);
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
            shapeRenderer.setColor(RenderObstacle.color);
            if (CreateLineFromMouse.getStartPoint() != null) {
                shapeRenderer.line(CreateLineFromMouse.getStartPoint().x, CreateLineFromMouse.getStartPoint().y, touchPos.x, touchPos.y);
            }
            if (CreateLineFromMouse.isCompleteLine()) {
                environment.addObstacle(CreateLineFromMouse.getCompleteLine(Parameter.ENV_CELLS_MAP));
            }
            shapeRenderer.end();
        }
    }

    private void setRenderFlag() {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            // LDA
            if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                RenderLDA.switchRenderFlag();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                RenderLDA.setNumTopics(RenderLDA.getNumTopics() + 1);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                RenderLDA.setNumTopics(RenderLDA.getNumTopics() - 1);
            }
        }
    }
}
