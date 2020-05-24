package com.github.ryuseiishida.pedestrian_simulation.log;

import com.badlogic.gdx.math.Vector2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class LoadLog {
    private String simulationLogPath;
    private ArrayList<File> agentFileList;

    public LoadLog(String loadPath) {
        setSimulationLogPath(loadPath);
        setAgentFileList();
    }

    private void setSimulationLogPath(String parentPath) {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File file, String str) {
                //指定文字列でフィルタする
                if (str.indexOf("SIM_LOG") != -1) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        File[] files = new File(parentPath).listFiles(filter);
        if (files.length == 0) {
            throw new IllegalArgumentException("simulation log file is not included");
        } else if (files.length != 1) {
            throw new IllegalArgumentException("Multiple log files detected.\n" +
                    "There should be only one specified directory.");
        }
        simulationLogPath = files[0].getPath();
        System.out.println(simulationLogPath);
    }

    private void setAgentFileList() {
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

        File[] files = new File(simulationLogPath).listFiles(filter);
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

    private void print(ArrayList<Vector2> vec) {
        for (Vector2 vector2 : vec) {
            System.out.println("vector2 = " + vector2);
        }
    }

    public String getSimulationLogPath() {
        return simulationLogPath;
    }

    public static void main(String[] args) {
        LoadLog loadlog = new LoadLog("core/assets/out/2020-04-07_075459/");
        for (String stepLine : loadlog.getStepLines(0)) {
            System.out.println(stepLine);
        }
    }

}
