package com.github.ryuseiishida.pedestrian_simulation.environment;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.agent.StateTag;
import com.github.ryuseiishida.pedestrian_simulation.cell.Cell;
import com.github.ryuseiishida.pedestrian_simulation.goal.Goal;
import com.github.ryuseiishida.pedestrian_simulation.log.LoadLog;
import com.github.ryuseiishida.pedestrian_simulation.log.WriterLog;
import com.github.ryuseiishida.pedestrian_simulation.obstacle.Box;
import com.github.ryuseiishida.pedestrian_simulation.obstacle.BoxLine;
import com.github.ryuseiishida.pedestrian_simulation.obstacle.Fire;
import com.github.ryuseiishida.pedestrian_simulation.obstacle.Obstacle;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;
import com.github.ryuseiishida.pedestrian_simulation.analysis.LDA;
import com.github.ryuseiishida.pedestrian_simulation.agent.Agent;

import java.io.File;
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
    private WriterLog writerLog;


    public Environment() {
        for (BoxLine box : Parameter.BOX_LIST) {
            obstacles.addAll(box.getLines());
        }
        LoadMap.setObstacle(obstacles);
        spawnInitAgents();
    }

    public Environment(boolean loopFlag) {
        updateFlag = loopFlag;
        for (BoxLine box : Parameter.BOX_LIST) {
            obstacles.addAll(box.getLines());
        }
        LoadMap.setObstacle(obstacles);
        spawnInitAgents();
    }

    public Environment(String logDirPath) {
        loadLog = new LoadLog(logDirPath);
        for (BoxLine box : Parameter.BOX_LIST) {
            obstacles.addAll(box.getLines());
        }
        spawnLogAgents();
    }

    public void update() {
        if (updateFlag) {
            if (writerLog == null) {
                initWriterLog();
            }
            ifLoadLog();
            writerLog.ifWriteLog(Parameter.IS_WRITE_LOG);
            step++;
            agentList.stream()
                    .parallel()
                    .forEach(Agent::action);
            ifAgentInGoal();
            ifAgentInFire();
            fire.spreadFire();
            ldaStepSplit.recordStepSplit(step);
            ldaGroupSizeSplit.recordGroupSizeSplit(step);
        }
    }

    public void initWriterLog() {
        writerLog = new WriterLog(this);
        ldaStepSplit = new LDA(agentList, Parameter.LDA_OUT_PRINT_STEP, writerLog.getPath());
        ldaGroupSizeSplit = new LDA(agentList, Parameter.LDA_OUT_PRINT_STEP, writerLog.getPath());
    }

    public void ifLoadLog() {
        if (loadLog != null) {
            if (step == loadLog.endStep()) {
                return;
            }
        }
    }

    public void saveLDA() {
        ldaGroupSizeSplit.outPrint("group_size_split_corpus");
        ldaStepSplit.outPrint("step_split_corpus");
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
            for (Agent agent : agentList) {
                if (goal.isAgentInGoal(agent)) {
                    goalAgentNum++;
                }
            }
            agentList.removeIf(goal::isAgentInGoal);
        }
        agentList.removeIf(agent -> agent.getStateTag().equals(StateTag.escaped));
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
            Vector2 position = getRandomPosition();
            if (i < Parameter.GOAL_AGENT_NUM) {
                agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, position, goals.get(0)));
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
        for (BoxLine box : Parameter.BOX_LIST) {
            if (box.isPositionInBox(x, y)) {
                return true;
            }
        }
        return false;
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
