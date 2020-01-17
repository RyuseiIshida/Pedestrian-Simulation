package com.gihutb.ryuseiishida.simulation.evacuation.analysis;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.gihutb.ryuseiishida.simulation.evacuation.Parameter;
import com.gihutb.ryuseiishida.simulation.evacuation.agent.Agent;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.Cell;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.CellsMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class oldLDA {
    private CellsMap positionMap = new CellsMap(Parameter.SCALE, 1000);
    private List<LinkedHashMap> bowList = new ArrayList<>();
    private ArrayList<String> corpus2Value = new ArrayList<>();
    private int splitStep = 60;

    public void setBoW(int step, ArrayList<Agent> agentList) {
        if (step == 0) {
            //setAgentBoW(agentList);
            setAgentBoW2(agentList);
        }
        if (step % splitStep == 0) {
            //setAgentBoW(agentList);
            setAgentBoW2(agentList);
        }
    }


    private void setAgentBoW2(ArrayList<Agent> agentList) {
        for (Agent agent : agentList) {
            String pos = String.valueOf(positionMap.getCells().indexOf(positionMap.getCell(agent.getPosition())));
            String dir = String.valueOf(getAgentDirection(agent));
            corpus2Value.add("p" + pos + "d" + dir);
        }
    }


    private int getAgentDirection(Agent agent) {
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

    public void outPrintBoW() {
        outPrintBoW2();
//        checkBoWList();
//        int step = 0;
//        for (LinkedHashMap<String, Integer> bow : bowList) {
//            outPrintBoW(bow, String.valueOf(step));
//            step += 60;
//        }
    }


    private void outPrintBoW2() {
        String path = "core/assets/corpus.txt";
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(path))) {
            for (String s : corpus2Value) {
                bw.append(s);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void renderCell(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        for (Cell cell : positionMap.getCells()) {
            shapeRenderer.rect(cell.getLeftBottomPoint().x, cell.getLeftBottomPoint().y, cell.getCellInterval(), cell.getCellInterval());
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (Cell cell : positionMap.getCells()) {
            shapeRenderer.line(cell.getLeftBottomPoint(), cell.getLeftTopPoint());
            shapeRenderer.line(cell.getLeftBottomPoint(), cell.getRightBottomPoint());
            shapeRenderer.line(cell.getRightBottomPoint(), cell.getRightTopPoint());
            shapeRenderer.line(cell.getLeftTopPoint(), cell.getRightTopPoint());
        }
        shapeRenderer.end();
    }

    public void renderCellIndex(Batch batch, BitmapFont bitmapFont) {
        batch.begin();
        //batch.draw(texture, 0, 0);
        for (Cell cell : positionMap.getCells()) {
            bitmapFont.draw(batch, String.valueOf(positionMap.getCells().indexOf(cell)), cell.getCenterPoint().x, cell.getCenterPoint().y);
        }
        batch.end();
    }


    public static void main(String[] args) {
        //createDictionary();
        //createCorpus();
    }


//
//
//    private void initBoW(LinkedHashMap<String, Integer> bow) {
//        for (Cell cell : positionMap.getCells()) {
//            String position = "p" + positionMap.getCells().indexOf(cell);
//            for (int i = 1; i <= 8; i++) {
//                String direction = "d" + i;
//                bow.put(position + direction, 0);
//            }
//        }
//    }
//
//    private void setAgentBoW(Agent agent, LinkedHashMap<String, Integer> bow) {
//        String position = "p" + positionMap.getCells().indexOf(positionMap.getCell(agent.getPosition()));
//        String direction = "d" + getAgentDirection(agent);
//        String vocabulary = position + direction;
//        for (Map.Entry<String, Integer> entry : bow.entrySet()) {
//            if (entry.getKey().equals(vocabulary)) {
//                entry.setValue(entry.getValue() + 1);
//            }
//        }
//    }
//
//
//    private void checkBoW(LinkedHashMap<String, Integer> bow) {
//        int counter = 0;
//        for (Integer value : bow.values()) {
//            counter += value;
//            if (value != 0) {
//                System.out.println("value = " + value);
//            }
//        }
//        System.out.println("counter = " + counter);
//        if (counter != 100) {
//            //throw new IllegalArgumentException("counter = " + counter);
//        }
//    }
//
//    private void checkBoWList() {
//        int i = 0;
//        for (LinkedHashMap linkedHashMap : bowList) {
//            System.out.println("bowlist[ " + i + " ]");
//            checkBoW(linkedHashMap);
//            i++;
//        }
//    }
//
//    private void setAgentBoW(ArrayList<Agent> agentList) {
//        LinkedHashMap<String, Integer> bow = new LinkedHashMap<>();
//        initBoW(bow);
//        for (Agent agent : agentList) {
//            setAgentBoW(agent, bow);
//        }
//        bowList.add(bow);
//    }
//
//    ArrayList<String> corpus2Value = new ArrayList<>();
//
//
//    private void outPrintBoW(LinkedHashMap<String, Integer> bow, String fileName) {
//        System.out.println("fileName = " + fileName);
//        String path = "core/assets/corpus/" + fileName + ".txt";
//        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(path))) {
//            for (Map.Entry<String, Integer> entry : bow.entrySet()) {
//                bw.append(entry.getKey() + "," + entry.getValue());
//                bw.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void analyseBoW() {
//        try (BufferedReader br = Files.newBufferedReader(Paths.get("core/assets/corpus/660.txt"))) {
//            String line;
//            int counter = 0;
//            while ((line = br.readLine()) != null) {
//                String[] bow = line.split(",");
//                if (!bow[1].equals("0")) {
//                    System.out.println("b = " + bow[1]);
//                }
//                counter += Integer.parseInt(bow[1]);
//            }
//            System.out.println("counter = " + counter);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private static void createDictionary() {
//        LinkedHashMap<String, Integer> dictionary = new LinkedHashMap<>();
//        String path = "core/assets/corpus/0.txt";
//        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
//            String line;
//            int num = 0;
//            while ((line = br.readLine()) != null) {
//                dictionary.put(line.split(",")[0], num);
//                num++;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        path = "core/assets/dictionary.txt";
//        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(path))) {
//            for (Map.Entry<String, Integer> entry : dictionary.entrySet()) {
//                bw.append(entry.getKey() + ":" + entry.getValue());
//                bw.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static LinkedHashMap<String, Integer> getDictionary() {
//        LinkedHashMap<String, Integer> dictionary = new LinkedHashMap<>();
//        String path = "core/assets/corpus/0.txt";
//        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
//            String line;
//            int num = 0;
//            while ((line = br.readLine()) != null) {
//                dictionary.put(line.split(",")[0], num);
//                num++;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return dictionary;
//    }
//
//    private static void createCorpus() {
//        ArrayList<ArrayList<String>> corpus = new ArrayList<>();
//        ArrayList<String> corpusValue;
//        LinkedHashMap<String, Integer> dictionary = getDictionary();
//        for (File file : getFiles()) {
//            corpusValue = new ArrayList<>();
//            for (String bow : getFileValue(file)) {
//                for (Map.Entry<String, Integer> entry : dictionary.entrySet()) {
//                    if (entry.getKey().equals(bow.split(",")[0])) {
//                        corpusValue.add(entry.getValue() + ":" + bow.split(",")[1]);
//                    }
//                }
//            }
//            corpus.add(corpusValue);
//        }
//        outPrintCorpus(corpus);
//    }
//
//    private static File[] getFiles() {
//        String path = "core/assets/corpus";
//        File dir = new File(path);
//        return dir.listFiles();
//    }
//
//    private static ArrayList<String> getFileValue(File file) {
//        ArrayList<String> lines = new ArrayList<>();
//        try (BufferedReader br = Files.newBufferedReader(Paths.get(file.getPath()))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                lines.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return lines;
//    }
//
//    private static void outPrintCorpus(ArrayList<ArrayList<String>> corpus) {
//        String path = "core/assets/corpus.txt";
//        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(path))) {
//            for (ArrayList<String> line : corpus) {
//                String outLine = "";
//                for (String str : line) {
//                    outLine += str + ",";
//                }
//                outLine = outLine.substring(0, outLine.length() - 1);
//                bw.append(outLine);
//                bw.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
