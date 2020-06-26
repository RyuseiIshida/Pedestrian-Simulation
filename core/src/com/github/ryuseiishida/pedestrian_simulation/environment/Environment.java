package com.github.ryuseiishida.pedestrian_simulation.environment;

import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.StateTag;
import com.github.ryuseiishida.pedestrian_simulation.analysis.LDA;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.cell.Cell;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.Goal;
import com.github.ryuseiishida.pedestrian_simulation.util.LoadLog;
import com.github.ryuseiishida.pedestrian_simulation.util.UtilVector;
import com.github.ryuseiishida.pedestrian_simulation.util.WriteLog;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.*;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Environment {
    private static int step;
    private static boolean updateFlag;
    private ArrayList<Goal> goals;
    private ArrayList<Obstacle> obstacles;
    private HashSet<Vector2> obstaclePosition;
    private ArrayList<Agent> agentList;
    private int goalAgentNum;

    private LDA ldaStepSplit;
    private LDA ldaGroupSizeSplit;

    private LoadLog loadLog;
    private WriteLog writeLog;

    //control flags
    private static boolean spawnRandomAgentsFlag = false;
    private static boolean removeAllAgentFlag = false;
    private static boolean removeAllGoalFlag = false;
    private static boolean removeAllObstacleFlag = false;

    public Environment() {
        initEnvironment();
    }

    public Environment(String logDirPath) {
        initEnvironment();
        loadLog = new LoadLog(this, logDirPath);
        loadLog.setAgents(logDirPath);
        loadLog.setObstacle(logDirPath + "/obstacle.obs");
        loadLog.setGoal(logDirPath + "/goal.gl");
    }

    public void initEnvironment() {
        step = 0;
        updateFlag = false;
        goals = new ArrayList<>();
        obstacles = new ArrayList<>();
        obstaclePosition = new HashSet<>();
        agentList = new ArrayList<>();
        loadLog = new LoadLog(this);
        writeLog = new WriteLog(this);
        setWallObstacles();
        spawnRandomAgentsFlag = false;
        removeAllAgentFlag = false;
        removeAllGoalFlag = false;
        removeAllObstacleFlag = false;
    }

    public void update() {
        ifRemoveAllAgent();
        ifRemoveAllGoal();
        ifRemoveAllObstacle();
        ifAgentInGoal();
        if (updateFlag) {
            ifInitWriterLog();
            writeLog.writeLog();
            step++;
            agentList.stream().parallel().forEach(Agent::action);
//            ldaStepSplit.recordStepSplit(step);
//            ldaGroupSizeSplit.recordGroupSizeSplit(step);
        }
    }

    public static int getStep() {
        return step;
    }

    public static void setStep(int stepNum) {
        step = stepNum;
    }

    public static boolean getUpdateFlag() {
        return updateFlag;
    }

    public static void setUpdateFlag(boolean flag) {
        updateFlag = flag;
    }

    public void switchUpdateFlag() {
        updateFlag = !updateFlag;
    }

    private void ifAgentInGoal() {
        for (Goal goal : goals) {
            for (Agent agent : agentList) {
                if (goal.isAgentInGoal(agent)) goalAgentNum++;
            }
            agentList.removeIf(goal::isAgentInGoal);
        }
        agentList.removeIf(agent -> agent.getStateTag().equals(StateTag.escaped));
    }

    public void spawnRandomNonGoalAgents(float startX, float startY, float endX, float endY, int num) {
        for (int i = 0; i < num; i++) {
            Vector2 position = UtilVector.getRandomPosition(startX, startY, endX, endY);
            spawnAgent(position);
        }
    }

    public void spawnRandomGoalAgents(float startX, float startY, float endX, float endY, int num, String id) {
        for (int i = 0; i < num; i++) {
            Vector2 position = UtilVector.getRandomPosition(startX, startY, endX, endY);
            spawnAgent(position, id);
        }
    }

    public void addAgent(Agent agent) {
        agentList.add(agent);
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
                agentList.add(new Agent(String.valueOf(agentList.size() + 1), this, pos, speed, goal));
            }
        }
    }

    public static void removeAllAgent() {
        removeAllAgentFlag = true;
    }

    private void ifRemoveAllAgent() {
        if (removeAllAgentFlag) {
            agentList.clear();
            removeAllAgentFlag = false;
        }
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

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
        addObstaclePosition(obstacle);
    }

    public static void removeAllObstacle() {
        removeAllObstacleFlag = true;
    }

    private void ifRemoveAllObstacle() {
        if (removeAllObstacleFlag) {
            obstacles.clear();
            obstaclePosition.clear();
            removeAllObstacleFlag = false;
        }
    }

    private void addObstaclePosition(Obstacle targetObstacle) {
        for (Cell targetObstacleCell : targetObstacle.getObstacleCells()) {
            obstaclePosition.add(targetObstacleCell.getCenterPoint());
        }
    }

    public void resetObstaclePosition() {
        obstaclePosition.clear();
        for (Obstacle obstacle : obstacles) {
            addObstaclePosition(obstacle);
        }
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public Set<Vector2> getObstaclesPosition() {
        return obstaclePosition;
    }

    private void setWallObstacles() {
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

    public void removeGoalAll(ArrayList<Goal> removeGoalList) {
        ArrayList<Agent> removeGoalAgentList = new ArrayList<>();
        for (Goal goal : removeGoalList) {
            for (Agent agent : agentList) {
                if (agent.getGoal().equals(goal)) removeGoalAgentList.add(agent);
            }
        }
        goals.removeAll(removeGoalList);
        agentList.removeAll(removeGoalAgentList);
    }

    public static void removeAllGoal() {
        removeAllGoalFlag = true;
    }

    private void ifRemoveAllGoal() {
        if (removeAllGoalFlag) {
            goals.clear();
            agentList.removeIf(agent -> agent.getStateTag().equals(StateTag.moveGoal));
            removeAllGoalFlag = false;
        }
    }

    public Goal getGoal(String id) {
        for (Goal goal : goals) {
            if (goal.exists(id)) {
                return goal;
            }
        }
        return null;
    }

    public ArrayList<Goal> getGoals() {
        return goals;
    }

    public void ifInitWriterLog() {
        if (writeLog == null) {
            writeLog = new WriteLog(this);
            ldaStepSplit = new LDA(agentList, Parameter.LDA_OUT_PRINT_STEP, writeLog.getPath());
            ldaGroupSizeSplit = new LDA(agentList, Parameter.LDA_OUT_PRINT_STEP, writeLog.getPath());
        }
    }
}
