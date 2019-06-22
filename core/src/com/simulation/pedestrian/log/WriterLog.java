package com.simulation.pedestrian.log;

import com.simulation.pedestrian.agent.Agent;
import com.simulation.pedestrian.agent.StateTag;
import com.simulation.pedestrian.Environment;
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
    String path;
    Environment env;
    ArrayList<Agent> agents;

    public WriterLog(Environment env) {
        this.env = env;
        this.agents = env.getAllAgent();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_hhmm");
        String time = format.format(Calendar.getInstance().getTime());
        path = Paths.get("SimLog/" + time).toString();
        new File(path).mkdir();
        writeSourceCode(path);
        writeAgentPos(path);
    }

    public void writeSourceCode(String path) {
        path += "/SourceCode";
        new File(path).mkdir();
        writeParameter(path);
        writeEnvironment(path);
        writeAgent(path);
    }

    public void writeParameter(String outPath) {
        Path path = Paths.get("core/src/com/simulation/pedestrian/Parameter.java");
        Path out = Paths.get(outPath + "/Parameter.txt");
        List<String> readList = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
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

    public void writeEnvironment(String outPath) {
        Path path = Paths.get("core/src/com/simulation/pedestrian/Environment.java");
        Path out = Paths.get(outPath + "/Envionment.txt");
        List<String> readList = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
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

    public void writeAgent(String outPath) {
        Path path = Paths.get("core/src/com/simulation/pedestrian/agent/agent.java");
        Path out = Paths.get(outPath + "/agent.txt");
        List<String> readList = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
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

    public void writeAgentPos(String outPath) {
        Path out = Paths.get(outPath + "/AgentPos.txt");
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            for (Agent agent : agents) {
                bufferedWriter.append(agent.getPosition().toString().replace("(","").replace(")",""));
                bufferedWriter.newLine();
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
                    printer.printRecord(csvRecord.get(0), csvRecord.get(1), csvRecord.get(2), csvRecord.get(3), csvRecord.get(4), csvRecord.get(5), csvRecord.get(6), csvRecord.get(7), csvRecord.get(8), csvRecord.get(9), csvRecord.get(10), csvRecord.get(11), csvRecord.get(12), csvRecord.get(13), csvRecord.get(14), csvRecord.get(15), csvRecord.get(16), csvRecord.get(17), csvRecord.get(18), csvRecord.get(19), csvRecord.get(20), csvRecord.get(21), csvRecord.get(22), csvRecord.get(23), csvRecord.get(24), csvRecord.get(25), csvRecord.get(26));
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
                        agent.getPerceptionAgentList().toString().replace(",", ":"), //9
                        agent.getPerceptionAgentList().size(), //10
                        agent.getPerceptionFollowAgentList().toString().replace(",", ":"), //11
                        agent.getPerceptionFollowAgentList().size(), //12
                        agent.getPerceptionContinueStep(), //13
                        agent.getPerceptionContinueDst(), //14
                        agent.getPerceptionAllDst(), //15
                        agent.getURandomWalk(), //16
                        agent.getUFollowAgent(), //17
                        agent.getUMoveGoal(), //18
                        agent.getAlpha(), //19
                        agent.getBeta(), //20
                        agent.getGamma(), //21
                        agent.getDelta(), //22
                        agent.getEpsilon(), //23
                        agent.getUtilityRandomWalk(), //24
                        agent.getUtilityFollow(), //25
                        agent.getUtilityMoveGoal()); //26
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
                    "p_AllDst",
                    "u_utilityRandomWalk",
                    "u_utilityFollow",
                    "u_utilityMoveGoal",
                    "alpha", "beta", "gamma", "delta", "epsilon",
                    "utilityRandomWalk", "utilityFollow", "utilityMoveGoal");
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
            for (Agent agent : env.getAllAgent()) {
                if (agent.getStateTag().equals(StateTag.follow)) {
                    follow++;
                }
            }
            printer.printRecord(
                    env.getStep(), //0
                    env.getGoalAgentNum(), //1
                    env.getCrowd().getCrowdNum(), //2
                    follow //3
            );
            printer.close();
            csvParser.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
