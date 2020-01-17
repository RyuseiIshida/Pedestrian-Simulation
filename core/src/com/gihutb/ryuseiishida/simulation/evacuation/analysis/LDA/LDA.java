package com.gihutb.ryuseiishida.simulation.evacuation.analysis.LDA;

import com.gihutb.ryuseiishida.simulation.evacuation.Parameter;
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
    private static ArrayList<ArrayList<String>> dataList = new ArrayList<>();
    private static ArrayList<ArrayList<String>> dataList3 = new ArrayList<>();
    private static int splitStep = 60;

    public static void record(int step, ArrayList<Agent> agentList) {
        if (step == 0 || step % splitStep == 0) {
            setAgent(agentList);
        }
    }

    // group sizeで文書を区切る
    private static int perceptionGroupSize = 0;

    public static void record2(ArrayList<Agent> agentList) {
        ArrayList<ArrayList<Agent>> AgentGroups = Group.getGroup3(agentList);
        if (AgentGroups.size() != perceptionGroupSize) {
            System.out.print("Groups size = " + AgentGroups.size());
            System.out.println("    record");
            setAgent(agentList);
        } else {
            System.out.println("Groups size = " + AgentGroups.size());
        }
        perceptionGroupSize = AgentGroups.size();
    }

    // agentごと
    public static void record3(int step, ArrayList<Agent> agentList)  {
        if (step == 0 || step % splitStep == 0) {
            setAgent2(agentList);
        }
    }

    // シミュレーションごと

    private static void setAgent(ArrayList<Agent> agentList) {
        ArrayList<String> data = new ArrayList<>();
        for (Agent agent : agentList) {
            String pos = String.valueOf(positionMap.getCells().indexOf(positionMap.getCell(agent.getPosition())));
            String dir = String.valueOf(getAgentDirection(agent));
            String state = agent.getStateTag();
            data.add("p" + pos + "d" + dir + state);
        }
        dataList.add(data);
    }

    private static void setAgent2(ArrayList<Agent> agentList) {
        if(dataList3.isEmpty()){
            for (Agent agent : agentList) {
                dataList3.add(new ArrayList<String>());
            }
        }
        for (Agent agent : agentList) {
            String pos = String.valueOf(positionMap.getCells().indexOf(positionMap.getCell(agent.getPosition())));
            String dir = String.valueOf(getAgentDirection(agent));
            String state = agent.getStateTag();
            dataList3.get(Integer.parseInt(agent.getID())-1).add("p" + pos + "d" + dir + state);
        }
    }

    public static int getAgentDirection(Agent agent) {
        float direction = agent.getDirectionDegree();
        if (direction <= 0 && direction <= 45) {
            return 1;
        }
        if (direction < 45 && direction <= 90) {
            return 2;
        }
        if (direction < 90 && direction <= 135) {
            return 3;
        }
        if (direction < 135 && direction <= 180) {
            return 4;
        }
        if (direction < 180 && direction <= 225) {
            return 5;
        }
        if (direction < 225 && direction <= 270) {
            return 6;
        }
        if (direction < 270 && direction <= 315) {
            return 7;
        }
        if (direction < 315 && direction <= 360) {
            return 8;
        }
        throw new IllegalArgumentException("範囲外");
    }

    public static void outPrint() {
        String path = "core/assets/corpus.txt";
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(path))) {
            for (ArrayList<String> data : dataList) {
                for (String s : data) {
                    bw.append(s);
                    if (s != data.get(data.size() - 1)) {
                        bw.append(",");
                    }
                }
                bw.newLine();
            }
            System.out.println("save corpus data");
        } catch (IOException e) {
            e.printStackTrace();
        }
        outPrint3();
    }
    public static void outPrint3() {
        String path = "core/assets/corpus3.txt";
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(path))) {
            for (ArrayList<String> data : dataList3) {
                for (String s : data) {
                    bw.append(s);
                    if (s != data.get(data.size() - 1)) {
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
