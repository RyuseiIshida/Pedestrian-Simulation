package com.simulation.pedestrian;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.simulation.pedestrian.Agent.Agent;
import com.simulation.pedestrian.Potential.PotentialCell;

public class Main extends ApplicationAdapter {
    //libGdx
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;

    //myClass
    private boolean PLAY = false;
    private Environment environment;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Parameter.SCALE.x, Parameter.SCALE.y);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.BLACK);
        bitmapFont.getData().setScale(1);
        environment = new Environment();
    }

    @Override
    public void render() {
        if (PLAY) {
            environment.update();
        }
        Gdx.gl.glClearColor(255, 255, 255, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //Sprite・文字の描画
        batch.begin();
        bitmapFont.draw(batch, "time " + String.format("%.2f", environment.getStep() / 60), Parameter.SCALE.x - 200, Parameter.SCALE.y - 10);
        bitmapFont.draw(batch, "pedestrian = " + String.format(String.valueOf(environment.getAgents().size())), Parameter.SCALE.x - 450, Parameter.SCALE.y - 10);
//        //障害物
//        for (PotentialCell potentialCell : PotentialManager.getEnvPotentialMap().getPotentialCells()) {
//            shapeRenderer.setColor(Color.FIREBRICK);
//            float potential = potentialCell.getPotential();
//            if (potential != 0) {
//                bitmapFont.draw(batch, String.format("%.1f", potential), potentialCell.getCenterPoint().x, potentialCell.getCenterPoint().y);
//            }
//        }

        batch.end();

        //塗りつぶし
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Agent agent : environment.getAgents()) {//Agent
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.agentRadius);
        }

        //出口
        for (Goal goal : environment.getGoals()) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(goal.getPositionX(), goal.getPositionY(), goal.getWidth(), goal.getHeight());
        }

        //障害物
        for (PotentialCell PotentialCell : environment.getEnvPotentialMap().getPotentialCells()) {
            shapeRenderer.setColor(Color.DARK_GRAY);
            if (PotentialCell.getObstaclePotential() != 0) {
                shapeRenderer.rect(PotentialCell.getLeftButtomPoint().x, PotentialCell.getLeftButtomPoint().y, PotentialCell.getCellInterval(), PotentialCell.getCellInterval());
                shapeRenderer.rect(PotentialCell.getLeftButtomPoint().x, PotentialCell.getLeftButtomPoint().y, PotentialCell.getCellInterval(), PotentialCell.getCellInterval());
            }
        }
        shapeRenderer.end();

        //セルの描画
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (PotentialCell cell : environment.getEnvPotentialMap().getPotentialCells()) {
            shapeRenderer.line(cell.getRightButtomPoint(), cell.getRightTopPoint());
            shapeRenderer.line(cell.getLeftTopPoint(), cell.getRightTopPoint());
        }
        shapeRenderer.end();

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            System.out.println("touchPos = " + touchPos);
            if (Gdx.input.isKeyPressed(Input.Keys.F)) environment.spawnAgent1(new Vector2(touchPos.x, touchPos.y));
            else environment.spawnAgent2(new Vector2(touchPos.x, touchPos.y));
        }

        //Input処理
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            environment.getAgents().clear();
            environment.setStep(0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            PLAY = PLAY ? false : true;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
