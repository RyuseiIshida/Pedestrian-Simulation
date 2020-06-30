package com.github.ryuseiishida.pedestrian_simulation.util;

import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.Group;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.StateTag;
import com.github.ryuseiishida.pedestrian_simulation.analysis.LDA;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.Goal;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.Obstacle;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WriteLog {
    private String path = Parameter.WRITE_LOG_PATH;
    private static Environment environment;
    private boolean isInitialized = false;

    public WriteLog(Environment env) {
        environment = env;
    }

    public String getPath() {
        return path;
    }

    public void writeLog() {
        if (Parameter.IS_WRITE_LOG) {
            if (!isInitialized) {
                initialize();
                isInitialized = true;
            }
            writeAgentLog();
            writeMacroLog();
        }
    }

    public void initialize() {
        path = Parameter.WRITE_LOG_PATH;
        new File(path).mkdir();
//        writeSourceCodeToParameter();
        writeObstacleLog(path + "/obstacle");
        writeGoalLog(path + "/goal");
    }

    public void writeSourceCodeToParameter() {
        Path codePath = Paths.get("core/src/com/github/ryuseiishida/pedestrian_simulation/util/Parameter.java");
        Path out = Paths.get(path + "/Parameter.txt");
        List<String> readList = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(codePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                readList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            for (String s : readList) {
                bufferedWriter.append(s);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeAgentLog() {
        environment.getAgentList().stream().parallel().forEach(agent -> {
            String path = this.path + "/agent" + agent.getID() + ".txt";
            if (!(new File(path).exists())) {
                initWriteAgentLog(path);
            }
            try {
                //パース
                CSVParser csvParser = new CSVParser(Files.newBufferedReader(Paths.get(path)), CSVFormat.DEFAULT);
                ArrayList<CSVRecord> csvRecords = new ArrayList<>();
                for (CSVRecord record : csvParser) {
                    csvRecords.add(record);
                }
                csvParser.close();
                //書き込み
                CSVPrinter printer = new CSVPrinter(new FileWriter(path), CSVFormat.DEFAULT);
                for (CSVRecord csvRecord : csvRecords) {
                    printer.printRecord(csvRecord.get(0), csvRecord.get(1), csvRecord.get(2), csvRecord.get(3));
                }
                printer.printRecord(
                        environment.getStep(), //0
                        agent.getStateTag(), //1
                        agent.getPosition().toString().replace(",", ":"),
                        agent.getVelocity().toString().replace(",", ":")
                ); //2
                printer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initWriteAgentLog(String path) {
        try {
            CSVPrinter printer = new CSVPrinter(new FileWriter(path), CSVFormat.DEFAULT);
            printer.printRecord("step", "tag", "position", "velocity");
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMacroLog() {
        if (environment.getAgentList().size() == 0) {
            return;
        }
        try {
            String path = this.path + "/macro" + ".txt";
            if (!(new File(path).exists())) {
                CSVPrinter printer = new CSVPrinter(new FileWriter(path), CSVFormat.DEFAULT);
                printer.printRecord("step", "goal_agent_num", "group", "group_size", "follow");
                printer.close();
            }
            Reader reader = Files.newBufferedReader(Paths.get(path));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            ArrayList<CSVRecord> csvRecords = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                csvRecords.add(record);
            }
            CSVPrinter printer = new CSVPrinter(new FileWriter(path), CSVFormat.DEFAULT);
            for (CSVRecord csvRecord : csvRecords) {
                printer.printRecord(
                        csvRecord.get(0),
                        csvRecord.get(1),
                        csvRecord.get(2),
                        csvRecord.get(3),
                        csvRecord.get(4)
                );
            }
            int follow = 0;
            for (Agent agent : environment.getAgentList()) {
                if (agent.getStateTag().equals(StateTag.follow)) {
                    follow++;
                }
            }

            ArrayList<ArrayList<Agent>> group = Group.getGroup(environment.getAgentList());
            StringBuilder groupList = new StringBuilder();
            for (ArrayList<Agent> agents : group) {
                for (Agent agent : agents) {
                    groupList.append(agent).append(":");
                }
                groupList.append("*");
            }
            groupList.setLength(groupList.length() - 2);//末尾削除
            printer.printRecord(
                    environment.getStep(), //0
                    environment.getGoalAgentNum(), //1
                    groupList.toString(), //2
                    Group.getGroup(environment.getAgentList()).size(), //3
                    follow //4
            );
            printer.close();
            csvParser.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLDACorpus(LDA lda, ArrayList<ArrayList<String>> dataList, String fileName) {
        String path = this.path + fileName + ".txt";
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

    public static void writeAgentInitLog(String saveFilePath) {
        if (environment == null || environment.getAgentList().size() == 0) return;
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(saveFilePath + ".ag"))) {
            for (Agent agent : environment.getAgentList()) {
                bw.append(agent.getID()).append(",");
                bw.append(String.valueOf(agent.getPosition().x)).append(",");
                bw.append(String.valueOf(agent.getPosition().y)).append(",");
                bw.append(String.valueOf(agent.getSpeed()));
                if (agent.getGoal() != null) bw.append(",").append(agent.getGoal().getID());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeObstacleLog(String saveFilePath) {
        if (environment == null || environment.getObstacles().size() == 0) return;
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(saveFilePath + ".obs"))) {
            bw.append(String.valueOf(Parameter.SCALE.x)).append(",");
            bw.append(String.valueOf(Parameter.SCALE.y));
            bw.newLine();
            for (Obstacle obstacle : environment.getObstacles()) {
                bw.append(String.valueOf(obstacle.getStartPoint().x)).append(",");
                bw.append(String.valueOf(obstacle.getStartPoint().y)).append(",");
                bw.append(String.valueOf(obstacle.getEndPoint().x)).append(",");
                bw.append(String.valueOf(obstacle.getEndPoint().y));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeGoalLog(String saveFilePath) {
        if (environment == null || environment.getGoals().size() == 0) return;
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(saveFilePath + ".gl"))) {
            for (Goal goal : environment.getGoals()) {
                bw.append(goal.getID()).append(",");
                bw.append(String.valueOf(goal.getPositionX())).append(",");
                bw.append(String.valueOf(goal.getPositionY())).append(",");
                bw.append(String.valueOf(goal.getWidth())).append(",");
                bw.append(String.valueOf(goal.getHeight()));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


