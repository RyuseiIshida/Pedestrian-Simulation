package com.github.ryuseiishida.pedestrian_simulation.environment;

import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.StateTag;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.cell.Cell;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.Goal;
import com.github.ryuseiishida.pedestrian_simulation.util.LoadLog;
import com.github.ryuseiishida.pedestrian_simulation.util.UtilVector;
import com.github.ryuseiishida.pedestrian_simulation.util.WriteLog;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.*;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

import java.util.ArrayList;
import java.util.HashMap;

public class Environment {
    private static int step;
    private static boolean updateFlag;
    private ArrayList<Goal> goals;
    private ArrayList<Obstacle> obstacles;
    private HashMap<Vector2, Float> obstaclePosition;
    private ArrayList<Agent> agentList;
    private ArrayList<Agent> escapedAgentList;
    private int goalAgentNum;

    private LoadLog loadLog;

    //control flags
    private static boolean removeAllAgentFlag = false;
    private static boolean removeAllGoalFlag = false;
    private static boolean removeAllObstacleFlag = false;

    public Environment() {
        initEnvironment();
    }

    public Environment(String logDirPath) {
        initEnvironment();
        loadLog = new LoadLog(this, logDirPath);
        loadLog.setParameter(logDirPath);
        loadLog.setAgents(logDirPath);
        loadLog.setObstacle(logDirPath);
        loadLog.setGoal(logDirPath);
    }

    public void initEnvironment() {
        step = 0;
        updateFlag = false;
        goals = new ArrayList<>();
        obstacles = new ArrayList<>();
        obstaclePosition = new HashMap<>();
        agentList = new ArrayList<>();
        escapedAgentList = new ArrayList<>();
        new LoadLog(this);
        new WriteLog(this);
        setWallObstacles();
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
            step++;
            agentList.stream().parallel().forEach(Agent::action);
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
                if (goal.isAgentInGoal(agent)) {
                    goalAgentNum++;
                    escapedAgentList.add(agent);
                }
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
        addAgent(new Agent(issueAgentID(), this, pos));
    }

    public void spawnAgent(Vector2 pos, float speed) {
        addAgent(new Agent(issueAgentID(), this, pos, speed));
    }

    public void spawnAgent(Vector2 pos, String goalID) {
        for (Goal goal : goals) {
            if (goal.exists(goalID)) {
                addAgent(new Agent(issueAgentID(), this, pos, goal));
            }
        }
    }

    public void spawnAgent(Vector2 pos, String goalID, float speed) {
        for (Goal goal : goals) {
            if (goal.exists(goalID)) {
                addAgent(new Agent(issueAgentID(), this, pos, speed, goal));
            }
        }
    }

    private String issueAgentID() {
        return String.valueOf(agentList.size() + escapedAgentList.size() + 1);
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

    public ArrayList<Agent> getEscapedAgentList() {
        return escapedAgentList;
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
            obstaclePosition.put(targetObstacleCell.getCenterPoint(), Parameter.OBSTACLE_POTENTIAL_WEIGHT);
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

    public HashMap<Vector2, Float> getObstaclesPosition() {
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
}
