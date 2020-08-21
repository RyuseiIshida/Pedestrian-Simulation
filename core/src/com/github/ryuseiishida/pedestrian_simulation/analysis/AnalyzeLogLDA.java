package com.github.ryuseiishida.pedestrian_simulation.analysis;

import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.Group;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.cell.CellsMap;
import com.github.ryuseiishida.pedestrian_simulation.util.LoadLog;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;
import com.github.ryuseiishida.pedestrian_simulation.util.UtilVector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AnalyzeLogLDA {
    //Python program path
    private static InputStream pyStream;
    //split parameter
    private static CellsMap positionMap = new CellsMap(Parameter.SCALE, 1000);
    private LoadLog loadLog;

    private ArrayList<ArrayList<String>> dataList = new ArrayList<>();

    public AnalyzeLogLDA() {
        //load topic_analysis.py
        pyStream = getClass().getClassLoader().getResourceAsStream("topic_analysis.py");
    }

    public AnalyzeLogLDA(String dataDirPath) {
        loadLog = new LoadLog(dataDirPath);
    }

    public static CellsMap getPositionMap() {
        return positionMap;
    }

    public static void setMapSplitSize(int size_int) {
        positionMap = new CellsMap(Parameter.SCALE, size_int);
    }

    private int getAgentDirection(String dir) {
        Vector2 velocity = UtilVector.strToVector(dir);
        float radian = (float) Math.atan2(velocity.y, velocity.x);
        int degree = (int) Math.toDegrees(radian);
        if (degree < 0) degree += 360;
        if (degree >= 0 && degree <= 45) return 1;
        else if (degree > 45 && degree <= 90) return 2;
        else if (degree > 90 && degree <= 135) return 3;
        else if (degree > 135 && degree <= 180) return 4;
        else if (degree > 180 && degree <= 225) return 5;
        else if (degree > 225 && degree <= 270) return 6;
        else if (degree > 270 && degree <= 315) return 7;
        else if (degree > 315 && degree < 360) return 8;
        throw new IllegalArgumentException("Direction is out of range. [ direction = " + degree + " ]");
    }

    // stepで文書を区切る
    public void recordStepSplit(int splitStep) {
        int endStep = loadLog.getEndStep();
        for (int step = 0; step <= endStep; step++) {
            if (step % splitStep == 0) {
                setDataList(loadLog.getStepLines(step));
            }
        }
        outPrint("step_split_corpus" + splitStep);
    }

    // グループサイズが変わったタイミングで文書を区切る
    public void recordGroupSizeSplit() {
        int endStep = loadLog.getEndStep();
        int perceptionGroupSize = 0;
        for (int step = 0; step <= endStep; step++) {
            ArrayList<Vector2> positions = loadLog.getLogAgentPositions(step);
            ArrayList<ArrayList<Vector2>> agentGroups = Group.getGroup(positions);
            if (agentGroups.size() != perceptionGroupSize) {
                setDataList(loadLog.getStepLines(step));
            }
            perceptionGroupSize = agentGroups.size();
        }
        outPrint("group_size_split_corpus");
    }

    private void setDataList(ArrayList<String> agentStepLog) {
        ArrayList<String> data = new ArrayList<>();
        agentStepLog.forEach(line -> {
            String[] splitLine = line.split(" ");
            String state = splitLine[1];
            String pos = String.valueOf(positionMap.getCells().
                    indexOf(positionMap.getCell(UtilVector.strToVector(splitLine[2]))));
            String dir = String.valueOf(getAgentDirection(splitLine[3]));
            data.add("p" + pos + "d" + dir + state);
        });
        dataList.add(data);
    }

    private void outPrint(String fileName) {
        String path = loadLog.getLogPath() + "/" + fileName + ".txt";
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(path))) {
            for (ArrayList<String> data : dataList) {
                for (String s : data) {
                    bw.append(s);
                    if (!s.equals(data.get(data.size() - 1))) {
                        bw.append(",");
                    }
                }
                bw.newLine();
            }
            System.out.println("save corpus data [" + fileName + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createTopicData() {
        createPythonFile();
        try {
            String currentDir = System.getProperty("user.dir");
            String[] commands = {"python3",loadLog.getLogPath() + "/topic_analysis.py", loadLog.getLogPath()};
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(new File(currentDir));
            Process p = pb.start();
            BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
            System.out.println("Result : " + output.readLine());
            deletePythonFile();
        } catch (IOException e) {
            deletePythonFile();
            e.printStackTrace();
        }
    }

    public void createPythonFile() {
        ArrayList<String> codeLine = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(pyStream));
            String line;
            while ((line = br.readLine()) != null) {
                codeLine.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String createPath = loadLog.getLogPath() + "/topic_analysis.py";
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(createPath))) {
            for (String line : codeLine) {
                bw.append(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deletePythonFile() {
        try {
            String currentDir = System.getProperty("user.dir");
            String[] commands = {"rm", loadLog.getLogPath() + "/topic_analysis.py"};
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(new File(currentDir));
            Process p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
