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
import com.simulation.pedestrian.Log.LoadLog;
import com.simulation.pedestrian.Obstacle.Obstacle;
import com.simulation.pedestrian.Potential.PotentialCell;

import java.awt.*;
import java.util.ArrayList;

public class Main extends ApplicationAdapter {
    //libGdx
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;

    private Environment environment;

    //Operation
    public boolean PLAY = false;
    private int attemptsNum = 0;

    //modeLog
    private int agentNumber = 1;
    private boolean drawAllTrajectory = false;

    //drawFlag
    private boolean drawPotential = false;
    private boolean drawPVec = false;
    private boolean drawConcentrationLevel = false;
    private boolean drawCell = false;
    private boolean drawView = false;


    @Override
    public void create() {
        //createSubWindow();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Parameter.SCALE.x, Parameter.SCALE.y);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.BLACK);
        bitmapFont.getData().setScale(5);
        environment = new Environment();
    }

    public void createSubWindow(){
        ParameterWindow frame = new ParameterWindow("タイトル");
        frame.setVisible(true);
    }

    @Override
    public void render() {
        if (Parameter.MODE == 0) {
            simulationMode();
        }
        if (Parameter.MODE == 1){
            logMode();
        }
    }

    private void simulationMode() {
        if (Parameter.ENDSTEP != 0 && Parameter.ENDSTEP + 1 == environment.getStep()) {
            environment = new Environment();
            attemptsNum++;
            System.out.println("attemptsNum = " + attemptsNum);
            if (attemptsNum == Parameter.ATTEMPTSNUM) {
                dispose();
            }
        }
        if (PLAY) {
            environment.update();
        }
        Gdx.gl.glClearColor(255, 255, 255, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //文字の描画
        batch.begin();
        bitmapFont.draw(batch,
                "time " + String.format("%.2f", environment.getStep() / 60)
                        + "  " + "agentNum = " + String.format(String.valueOf(environment.getAgents().size()))
                        + "  " + "groupNum= " + String.format(String.valueOf(environment.getCrowd().getCrowdNum()))
                        + "  " + "goalNum= " + String.format(String.valueOf(environment.getGoalAgentNum())),
                30, Parameter.SCALE.y - 10);
        batch.end();

        //ポテンシャル
        renderPotential();
        //Agent
        renderAgent();
        renderAgentView();
        renderGoalLine();
        renderAgentFollowLine();
        //出口
        renderGoal();
        //障害物
        renderObstacle();
        //ベクトル場
        renderPotentialVec();
        //密集度合い
        renderConcentrationLevel();
        //セルの描画
        renderCell();
        //入力処理
        input();
    }

    private void logMode() {
        Gdx.gl.glClearColor(255, 255, 255, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);


        //文字の描画
        batch.begin();
        if(drawAllTrajectory) {
            bitmapFont.draw(batch, "agent all", 30, Parameter.SCALE.y - 10);
        } else {
            bitmapFont.draw(batch, "agent" + agentNumber, 30, Parameter.SCALE.y - 10);
        }
        batch.end();

        //出口
        renderGoal();
        //障害物
        renderObstacle();
        //入力処理
        input();

        //軌跡
        if(drawAllTrajectory) {
            trajectory();
        } else {
            trajectory(agentNumber);
        }

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
                case StateTag.moveGroupPosition:
                    shapeRenderer.setColor(Color.BLUE);
                    shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.agentRadius * range);
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

    private void renderGoalLine() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (Agent agent : environment.getAgents()) {
            if (agent.getStateTag() == StateTag.moveGoal) {
                shapeRenderer.line(agent.getPosition(), agent.getGoal());
            }
        }
        shapeRenderer.end();
    }

    private void renderAgentFollowLine() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (Agent agent : environment.getAgents()) {
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
        shapeRenderer.setColor(Color.DARK_GRAY);
        for (Obstacle obstacle : environment.getObstacles()) {
            for (PotentialCell obstacleCell : obstacle.getObstacleCells()) {
                shapeRenderer.rect(obstacleCell.getLeftBottomPoint().x,
                        obstacleCell.getLeftBottomPoint().y,
                        obstacleCell.getCellInterval(),
                        obstacleCell.getCellInterval());
                shapeRenderer.rect(obstacleCell.getLeftBottomPoint().x,
                        obstacleCell.getLeftBottomPoint().y,
                        obstacleCell.getCellInterval(),
                        obstacleCell.getCellInterval());
            }
        }
        shapeRenderer.end();
    }

    private void renderPotential() {
        if (drawPotential) {
            environment.setAgentKimPotentialCell();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (PotentialCell potentialCell : environment.getEnvPotentialMap().getPotentialCells()) {
                if (potentialCell.getPotential() != 0) {
                    shapeRenderer.setColor(new Color(1, 0, 0, potentialCell.getPotential() * 1.5f));
                    shapeRenderer.rect(potentialCell.getLeftBottomPoint().x,
                            potentialCell.getLeftBottomPoint().y,
                            potentialCell.getCellInterval(),
                            potentialCell.getCellInterval());
                    shapeRenderer.rect(potentialCell.getLeftBottomPoint().x,
                            potentialCell.getLeftBottomPoint().y,
                            potentialCell.getCellInterval(),
                            potentialCell.getCellInterval());
                }
            }
            shapeRenderer.end();
        }
    }

    private void renderConcentrationLevel() {
        if (drawConcentrationLevel) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (PotentialCell potentialCell : environment.getEnvPotentialMap().getPotentialCells()) {
                float concentrationLevel = 0;
                float agentCounter = 0;
                for (Agent agent : environment.getAgents()) {
                    float tmp = (float) (1.0 / potentialCell.getCenterPoint().dst(agent.getPosition()));
                    if (tmp >= 0.003) {
                        concentrationLevel += tmp;
                        agentCounter++;
                    }
                }
                if (agentCounter >= 3) {
                    shapeRenderer.setColor(new Color(1, 0, 0, concentrationLevel * 1.5f));
                    shapeRenderer.rect(potentialCell.getLeftBottomPoint().x,
                            potentialCell.getLeftBottomPoint().y,
                            potentialCell.getCellInterval(),
                            potentialCell.getCellInterval());
                    shapeRenderer.rect(potentialCell.getLeftBottomPoint().x,
                            potentialCell.getLeftBottomPoint().y,
                            potentialCell.getCellInterval(),
                            potentialCell.getCellInterval());
                }
            }
            shapeRenderer.end();
        }
    }

    private void renderPotentialVec() {
        if (drawPVec) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (PotentialCell cell : environment.getEnvPotentialMap().getPotentialCells()) {
                if (cell.getPotential() != 0) {
                    shapeRenderer.setColor(Color.RED);
                    Vector2 targetPos = new Vector2(cell.getCenterPoint().x + environment.getAgentGrad(cell.getCenterPoint()).x,
                            cell.getCenterPoint().y + environment.getAgentGrad(cell.getCenterPoint()).y);
                    shapeRenderer.line(cell.getCenterPoint(), targetPos);
                    shapeRenderer.circle(targetPos.x, targetPos.y, 1.5f);
                }
            }
            shapeRenderer.end();
        }
    }

    private void renderCell() {
        if (drawCell) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);
            for (PotentialCell cell : environment.getEnvPotentialMap().getPotentialCells()) {
                shapeRenderer.line(cell.getRightBottomPoint(), cell.getRightTopPoint());
                shapeRenderer.line(cell.getLeftTopPoint(), cell.getRightTopPoint());
            }
        }
        shapeRenderer.end();
    }

    private void trajectory() {
        LoadLog loadLog = new LoadLog();
        for (int i = 1; i <= loadLog.getAgentNum(); i++) {
            trajectory(i);
        }
    }

    private void trajectory(int num) {
        LoadLog loadLog = new LoadLog();
        ArrayList<Vector2> posList = loadLog.getPosList(num);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        Vector2 tmp = null;
        for (Vector2 vector2 : posList) {
            if(tmp == null) {
                tmp = vector2;
            } else {
                shapeRenderer.line(tmp, vector2);
                tmp = vector2;
            }
        }
        shapeRenderer.end();
    }

    private void input() {
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (Gdx.input.isKeyPressed(Input.Keys.O)) {
                environment.spawnObstacle(new Vector2(touchPos.x, touchPos.y));
            } else if (Gdx.input.justTouched()) {
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                System.out.println("touchPos = " + touchPos);
                if (Gdx.input.isKeyPressed(Input.Keys.F)) {
                    environment.spawnAgent1(new Vector2(touchPos.x, touchPos.y));
                } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                    environment.spawnAgent2(new Vector2(touchPos.x, touchPos.y), 0);
                } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                    environment.spawnAgent2(new Vector2(touchPos.x, touchPos.y), 1);
                } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
                    environment.spawnAgent2(new Vector2(touchPos.x, touchPos.y), 2);
                } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
                    environment.spawnAgent2(new Vector2(touchPos.x, touchPos.y), 3);
                } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
                    environment.spawnAgent2(new Vector2(touchPos.x, touchPos.y), 4);
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            environment.getAgents().clear();
            environment.setStep(0);
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.S))) {
            environment.spawnInitAgents();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            PLAY = PLAY ? false : true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            drawPotential = drawPotential ? false : true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            drawPVec = drawPVec ? false : true;
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.M))) {
            drawConcentrationLevel = drawConcentrationLevel ? false : true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            drawCell = drawCell ? false : true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            drawView = drawView ? false : true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            agentNumber++;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            agentNumber--;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            drawAllTrajectory = drawAllTrajectory ? false : true;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
