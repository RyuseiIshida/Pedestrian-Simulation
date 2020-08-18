package com.github.ryuseiishida.pedestrian_simulation.util;

import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.controller.GdxController;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.Goal;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.Line;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadLog {
    private static Environment environment;
    private String simulationLogPath;
    private ArrayList<File> agentFileList;

    public LoadLog(Environment environment) {
        this.environment = environment;
    }

    ArrayList<ArrayList<String>> logAgentListLines = new ArrayList<>();

    public LoadLog(Environment environment, String loadPath) {
        this.environment = environment;
        simulationLogPath = loadPath;
        agentFileList = searchAgentFileList(loadPath);
    }

    public LoadLog(String loadPath) {
        simulationLogPath = loadPath;
        agentFileList = searchAgentFileList(loadPath);
    }

    public static void setParameter(String dirPath) {
        String filePath = dirPath + "/parameter.txt";
        if (!new File(filePath).exists()) return;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                float value = Float.parseFloat(line.split(" ")[1]);
                if(line.contains("SCALE.x")) Parameter.SCALE.x = value;
                if(line.contains("SCALE.y")) Parameter.SCALE.y = value;
                if(line.contains("BACKGROUND_TEXTURE_SIZE.x")) Parameter.BACKGROUND_TEXTURE_SIZE.x = value;
                if(line.contains("BACKGROUND_TEXTURE_SIZE.y")) Parameter.BACKGROUND_TEXTURE_SIZE.y = value;
                if(line.contains("CELL_INTERVAL")) Parameter.CELL_INTERVAL = (int) value;
                if(line.contains("AGENT_SPEED")) Parameter.AGENT_SPEED = value;
                if(line.contains("AGENT_RADIUS")) Parameter.AGENT_RADIUS = value;
                if(line.contains("VIEW_DEGREE")) Parameter.VIEW_DEGREE = value;
                if(line.contains("VIEW_RADIUS_LENGTH")) Parameter.VIEW_RADIUS_LENGTH = value;
                if(line.contains("GOAL_POTENTIAL_WEIGHT")) Parameter.GOAL_POTENTIAL_WEIGHT = value;
                if(line.contains("GOAL_POTENTIAL_RANGE")) Parameter.GOAL_POTENTIAL_RANGE = value;
                if(line.contains("AGENT_POTENTIAL_WEIGHT")) Parameter.AGENT_POTENTIAL_WEIGHT = value;
                if(line.contains("AGENT_POTENTIAL_RANGE")) Parameter.AGENT_POTENTIAL_RANGE = value;
                if(line.contains("OBSTACLE_POTENTIAL_WEIGHT")) Parameter.OBSTACLE_POTENTIAL_WEIGHT = value;
                if(line.contains("OBSTACLE_POTENTIAL_RANGE")) Parameter.OBSTACLE_POTENTIAL_RANGE = value;
                if(line.contains("POTENTIAL_DELTA")) Parameter.POTENTIAL_DELTA = value;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setBackgroundTexture(String dirPath) {
        String filePath = dirPath + "/background.png";
        if(!new File(filePath).exists()) return;
        Parameter.BACKGROUND_TEXTURE_Path = filePath;
        GdxController.setBackgroundTexture(filePath);
    }

    public static ArrayList<File> searchAgentFileList(String dirPath) {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File file, String str) {
                if (str.indexOf("agent") != -1) return true;
                 else return false;
            }
        };
        File[] files = new File(dirPath).listFiles(filter);
        return new ArrayList<File>(Arrays.asList(files));
    }

    public String getLogPath() {
        return simulationLogPath;
    }

    public ArrayList<String> getStepLines(int step) {
        ArrayList<String> out = new ArrayList<>();
        agentFileList.forEach(agentFile -> {
            String line = getStepLine(agentFile, step);
            if (line != null) {
                out.add(line);
            }
        });
        return out;
    }

    public int getEndStep() {
        int endStep = 0;
        for (File file : agentFileList) {
            try (BufferedReader br = Files.newBufferedReader(file.toPath())) {
                String line = null;
                int step = -1; //header
                while((line = br.readLine()) != null) {
                    step += 1;
                }
                if(endStep < step) {
                    endStep = step;
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return endStep;
    }

    private String getStepLine(File file, int step) {
        try (BufferedReader br = Files.newBufferedReader(file.toPath())) {
            String line ;
            while ((line = br.readLine()) != null) {
                if (line.split(" ")[0].equals(String.valueOf(step))) {
                    return line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setLogAgentListLines() {
        for (File file : agentFileList) {
            ArrayList<String> lines = new ArrayList<>();
            try(BufferedReader br = Files.newBufferedReader(file.toPath())) {
                String line = null;
                if(br.readLine() == br.readLine()) return; //header
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
                logAgentListLines.add(lines);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Vector2> getLogAgentPositions(int step) {
        if(logAgentListLines.size() == 0) {
            setLogAgentListLines();
        }
        ArrayList<Vector2> positions = new ArrayList<>();
        for (ArrayList<String> logLines : logAgentListLines) {
            if (logLines.size()-1 >= step) {
                positions.add(UtilVector.strToVector(logLines.get(step).split(" ")[2]));
            }
        }
        return positions;
    }

    public static void setInitAgent(String dirPath) {
        String filePath = dirPath + "/spawn_ag.txt";
        if (environment == null || !new File(filePath).exists()) return;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] element = line.split(",");
                if (element.length == 4) {
                    environment.addAgent(new Agent(element[0],
                            environment,
                            new Vector2(Float.parseFloat(element[1]), Float.parseFloat(element[2])),
                            Float.parseFloat(element[3])
                    ));
                } else if (element.length == 5) {
                    environment.addAgent(new Agent(
                            element[0],
                            environment,
                            new Vector2(Float.parseFloat(element[1]), Float.parseFloat(element[2])),
                            Float.parseFloat(element[3]),
                            environment.getGoal(element[4])
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAgents(String filePath) {
        if (agentFileList.size() == 0) return;
        for (File AgentLogFile : agentFileList) {
            environment.addAgent(new Agent(AgentLogFile, environment));
        }
    }

    public static void setObstacle(String dirPath) {
        String filePath = dirPath + "/obstacle.txt";
        if (environment == null || !new File(filePath).exists()) return;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] element = line.split(",");
                environment.addObstacle(new Line(
                        Float.parseFloat(element[0]),
                        Float.parseFloat(element[1]),
                        Float.parseFloat(element[2]),
                        Float.parseFloat(element[3]),
                        Parameter.ENV_CELLS_MAP));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setGoal(String dirPath) {
        String filePath = dirPath + "/goal.txt";
        if (environment == null || !new File(filePath).exists()) return;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] element = line.split(",");
                environment.addGoal(new Goal(
                        element[0],
                        Float.parseFloat(element[1]),
                        Float.parseFloat(element[2]),
                        Float.parseFloat(element[3]),
                        Float.parseFloat(element[4])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
