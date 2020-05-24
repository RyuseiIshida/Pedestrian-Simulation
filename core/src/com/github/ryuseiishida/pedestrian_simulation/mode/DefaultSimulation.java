package com.github.ryuseiishida.pedestrian_simulation.mode;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.environment.LoopController;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;
import com.github.ryuseiishida.pedestrian_simulation.input.Inputs;
import com.github.ryuseiishida.pedestrian_simulation.render.*;

public class DefaultSimulation extends ApplicationAdapter {
    //libGdx
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;
    private LoopController loopController;
    private static Environment environment;
    private Texture texture;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Parameter.SCALE.x, Parameter.SCALE.y);
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.BLACK);
        bitmapFont.getData().setScale(15);
        environment = new Environment();
        loopController = new LoopController(environment);
        texture = new Texture("core/assets/tressa-yokohama.png");
    }

    @Override
    public void render() {
        environment.update();
        new Font(batch, bitmapFont, texture, camera, environment);
        new RenderAgent(shapeRenderer, camera, environment);
        new RenderCell(shapeRenderer, camera, environment);
        new RenderFire(shapeRenderer, camera, environment);
        new RenderGoal(shapeRenderer, camera, environment);
        new RenderObstacle(shapeRenderer, camera, environment);
        new Inputs(camera, environment);
        if (loopController.isResetLoop(environment.getStep())) {
            System.out.println("looped " + loopController.getCountLoopNum());
            environment = new Environment(true);
            loopController.newEnvironment(environment);
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
