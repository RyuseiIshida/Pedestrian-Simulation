package com.github.ryuseiishida.pedestrian_simulation.mode;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.input.Inputs;
import com.github.ryuseiishida.pedestrian_simulation.render.*;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

public class LogSimulation extends ApplicationAdapter {
    //libGdx
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;
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
        environment = new Environment(Parameter.LOG_DIR_PATH);
        texture = new Texture("core/assets/tressa-yokohama.png");
//        Parameter.IS_WRITE_LOG = false;
//        Parameter.IS_WRITE_LOG_AGENT = false;
//        Parameter.IS_WRITE_LOG_MACRO = false;
//        Parameter.LDA_OUT_PRINT_STEP = -1;
//        Parameter.LDA_SPLIT_STEP = -1;
    }

    @Override
    public void render() {
        environment.update();
        new Font(batch, bitmapFont, texture, camera, environment);
        new RenderAgent(shapeRenderer, camera, environment);
        new RenderGoal(shapeRenderer, camera, environment);
//        new RenderObstacle(shapeRenderer, camera, environment);
        new RenderLDA(batch, bitmapFont, shapeRenderer, camera, environment.getLoadLog().getSimulationLogPath());
        new Inputs(camera, environment);
    }

    @Override
    public void dispose() {
        batch.dispose();
        System.exit(1);
    }
}
