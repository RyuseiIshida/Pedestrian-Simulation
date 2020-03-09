package com.github.ryuseiishida.pedestrian_simulation.potential;

import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.cell.Cell;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.obstacle.Obstacle;
import org.apache.commons.math3.analysis.function.Exp;

import java.util.ArrayList;

public class KimPotentialModel {
    private float cg = Parameter.AGENT_KIM_POTENTIAL_WEIGHT;
    private float lg = Parameter.AGENT_KIM_POTENTIAL_RANGE;
    private float co = Parameter.OBSTACLE_KIM_POTENTIAL_WEIGHT;
    private float lo = Parameter.OBSTACLE_KIM_POTENTIAL_RANGE;
    private float potentialDelta = Parameter.POTENTIAL_DELTA;

    private Environment env;
    private Agent agent;

    public KimPotentialModel(Environment env, Agent agent) {
        this.env = env;
        this.agent = agent;
    }

    public float attractivePotential(Vector2 targetPosition, Vector2 goalPosition) {
        float len = targetPosition.dst2(goalPosition);
        double potentialWeight = cg * (1 - new Exp().value(-1 * (len / (lg * lg))));
        return (float) potentialWeight;
    }

    public float repulsivePotential(Vector2 targetPosition, ArrayList<Vector2> ObstaclePositionList) {
        float potentialWeight = 0f;
        for (Vector2 vec : ObstaclePositionList) {
            potentialWeight += co * new Exp().value(-1 * (targetPosition.dst2(vec) / (lo * lo)));
        }
        return potentialWeight;
    }

    public float Composition(float Ug, float Uo) {
        return (((1 / cg) * Uo) + 1) * Ug;
    }

    public Vector2 getMoveDirection() {
        Vector2 direction = new Vector2();
        Vector2 deltaPosition;
        deltaPosition = new Vector2(agent.getPosition().x + potentialDelta, agent.getPosition().y);
        direction.x = pDirection(deltaPosition);
        deltaPosition = new Vector2(agent.getPosition().x, agent.getPosition().y + potentialDelta);
        direction.y = pDirection(deltaPosition);
        return direction;
    }

    private float pDirection(Vector2 deltaPosition) {
        return -1 * (getPotential(deltaPosition) - getPotential(agent.getPosition())) / potentialDelta;
    }

    private float getPotential(Vector2 targetPosition) {
        float Ug = attractivePotential(targetPosition, agent.getMovePos());
        float Uo = repulsivePotential(targetPosition, env.getAgentsPosition(agent)) + repulsivePotential(agent.getPosition(), env.getObstaclesPosition());
        return Composition(Ug, Uo);
    }

    public ArrayList<Vector2> getNearObstaclePotentialCells() {
        ArrayList<Obstacle> nearObstacles = new ArrayList<>();
        for (Obstacle obstacle : env.getObstacles()) {
            if (agent.getPosition().dst2(obstacle.getMinPoint()) < 2000
                    && agent.getPosition().dst2(obstacle.getMaxPoint()) < 2000) {
                nearObstacles.add(obstacle);
            }
        }
        ArrayList<Vector2> potentialCells = new ArrayList<>();
        for (Obstacle nearObstacle : nearObstacles) {
            for (Cell cell : nearObstacle.getCellsMap().getCells()) {
                potentialCells.add(cell.getCenterPoint());
            }
        }
        return potentialCells;
    }

    public void setCg(float cg) {
        this.cg = cg;
    }

    public void setCo(float co) {
        this.co = co;
    }

    public void setLg(float lg) {
        this.lg = lg;
    }

    public void setLo(float lo) {
        this.lo = lo;
    }

    public void setPotentialDelta(float potentialDelta) {
        this.potentialDelta = potentialDelta;
    }
}
