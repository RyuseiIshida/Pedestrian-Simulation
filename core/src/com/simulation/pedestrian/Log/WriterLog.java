package com.simulation.pedestrian.Log;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Agent.Agent;
import com.simulation.pedestrian.Agent.StateTag;
import com.simulation.pedestrian.Environment;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class WriterLog {
    String path;
    Environment env;
    ArrayList<Agent> agents;

    public WriterLog(Environment env) {
        this.env = env;
        this.agents = env.getAgents();
        path = Paths.get("core/src/com/simulation/pedestrian/Log/" + LocalDateTime.now().toString()).toAbsolutePath().toString();
        new File(path).mkdir();
        //writeParameter();
    }

    //path test
    public WriterLog() {
        path = Paths.get("core/src/com/simulation/pedestrian/Log/" + LocalDateTime.now().toString()).toAbsolutePath().toString();
        System.out.println("path = " + path);
        //new File(path ).mkdir();
        //new File(path).mkdir();
    }

    public void writeAgentLog() {
        for (Agent agent : agents) {
            try {
                String path = this.path + "/agent" + agent.getID() + ".txt";
                if (!(new File(path).exists())) {
                    CSVPrinter printer = new CSVPrinter(new FileWriter(path), CSVFormat.DEFAULT);
                    printer.printRecord("step", "tag", "position", "velocity", "movePos", "goal", "followAgent", "followers");
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
                            csvRecord.get(4),
                            csvRecord.get(5),
                            csvRecord.get(6),
                            csvRecord.get(7)
                    );
                }
                printer.printRecord(
                        env.getStep(), //0
                        agent.getStateTag(), //1
                        agent.getPosition().toString().replace("\"", "").replace(",", ":"), //2
                        agent.getVelocity().toString().replace("\"", "").replace(",", ":"), //3
                        agent.getMovePos().toString().replace("\"", "").replace(",", ":"), //4
                        agent.getGoal().toString().replace("\"", "").replace(",", ":"), //5
                        agent.getFollowAgent(), //6
                        agent.getFollowers().toString().replace(",", ":")); //7
                printer.close();
                csvParser.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeEnvLog() {
        try {
            String path = this.path + "/env" + ".txt";
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
                if(agent.getStateTag().equals(StateTag.follow)){
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

    public void writeParameter() {
        try {
            CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(path), CSVFormat.DEFAULT);
            //リフレクションを使う
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WriterLog writerLog = new WriterLog();
    }
}
