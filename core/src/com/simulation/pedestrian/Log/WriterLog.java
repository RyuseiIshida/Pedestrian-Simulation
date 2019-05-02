package com.simulation.pedestrian.Log;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Agent.Agent;
import com.simulation.pedestrian.Agent.StateTag;
import com.simulation.pedestrian.Environment;
import com.simulation.pedestrian.Parameter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

public class WriterLog {
    String path;
    Environment env;
    ArrayList<Agent> agents;

    public WriterLog(Environment env) {
        this.env = env;
        this.agents = env.getAgents();
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd_hhmm");
        String time = format.format(Calendar.getInstance().getTime());
        path = Paths.get("SimLog/" + time).toString();
        new File(path).mkdir();
        writeSourceCode(path);
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
        Path path = Paths.get("core/src/com/simulation/pedestrian/Agent/Agent.java");
        Path out = Paths.get(outPath + "/Agent.txt");
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
                    printer.printRecord(csvRecord.get(0), csvRecord.get(1), csvRecord.get(2), csvRecord.get(3), csvRecord.get(4), csvRecord.get(5), csvRecord.get(6), csvRecord.get(7),csvRecord.get(8),csvRecord.get(9), csvRecord.get(10), csvRecord.get(11), csvRecord.get(12), csvRecord.get(13), csvRecord.get(14), csvRecord.get(15), csvRecord.get(16), csvRecord.get(17), csvRecord.get(18), csvRecord.get(19),csvRecord.get(20));
                }

                String wgoal = agent.getGoal() == null ? "null" : agent.getGoal().toString().replace("\"", "").replace(",", ":");
                printer.printRecord(
                        env.getStep(), //0
                        agent.getStateTag(), //1
                        agent.getPosition().toString().replace("\"", "").replace(",", ":"), //2
                        agent.getVelocity().toString().replace("\"", "").replace(",", ":"), //3
                        agent.getMovePos().toString().replace("\"", "").replace(",", ":"), //4
                        //agent.getGoal().toString().replace("\"", "").replace(",", ":"), //5
                        wgoal, //5
                        agent.getFollowAgent(), //6
                        agent.getFollowers().toString().replace(",", ":"), //7
                        agent.getPerceptionAgentList().toString().replace(",", ":"), //8
                        agent.getPerceptionFollowAgentList().toString().replace(",", ":"), //9
                        agent.getPerceptionContinueStep(), //10
                        agent.getPerceptionContinueDst(), //11
                        agent.getPerceptionAllDst(), //12
                        agent.getAlpha(), //13
                        agent.getBeta(), //14
                        agent.getGamma(), //15
                        agent.getDelta(), //16
                        agent.getEpsilon(), //17
                        agent.getUtilityRandomWalk(), //18
                        agent.getUtilityFollow(), //19
                        agent.getUtilityMoveGoal()); //20

                printer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void initWriteAgentLog(String path) {
        try {
            CSVPrinter printer = new CSVPrinter(new FileWriter(path), CSVFormat.DEFAULT);
            printer.printRecord("step", "tag", "position", "velocity", "movePos", "goal",
                    "followAgent", "followers", "p_viewAgent", "p_followAgent", "p_ContinueStep",
                    "p_ContinueDst", "p_AllDst", "alpha", "beta", "gamma", "delta", "epsilon",
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
            for (Agent agent : env.getAgents()) {
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
