package com.gihutb.ryuseiishida.simulation.evacuation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gihutb.ryuseiishida.simulation.evacuation.environment.Environment;

public class Inputs {
    Camera camera;
    Environment environment;

    public Inputs(Camera camera, Environment environment) {
        this.camera = camera;
        this.environment = environment;
        input();
    }

    private void input() {
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.O)) {
                environment.spawnObstacle(new Vector2(touchPos.x, touchPos.y));
            } else if (Gdx.input.justTouched()) {
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                //System.out.println("touchPos = " + touchPos);
                //new LDA().getDeleteCellIndex(new Vector2(touchPos.x, touchPos.y));
                if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.F)) {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y));
                } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.NUM_1)) {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), 0);
                } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.NUM_2)) {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), 1);
                } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.NUM_3)) {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), 2);
                } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.NUM_4)) {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), 3);
                } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.NUM_5)) {
                    environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), 4);
                } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.NUM_0)) {
                    environment.spawnFire(new Vector2(touchPos.x, touchPos.y));
                }
            }
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.D)) {
            environment.getAgentList().clear();
            environment.setStep(0);
            environment.setGoalAgentNum(0);
        } else if (Gdx.input.isKeyJustPressed((com.badlogic.gdx.Input.Keys.S))) {
            environment.spawnInitAgents();
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            //PLAY = !PLAY;
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.P)) {
//            drawPotential = !drawPotential;
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.B)) {
//            drawPVec = !drawPVec;
        } else if (Gdx.input.isKeyJustPressed((com.badlogic.gdx.Input.Keys.M))) {
//            drawConcentrationLevel = !drawConcentrationLevel;
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.C)) {
//            drawCell = !drawCell;
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.V)) {
//            drawView = !drawView;
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.UP)) {
//            agentNumber++;
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
//            agentNumber--;
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
//            drawAllTrajectory = !drawAllTrajectory;
        }

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.R)) {
            environment.loadMap();
        }

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.A)) {
            environment.spawnInitLogAgent();
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.B)) {
            System.out.println("environment = " + environment.getObstacles());
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.L)) {
            environment.writeExperiment();
            System.out.println("savedLog");
        }
    }

}
