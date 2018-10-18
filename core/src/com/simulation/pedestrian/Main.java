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

import java.util.ArrayList;

public class Main extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Texture goalImage;
    private Sprite goal;

    private ArrayList<Agent> agents = new ArrayList<>();

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Parameter.SCALE.x, Parameter.SCALE.y);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        goalImage = new Texture("exit.png");
        goal = new Sprite(goalImage);
        goal.setPosition(Parameter.GOAL.x, Parameter.GOAL.y);
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

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GRAY);
        for (Agent agent : agents) {
            shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.agentRadius);
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
