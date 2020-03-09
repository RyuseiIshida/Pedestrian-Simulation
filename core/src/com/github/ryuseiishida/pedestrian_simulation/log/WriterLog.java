package com.github.ryuseiishida.pedestrian_simulation.log;

import com.github.ryuseiishida.pedestrian_simulation.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.agent.Group;
import com.github.ryuseiishida.pedestrian_simulation.agent.StateTag;
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
            writeAgentInitPosition();
            writeSourceCodeToParameter();
        }
    }

    private void makeDir() {
        new File(path).mkdir();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_hhmm");
        String time = format.format(Calendar.getInstance().getTime());
        path = path + "/" + time;
        new File(path).mkdir();
    }

    public String getPath() {
        return path;
    }

    public void ifWriteLog(boolean writeFlag) {
        if (writeFlag) {
            if (Parameter.IS_WRITE_LOG_AGENT) {
                writeAgentLog();
            }
            if (Parameter.IS_WRITE_LOG_MACRO) {
                writeMacroLog();
            }
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
            String path = this.path + "/Agents" + "/agent" + agent.getID() + ".txt";

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
                    printer.printRecord(csvRecord.get(0), csvRecord.get(1), csvRecord.get(2), csvRecord.get(3), csvRecord.get(4), csvRecord.get(5), csvRecord.get(6), csvRecord.get(7), csvRecord.get(8), csvRecord.get(9), csvRecord.get(10), csvRecord.get(11), csvRecord.get(12), csvRecord.get(13), csvRecord.get(14), csvRecord.get(15));
                }

                String wgoal = agent.getGoal() == null ? "null" : agent.getGoal().toString().replace("\"", "").replace(",", ":");
                printer.printRecord(
                        env.getStep(), //0
                        agent.getStateTag(), //1
                        agent.getPosition().toString().replace("\"", "").replace(",", ":"), //2
                        agent.getVelocity().toString().replace("\"", "").replace(",", ":"), //3
                        agent.getMovePos().toString().replace("\"", "").replace(",", ":"), //4
                        wgoal, //5
                        agent.getFollowAgent(), //6
                        agent.getFollowers().toString().replace(",", ":"), //7
                        agent.getFollowers().size(), //8
                        agent.getPerceptionInViewAgentList().toString().replace(",", ":"), //9
                        agent.getPerceptionInViewAgentList().size(), //10
                        agent.getPerceptionFollowAgentList().toString().replace(",", ":"), //11
                        agent.getPerceptionFollowAgentList().size(), //12
                        agent.getPerceptionContinueStep(), //13
                        agent.getPerceptionContinueDst(), //14
                        agent.getPerceptionAllDst()); //15
                printer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void initWriteAgentLog(String path) {
        try {
            CSVPrinter printer = new CSVPrinter(new FileWriter(path), CSVFormat.DEFAULT);
            printer.printRecord("step",
                    "tag",
                    "position",
                    "velocity",
                    "movePos",
                    "goal",
                    "followAgent",
                    "followers",
                    "followersSize",
                    "p_viewAgent",
                    "p_viewAgentSize",
                    "p_followAgent",
                    "p_followAgentSize",
                    "p_ContinueStep",
                    "p_ContinueDst",
                    "p_AllDst");
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMacroLog() {
        try {
            new File(this.path + "/Macro").mkdir();
            String path = this.path + "/Macro" + "/macro" + ".txt";
            if (!(new File(path).exists())) {
                CSVPrinter printer = new CSVPrinter(new FileWriter(path), CSVFormat.DEFAULT);
                printer.printRecord("step", "goalAgentNum", "groupSize", "follow");
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
                        csvRecord.get(3)
                );
            }
            int follow = 0;
            for (Agent agent : env.getAgentList()) {
                if (agent.getStateTag().equals(StateTag.follow)) {
                    follow++;
                }
            }
            printer.printRecord(
                    env.getStep(), //0
                    env.getGoalAgentNum(), //1
                    Group.getGroupNum(env.getAgentList()), //2
                    follow //3
            );
            printer.close();
            csvParser.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeEntropy(ArrayList<String> logText) {
        String path = "core/assets/entropy.txt";
        try (BufferedWriter br = Files.newBufferedWriter(Paths.get(path))) {
            for (String str : logText) {
                br.append(str);
                br.newLine();
            }
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


