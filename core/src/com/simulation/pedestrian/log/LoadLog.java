package com.simulation.pedestrian.log;

import com.badlogic.gdx.math.Vector2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LoadLog {
    private String strPath = "core/src/com/simulation/pedestrian/log/LoadMode/";
    private ArrayList<File> agentFileList;

    public LoadLog() {
        setAgentFileList();
    }

    private void setAgentFileList() {
        agentFileList = new ArrayList<>();
        File agentFileList = new File(strPath + "Agents");
        for (File listFile : agentFileList.listFiles()) {
            this.agentFileList.add(listFile);
        }
    }

    public ArrayList<File> getAgentFileList() {
        return agentFileList;
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
                    String pos[] = csvRecord.get(2).replace("(", "").replace(")", "").split(":");
                    posList.add(new Vector2(Float.valueOf(pos[0]), Float.valueOf(pos[1])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posList;
    }

    private void print(ArrayList<Vector2> vec) {
        for (Vector2 vector2 : vec) {
            System.out.println("vector2 = " + vector2);
        }
    }

    public static void main(String[] args) {
        LoadLog loadLog = new LoadLog();
        loadLog.print(loadLog.getPosList(1));
    }
}
