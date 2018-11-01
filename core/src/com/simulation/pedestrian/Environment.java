package com.simulation.pedestrian;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Agent.Agent;
import com.simulation.pedestrian.Obstacle.Obstacle;
import com.simulation.pedestrian.Potential.PotentialCell;
import com.simulation.pedestrian.Potential.PotentialMap;
import com.simulation.pedestrian.Util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Environment {
    private float step;
    private Vector2 scale = Parameter.SCALE;
    private PotentialMap envPotentialMap;
    private float cellInterval = Parameter.CELLINTERVAL;
    private float maxPotential = Parameter.MAXPOTENTIAL;
    private float obstaclePotential = Parameter.OBSTACLEPOTENTIAL;
    private List<Goal> goals;
    private List<Obstacle> obstacles;
    private List<Agent> agents;

    public Environment() {
        step = 0;
        goals = new ArrayList<>(Arrays.asList(Parameter.INITGOAL));
        agents = new ArrayList<>();
        spawnInitAgents();
        envPotentialMap = new PotentialMap(scale, cellInterval, maxPotential);
        obstacles = new ArrayList<>();
        //obstacles.add(new Obstacle(150, 150, 300, 100, envPotentialMap, obstaclePotential));
        setEdgePotential();
        setObstaclePotential();
    }

    public void update() {
        agents.stream()
                .parallel()
                .forEach(agent -> {
                    try {
                        agent.action();
                    } catch (final Exception l_exception) {
                    }
                });
        setAgentPotential();
        ifAgentInGoal();
        step++;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public float getStep() {
        return step;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    private void ifAgentInGoal() {
        Iterator<Agent> iterator = agents.iterator();
        while (iterator.hasNext()) {
            Agent agent = iterator.next();
            for (Goal goal : goals) {
                float aPosX = agent.getPosition().x;
                float aPosY = agent.getPosition().y;
                if (aPosX > goal.getPositionX()
                        && aPosY > goal.getPositionY()
                        && aPosX < goal.getRightTop().x
                        && aPosY < goal.getRightTop().y) {
                    iterator.remove();
                }
            }
        }
    }

    //Potential

    public PotentialMap getEnvPotentialMap() {
        return envPotentialMap;
    }

    private void setEdgePotential() {
        //Bottom
        for (int i = 0; i < envPotentialMap.getLastIndex().t1; i++) {
            envPotentialMap.getMatrixPotentialCell(i, 0).setObstaclePotential(obstaclePotential);
        }
        //Top
        for (int i = 0; i < envPotentialMap.getLastIndex().t1; i++) {
            envPotentialMap.getMatrixPotentialCell(i, envPotentialMap.getLastIndex().t2).setObstaclePotential(obstaclePotential);
        }
        //Left
        for (int j = 0; j < envPotentialMap.getLastIndex().t2; j++) {
            envPotentialMap.getMatrixPotentialCell(0, j).setObstaclePotential(obstaclePotential);
        }
        //Right
        for (int j = 0; j < envPotentialMap.getLastIndex().t2; j++) {
            envPotentialMap.getMatrixPotentialCell(envPotentialMap.getLastIndex().t1, j).setObstaclePotential(obstaclePotential);
        }
    }

    private void setAgentPotential() {
        for (PotentialCell potentialCell : envPotentialMap.getPotentialCells()) {
            potentialCell.setAgentPotential(0);
        }
        for (Agent agent : agents) {
            PotentialCell targetCell = envPotentialMap.getPotentialCell(agent.getPosition());
            targetCell.setAgentPotential(Parameter.AGENTPOTENTIAL);
        }
    }

    private void setObstaclePotential() {
        for (Obstacle obstacle : obstacles) {
            Tuple startIndex = obstacle.getStartIndex();
            Tuple endIndex = obstacle.getEndIndex();
            int range = 0;
            for (int i = startIndex.t1 - range; i <= endIndex.t1 + range; i++) {
                for (int j = startIndex.t2 - range; j <= endIndex.t2 + range; j++) {
                    if (startIndex.t1 > i || startIndex.t2 > j || endIndex.t1 < i || endIndex.t2 < j) {
                        manhattanDistance(startIndex, endIndex, new Tuple(i, j));
                        envPotentialMap.getMatrixPotentialCell(i, j).setObstaclePotential(range);
                    } else {
                        envPotentialMap.getMatrixPotentialCell(i, j).setObstaclePotential(obstaclePotential);
                    }
                }
            }
        }
    }

    public void spawnObstacle(Vector2 pos) {
        obstacles.add(new Obstacle(pos.x, pos.y, Parameter.CELLINTERVAL, Parameter.CELLINTERVAL, envPotentialMap, obstaclePotential));
        setObstaclePotential();
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    private int manhattanDistance(Tuple startIndex, Tuple endIndex, Tuple targetIndex) {
        int minDistance = 0;
        for (Obstacle obstacle : obstacles) {
            for (int i = startIndex.t1; i <= endIndex.t2; i++) {
                for (int j = startIndex.t2; j <= endIndex.t2; j++) {
                    int x = Math.abs(targetIndex.t1 - startIndex.t1);
                    int y = Math.abs(targetIndex.t2 - startIndex.t2);
                    int tmpDistance = x + y;
                    if (minDistance == 0) minDistance = tmpDistance;
                    minDistance = minDistance > tmpDistance ? tmpDistance : minDistance;
                }
            }
        }
        return minDistance;
    }

    //Agent

    public void spawnInitAgents() {
        for (int i = 0; i < Parameter.initAgentNum; i++) {
            float x = MathUtils.random(0, scale.x);
            float y = MathUtils.random(0, scale.y);
            Vector2 position = new Vector2(x, y);
            if (i < Parameter.goalAgentNum) {
                agents.add(new Agent(this, position, Parameter.INITGOAL));
            } else {
                agents.add(new Agent(this, position));
            }
        }
    }

    public void spawnAgent1(Vector2 pos) {
        agents.add(new Agent(this, pos));
    }

    public void spawnAgent2(Vector2 pos) {
        agents.add(new Agent(this, pos, Parameter.INITGOAL));
    }

    public List<Agent> getAgents() {
        return agents;
    }
}
