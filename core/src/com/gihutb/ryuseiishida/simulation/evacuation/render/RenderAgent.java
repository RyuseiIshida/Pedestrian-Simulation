package com.gihutb.ryuseiishida.simulation.evacuation.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gihutb.ryuseiishida.simulation.evacuation.util.Parameter;
import com.gihutb.ryuseiishida.simulation.evacuation.agent.Agent;
import com.gihutb.ryuseiishida.simulation.evacuation.agent.StateTag;
import com.gihutb.ryuseiishida.simulation.evacuation.environment.Environment;

public class RenderAgent {

    public RenderAgent(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        renderBody(shapeRenderer, camera, environment);
        renderView(shapeRenderer, camera, environment);
        renderMoveGoalLine(shapeRenderer, camera, environment);
        renderFollowLine(shapeRenderer, camera, environment);
    }

    public static void renderBody(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Agent agent : environment.getAgentList()) {
            if (agent.getPosition() == null) {
                shapeRenderer.end();
                return;
            }
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.AGENT_RADIUS);
            float range = 0.7f;
            switch (agent.getStateTag()) {
                case StateTag.moveGoal:
                    shapeRenderer.setColor(Color.RED);
                    shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.AGENT_RADIUS * range);
                    break;
                case StateTag.follow:
                    shapeRenderer.setColor(Color.GREEN);
                    shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.AGENT_RADIUS * range);
                    break;
                case StateTag.randomWalk:
                    shapeRenderer.setColor(Color.BLACK);
                    shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.AGENT_RADIUS * range);
                    break;
            }
        }
        shapeRenderer.end();
    }

    public static void renderView(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
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

    public static void renderMoveGoalLine(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
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


    public static void renderFollowLine(ShapeRenderer shapeRenderer, Camera camera, Environment environment) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (Agent agent : environment.getAgentList()) {
            if (agent.getFollowAgent() != null) {
                shapeRenderer.line(agent.getPosition(), agent.getFollowAgent().getPosition());
            }
        }
        shapeRenderer.end();
    }


}
