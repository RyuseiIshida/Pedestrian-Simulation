package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.environment.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.environment.agent.StateTag;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

public class RenderAgent {
    public static Color nonGoalAgentColor = Color.BLACK;
    public static Color goalAgentColor = Color.RED;
    private static boolean renderViewFlag = false;
    private static boolean renderMoveGoalLineFlag = false;
    private static boolean renderFollowLineFlag = false;

    public RenderAgent(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        body(shapeRenderer, camera, environment);
        isView(shapeRenderer, camera, environment);
        isMoveGoalLine(shapeRenderer, camera, environment);
    }

    public static void body(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Agent agent : environment.getAgentList()) {
            if (agent.getPosition() == null) {
                shapeRenderer.end();
                return;
            }
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.AGENT_RADIUS);
            switch (agent.getStateTag()) {
                case StateTag.moveGoal:
                    shapeRenderer.setColor(Color.RED);
                    shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.AGENT_RADIUS);
                    break;
                case StateTag.follow:
                    shapeRenderer.setColor(Color.GREEN);
                    shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.AGENT_RADIUS);
                    break;
                case StateTag.randomWalk:
                    shapeRenderer.setColor(Color.BLACK);
                    shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.AGENT_RADIUS);
                    break;
            }
        }
        shapeRenderer.end();
    }

    public static void isView(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        if (renderViewFlag) {
            view(shapeRenderer, camera, environment);
        }
    }

    public static void view(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 1, 0, 0.5f));
        for (Agent agent : environment.getAgentList()) {
            float moveDegree = agent.getDirectionDegree();
            moveDegree -= Parameter.VIEW_DEGREE / 2;
            shapeRenderer.arc(agent.getPosition().x, agent.getPosition().y, Parameter.VIEW_RADIUS_LENGTH, moveDegree, Parameter.VIEW_DEGREE);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public static void isMoveGoalLine(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        if (renderMoveGoalLineFlag) {
            moveGoalLine(shapeRenderer, camera, environment);
        }
    }

    public static void moveGoalLine(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (Agent agent : environment.getAgentList()) {
            if (agent.getStateTag().equals(StateTag.moveGoal)) {
                shapeRenderer.line(agent.getPosition(), agent.getGoal());
            }
        }
        shapeRenderer.end();
    }

    public static void switchViewFlag() {
        renderViewFlag = !renderViewFlag;
    }

    public static void switchMoveGoalLineFlag() {
        renderMoveGoalLineFlag = !renderMoveGoalLineFlag;
    }

    public static void switchFollowLineFlag() {
        renderFollowLineFlag = !renderFollowLineFlag;
    }
}
