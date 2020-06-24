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
        this.environment = environment;
        simulationLogPath = loadPath;
        setAgentFileList(loadPath);
    }

    private void setAgentFileList(String dirPath) {
        agentFileList = new ArrayList<>();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File file, String str) {
                //指定文字列でフィルタする
                if (str.indexOf("agent") != -1) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        File[] files = new File(dirPath).listFiles(filter);
        this.agentFileList.addAll(Arrays.asList(files));
    }

    public ArrayList<File> getAgentFileList() {
        return agentFileList;
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

    public int getAgentNum() {
        return agentFileList.size();
    }

    public ArrayList<Vector2> getPosList(int ID) {
        ArrayList<Vector2> posList = new ArrayList<>();
        String agentID = "agent" + ID + ".txt";
        String agentPath = "";
        for (File file : agentFileList) {
            if (file.getPath().contains(agentID)) {
                agentPath = file.getPath();
                break;
            }
        }
        try {
            Reader reader = Files.newBufferedReader(Paths.get(agentPath));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            for (CSVRecord csvRecord : csvParser) {
                if (!csvRecord.get(2).equals("position")) {
                    String[] pos = csvRecord.get(2).replace("(", "").replace(")", "").split(":");
                    posList.add(new Vector2(Float.parseFloat(pos[0]), Float.parseFloat(pos[1])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posList;
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
        //throw new IllegalArgumentException("line you specified could not be found");
    }

    public void setAgents(String filePath) {
        for (File AgentLogFile : getAgentFileList()) {
            environment.addAgent(new Agent(AgentLogFile, environment));
        }
    }

    public static void setInitAgent(String filePath) {
        if (environment == null || !new File(filePath).exists()) return;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] element = line.split(",");
                if(element.length == 4) {
                    environment.addAgent(new Agent(element[0],
                            environment,
                            new Vector2(Float.parseFloat(element[1]), Float.parseFloat(element[2])),
                            Float.parseFloat(element[3])
                            ));
                } else if(element.length == 5) {
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

    public static void setObstacle(String filePath) {
        if (environment == null || !new File(filePath).exists()) return;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] points = line.split(",");
                environment.addObstacle(new Line(
                        Float.parseFloat(points[0]),
                        Float.parseFloat(points[1]),
                        Float.parseFloat(points[2]),
                        Float.parseFloat(points[3]),
                        Parameter.ENV_CELLS_MAP));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setGoal(String filePath) {
        if (environment == null || !new File(filePath).exists()) return;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] points = line.split(",");
                environment.addGoal(new Goal(
                        points[0],
                        Float.parseFloat(points[1]),
                        Float.parseFloat(points[2]),
                        Float.parseFloat(points[3]),
                        Float.parseFloat(points[4])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
