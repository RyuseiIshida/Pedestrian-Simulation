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
import com.simulation.pedestrian.Agent.StateTag;
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

    //drawFlag
    private boolean drawCell = false;
    private boolean drawView = false;

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

        //文字の描画
        batch.begin();
        bitmapFont.draw(batch, "time " + String.format("%.2f", environment.getStep() / 60), Parameter.SCALE.x - 200, Parameter.SCALE.y - 10);
        bitmapFont.draw(batch, "pedestrian = " + String.format(String.valueOf(environment.getAgents().size())), Parameter.SCALE.x - 450, Parameter.SCALE.y - 10);
        batch.end();

        //Agent
        renderAgent();
        renderAgentView();
        renderAgentFollowLine();
        //出口
        renderGoal();
        //障害物
        //renderObstacle();
        //ポテンシャル
        renderPotential();
        //セルの描画
        renderCell();
        //入力処理
        input();
    }

    private void renderAgent() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Agent agent : environment.getAgents()) {
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.agentRadius);
            float range = 0.7f;
            switch (agent.getStateTag()) {
                case StateTag.moveGoal:
                    shapeRenderer.setColor(Color.RED);
                    shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.agentRadius * range);
                    break;
                case StateTag.follow:
                    shapeRenderer.setColor(Color.GREEN);
                    shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.agentRadius * range);
                    break;
                case StateTag.randomWalk:
                    shapeRenderer.setColor(Color.BLACK);
                    shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.agentRadius * range);
                    break;
            }
        }
        shapeRenderer.end();
    }

    private void renderAgentView() {
        if (drawView) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(0, 1, 0, 0.5f));
            for (Agent agent : environment.getAgents()) {
                float moveDegree = agent.getDirectionDegree();
                moveDegree -= Parameter.viewDegree / 2;
                shapeRenderer.arc(agent.getPosition().x, agent.getPosition().y, Parameter.viewRadius, moveDegree, Parameter.viewDegree);
            }
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    private void renderAgentFollowLine() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (Agent agent : environment.getAgents()) {
            shapeRenderer.setColor(Color.BLACK);
            if (agent.getFollowAgent() != null) {
                shapeRenderer.line(agent.getPosition(), agent.getFollowAgent().getPosition());
            }
        }
        shapeRenderer.end();
    }

    private void renderGoal() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Goal goal : environment.getGoals()) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(goal.getPositionX(), goal.getPositionY(), goal.getWidth(), goal.getHeight());
        }
        shapeRenderer.end();
    }

    private void renderObstacle() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.FIREBRICK);
        for (PotentialCell potentialCell : environment.getEnvPotentialMap().getPotentialCells()) {
            if (potentialCell.getObstaclePotential() != 0) {
                shapeRenderer.rect(potentialCell.getLeftButtomPoint().x,
                        potentialCell.getLeftButtomPoint().y,
                        potentialCell.getCellInterval(),
                        potentialCell.getCellInterval());
                shapeRenderer.rect(potentialCell.getLeftButtomPoint().x,
                        potentialCell.getLeftButtomPoint().y,
                        potentialCell.getCellInterval(),
                        potentialCell.getCellInterval());
            }
        }

        shapeRenderer.setColor(Color.DARK_GRAY);
        for (Obstacle obstacle : environment.getObstacles()) {
            for (PotentialCell obstacleCell : obstacle.getObstacleCells()) {
                shapeRenderer.rect(obstacleCell.getLeftButtomPoint().x,
                        obstacleCell.getLeftButtomPoint().y,
                        obstacleCell.getCellInterval(),
                        obstacleCell.getCellInterval());
                shapeRenderer.rect(obstacleCell.getLeftButtomPoint().x,
                        obstacleCell.getLeftButtomPoint().y,
                        obstacleCell.getCellInterval(),
                        obstacleCell.getCellInterval());
            }
        }
        shapeRenderer.end();
    }

    private void renderPotential() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(PotentialCell potentialCell : environment.getEnvPotentialMap().getPotentialCells()){
            if(potentialCell.getPotential() != 0){
                shapeRenderer.setColor(new Color(1, 0, 0, potentialCell.getPotential() * 0.5f));
                shapeRenderer.rect(potentialCell.getLeftButtomPoint().x,
                        potentialCell.getLeftButtomPoint().y,
                        potentialCell.getCellInterval(),
                        potentialCell.getCellInterval());
                shapeRenderer.rect(potentialCell.getLeftButtomPoint().x,
                        potentialCell.getLeftButtomPoint().y,
                        potentialCell.getCellInterval(),
                        potentialCell.getCellInterval());
            }
        }
        shapeRenderer.end();
    }

    private void renderCell() {
        if (drawCell) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);
            for (PotentialCell cell : environment.getEnvPotentialMap().getPotentialCells()) {
                shapeRenderer.line(cell.getRightButtomPoint(), cell.getRightTopPoint());
                shapeRenderer.line(cell.getLeftTopPoint(), cell.getRightTopPoint());
            }
        }
        shapeRenderer.end();
    }

    private void input() {
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (Gdx.input.isKeyPressed(Input.Keys.O)) environment.spawnObstacle(new Vector2(touchPos.x, touchPos.y));
            else if (Gdx.input.justTouched()) {
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                System.out.println("touchPos = " + touchPos);
                if (Gdx.input.isKeyPressed(Input.Keys.F)) environment.spawnAgent1(new Vector2(touchPos.x, touchPos.y));
                else environment.spawnAgent2(new Vector2(touchPos.x, touchPos.y));
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            environment.getAgents().clear();
            environment.setStep(0);
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.P))) {
            environment.spawnInitAgents();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            PLAY = PLAY ? false : true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            drawCell = drawCell ? false : true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            drawView = drawView ? false : true;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
