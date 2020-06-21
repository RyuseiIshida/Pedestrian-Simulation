package com.github.ryuseiishida.pedestrian_simulation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.util.Inputs;
import com.github.ryuseiishida.pedestrian_simulation.render.*;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

public class GDXController extends ApplicationAdapter {
    //libGdx
    private static OrthographicCamera camera;
    private static SpriteBatch batch;
    private static ShapeRenderer shapeRenderer;
    private static BitmapFont bitmapFont;
    private static Environment environment;
    private static Texture backgroundTexture;
    private static Texture titleBackgroundTexture;

    public static boolean startFlag = false;

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
        titleBackgroundTexture = new Texture("PedSimAsset.png");
    }

    @Override
    public void render() {
        if (startFlag) {
            if (backgroundTexture != null) {
                new Font(batch, bitmapFont, backgroundTexture, camera, environment);
            } else {
                new Font(batch, bitmapFont, camera, environment);
            }
            environment.update();
            new RenderAgent(shapeRenderer, camera, environment);
            new RenderGoal(shapeRenderer, camera, environment);
            new RenderObstacle(shapeRenderer, camera, environment);
            // new RenderLDA(batch, bitmapFont, shapeRenderer, camera, environment.getLoadLog().getSimulationLogPath());
            new Inputs(camera, shapeRenderer, environment);
        } else {
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            camera.update();
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            batch.draw(titleBackgroundTexture, 0, 0, Parameter.SCALE.x, Parameter.SCALE.y);
            bitmapFont.draw(batch, "version" + Parameter.version, Parameter.SCALE.x - 900, 200);
            batch.end();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public static void resetEnvironment() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                environment = new Environment();
            }
        });
    }

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment env) {
        GDXController.startFlag = true;
        environment = env;
    }

    public static void setBackgroundTexture(String path) {
        if (path == null) {
            backgroundTexture = null;
            return;
        }
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                backgroundTexture = new Texture(path);
            }
        });
    }

}
