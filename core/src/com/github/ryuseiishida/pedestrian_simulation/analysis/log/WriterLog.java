package com.github.ryuseiishida.pedestrian_simulation.analysis.log;

import com.github.ryuseiishida.pedestrian_simulation.environment.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.environment.agent.Group;
import com.github.ryuseiishida.pedestrian_simulation.environment.agent.StateTag;
import com.github.ryuseiishida.pedestrian_simulation.analysis.LDA;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WriterLog {
    private String path = "core/assets/out";
    private Environment env;
    private ArrayList<Agent> agents;

    public WriterLog(Environment env) {
        if (Parameter.IS_WRITE_LOG) {
            this.env = env;
            this.agents = env.getAgentList();
            makeDir();
            writeSourceCodeToParameter();
        }
    }

    private void makeDir() {
        new File(path).mkdir();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String time = format.format(Calendar.getInstance().getTime());
        path = path + "/SIM_LOG_" + time;
        new File(path).mkdir();
    }

    public String getPath() {
        return path;
    }

    public void ifWriteLog(boolean writeFlag) {
        if (writeFlag) {
            writeAgentLog();
            writeMacroLog();
        }

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

    public void writeAgentInitPosition() {
        String path = this.path + "/agent_init_position.txt";
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(path))) {
            for (Agent agent : agents) {
                bw.append(agent.getPosition().toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeAgentLog() {
        agents.stream().parallel().forEach(agent -> {
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
                        env.getStep(), //0
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
            for (Agent agent : env.getAgentList()) {
                if (agent.getStateTag().equals(StateTag.follow)) {
                    follow++;
                }
            }

            ArrayList<ArrayList<Agent>> group = Group.getGroup(env.getAgentList());
            StringBuilder groupList = new StringBuilder();
            for (ArrayList<Agent> agents : group) {
                for (Agent agent : agents) {
                    groupList.append(agent).append(":");
                }
                groupList.append("*");
            }
            groupList.setLength(groupList.length() - 2);//末尾削除
            printer.printRecord(
                    env.getStep(), //0
                    env.getGoalAgentNum(), //1
                    groupList.toString(), //2
                    Group.getGroup(env.getAgentList()).size(), //3
                    follow //4
            );
            printer.close();
            csvParser.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeGroup(ArrayList<String> logText) {
        String path = "core/assets/group.txt";
        try (BufferedWriter br = Files.newBufferedWriter(Paths.get(path))) {
            for (String str : logText) {
                br.append(str);
                br.newLine();
            }
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
}


