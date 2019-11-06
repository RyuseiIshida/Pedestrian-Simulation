package com.simulation.pedestrian.mode;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.simulation.pedestrian.Parameter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CreateMap extends ApplicationAdapter {
    //libGdx
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture texture;
    private ShapeRenderer shapeRenderer;

    private ArrayList<float[]> lines = new ArrayList<>();
    private Vector3 startPoint;
    private Vector3 endPoint;

    private String strPath = "core/assets/out/createMap/saveMap.txt";

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Parameter.SCALE.x, Parameter.SCALE.y);
        batch = new SpriteBatch();
        texture = new Texture("core/assets/tressa-yokohama.png");
        shapeRenderer = new ShapeRenderer();
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(255, 255, 255, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(texture, 0, 0);
        batch.end();
        drawTmpLine();
        drawLine();
        mapInput();
    }

    private void mapInput() {
        redoCommand();
        setObstaclesLine();
        saveLines();
    }

    private void redoCommand() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            lines.remove(lines.size() - 1);
        }
    }

    private void setObstaclesLine() {
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            System.out.println("touchPos = " + touchPos);
            setPoint(touchPos);
        }
    }

    private void setPoint(Vector3 point) {
        if (startPoint == null) {
            startPoint = point;
        } else {
            endPoint = point;
            createObstacleFromMouseActions();
            startPoint = null;
        }
    }

    private void createObstacleFromMouseActions() {
        float[] point = new float[4];
        point[0] = startPoint.x;
        point[1] = startPoint.y;
        point[2] = endPoint.x;
        point[3] = endPoint.y;
        lines.add(point);
    }

    private void drawTmpLine() {
        if (startPoint != null) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.line(startPoint.x, startPoint.y, touchPos.x, touchPos.y);
            shapeRenderer.end();
        }
    }

    private void drawLine() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (float[] points : lines) {
            shapeRenderer.line(points[0], points[1], points[2], points[3]);
        }
        shapeRenderer.end();
    }

    private void saveLines() {
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            writeFile();
        }
    }

    private void writeFile() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(strPath))) {
            for (float[] line : lines) {
                bw.append(line[0] + "f,");
                bw.append(line[1] + "f,");
                bw.append(line[2] + "f,");
                bw.append(line[3] + "f");
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
