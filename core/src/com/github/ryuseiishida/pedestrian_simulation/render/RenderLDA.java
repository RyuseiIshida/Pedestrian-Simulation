package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.analysis.LDA;
import com.github.ryuseiishida.pedestrian_simulation.cell.Cell;
import com.github.ryuseiishida.pedestrian_simulation.agent.StateTag;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class RenderLDA {
    private static boolean renderFlag = false;
    private static int numTopics = 0;

    public RenderLDA(Batch batch, BitmapFont bitmapFont, ShapeRenderer shapeRenderer, Camera camera) {
        if (renderFlag) {
            renderTopicRegion(shapeRenderer, camera);
            //cellIndex(shapeRenderer, camera, batch, bitmapFont);
            numTopics(shapeRenderer, camera, batch, bitmapFont);
        }
    }

    public static void switchRenderFlag() {
        renderFlag = !renderFlag;
    }


    public static void cell(ShapeRenderer shapeRenderer, Camera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        for (Cell cell : LDA.getPositionMap().getCells()) {
            shapeRenderer.rect(cell.getLeftBottomPoint().x, cell.getLeftBottomPoint().y, cell.getCellInterval(), cell.getCellInterval());
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (Cell cell : LDA.getPositionMap().getCells()) {
            shapeRenderer.line(cell.getLeftBottomPoint(), cell.getLeftTopPoint());
            shapeRenderer.line(cell.getLeftBottomPoint(), cell.getRightBottomPoint());
            shapeRenderer.line(cell.getRightBottomPoint(), cell.getRightTopPoint());
            shapeRenderer.line(cell.getLeftTopPoint(), cell.getRightTopPoint());
        }
        shapeRenderer.end();
    }

    public static void cellIndex(ShapeRenderer shapeRenderer, Camera camera, Batch batch, BitmapFont bitmapFont) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.begin();
        //batch.draw(texture, 0, 0);
        for (Cell cell : LDA.getPositionMap().getCells()) {
            bitmapFont.draw(batch, String.valueOf(LDA.getPositionMap().getCells().indexOf(cell)), cell.getCenterPoint().x, cell.getCenterPoint().y);
        }
        batch.end();
    }

    public static void numTopics(ShapeRenderer shapeRenderer, Camera camera, Batch batch, BitmapFont bitmapFont) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.begin();
        bitmapFont.draw(batch, "Topic = " + RenderLDA.getNumTopics(), Parameter.SCALE.x - 1000, Parameter.SCALE.y - 100);
        batch.end();
    }

    private static ArrayList<String> readFileTopic() {
        ArrayList<String> topic = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("core/assets/group_topic" + numTopics + ".txt"))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                topic.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return topic;
    }

    public static void renderTopicRegion(ShapeRenderer shapeRenderer, Camera camera) {
        for (String s : readFileTopic()) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(0, 1, 0, Float.parseFloat(s.split(",")[1]) * 10 * 2));
            //shapeRenderer.setColor(new Color(0, 1, 0, 0.1f));
            int pos = Integer.parseInt(s.split("d")[0].replace("p", ""));
            int dir = Integer.parseInt(s.split("d")[1].substring(0, 1));
            float x = LDA.getPositionMap().getCells().get(pos).getCenterPoint().x;
            float y = LDA.getPositionMap().getCells().get(pos).getCenterPoint().y;
            String tag = s.split(",")[0].split("d")[1].substring(1);
            shapeRenderer.circle(x, y, 500);
            shapeRenderer.end();
            renderTopicLine(shapeRenderer, camera, x, y, dir, tag);
        }
    }

    public static void renderTopicLine(ShapeRenderer shapeRenderer, Camera camera, float x, float y, int direction, String tag) {
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

    public static void setNumTopics(int numTopics) {
        if (numTopics >= 0 && numTopics < 3) {
            RenderLDA.numTopics = numTopics;
        }
    }

    public static int getNumTopics() {
        return numTopics;
    }
}
