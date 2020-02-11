package com.gihutb.ryuseiishida.simulation.evacuation.mode;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gihutb.ryuseiishida.simulation.evacuation.util.Parameter;
import com.gihutb.ryuseiishida.simulation.evacuation.environment.Environment;
import com.gihutb.ryuseiishida.simulation.evacuation.input.Inputs;
import com.gihutb.ryuseiishida.simulation.evacuation.render.*;

public class DefaultSimulation extends ApplicationAdapter {
    //libGdx
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;
    private static Environment environment;

    //Operation
    private static boolean PLAY = false;
    private int attemptsNum = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Parameter.SCALE.x, Parameter.SCALE.y);
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.BLACK);
        bitmapFont.getData().setScale(10);
        environment = new Environment();
    }

    @Override
    public void render() {
        if (PLAY) {
            environment.update();
        }

        if (environment.agentClearFlag) {
            environment.getAgentList().clear();
            environment.agentClearFlag = false;
            environment.setStep(0);
            PLAY = false;
        }
        new Font(batch, bitmapFont, camera, environment);
        new RenderAgent(shapeRenderer, camera, environment);
        new RenderCell(shapeRenderer, camera, environment);
        new RenderFire(shapeRenderer, camera, environment);
        new RenderGoal(shapeRenderer, camera, environment);
        new RenderObstacle(shapeRenderer, camera, environment);
        new RenderLDA(batch, bitmapFont, shapeRenderer, camera);
        new Inputs(camera, environment);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
