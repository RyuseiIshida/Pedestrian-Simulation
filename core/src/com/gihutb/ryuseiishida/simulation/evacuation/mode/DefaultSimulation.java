package com.gihutb.ryuseiishida.simulation.evacuation.mode;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gihutb.ryuseiishida.simulation.evacuation.Parameter;
import com.gihutb.ryuseiishida.simulation.evacuation.agent.Agent;
import com.gihutb.ryuseiishida.simulation.evacuation.agent.Group;
import com.gihutb.ryuseiishida.simulation.evacuation.agent.StateTag;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.Cell;
import com.gihutb.ryuseiishida.simulation.evacuation.environment.Environment;
import com.gihutb.ryuseiishida.simulation.evacuation.goal.Goal;
import com.gihutb.ryuseiishida.simulation.evacuation.log.LoadLog;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.Fire;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.Obstacle;

import java.util.ArrayList;

public class DefaultSimulation extends ApplicationAdapter {
    //libGdx
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture texture;
    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;
    private static Environment environment;

    //Operation
    private static boolean PLAY = false;
    private int attemptsNum = 0;

    //modeLog
    private int agentNumber = 1;
    private boolean drawAllTrajectory = false;

    //drawFlag
    private static boolean drawGoalLine = true;
    private static boolean drawFollowLine = true;
    private static boolean drawPotential = false;
    private static boolean drawPVec = false;
    private static boolean drawConcentrationLevel = false;
    private static boolean drawCell = false;
    private static boolean drawView = false;


    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Parameter.SCALE.x, Parameter.SCALE.y);
        batch = new SpriteBatch();
        texture = new Texture("core/assets/tressa-yokohama.png");
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.BLACK);
        bitmapFont.getData().setScale(5);
        environment = new Environment();
    }

    @Override
    public void render() {
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
        if (environment.agentClearFlag) {
            environment.getAgentList().clear();
            environment.agentClearFlag = false;
            environment.setStep(0);
            PLAY = false;
        }
        Gdx.gl.glClearColor(255, 255, 255, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        //文字の描画
        batch.begin();
        batch.draw(texture, 0, 0);
        bitmapFont.draw(batch,
                "time " + environment.getStep()
                        + "  " + "agentNum = " + String.format(String.valueOf(environment.getAgentList().size()))
                        + "  " + "groupNum= " + String.format(String.valueOf(Group.getGroupNum(environment.getAgentList())))
                        + "  " + "goalNum= " + String.format(String.valueOf(environment.getGoalAgentNum())),
                30, Parameter.SCALE.y - 10);
        batch.end();

        renderAgent();
        renderAgentView();
        renderGoalLine();
        renderAgentFollowLine();
        renderGoal();
        renderObstacle();
        renderFire();
        renderConcentrationLevel();
        renderCell();

        input();
    }

    private void logMode() {
        Gdx.gl.glClearColor(255, 255, 255, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);


        //文字の描画
        batch.begin();
        if (drawAllTrajectory) {
            bitmapFont.draw(batch, "agent all", 30, Parameter.SCALE.y - 10);
        } else {
            bitmapFont.draw(batch, "agent" + agentNumber, 30, Parameter.SCALE.y - 10);
        }
        batch.end();

        //出口
        renderGoal();
        //障害物
        renderObstacle();
        /* 入力処理 */
        input();

        //軌跡
        if (drawAllTrajectory) {
            trajectory();
        } else {
            trajectory(agentNumber);
        }
    }

    private void renderAgent() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Agent agent : environment.getAgentList()) {
            if (agent.getPosition() == null) {
                shapeRenderer.end();
                return;
            }
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.AGENT_RADIUS);
            float range = 0.7f;
            switch (agent.getStateTag()) {
                case StateTag.moveGoal:
                    shapeRenderer.setColor(Color.RED);
                    shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.AGENT_RADIUS * range);
                    break;
                case StateTag.follow:
                    shapeRenderer.setColor(Color.GREEN);
                    shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.AGENT_RADIUS * range);
                    break;
                case StateTag.randomWalk:
                    shapeRenderer.setColor(Color.BLACK);
                    shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.AGENT_RADIUS * range);
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
            for (Agent agent : environment.getAgentList()) {
                float moveDegree = agent.getDirectionDegree();
                moveDegree -= Parameter.VIEW_DEGREE / 2;
                shapeRenderer.arc(agent.getPosition().x, agent.getPosition().y, Parameter.VIEW_RADIUS_LENGTH, moveDegree, Parameter.VIEW_DEGREE);
            }
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    private void renderGoalLine() {
        if (drawGoalLine) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            for (Agent agent : environment.getAgentList()) {
                if (agent.getStateTag().equals(StateTag.moveGoal)) {
                    shapeRenderer.line(agent.getPosition(), agent.getGoal());
                }
            }
            shapeRenderer.end();
        }
    }

    private void renderAgentFollowLine() {
        if (drawFollowLine) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);
            for (Agent agent : environment.getAgentList()) {
                if (agent.getFollowAgent() != null) {
                    shapeRenderer.line(agent.getPosition(), agent.getFollowAgent().getPosition());
                }
            }
            shapeRenderer.end();
        }
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
        shapeRenderer.setColor(Color.RED);
        for (Obstacle obstacle : environment.getObstacles()) {
            for (Cell obstacleCell : obstacle.getObstacleCells()) {
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

    private void renderFire() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.FIREBRICK);
        shapeRenderer.setColor(Color.RED);
        Fire fire = environment.getFire();
        shapeRenderer.circle(fire.getFirePoint().x, fire.getFirePoint().y, fire.getSpreadFireRange());
        shapeRenderer.end();
    }

    private void renderConcentrationLevel() {
        if (drawConcentrationLevel) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (Cell potentialCell : environment.getEnvCellsMap().getCells()) {
                float concentrationLevel = 0;
                float agentCounter = 0;
                for (Agent agent : environment.getAgentList()) {
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

    private void renderCell() {
        if (drawCell) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);
            for (Cell cell : environment.getEnvCellsMap().getCells()) {
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
            if (tmp == null) {
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
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y));
                } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), 0);
                } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), 1);
                } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), 2);
                } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), 3);
                } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), 4);
                } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_0)) {
                    environment.spawnFire(new Vector2(touchPos.x, touchPos.y));
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            environment.getAgentList().clear();
            environment.setStep(0);
            environment.setGoalAgentNum(0);
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

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            environment.loadMap();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            System.out.println("environment = " + environment.getObstacles());
        }
    }


    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment env) {
        environment = env;
    }

    public static boolean getPLAY() {
        return PLAY;
    }

    public static void setPLAY(boolean flag) {
        PLAY = flag;
    }

    public static void setDrawGoalLine() {
        drawGoalLine = !drawGoalLine;
    }

    public static void setDrawFollowLine() {
        drawFollowLine = !drawFollowLine;
    }

    public static void setDrawPotential() {
        drawPotential = !drawPotential;
    }

    public static void setDrawPVec() {
        drawPVec = !drawPVec;
    }

    public static void setDrawConcentrationLevel() {
        drawConcentrationLevel = !drawConcentrationLevel;
    }

    public static void setDrawCell() {
        drawCell = !drawCell;
    }

    public static void setDrawView() {
        drawView = !drawView;
    }


    @Override
    public void dispose() {
        batch.dispose();
    }
}
