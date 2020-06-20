package com.github.ryuseiishida.pedestrian_simulation.analysis;

import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.cell.CellsMap;
import com.github.ryuseiishida.pedestrian_simulation.analysis.log.LoadLog;
import com.github.ryuseiishida.pedestrian_simulation.util.UtilVector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AnalyzeLogLDA {
    private String loadFilesDir = "core/assets/";
    private String outPrintPath = loadFilesDir;
    private LoadLog loadLog = new LoadLog(loadFilesDir);

    private int splitStep = 30;

    //Parameterのから
    private static float METER = 100f;
    private static CellsMap positionMap = new CellsMap(new Vector2(100 * METER, 80 * METER), 1000);

    // group sizeで文書を区切る
    private int perceptionGroupSize = 0;
    private ArrayList<ArrayList<String>> dataList = new ArrayList<>();

    // stepで文書を区切る
    // TODO LDAクラスとの要素数の違い
    private void recordStepSplit() {
        for (int step = 0; step <= loadLog.endStep(); step++) {
            if (step % splitStep == 0) {
                setDataList(loadLog.getStepLines(step));
            }
        }
        outPrint("step_split_corpus" + splitStep);
    }

    // グループサイズが変わったタイミングで文書を区切る
//    public void recordGroupSizeSplit(int step) {
//        if (AgentGroups.size() != perceptionGroupSize) {
//            setDataList(agentList);
//        }
//        perceptionGroupSize = AgentGroups.size();
//
//        ArrayList<Agent> goalAgent = new ArrayList<>();
//        agentList.stream().forEach(agent -> {
//            if (agent.getStateTag().equals(StateTag.moveGoal)) goalAgent.add(agent);
//        });
//        outPrint("stepGroupSizeSplit_Corpus");
//    }

    private void setDataList(ArrayList<String> agentStepLog) {
        ArrayList<String> data = new ArrayList<>();
        agentStepLog.forEach(line -> {
            String[] splitLine = line.split(",");
            String state = splitLine[1];
            String pos = String.valueOf(positionMap.getCells().
                    indexOf(positionMap.getCell(UtilVector.strToVector(splitLine[2]))));
            String dir = String.valueOf(getAgentDirection(splitLine[3]));
            data.add("p" + pos + "d" + dir + state);
        });
        dataList.add(data);
    }

    private int getAgentDirection(String dir) {
        Vector2 velocity = UtilVector.strToVector(dir);
        float radian = (float) Math.atan2(velocity.y, velocity.x);
        int degree = (int) Math.toDegrees(radian);
        if (degree < 0) {
            degree += 360;
        }
        if (degree >= 0 && degree <= 45) {
            return 1;
        } else if (degree > 45 && degree <= 90) {
            return 2;
        } else if (degree > 90 && degree <= 135) {
            return 3;
        } else if (degree > 135 && degree <= 180) {
            return 4;
        } else if (degree > 180 && degree <= 225) {
            return 5;
        } else if (degree > 225 && degree <= 270) {
            return 6;
        } else if (degree > 270 && degree <= 315) {
            return 7;
        } else if (degree > 315 && degree < 360) {
            return 8;
        }
        throw new IllegalArgumentException("Direction is out of range. [ direction = " + degree + " ]");
    }

    private void outPrint(String fileName) {
        String path = outPrintPath + "/" + fileName + ".txt";
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

    public static void main(String[] args) {
        AnalyzeLogLDA analyzeLogLDA = new AnalyzeLogLDA();
        analyzeLogLDA.recordStepSplit();
    }
}
