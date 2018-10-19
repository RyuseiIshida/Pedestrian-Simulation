package com.simulation.pedestrian;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.simulation.pedestrian.Agent.Agent;
import com.simulation.pedestrian.Obstacle.Obstacle;
import com.simulation.pedestrian.Potential.PotentialCell;
import com.simulation.pedestrian.Potential.PotentialManager;
import com.simulation.pedestrian.Potential.PotentialMap;

import java.util.ArrayList;

public class Main extends ApplicationAdapter {
    //libGdx
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture goalImage;
    private Sprite goal;

    //myClass
    private ArrayList<Agent> agents = new ArrayList<>();

    //tmp
    PotentialMap potentialMap = new PotentialMap(Parameter.SCALE, Parameter.CELLINTERVAL, 10);

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Parameter.SCALE.x, Parameter.SCALE.y);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        goalImage = new Texture("exit.png");
        goal = new Sprite(goalImage);
        goal.setPosition(Parameter.GOAL.x, Parameter.GOAL.y);

        new PotentialManager();
    }

    public void spawnAgent1(Vector2 pos) {
        agents.add(new Agent(pos));
    }

    public void spawnAgent2(Vector2 pos) {
        agents.add(new Agent(pos, Parameter.GOAL));
    }

    @Override
    public void render() {
        update();
        Gdx.gl.glClearColor(255, 255, 255, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //描画
        batch.begin();
        goal.draw(batch);
        batch.end();


        //塗りつぶし
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //Agent
        for (Agent agent : agents) {
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.agentRadius);
        }

        //Obstacle
        for (Obstacle obstacle : PotentialManager.getObstacles()) {
            for (PotentialCell obstacleCell : obstacle.getObstacleCells()) {
                shapeRenderer.setColor(Color.FIREBRICK);
                shapeRenderer.rect(obstacleCell.getLeftButtomPoint().x, obstacleCell.getLeftButtomPoint().y,obstacleCell.getCellInterval(), obstacleCell.getCellInterval());
                shapeRenderer.rect(obstacleCell.getLeftButtomPoint().x, obstacleCell.getLeftButtomPoint().y,obstacleCell.getCellInterval(), obstacleCell.getCellInterval());
            }
        }
        shapeRenderer.end();


        //セルの描画
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (PotentialCell cell : potentialMap.getPotentialCells()) {
            shapeRenderer.line(cell.getRightButtomPoint(), cell.getRightTopPoint());
            shapeRenderer.line(cell.getLeftTopPoint(), cell.getRightTopPoint());
        }
        shapeRenderer.end();

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            System.out.println("touchPos = " + touchPos);
            if (Gdx.input.isKeyPressed(Input.Keys.F)) spawnAgent2(new Vector2(touchPos.x, touchPos.y));
            else spawnAgent1(new Vector2(touchPos.x, touchPos.y));
        }
    }

    public void update() {
        agents.stream()
                .parallel()
                .forEach(agent -> {
                    try {
                        agent.action();
                    } catch (final Exception l_exception) {
                    }
                });
    }


    @Override
    public void dispose() {
        batch.dispose();
        goalImage.dispose();
    }
}
