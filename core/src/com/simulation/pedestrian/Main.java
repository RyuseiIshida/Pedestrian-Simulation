package com.simulation.pedestrian;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.simulation.pedestrian.Agent.Agent;
import com.simulation.pedestrian.Potential.PotentialCell;
import com.simulation.pedestrian.Potential.PotentialManager;
import com.simulation.pedestrian.Potential.PotentialMap;
import com.simulation.pedestrian.Util.Vector;

import java.util.ArrayList;

public class Main extends ApplicationAdapter {
    //libGdx
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;
    private Texture goalImage;
    private Sprite goal;

    //myClass
    private boolean PLAY = false;
    private float step = 0;
    private ArrayList<Agent> agents = new ArrayList<>();

    //tmp
    PotentialMap potentialMap = new PotentialMap(Parameter.SCALE, Parameter.CELLINTERVAL, 10);

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Parameter.SCALE.x, Parameter.SCALE.y);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.BLACK);
        bitmapFont.getData().setScale(1);

        goalImage = new Texture("exit.png");
        goal = new Sprite(goalImage);
        goal.setPosition(Parameter.GOAL.x, Parameter.GOAL.y);

        new PotentialManager();
    }

    public void spawnAgent1(Vector2 pos) {
        agents.add(new Agent(pos));
    }

    public void spawnAgent2(Vector2 pos) {
        agents.add(new Agent(pos, Parameter.GOAL));
    }

    @Override
    public void render() {
        if (PLAY) {
            update();
            step++;
        }
        Gdx.gl.glClearColor(255, 255, 255, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //Sprite・文字の描画
        batch.begin();
        bitmapFont.draw(batch, "time " + String.format("%.2f", step / 60), Parameter.SCALE.x - 200, Parameter.SCALE.y - 10);
        bitmapFont.draw(batch, "pedestrian = " + String.format(String.valueOf(agents.size())), Parameter.SCALE.x - 450, Parameter.SCALE.y - 10);
        //障害物
        for (PotentialCell potentialCell : PotentialManager.getEnvPotentialMap().getPotentialCells()) {
            shapeRenderer.setColor(Color.FIREBRICK);
            float potential = potentialCell.getPotential();
            if (potential != 0) {
                bitmapFont.draw(batch, String.format("%.1f", potential), potentialCell.getCenterPoint().x, potentialCell.getCenterPoint().y);
            }
        }

        goal.draw(batch);
        batch.end();


        //塗りつぶし
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Agent agent : agents) {//Agent
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.circle(agent.getPosition().x, agent.getPosition().y, Parameter.agentRadius);
        }

//        //障害物
//        for (PotentialCell PotentialCell : PotentialManager.getEnvPotentialMap().getPotentialCells()) {
//            shapeRenderer.setColor(Color.FIREBRICK);
//            if (PotentialCell.getObstaclePotential() != 0) {
//                shapeRenderer.rect(PotentialCell.getLeftButtomPoint().x, PotentialCell.getLeftButtomPoint().y, PotentialCell.getCellInterval(), PotentialCell.getCellInterval());
//                shapeRenderer.rect(PotentialCell.getLeftButtomPoint().x, PotentialCell.getLeftButtomPoint().y, PotentialCell.getCellInterval(), PotentialCell.getCellInterval());
//            }
//        }
        shapeRenderer.end();

        //セルの描画
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (PotentialCell cell : potentialMap.getPotentialCells()) {
            shapeRenderer.line(cell.getRightButtomPoint(), cell.getRightTopPoint());
            shapeRenderer.line(cell.getLeftTopPoint(), cell.getRightTopPoint());
        }


        //ポテンシャルの描画
        for (Agent agent : agents) {
            for (PotentialCell potentialCell : PotentialManager.getEnvPotentialMap().getPotentialCells()) {
                if (potentialCell.getObstaclePotential() != 0){
                    Vector2 direction = Vector.direction( potentialCell.getCenterPoint(), agent.getPosition());
                    direction.scl(30);
                    shapeRenderer.line(potentialCell.getCenterPoint().x, potentialCell.getCenterPoint().y,
                            potentialCell.getCenterPoint().x + direction.x , potentialCell.getCenterPoint().y + direction.y);
                }
            }
        }
        shapeRenderer.end();

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            System.out.println("touchPos = " + touchPos);
            if (Gdx.input.isKeyPressed(Input.Keys.F)) spawnAgent1(new Vector2(touchPos.x, touchPos.y));
            else spawnAgent2(new Vector2(touchPos.x, touchPos.y));
        }


        //Input処理
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            agents.clear();
            step = 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            PLAY = PLAY ? false : true;
        }


    }

    public void update() {
        agents.stream()
                .parallel()
                .forEach(agent -> {
                    try {
                        agent.action();
                    } catch (final Exception l_exception) {
                    }
                });
    }


    @Override
    public void dispose() {
        batch.dispose();
        goalImage.dispose();
    }
}
