package com.github.ryuseiishida.pedestrian_simulation.environment;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.environment.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.environment.agent.StateTag;
import com.github.ryuseiishida.pedestrian_simulation.analysis.LDA;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.cell.Cell;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.goal.Goal;
import com.github.ryuseiishida.pedestrian_simulation.analysis.log.LoadLog;
import com.github.ryuseiishida.pedestrian_simulation.analysis.log.WriterLog;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.*;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Environment {
    private int step;
    private static boolean updateFlag;
    private ArrayList<Goal> goals;
    private ArrayList<Obstacle> obstacles;
    private HashSet<Vector2> obstaclePosition = new HashSet<>();
    private ArrayList<Agent> agentList;
    private int goalAgentNum;

    private LDA ldaStepSplit;
    private LDA ldaGroupSizeSplit;

    private LoadLog loadLog;
    private WriterLog writerLog;

    //flags
    public static boolean spawnAgentsFlag;
    public static boolean deleteAllAgentFlag;
    public static boolean deleteAllGoalFlag;
    public static boolean deleteAllObstacleFlag;

    public Environment() {
        step = 0;
        updateFlag = false;
        goals = new ArrayList<>();
        obstacles = new ArrayList<>();
        agentList = new ArrayList<>();

        addWallObstacles();
        spawnAgentsFlag = false;
        deleteAllAgentFlag = false;
        deleteAllGoalFlag = false;
        deleteAllObstacleFlag = false;
//        LoadMap.setObstacle(obstacles);
    }

    public Environment(String logDirPath) {
        step = 0;
        updateFlag = false;
        loadLog = new LoadLog(logDirPath);
        goals = new ArrayList<>();
        obstacles = new ArrayList<>();
        agentList = new ArrayList<>();
        addWallObstacles();
        spawnAgentsFlag = false;
        deleteAllAgentFlag = false;
        deleteAllGoalFlag = false;
        deleteAllObstacleFlag = false;
//        LoadMap.setObstacle(obstacles);
        spawnLogAgents();
    }

    public void update() {
        if (spawnAgentsFlag) {
            spawnInitAgents();
        }
        if (deleteAllAgentFlag) {
            deleteAllAgent();
        }
        if (deleteAllGoalFlag) {
            deleteAllGoal();
        }
        if (deleteAllObstacleFlag) {
            deleteAllObstacle();
        }

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

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public Set<Vector2> getObstaclesPosition() {
        return obstaclePosition;
    }

    public HashSet<Vector2> obstaclePosition() {
        return obstaclePosition;
    }

    public void spawnInitAgents() {
        for (int i = 0; i < Parameter.INIT_AGENT_NUM; i++) {
            agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, getRandomPosition()));
        }
        spawnAgentsFlag = false;
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

    public void spawnAgent(Vector2 pos, float speed) {
        agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, pos, speed));
    }


    public void spawnAgent(Vector2 pos, String goalID) {
        for (Goal goal : goals) {
            if (goal.exists(goalID)) {
                agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, pos, goal));
            }
        }
    }

    public void spawnAgent(Vector2 pos, String goalID, float speed) {
        for (Goal goal : goals) {
            if (goal.exists(goalID)) {
                agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, pos, goal, speed));
            }
        }
    }

    public void deleteAllAgent() {
        agentList.clear();
        deleteAllAgentFlag = false;
    }

    public void deleteAllGoal() {
        goals.clear();
        agentList.removeIf(agent -> agent.getStateTag().equals(StateTag.moveGoal));
        deleteAllGoalFlag = false;
    }

    public void deleteAllObstacle() {
        obstacles.clear();
        for (Agent agent : agentList) {
//            agent.setObstaclePositionMap();
        }
        deleteAllObstacleFlag = false;
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

    public static boolean getUpdateFlag() {
        return updateFlag;
    }

    public static void setUpdateFlag(boolean flag) {
        updateFlag = flag;
    }

    public LoadLog getLoadLog() {
        return loadLog;
    }

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
        addObstaclePosition(obstacle);
    }

    private void addObstaclePosition(Obstacle targetObstacle) {
        for (Cell targetObstacleCell : targetObstacle.getObstacleCells()) {
            obstaclePosition.add(targetObstacleCell.getCenterPoint());
        }
    }

    private void addWallObstacles() {
        //left
        addObstacle(new Line(0, 0, 0, Parameter.SCALE.y, Parameter.ENV_CELLS_MAP));
        //right
        addObstacle(new Line(Parameter.SCALE.x, 0, Parameter.SCALE.x, Parameter.SCALE.y, Parameter.ENV_CELLS_MAP));
        //top
        addObstacle(new Line(0, Parameter.SCALE.y, Parameter.SCALE.x, Parameter.SCALE.y, Parameter.ENV_CELLS_MAP));
        //bottom
        addObstacle(new Line(0, 0, Parameter.SCALE.x, 0, Parameter.ENV_CELLS_MAP));
    }

    public void addGoal(Goal goal) {
        for (Goal goalElement : goals) {
            if (goalElement.getID().equals(goal.getID())) {
                return;
            }
        }
        goals.add(goal);
    }

}
