package com.github.ryuseiishida.pedestrian_simulation.analysis;

import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.agent.StateTag;
import com.github.ryuseiishida.pedestrian_simulation.cell.CellsMap;
import com.github.ryuseiishida.pedestrian_simulation.log.WriterLog;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;
import com.github.ryuseiishida.pedestrian_simulation.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.agent.Group;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LDA {
    private static float METER = 100f;
    private static CellsMap positionMap = new CellsMap(new Vector2(100 * METER, 80 * METER), 1000);
    // group sizeで文書を区切る
    private int perceptionGroupSize = 0;
    private ArrayList<Agent> agentList;
    private Integer outPrintStep;
    private ArrayList<ArrayList<String>> dataList = new ArrayList<>();
    private ArrayList<String> print = new ArrayList<>();

    private String printPath;

    public LDA(ArrayList<Agent> agentList, String printPath) {
        this.agentList = agentList;
        this.printPath = printPath;
    }

    public LDA(ArrayList<Agent> agentList, int outPrintStep, String printPath) {
        this.agentList = agentList;
        this.outPrintStep = outPrintStep;
        this.printPath = printPath;
    }

    // 引数stepで文書を区切る
    public void recordStepSplit(int step) {
        if (step == 0 || step % Parameter.LDA_SPLIT_STEP == 0) {
            setDataList(agentList);
        }
        if (outPrintStep == null) {
            return;
        }
        ArrayList<Agent> goalAgent = new ArrayList<>();
        agentList.stream().forEach(agent -> {
            if (agent.getStateTag().equals(StateTag.moveGoal)) goalAgent.add(agent);
        });
        if (Parameter.END.equals("ESCAPED") && goalAgent.size()==0) {
            outPrintStep = null;
            outPrint("step_split_corpus");
        } else if (Parameter.END.equals("END_STEP") && step >= outPrintStep) {
            outPrintStep = null;
            outPrint("step_split_corpus");
        }
    }

    // グループサイズが変わったタイミングで文書を区切る
    public void recordGroupSizeSplit(int step) {
        ArrayList<ArrayList<Agent>> AgentGroups = Group.getGroup3(agentList);
        if (AgentGroups.size() != perceptionGroupSize) {
            setDataList(agentList);
        }
        perceptionGroupSize = AgentGroups.size();
        if (outPrintStep == null) {
            return;
        }
        ArrayList<Agent> goalAgent = new ArrayList<>();
        agentList.stream().forEach(agent -> {
            if (agent.getStateTag().equals(StateTag.moveGoal)) goalAgent.add(agent);
        });
        if (Parameter.END.equals("ESCAPED") && goalAgent.size()==0) {
            outPrintStep = null;
            outPrint("group_size_split_corpus");
        } else if (Parameter.END.equals("END_STEP") && step >= outPrintStep) {
            outPrintStep = null;
            outPrint("group_size_split_corpus");
        }
    }

    // シミュレーションごと
    private void setDataList(ArrayList<Agent> agentList) {
        ArrayList<String> data = new ArrayList<>();
        for (Agent agent : agentList) {
            String pos = String.valueOf(positionMap.getCells().indexOf(positionMap.getCell(agent.getPosition())));
            String dir = String.valueOf(getAgentDirection(agent));
            String state = agent.getStateTag();
            data.add("p" + pos + "d" + dir + state);
        }
        dataList.add(data);
    }

    public int getAgentDirection(Agent agent) {
        int direction = (int) agent.getDirectionDegree();
        if (direction < 0) {
            direction += 360;
        }
        if (direction >= 0 && direction <= 45) {
            return 1;
        } else if (direction > 45 && direction <= 90) {
            return 2;
        } else if (direction > 90 && direction <= 135) {
            return 3;
        } else if (direction > 135 && direction <= 180) {
            return 4;
        } else if (direction > 180 && direction <= 225) {
            return 5;
        } else if (direction > 225 && direction <= 270) {
            return 6;
        } else if (direction > 270 && direction <= 315) {
            return 7;
        } else if (direction > 315 && direction < 360) {
            return 8;
        }
        throw new IllegalArgumentException("Direction is out of range. [ direction = " + direction + " ]");
    }

    public void outPrint(String fileName) {
        String path = printPath + "/" + fileName + ".txt";
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

    public static CellsMap getPositionMap() {
        return positionMap;
    }
}
