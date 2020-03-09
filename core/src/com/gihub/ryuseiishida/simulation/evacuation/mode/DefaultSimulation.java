package com.gihub.ryuseiishida.simulation.evacuation.mode;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gihub.ryuseiishida.simulation.evacuation.environment.LoopController;
import com.gihub.ryuseiishida.simulation.evacuation.render.*;
import com.gihub.ryuseiishida.simulation.evacuation.util.Parameter;
import com.gihub.ryuseiishida.simulation.evacuation.environment.Environment;
import com.gihub.ryuseiishida.simulation.evacuation.input.Inputs;
import com.gihub.ryuseiishida.simulation.evacuation.render.*;

public class DefaultSimulation extends ApplicationAdapter {
    //libGdx
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;
    private LoopController loopController;
    private static Environment environment;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Parameter.SCALE.x, Parameter.SCALE.y);
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.BLACK);
        bitmapFont.getData().setScale(15);
        loopController = new LoopController();
        environment = loopController.isLoop() ? new Environment(true) : new Environment();
    }

    @Override
    public void render() {
        environment.update();
        new Font(batch, bitmapFont, camera, environment);
        new RenderAgent(shapeRenderer, camera, environment);
        new RenderCell(shapeRenderer, camera, environment);
        new RenderFire(shapeRenderer, camera, environment);
        new RenderGoal(shapeRenderer, camera, environment);
        new RenderObstacle(shapeRenderer, camera, environment);
        new RenderLDA(batch, bitmapFont, shapeRenderer, camera);
        new Inputs(camera, environment);
        if (loopController.isResetLoop(environment.getStep())) {
            System.out.println("looped " + loopController.getCountLoopNum());
            environment = new Environment(true);
        }
        if (loopController.isEndLoop()) {
            dispose();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        System.exit(1);
    }
}
