package com.simulation.pedestrian;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Agent.Agent;
import com.simulation.pedestrian.Agent.Crowd;
import com.simulation.pedestrian.Log.WriterLog;
import com.simulation.pedestrian.Obstacle.Obstacle;
import com.simulation.pedestrian.Potential.PotentialCell;
import com.simulation.pedestrian.Potential.PotentialMap;
import com.simulation.pedestrian.Util.Tuple;

import java.util.ArrayList;
import java.util.Iterator;

public class Environment {
    private float step;
    private Vector2 scale = Parameter.SCALE;
    private PotentialMap envPotentialMap;
    private float cellInterval = Parameter.CELLINTERVAL;
    private float maxPotential = Parameter.MAXPOTENTIAL;
    private ArrayList<Goal> goals = new ArrayList<>(Parameter.GOALS);
    private ArrayList<Obstacle> obstacles;
    private ArrayList<Agent> agents;
    private Crowd crowd;
    private int agentCounter;

    private WriterLog writerLog;

    public Environment() {
        step = 0;
        agents = new ArrayList<>();
        agentCounter = 0;
        crowd = new Crowd(this);
        spawnInitAgents();
        envPotentialMap = new PotentialMap(scale, cellInterval, maxPotential);
        obstacles = new ArrayList<>();
        //obstacles.add(new Obstacle(900, 200, 1000, 1500, envPotentialMap));
        //leftLine
        obstacles.add(new Obstacle(900, 200, 10, 1500, envPotentialMap));
        //rightLine
        obstacles.add(new Obstacle(1900, 200, 10, 1500, envPotentialMap));
        //bottomLine
        obstacles.add(new Obstacle(1010, 200, 810, 10, envPotentialMap));
        //TopLine
        obstacles.add(new Obstacle(900, 1700, 1000, 10, envPotentialMap));
        setObstaclePotential();
        //setEdgePotential();
        if(Parameter.ISWRITELOG) {
            writerLog = new WriterLog(this);
        }
    }

    public void update() {
        if (Parameter.ISWRITELOG) {
            writerLog.writeAgentLog();
        }
        agents.stream()
                .parallel()
                .forEach(agent -> {
                    try {
                        agent.action();
                    } catch (final Exception l_exception) {
                    }
                });
        ifAgentInGoal();
        step++;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public float getStep() {
        return step;
    }

    public ArrayList<Goal> getGoals() {
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

    public void setAgentKimPotentialCell() {
        float weightPotential;
        float co = Parameter.AGENT_KIMPOTENTIALWEIGHT;
        float lo = Parameter.AGENT_KIMPOTENTIALRANGE;
        for (PotentialCell cell : envPotentialMap.getPotentialCells()) {
            cell.setAgentPotential(0); //前ステップ時のポテンシャルセルを初期化
            weightPotential = 0;
            for (Agent agent : agents) {
                float kimPotential = (float) (co * Math.exp(-1 * (agent.getPosition().dst2(cell.getCenterPoint()) / (lo * lo))));
                //float movePotential = (float) (Math.exp(-1 * ( /(lo*lo))));
                weightPotential += kimPotential;
            }
            cell.setAgentPotential(weightPotential);
        }
    }


    public Vector2 getAgentGrad(Vector2 targetPos) {
        if (targetPos.x < 0 || targetPos.x > Parameter.SCALE.x
                || targetPos.y < 0 || targetPos.y > Parameter.SCALE.y) {
            return new Vector2(0, 0);
        }
        Vector2 gradVec = new Vector2();
        float delta = Parameter.CELLINTERVAL / 2;
        PotentialCell targetCell = envPotentialMap.getPotentialCell(targetPos);
        PotentialCell deltaXCell = envPotentialMap.getPotentialCell(targetPos.x + delta, targetPos.y);
        PotentialCell deltaYCell = envPotentialMap.getPotentialCell(targetPos.x, targetPos.y + delta);
        gradVec.x = -(deltaXCell.getAgentPotential() - targetCell.getAgentPotential() / delta);
        gradVec.y = -(deltaYCell.getAgentPotential() - targetCell.getAgentPotential() / delta);
        float v = (float) Math.sqrt(gradVec.x * gradVec.x + gradVec.y * gradVec.y);
        //gradVec.x *= 5;
        //gradVec.y *= 5;
        gradVec.x /= v;
        gradVec.y /= v;
        return gradVec;
    }

    private void setObstaclePotential() {
        ArrayList<Vector2> obstaclePosList = new ArrayList<>();
        float co = Parameter.OBSTACLE_KIMPOTENTIALWEIGHT;
        float lo = Parameter.OBSTACLE_KIMPOTENTIALRANGE;
        for (Obstacle obstacle : obstacles) {
            Tuple startIndex = obstacle.getStartIndex();
            Tuple endIndex = obstacle.getEndIndex();
            for (int i = startIndex.t1; i <= endIndex.t1; i++) {
                for (int j = startIndex.t2; j <= endIndex.t2; j++) {
                    obstaclePosList.add(envPotentialMap.getMatrixPotentialCell(i, j).getCenterPoint());
                }
            }
        }
        for (PotentialCell cell : envPotentialMap.getPotentialCells()) {
            cell.setObstaclePotential(getKIMPotential(cell.getCenterPoint(), obstaclePosList, co, lo));
        }
    }

    private float getKIMPotential(Vector2 targetPos, ArrayList<Vector2> obstaclePosList, float c, float l) {
        float weightPotential = 0;
        for (Vector2 pos : obstaclePosList) {
            weightPotential += (float) (c * Math.exp(-1 * (pos.dst2(targetPos) / (l * l))));
        }
        return weightPotential;
    }

    public void spawnObstacle(Vector2 pos) {
        obstacles.add(new Obstacle(pos.x, pos.y, Parameter.CELLINTERVAL, Parameter.CELLINTERVAL, envPotentialMap));
        setObstaclePotential();
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }


    //Agent
    public void spawnInitAgents() {
        for (int i = 0; i < Parameter.initAgentNum; i++) {
            float x = MathUtils.random(950, 1800);
            float y = MathUtils.random(1300, 1600);
            Vector2 position = new Vector2(x, y);
            if (i < Parameter.goalAgentNum) {
                agents.add(new Agent(String.valueOf(++agentCounter), this, position, goals.get(0)));
            } else {
                agents.add(new Agent(String.valueOf(++agentCounter), this, position));
            }
        }
    }

    public void spawnAgent1(Vector2 pos) {
        agents.add(new Agent(String.valueOf(++agentCounter), this, pos));
    }

    public void spawnAgent2(Vector2 pos, int goalIndex) {
        agents.add(new Agent(String.valueOf(++agentCounter), this, pos, goals.get(goalIndex)));
    }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

    public Crowd getCrowd() {
        return crowd;
    }

}
