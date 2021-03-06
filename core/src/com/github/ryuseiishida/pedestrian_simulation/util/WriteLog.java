package com.github.ryuseiishida.pedestrian_simulation.util;

import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.Goal;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.Obstacle;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class WriteLog {

    private WriteLog(Environment env) {
    }

    public static void writeParameterLog(String dirPath) {
        String filePath = dirPath + "/parameter.txt";
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath))) {
            String parameterLog = ""
                    + "VERSION " + Parameter.VERSION + "\n"
                    + "SCALE.x " + Parameter.SCALE.x + "\n"
                    + "SCALE.y " + Parameter.SCALE.y + "\n"
                    + "BACKGROUND_TEXTURE_SIZE.x " + Parameter.BACKGROUND_TEXTURE_SIZE.x + "\n"
                    + "BACKGROUND_TEXTURE_SIZE.y " + Parameter.BACKGROUND_TEXTURE_SIZE.y + "\n"
                    + "CELL_INTERVAL " + Parameter.CELL_INTERVAL + "\n"
                    + "AGENT_SPEED " + Parameter.AGENT_SPEED + "\n"
                    + "AGENT_RADIUS " + Parameter.AGENT_RADIUS + "\n"
                    + "VIEW_DEGREE " + Parameter.VIEW_DEGREE + "\n"
                    + "VIEW_RADIUS_LENGTH " + Parameter.VIEW_RADIUS_LENGTH + "\n"
                    + "GOAL_POTENTIAL_WEIGHT " + Parameter.GOAL_POTENTIAL_WEIGHT + "\n"
                    + "GOAL_POTENTIAL_RANGE " + Parameter.GOAL_POTENTIAL_RANGE + "\n"
                    + "AGENT_POTENTIAL_WEIGHT " + Parameter.AGENT_POTENTIAL_WEIGHT + "\n"
                    + "AGENT_POTENTIAL_RANGE " + Parameter.AGENT_POTENTIAL_RANGE + "\n"
                    + "OBSTACLE_POTENTIAL_WEIGHT " + Parameter.OBSTACLE_POTENTIAL_WEIGHT + "\n"
                    + "OBSTACLE_POTENTIAL_RANGE " + Parameter.OBSTACLE_POTENTIAL_RANGE + "\n"
                    + "POTENTIAL_DELTA " + Parameter.POTENTIAL_DELTA;
            bw.append(parameterLog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyBackgroundFile(String dirPath) {
        if (Parameter.BACKGROUND_TEXTURE_Path == null || !new File(Parameter.BACKGROUND_TEXTURE_Path).exists()) {
            return;
        }
        String saveFilePath = dirPath + "/background.png";
        try {
            Files.copy(Paths.get(Parameter.BACKGROUND_TEXTURE_Path), Paths.get(saveFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeAgentLog(String saveFilePath) {
        if (Environment.getInstance().getAgentList() == null
                || (Environment.getInstance().getAgentList().size() == 0
                && Environment.getInstance().getEscapedAgentList().size() == 0)) {
            return;
        }
        ArrayList<Agent> logAgentList = new ArrayList<>();
        logAgentList.addAll(Environment.getInstance().getEscapedAgentList());
        logAgentList.addAll(Environment.getInstance().getAgentList());
        logAgentList.parallelStream().forEach(agent -> {
            String path = saveFilePath + "/agent" + agent.getID() + ".txt";
            try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(path))) {
                bw.append("step tag position velocity");
                bw.newLine();
                for (String log : agent.getLogList()) {
                    bw.append(log);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void writeAgentInitLog(String dirPath) {
        String saveFilePath = dirPath + "/spawn_ag.txt";
        if (Environment.getInstance() == null || Environment.getInstance().getAgentList().size() == 0) return;
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(saveFilePath))) {
            for (Agent agent : Environment.getInstance().getAgentList()) {
                bw.append(agent.getID()).append(",");
                bw.append(String.valueOf(agent.getPosition().x)).append(",");
                bw.append(String.valueOf(agent.getPosition().y)).append(",");
                bw.append(String.valueOf(agent.getSpeed()));
                if (agent.getGoal() != null) bw.append(",").append(agent.getGoal().getID());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeObstacleLog(String dirPath) {
        String filePath = dirPath + "/obstacle.txt";
        if (Environment.getInstance() == null || Environment.getInstance().getObstacles().size() == 0) return;
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath))) {
            bw.append(String.valueOf(Parameter.SCALE.x)).append(",");
            bw.append(String.valueOf(Parameter.SCALE.y));
            bw.newLine();
            for (Obstacle obstacle : Environment.getInstance().getObstacles()) {
                bw.append(String.valueOf(obstacle.getStartPoint().x)).append(",");
                bw.append(String.valueOf(obstacle.getStartPoint().y)).append(",");
                bw.append(String.valueOf(obstacle.getEndPoint().x)).append(",");
                bw.append(String.valueOf(obstacle.getEndPoint().y));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeGoalLog(String dirPath) {
        String filePath = dirPath + "/goal.txt";
        if (Environment.getInstance() == null || Environment.getInstance().getGoals().size() == 0) return;
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath))) {
            for (Goal goal : Environment.getInstance().getGoals()) {
                bw.append(goal.getID()).append(",");
                bw.append(String.valueOf(goal.getPositionX())).append(",");
                bw.append(String.valueOf(goal.getPositionY())).append(",");
                bw.append(String.valueOf(goal.getWidth())).append(",");
                bw.append(String.valueOf(goal.getHeight()));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMacroLog() {
        if (Environment.getInstance().getAgentList().size() == 0) return;
        String label = "step,goal_agent_num,group,group_size,follow";
    }

}


