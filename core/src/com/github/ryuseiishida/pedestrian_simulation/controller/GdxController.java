package com.github.ryuseiishida.pedestrian_simulation.controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.util.Inputs;
import com.github.ryuseiishida.pedestrian_simulation.render.*;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

public class GdxController extends ApplicationAdapter {
    private static OrthographicCamera camera;
    private static SpriteBatch batch;
    private static ShapeRenderer shapeRenderer;
    private static BitmapFont bitmapFont;
    private static Environment environment;
    private static Texture backgroundTexture;
    private static Texture titleBackgroundTexture;

    public static boolean startFlag = false;

    public static void resetEnvironment() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                environment = Environment.newInstance();
                Parameter.IS_WRITE_LOG = false;
            }
        });
    }

    @Override
    public void render() {
        if (startFlag) {
            environment.update();
            new RenderBackground(backgroundTexture);
            new RenderFont();
            new RenderAgent();
            new RenderGoal();
            new RenderObstacle();
            new RenderTopic();
            new Inputs(camera, shapeRenderer, environment);
        } else new RenderBackground(titleBackgroundTexture);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public static Camera getCamera() {
        return camera;
    }

    public static SpriteBatch getBatch() {
        return batch;
    }

    public static ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public static BitmapFont getBitmapFont() {
        return bitmapFont;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Parameter.SCALE.x, Parameter.SCALE.y);
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.BLACK);
        bitmapFont.getData().setScale(10);
        environment = Environment.getInstance();
        titleBackgroundTexture = new Texture("start_background.png");
    }

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment env) {
        GdxController.startFlag = true;
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
                Parameter.BACKGROUND_TEXTURE_Path = path;
                backgroundTexture = new Texture(path);
            }
        });
    }

}
