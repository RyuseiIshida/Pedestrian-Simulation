package com.github.ryuseiishida.pedestrian_simulation.util;

import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.Goal;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.Line;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class LoadLog {
    private static Environment environment;
    private String simulationLogPath;
    private ArrayList<File> agentFileList;

    public LoadLog(Environment environment) {
        this.environment = environment;
    }

    public LoadLog(Environment environment, String loadPath) {
        this.environment = environment;
        simulationLogPath = loadPath;
        setAgentFileList(loadPath);
    }

    public LoadLog(String loadPath) {
        simulationLogPath = loadPath;
        setAgentFileList(loadPath);
    }

    private void setAgentFileList(String dirPath) {
        agentFileList = new ArrayList<>();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File file, String str) {
                if (str.indexOf("agent") != -1) return true;
                 else return false;
            }
        };
        File[] files = new File(dirPath).listFiles(filter);
        this.agentFileList.addAll(Arrays.asList(files));
    }

    public int endStep() {
        int endStep = 0;
        String path = simulationLogPath + "/macro.txt";
        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
            String line;
            br.readLine(); //ヘッダーを抜かす処理
            while ((line = br.readLine()) != null) {
                endStep++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return endStep;
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

    private String getStepLine(File file, int step) {
        try (BufferedReader br = Files.newBufferedReader(file.toPath())) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.split(",")[0].equals(String.valueOf(step))) {
                    return line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setAgents(String filePath) {
        for (File AgentLogFile : agentFileList) {
            environment.addAgent(new Agent(AgentLogFile, environment));
        }
    }

    public static void setInitAgent(String dirPath) {
        String filePath = dirPath + "/spawn_agents.txt";
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
