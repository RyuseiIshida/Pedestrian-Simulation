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
import com.simulation.pedestrian.Obstacle.Obstacle;
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
        bitmapFont.getData().setScale(2);
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
        batch.end();

        //塗りつぶし
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //Agent
        for (Agent agent : environment.getAgents()) {
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.agentRadius);
        }

        //AgentPotentialCell
        for (PotentialCell potentialCell : environment.getEnvPotentialMap().getPotentialCells()) {
            shapeRenderer.setColor(Color.BLUE);
            if(potentialCell.getAgentPotential() != 0){
                shapeRenderer.rect(potentialCell.getLeftButtomPoint().x, potentialCell.getLeftButtomPoint().y, potentialCell.getCellInterval(), potentialCell.getCellInterval());
                shapeRenderer.rect(potentialCell.getLeftButtomPoint().x, potentialCell.getLeftButtomPoint().y, potentialCell.getCellInterval(), potentialCell.getCellInterval());
            }
        }

        //出口
        for (Goal goal : environment.getGoals()) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(goal.getPositionX(), goal.getPositionY(), goal.getWidth(), goal.getHeight());
        }

        //障害物
        for (PotentialCell potentialCell : environment.getEnvPotentialMap().getPotentialCells()) {
            shapeRenderer.setColor(Color.FIREBRICK);
            if (potentialCell.getObstaclePotential() != 0) {
                shapeRenderer.rect(potentialCell.getLeftButtomPoint().x, potentialCell.getLeftButtomPoint().y, potentialCell.getCellInterval(), potentialCell.getCellInterval());
                shapeRenderer.rect(potentialCell.getLeftButtomPoint().x, potentialCell.getLeftButtomPoint().y, potentialCell.getCellInterval(), potentialCell.getCellInterval());
            }
        }

        for (Obstacle obstacle : environment.getObstacles()) {
            for (PotentialCell obstacleCell : obstacle.getObstacleCells()) {
                shapeRenderer.setColor(Color.DARK_GRAY);
                shapeRenderer.rect(obstacleCell.getLeftButtomPoint().x, obstacleCell.getLeftButtomPoint().y, obstacleCell.getCellInterval(), obstacleCell.getCellInterval());
                shapeRenderer.rect(obstacleCell.getLeftButtomPoint().x, obstacleCell.getLeftButtomPoint().y, obstacleCell.getCellInterval(), obstacleCell.getCellInterval());
            }
        }

        for (PotentialCell potentialCell : environment.getEnvPotentialMap().getPotentialCells()) {

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

        //薄い色
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 1, 0, 0.5f));
        for (Agent agent : environment.getAgents()) {
            float moveDegree = agent.getDirectionDegree();
            moveDegree -= Parameter.viewDegree / 2;
            shapeRenderer.arc(agent.getPosition().x , agent.getPosition().y, Parameter.viewRadius, moveDegree, Parameter.viewDegree);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

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
