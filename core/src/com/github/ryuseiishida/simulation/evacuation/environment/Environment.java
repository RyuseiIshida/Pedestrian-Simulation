package com.github.ryuseiishida.simulation.evacuation.environment;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.simulation.evacuation.cell.Cell;
import com.github.ryuseiishida.simulation.evacuation.goal.Goal;
import com.github.ryuseiishida.simulation.evacuation.log.LoadLog;
import com.github.ryuseiishida.simulation.evacuation.log.WriterLog;
import com.github.ryuseiishida.simulation.evacuation.obstacle.Box;
import com.github.ryuseiishida.simulation.evacuation.obstacle.BoxLine;
import com.github.ryuseiishida.simulation.evacuation.obstacle.Fire;
import com.github.ryuseiishida.simulation.evacuation.obstacle.Obstacle;
import com.github.ryuseiishida.simulation.evacuation.util.Parameter;
import com.github.ryuseiishida.simulation.evacuation.analysis.LDA;
import com.github.ryuseiishida.simulation.evacuation.agent.Agent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Environment {
    private int step = 0;
    private boolean updateFlag = false;
    private ArrayList<Goal> goals = Parameter.GOALS;
    private ArrayList<Obstacle> obstacles = Parameter.OBSTACLES;
    private Fire fire = new Fire(Parameter.FIRE_POINT, 0);
    private ArrayList<Agent> agentList = new ArrayList<>();
    private int goalAgentNum;

    private LDA ldaStepSplit;
    private LDA ldaGroupSizeSplit;

    private LoadLog loadLog;
    private WriterLog writerLog = new WriterLog(this);


    public Environment() {
        for (BoxLine box : Parameter.Boxes) {
            obstacles.addAll(box.getLines());
        }
        if (Parameter.MODE.equals("LogSimulation")) {
            loadLog = new LoadLog();
            spawnLogAgents();
        } else {
            spawnInitAgents();
        }
        LoadMap.setObstacle(obstacles);
        ldaStepSplit = new LDA(agentList);
        ldaGroupSizeSplit = new LDA(agentList);
        //writerLog.writeSourceCodeToParameter();
        writerLog.writeAgentInitPosition();
    }

    public Environment(boolean loopFlag) {
        updateFlag = loopFlag;
        for (BoxLine box : Parameter.Boxes) {
            obstacles.addAll(box.getLines());
        }
        if (Parameter.MODE.equals("LogSimulation")) {
            loadLog = new LoadLog();
            spawnLogAgents();
        } else {
            spawnInitAgents();
        }
        LoadMap.setObstacle(obstacles);
        ldaStepSplit = new LDA(agentList, Parameter.LDA_OUT_PRINT_STEP);
        ldaGroupSizeSplit = new LDA(agentList, Parameter.LDA_OUT_PRINT_STEP);
        //writerLog.writeSourceCodeToParameter();
        writerLog.writeAgentInitPosition();
    }

    public void saveLDA() {
        ldaGroupSizeSplit.outPrint("stepGroupSizeSplit_Corpus");
        ldaStepSplit.outPrint("stepSplit_Corpus");
    }

    public void update() {
        if (updateFlag) {
            writerLog.ifWriteLog(Parameter.IS_WRITE_LOG);
            step++;
            agentList.stream()
                    .parallel()
                    .forEach(Agent::action);
            ifAgentInGoal();
            ifAgentInFire();
            ldaStepSplit.recordStepSplit(step);
            ldaGroupSizeSplit.recordGroupSizeSplit(step);
            fire.spreadFire();
        }
    }


    public void setStep(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    public ArrayList<Goal> getGoals() {
        return goals;
    }

    private void ifAgentInGoal() {
        for (Goal goal : goals) {
            agentList.removeIf(goal::isAgentInGoal);
        }
    }

    private void ifAgentInFire() {
        agentList.removeIf(agent -> fire.dstFireEdge(agent) < 0);
    }

    public void spawnObstacle(Vector2 pos) {
        obstacles.add(new Box(pos.x, pos.y, Parameter.CELL_INTERVAL, Parameter.CELL_INTERVAL, Parameter.ENV_CELLS_MAP));
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public ArrayList<Vector2> getObstaclesPosition() {
        ArrayList<Vector2> positionList = new ArrayList<>();
        for (Obstacle obstacle : obstacles) {
            for (Cell obstacleCell : obstacle.getObstacleCells()) {
                positionList.add(obstacleCell.getCenterPoint());
            }
        }
        return positionList;
    }

    //agent
    public void spawnInitAgents() {
        for (int i = 0; i < Parameter.INIT_AGENT_NUM; i++) {
            float x = MathUtils.random(Parameter.INIT_RANDOM_X.valueA, Parameter.INIT_RANDOM_X.valueB);
            float y = MathUtils.random(Parameter.INIT_RANDOM_Y.valueA, Parameter.INIT_RANDOM_Y.valueB);
            Vector2 position = getRandomPosition();
            if (i < Parameter.GOAL_AGENT_NUM) {
                if (i < Parameter.GOAL_AGENT_NUM / 2) {
                    agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, position, goals.get(0)));
                } else {
                    agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, position, goals.get(0)));
                }
            } else {
                agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, position));
            }
        }

    }

    public Vector2 getRandomPosition() {
        float x;
        float y;
        while (true) {
            x = MathUtils.random(Parameter.INIT_RANDOM_X.valueA, Parameter.INIT_RANDOM_X.valueB);
            y = MathUtils.random(Parameter.INIT_RANDOM_Y.valueA, Parameter.INIT_RANDOM_Y.valueB);
            if (checkInBoxes(x, y)) {
                continue;
            }
            break;
        }
        return new Vector2(x, y);
    }

    public Boolean checkInBoxes(float x, float y) {
        for (BoxLine box : Parameter.Boxes) {
            if (box.isPositionInBox(x, y)) {
                return true;
            }
        }
        return false;
    }

    public void spawnInitLogAgent() {
        String str = "core/assets/agentList.txt";
        ArrayList<Vector2> position = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(str))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] pos = line.split(",");
                position.add(new Vector2(Float.parseFloat(pos[0]), Float.parseFloat(pos[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < Parameter.INIT_AGENT_NUM; i++) {
            if (i < Parameter.GOAL_AGENT_NUM) {
                if (i < Parameter.GOAL_AGENT_NUM / 2) {
                    agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, position.get(i), goals.get(0)));
                } else {
                    agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, position.get(i), goals.get(0)));
                }
            } else {
                agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, position.get(i)));
            }
        }
    }

    private void spawnLogAgents() {
        for (File AgentLogFile : loadLog.getAgentFileList()) {
            agentList.add(new Agent(AgentLogFile, this));

        }
    }

    public void spawnAgent(Vector2 pos) {
        agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, pos));
    }

    public void spawnAgent(Vector2 pos, int goalIndex) {
        agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, pos, goals.get(goalIndex)));
    }

    public void spawnFire(Vector2 pos) {
        fire = new Fire(pos);
    }

    public ArrayList<Agent> getAgentList() {
        return agentList;
    }

    public ArrayList<Agent> getAgentList(Agent targetAgent) {
        ArrayList<Agent> aAgentList = new ArrayList<>();
        for (Agent agent : agentList) {
            if (!agent.equals(targetAgent)) {
                aAgentList.add(agent);
            }
        }
        return aAgentList;
    }

    public Agent getAgent(String ID) {
        if (ID.contains("agent")) {//if txt is "agentNN"
            ID = ID.replace("agent", "");
        }
        for (Agent agent : agentList) {
            if (agent.getID().equals(ID)) {
                return agent;
            }
        }
        return null;
    }

    public ArrayList<Vector2> getAgentsPosition() {
        ArrayList<Vector2> agentVectorList = new ArrayList<>();
        for (Agent agent : agentList) {
            agentVectorList.add(agent.getPosition());
        }
        return agentVectorList;
    }

    public ArrayList<Vector2> getAgentsPosition(Agent targetAgent) {
        ArrayList<Vector2> agentVectorList = new ArrayList<>();
        for (Agent agent : getAgentList(targetAgent)) {
            agentVectorList.add(agent.getPosition());
        }
        return agentVectorList;
    }

    public int getGoalAgentNum() {
        return goalAgentNum;
    }

    public void setGoalAgentNum(int num) {
        goalAgentNum = num;
    }

    public void switchUpdateFlag() {
        updateFlag = !updateFlag;
    }

    public void setUpdateFlag(boolean flag) {
        updateFlag = flag;
    }

    public Fire getFire() {
        return this.fire;
    }

}
