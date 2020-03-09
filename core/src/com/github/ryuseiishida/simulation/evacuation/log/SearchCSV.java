package com.github.ryuseiishida.simulation.evacuation.log;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SearchCSV {
    private final Path path = Paths.get("SimLog/beta=0.3/macro.txt");
    private final int step = 0;
    private final int tag = 1;
    private final int positioin = 2;
    private final int velocity = 3;
    private final int movePos = 4;
    private final int goal = 5;
    private final int followAgent = 6;
    private final int followers = 7;
    private final int followersSize = 8;
    private final int p_viewAgent = 9;
    private final int p_viewAgentSize = 10;
    private final int p_followAgent = 11;
    private final int p_followAgentSize = 12;
    private final int p_ContinueStep = 13;
    private final int p_ContinueDst = 14;
    private final int p_AllDst = 15;

    public void printColumn() {
        int i = -1;
        try (Reader reader = Files.newBufferedReader(path)) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            for (CSVRecord record : csvParser.getRecords()) {
                if(i%100==0) {
                    System.out.println(record.get(1));
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(Path path, Path out) {
        ArrayList<String> strList = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(path)) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            csvParser.getRecords().forEach(record -> {
                strList.add(record.get(25));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            for (String s : strList) {
                bufferedWriter.append(s);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SearchCSV searchCSV = new SearchCSV();
        searchCSV.printColumn();
        //searchCSV.writers();
    }
}
