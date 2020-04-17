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
    private String logDirPath;
    private ArrayList<File> agentFileList;

    public LoadLog(String dirPath) {
        logDirPath = dirPath;
        setAgentFileList();
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

        File[] files = new File(logDirPath).listFiles(filter);
        this.agentFileList.addAll(Arrays.asList(files));
    }

    public ArrayList<File> getAgentFileList() {
        return agentFileList;
    }

    public int endStep() {
        int endStep = 0;
        String path = logDirPath + "macro.txt";
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

    private void print(ArrayList<Vector2> vec) {
        for (Vector2 vector2 : vec) {
            System.out.println("vector2 = " + vector2);
        }
    }

}
