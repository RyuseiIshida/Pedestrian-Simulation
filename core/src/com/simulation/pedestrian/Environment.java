package com.simulation.pedestrian;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.agent.Agent;
import com.simulation.pedestrian.agent.Crowd;
import com.simulation.pedestrian.log.LoadLog;
import com.simulation.pedestrian.log.WriterLog;
import com.simulation.pedestrian.obstacle.Obstacle;
import com.simulation.pedestrian.potential.PotentialCell;
import com.simulation.pedestrian.potential.PotentialMap;
import com.simulation.pedestrian.util.Tuple;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class Environment {
    private float step;
    private PotentialMap envPotentialMap = Parameter.POTENTIALMAP;
    private ArrayList<Goal> goals = new ArrayList<>(Parameter.GOALS);
    private ArrayList<Obstacle> obstacles = Parameter.ALL_OBSTACLE;
    private ArrayList<Agent> agentList;
    private Crowd crowd;
    private int agentCounter;
    private int goalAgentNum;
    public boolean agentClearFlag = false;

    private LoadLog loadLog;
    private WriterLog writerLog;

    public Environment() {
        step = 0;
        agentList = new ArrayList<>();
        agentCounter = 0;
        crowd = new Crowd(this);
        if (Parameter.MODE == "LogSimulation") {
            loadLog = new LoadLog();
            spawnLogAgents();
        } else {
            spawnInitAgents();
        }
        if (Parameter.ISWRITELOG) {
            writerLog = new WriterLog(this);
        }
    }

    public void update() {
        if (Parameter.ISWRITELOG) {
            writerLog.writeAgentLog();
            writerLog.writeMacroLog();
        }
        agentList.stream()
                .parallel()
                .forEach(agent -> agent.action());
        ifAgentInGoal();
        step++;
    }

    public void clearAgent() {
        agentClearFlag = true;
        goalAgentNum = 0;
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
        Iterator<Agent> iterator = agentList.iterator();
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
                    goalAgentNum++;
                }
            }
        }
    }

    //potential

    public PotentialMap getEnvPotentialMap() {
        return envPotentialMap;
    }

    public void setAgentKimPotentialCell() {
        float weightPotential;
        float co = Parameter.AGENT_KIM_POTENTIAL_WEIGHT;
        float lo = Parameter.AGENT_KIM_POTENTIAL_RANGE;
        for (PotentialCell cell : envPotentialMap.getPotentialCells()) {
            cell.setAgentPotential(0); //前ステップ時のポテンシャルセルを初期化
            weightPotential = 0;
            for (Agent agent : agentList) {
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
        float delta = Parameter.CELL_INTERVAL / 2;
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
        float co = Parameter.OBSTACLE_KIM_POTENTIAL_WEIGHT;
        float lo = Parameter.OBSTACLE_KIM_POTENTIAL_RANGE;
        obstacles.forEach(obstacle -> {
            Tuple startIndex = obstacle.getStartIndex();
            Tuple endIndex = obstacle.getEndIndex();
            for (int i = startIndex.t1; i <= endIndex.t1; i++) {
                for (int j = startIndex.t2; j <= endIndex.t2; j++) {
                    obstaclePosList.add(envPotentialMap.getMatrixPotentialCell(i, j).getCenterPoint());
                }
            }
        });
        envPotentialMap.getPotentialCells().forEach(cell ->  cell.setObstaclePotential(getKIMPotential(cell.getCenterPoint(), obstaclePosList, co, lo)));
    }

    private float getKIMPotential(Vector2 targetPos, ArrayList<Vector2> obstaclePosList, float c, float l) {
        float weightPotential = 0;
        for (Vector2 pos : obstaclePosList) {
            weightPotential += (float) (c * Math.exp(-1 * (pos.dst2(targetPos) / (l * l))));
        }
        return weightPotential;
    }

    public void spawnObstacle(Vector2 pos) {
        obstacles.add(new Obstacle(pos.x, pos.y, Parameter.CELL_INTERVAL, Parameter.CELL_INTERVAL, envPotentialMap));
        setObstaclePotential();
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }


    //agent
    public void spawnInitAgents() {
        for (int i = 0; i < Parameter.INIT_AGENT_NUM; i++) {
            float x = MathUtils.random(Parameter.INIT_RANDOM_X1, Parameter.INIT_RANDOM_X2);
            float y = MathUtils.random(Parameter.INIT_RANDOM_Y1, Parameter.INIT_RANDOM_Y2);
            Vector2 position = new Vector2(x, y);
            if (i < Parameter.GOAL_AGENT_NUM) {
                if (i < Parameter.GOAL_AGENT_NUM / 2) {
                    agentList.add(new Agent(String.valueOf(++agentCounter), this, position, goals.get(0)));
                } else {
                    agentList.add(new Agent(String.valueOf(++agentCounter), this, position, goals.get(0)));
                }
            } else {
                agentList.add(new Agent(String.valueOf(++agentCounter), this, position));
            }
        }
    }

    public void spawnLogAgents() {
        for (File AgentLogFile : loadLog.getAgentFileList()) {
            agentList.add(new Agent(AgentLogFile));
        }
    }

    public void spawnAgent(Vector2 pos) {
        agentList.add(new Agent(String.valueOf(++agentCounter), this, pos));
    }

    public void spawnAgent(Vector2 pos, int goalIndex) {
        agentList.add(new Agent(String.valueOf(++agentCounter), this, pos, goals.get(goalIndex)));
    }

    public ArrayList<Agent> getAgentList() {
        return agentList;
    }

    public Agent getAgent(String ID) {
        for (Agent agent : agentList) {
            if(agent.getID().equals(ID)){
                return agent;
            }
        }
        return null;
    }

    public int getGoalAgentNum() {
        return goalAgentNum;
    }

    public void setGoalAgentNum(int num) {
        this.goalAgentNum = num;
    }

    public Crowd getCrowd() {
        return crowd;
    }


}
