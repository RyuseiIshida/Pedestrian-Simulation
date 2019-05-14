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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class Environment {
    private float step;
    private PotentialMap envPotentialMap = Parameter.POTENTIALMAP;
    private ArrayList<Goal> goals = new ArrayList<>(Parameter.GOALS);
    private ArrayList<Obstacle> obstacles = Parameter.ALL_OBSTACLE;
    private ArrayList<Agent> allAgent;
    private Crowd crowd;
    private int agentCounter;
    private int goalAgentNum;

    private WriterLog writerLog;

    public Environment() {
        step = 0;
        allAgent = new ArrayList<>();
        agentCounter = 0;
        crowd = new Crowd(this);
        spawnInitAgents();
        if (Parameter.ISWRITELOG) {
            writerLog = new WriterLog(this);
        }
    }

    public void update() {
        if (Parameter.ISWRITELOG) {
            writerLog.writeAgentLog();
            writerLog.writeMacroLog();
        }
        allAgent.stream()
                .parallel()
                .forEach(agent -> agent.action());
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
        Iterator<Agent> iterator = allAgent.iterator();
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

    //Potential

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
            for (Agent agent : allAgent) {
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
        obstacles.add(new Obstacle(pos.x, pos.y, Parameter.CELL_INTERVAL, Parameter.CELL_INTERVAL, envPotentialMap));
        setObstaclePotential();
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }


    //Agent
    public void spawnInitAgents() {
        if (Parameter.MODE == 1) {
            spawnLogAgents();
            return;
        }
        for (int i = 0; i < Parameter.INIT_AGENT_NUM; i++) {
            float x = MathUtils.random(Parameter.INIT_RANDOM_X1, Parameter.INIT_RANDOM_X2);
            float y = MathUtils.random(Parameter.INIT_RANDOM_Y1, Parameter.INIT_RANDOM_Y2);
            Vector2 position = new Vector2(x, y);
            if (i < Parameter.GOAL_AGENT_NUM) {
                if (i < Parameter.GOAL_AGENT_NUM / 2) {
                    allAgent.add(new Agent(String.valueOf(++agentCounter), this, position, goals.get(0)));
                } else {
                    allAgent.add(new Agent(String.valueOf(++agentCounter), this, position, goals.get(0)));
                }
//                if(Parameter.GOALAGENT_DESTINATION.equals("random")) {
//                    allAgent.add(new Agent(String.valueOf(++agentCounter), this, position, goals.get(MathUtils.random(goals.size()-1))));
//                } else {
//                    allAgent.add(new Agent(String.valueOf(++agentCounter), this, position, goals.get()));
//                }
//                if (Parameter.GOALAGENT_DESTINATION.equals("random")) {
//                    allAgent.add(new Agent(String.valueOf(++agentCounter), this, position, goals.get(MathUtils.random(goals.size() - 1))));
//                } else {
//                    allAgent.add(new Agent(String.valueOf(++agentCounter), this, position, goals.get(Integer.valueOf(Parameter.GOALAGENT_DESTINATION))));
//                }
            } else {
                allAgent.add(new Agent(String.valueOf(++agentCounter), this, position));
            }
        }
    }

    public void spawnAgent1(Vector2 pos) {
        //allAgent.add(new Agent(String.valueOf(++agentCounter), this, pos));
    }

    public void spawnAgent2(Vector2 pos, int goalIndex) {
        //allAgent.add(new Agent(String.valueOf(++agentCounter), this, pos, goals.get(goalIndex)));
    }

    public void spawnLogAgents() {
        Path path = Paths.get("core/src/com/simulation/pedestrian/Log/AgentPos.txt");
        ArrayList<Vector2> posList = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] str = line.split(",");
                Vector2 position = new Vector2(new Vector2(Float.valueOf(str[0]), Float.valueOf(str[1])));
                posList.add(position);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < Parameter.INIT_AGENT_NUM; i++) {
            if (i < Parameter.GOAL_AGENT_NUM) {
                if (i < Parameter.GOAL_AGENT_NUM / 2) {
                    allAgent.add(new Agent(String.valueOf(++agentCounter), this, posList.get(i), goals.get(0)));
                } else {
                    allAgent.add(new Agent(String.valueOf(++agentCounter), this, posList.get(i), goals.get(1)));
                }
            } else {
                    allAgent.add(new Agent(String.valueOf(++agentCounter), this, posList.get(i)));
            }
        }
    }

    public ArrayList<Agent> getAllAgent() {
        return allAgent;
    }

    public int getGoalAgentNum() {
        return goalAgentNum;
    }

    public Crowd getCrowd() {
        return crowd;
    }

}
