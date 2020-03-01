package com.gihutb.ryuseiishida.simulation.evacuation.analysis;

import com.gihutb.ryuseiishida.simulation.evacuation.util.Parameter;
import com.gihutb.ryuseiishida.simulation.evacuation.agent.Agent;
import com.gihutb.ryuseiishida.simulation.evacuation.agent.Group;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.CellsMap;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LDA {
    private static CellsMap positionMap = new CellsMap(Parameter.SCALE, 1000);
    // group sizeで文書を区切る
    private int perceptionGroupSize = 0;
    private ArrayList<Agent> agentList;
    private Integer outPrintStep;
    private ArrayList<ArrayList<String>> dataList = new ArrayList<>();

    public LDA(ArrayList<Agent> agentList) {
        this.agentList = agentList;
    }

    public LDA(ArrayList<Agent> agentList, int outPrintStep) {
        this.agentList = agentList;
        this.outPrintStep = outPrintStep;
    }

    // 引数stepで文書を区切る
    public void recordStepSplit(int step) {
        int splitStep = 60;
        if (step == 0 || step % splitStep == 0) {
            setDataList(agentList);
        }
        if (outPrintStep == null) {
            return;
        }
        if (step > outPrintStep) {
            outPrintStep = null;
            outPrint("stepSplit_Corpus");
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
        if (step > outPrintStep) {
            outPrintStep = null;
            outPrint("groupSizeSplit_Corpus");
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
        String path = "core/assets/" + fileName;
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
            System.out.println("save corpus data");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CellsMap getPositionMap() {
        return positionMap;
    }
}
