package com.github.ryuseiishida.pedestrian_simulation.environment;

import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.analysis.AnalyzeLogLDA;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.StateTag;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.cell.Cell;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.Goal;
import com.github.ryuseiishida.pedestrian_simulation.util.LoadLog;
import com.github.ryuseiishida.pedestrian_simulation.util.UtilVector;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.*;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

import java.util.ArrayList;
import java.util.HashMap;

public class Environment {
    private static Environment environment = new Environment();
    private int step;
    private boolean updateFlag;
    private ArrayList<Goal> goals;
    private ArrayList<Obstacle> obstacles;
    private HashMap<Vector2, Float> obstaclePosition;
    private ArrayList<Agent> agentList;
    private ArrayList<Agent> escapedAgentList;
    private int goalAgentNum;
    private LoadLog loadLog;

    private Environment() {
        initEnvironment();
    }

    private Environment(String logDirPath) {
        setLog(logDirPath);
    }

    public static Environment getInstance() {
        return environment;
    }

    public static void resetEnvironment() {
        new Environment();
    }

    public static void resetEnvironment(String logDirPath) {
        new Environment(logDirPath);
    }

    public void initEnvironment() {
        step = 0;
        updateFlag = false;
        goals = new ArrayList<>();
        obstacles = new ArrayList<>();
        obstaclePosition = new HashMap<>();
        agentList = new ArrayList<>();
        escapedAgentList = new ArrayList<>();
        setWallObstacles();
    }

    public void setLog(String logDirPath) {
        initEnvironment();
        loadLog = new LoadLog(logDirPath);
        loadLog.setParameter(logDirPath);
        loadLog.setAgents(logDirPath);
        loadLog.setObstacle(logDirPath);
        loadLog.setGoal(logDirPath);
    }

    public int getStep() {
        return step;
    }

    public void setStep(int stepNum) {
        step = stepNum;
    }

    public boolean getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(boolean flag) {
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
        addAgent(new Agent(issueAgentID(), pos));
    }

    public void spawnAgent(Vector2 pos, float speed) {
        addAgent(new Agent(issueAgentID(), pos, speed));
    }

    public void spawnAgent(Vector2 pos, String goalID) {
        for (Goal goal : goals) {
            if (goal.exists(goalID)) {
                addAgent(new Agent(issueAgentID(), pos, goal));
            }
        }
    }

    public void spawnAgent(Vector2 pos, String goalID, float speed) {
        for (Goal goal : goals) {
            if (goal.exists(goalID)) {
                addAgent(new Agent(issueAgentID(), pos, speed, goal));
            }
        }
    }

    private String issueAgentID() {
        return String.valueOf(agentList.size() + escapedAgentList.size() + 1);
    }

    public void removeAllAgent() {
        agentList.clear();
    }

    public void update() {
        ifAgentInGoal();
        if (updateFlag) {
            step++;
            agentList.stream().parallel().forEach(Agent::action);
            if (Parameter.IS_ANALYSIS_MODE) new AnalyzeLogLDA(Parameter.WRITE_LOG_PATH).analysisMode(step);
        }

    }

    public void resetAgentLog() {
        for (Agent agent : agentList) {
            agent.resetLogList();
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

    public void removeAllObstacle() {
        obstacles.clear();
        obstaclePosition.clear();
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

    public void removeAllGoal() {
        goals.clear();
        agentList.removeIf(agent -> agent.getStateTag().equals(StateTag.moveGoal));
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
