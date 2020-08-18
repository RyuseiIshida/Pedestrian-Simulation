package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.analysis.AnalyzeLogLDA;
import com.github.ryuseiishida.pedestrian_simulation.controller.GdxController;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.agent.StateTag;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.cell.Cell;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class RenderTopic {
    private static int topicK;
    private static int selectTopicNumber = 1;
    private static String ldaFilePath;

    private static boolean renderTopicRegionFlag = false;
    private static boolean renderCellFlag = false;
    private static Camera camera = GdxController.getCamera();
    private static ShapeRenderer shapeRenderer = GdxController.getShapeRenderer();

    public RenderTopic() {
        if (renderTopicRegionFlag) topicRegion();
        if (renderCellFlag) cell();
    }

    public static void setRenderTopicRegionFlag(boolean renderTopicRegionFlag) {
        RenderTopic.renderTopicRegionFlag = renderTopicRegionFlag;
    }

    public static void setRenderCellFlag(boolean renderCellFlag) {
        RenderTopic.renderCellFlag = renderCellFlag;
    }

    public static void setLdaFilePath(String ldaFilePath) {
        System.out.println(ldaFilePath);
        String topicFileName = ldaFilePath.split("/")[ldaFilePath.split("/").length-1];
        topicK = Integer.parseInt(topicFileName.split("k")[1]);
        RenderTopic.ldaFilePath = ldaFilePath;
    }

    public static void cell() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        for (Cell cell : AnalyzeLogLDA.getPositionMap().getCells()) {
            shapeRenderer.line(cell.getLeftBottomPoint(), cell.getLeftTopPoint());
            shapeRenderer.line(cell.getLeftBottomPoint(), cell.getRightBottomPoint());
            shapeRenderer.line(cell.getRightBottomPoint(), cell.getRightTopPoint());
            shapeRenderer.line(cell.getLeftTopPoint(), cell.getRightTopPoint());
        }
        shapeRenderer.end();
    }

    private static ArrayList<String> readFileTopic() {
        ArrayList<String> topic = new ArrayList<>();
        String path = ldaFilePath + "/group_topic" + selectTopicNumber + ".txt";
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                topic.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return topic;
    }

    public static void topicRegion() {
        for (String s : readFileTopic()) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(0, 1, 0, Float.parseFloat(s.split(",")[1]) * 10 * 2));
            //shapeRenderer.setColor(new Color(0, 1, 0, 0.1f));
            int pos = Integer.parseInt(s.split("d")[0].replace("p", ""));
            int dir = Integer.parseInt(s.split("d")[1].substring(0, 1));
            float x = AnalyzeLogLDA.getPositionMap().getCells().get(pos).getCenterPoint().x;
            float y = AnalyzeLogLDA.getPositionMap().getCells().get(pos).getCenterPoint().y;
            String tag = s.split(",")[0].split("d")[1].substring(1);
            shapeRenderer.circle(x, y, 500);
            shapeRenderer.end();
            topicLine(x, y, dir, tag);
        }
    }

    public static void topicLine(float x, float y, int direction, String tag) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        if (StateTag.follow.equals(tag)) {
            shapeRenderer.setColor(Color.BLUE);
        }
        if (StateTag.moveGoal.equals(tag)) {
            shapeRenderer.setColor(Color.RED);
        }
        switch (direction) {
            case 1:
                shapeRenderer.arc(x, y, 500, 22.5f, 5);
                break;
            case 2:
                shapeRenderer.arc(x, y, 500, 67.5f, 5);
                break;
            case 3:
                shapeRenderer.arc(x, y, 500, 112.5f, 5);
                break;
            case 4:
                shapeRenderer.arc(x, y, 500, 157.5f, 5);
                break;
            case 5:
                shapeRenderer.arc(x, y, 500, 200.5f, 5);
                break;
            case 6:
                shapeRenderer.arc(x, y, 500, 247.5f, 5);
                break;
            case 7:
                shapeRenderer.arc(x, y, 500, 292.5f, 5);
                break;
            case 8:
                shapeRenderer.arc(x, y, 500, 337.5f, 5);
                break;
        }
        shapeRenderer.end();
    }

    public static int getSelectTopicNumber() {
        return selectTopicNumber;
    }

    public static void setSelectTopicNumber(int topicNum) {
        if (topicNum >= 1 && topicNum <= topicK) {
            selectTopicNumber = topicNum;
        }
    }

    public static int getTopicK() {
        return topicK;
    }
}
