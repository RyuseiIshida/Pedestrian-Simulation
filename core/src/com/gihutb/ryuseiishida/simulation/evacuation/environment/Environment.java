package com.gihutb.ryuseiishida.simulation.evacuation.environment;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gihutb.ryuseiishida.simulation.evacuation.Parameter;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.Cell;
import com.gihutb.ryuseiishida.simulation.evacuation.goal.Goal;
import com.gihutb.ryuseiishida.simulation.evacuation.log.LoadLog;
import com.gihutb.ryuseiishida.simulation.evacuation.log.WriterLog;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.CellsMap;
import com.gihutb.ryuseiishida.simulation.evacuation.agent.Agent;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.Box;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.Fire;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.Line;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.Obstacle;
import com.gihutb.ryuseiishida.simulation.evacuation.util.TimeMeasurement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class Environment {
    private int step;
    private CellsMap envCellsMap = new CellsMap(Parameter.SCALE, Parameter.CELL_INTERVAL);
    private ArrayList<Goal> goals = new ArrayList<>(Parameter.GOALS);
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private Fire fire = new Fire(Parameter.FIRE_POINT, 1000);
    private ArrayList<Agent> agentList;
    private int goalAgentNum;
    public boolean agentClearFlag = false;

    private LoadLog loadLog;
    private WriterLog writerLog;

    private final String MAP_PATH = "core/assets/saveMap.txt";

    public Environment() {
        step = 0;
        agentList = new ArrayList<>();
        if (Parameter.MODE.equals("LogSimulation")) {
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
        TimeMeasurement timeMeasurement = new TimeMeasurement();
        timeMeasurement.start();
        if (Parameter.ISWRITELOG) {
            writerLog.writeAgentLog();
            writerLog.writeMacroLog();
        }
        agentList.stream()
                .parallel()
                .forEach(Agent::action);
        ifAgentInGoal();
        ifAgentInFire();
        step++;
        timeMeasurement.stop();
        fire.spreadFire();
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

    //potential
    public CellsMap getEnvCellsMap() {
        return envCellsMap;
    }

    public void spawnObstacle(Vector2 pos) {
        obstacles.add(new Box(pos.x, pos.y, Parameter.CELL_INTERVAL, Parameter.CELL_INTERVAL, envCellsMap));
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
            float x = MathUtils.random(Parameter.INIT_RANDOM_X1, Parameter.INIT_RANDOM_X2);
            float y = MathUtils.random(Parameter.INIT_RANDOM_Y1, Parameter.INIT_RANDOM_Y2);
            Vector2 position = new Vector2(x, y);
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
        this.goalAgentNum = num;
    }

    public Fire getFire() {
        return this.fire;
    }

    public void loadMap() {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(MAP_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] points = line.split(",");
                obstacles.add(
                        new Line(
                                Float.parseFloat(points[0]),
                                Float.parseFloat(points[1]),
                                Float.parseFloat(points[2]),
                                Float.parseFloat(points[3]),
                                envCellsMap));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
