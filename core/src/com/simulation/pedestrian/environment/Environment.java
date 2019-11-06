package com.simulation.pedestrian.environment;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.goal.Goal;
import com.simulation.pedestrian.Parameter;
import com.simulation.pedestrian.agent.Agent;
import com.simulation.pedestrian.agent.Crowd;
import com.simulation.pedestrian.log.LoadLog;
import com.simulation.pedestrian.log.WriterLog;
import com.simulation.pedestrian.obstacle.Box;
import com.simulation.pedestrian.obstacle.Line;
import com.simulation.pedestrian.obstacle.Obstacle;
import com.simulation.pedestrian.potential.PotentialCell;
import com.simulation.pedestrian.potential.PotentialMap;
import com.simulation.pedestrian.util.Tuple;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class Environment {
    private int step;
    private PotentialMap envPotentialMap = Parameter.POTENTIALMAP;
    private ArrayList<Goal> goals = new ArrayList<>(Parameter.GOALS);
    private ArrayList<Obstacle> obstacles = Parameter.ALL_OBSTACLE;
    private ArrayList<Agent> agentList;
    private Crowd crowd;
    private int goalAgentNum;
    public boolean agentClearFlag = false;

    private LoadLog loadLog;
    private WriterLog writerLog;

    private final String MAP_PATH = "core/assets/out/createMap/saveMap.txt";

    public Environment() {
        step = 0;
        agentList = new ArrayList<>();
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

    public void spawnObstacle(Vector2 pos) {
        obstacles.add(new Box(pos.x, pos.y, Parameter.CELL_INTERVAL, Parameter.CELL_INTERVAL, envPotentialMap));
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

    public ArrayList<Agent> getAgentList() {
        return agentList;
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

    public void setGoalAgentNum(int num) {
        this.goalAgentNum = num;
    }

    public Crowd getCrowd() {
        return crowd;
    }

    public void loadMap() {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(MAP_PATH))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] points = line.split(",");
                obstacles.add(
                        new Line(
                                Float.valueOf(points[0]),
                                Float.valueOf(points[1]),
                                Float.valueOf(points[2]),
                                Float.valueOf(points[3]),
                                envPotentialMap));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
