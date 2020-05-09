package com.github.ryuseiishida.pedestrian_simulation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.render.RenderAgent;
import com.github.ryuseiishida.pedestrian_simulation.render.RenderFire;
import com.github.ryuseiishida.pedestrian_simulation.render.RenderLDA;

public class Inputs {
    private Camera camera;
    private Environment environment;

    public Inputs(Camera camera, Environment environment) {
        this.camera = camera;
        this.environment = environment;
        systemController();
        setRenderFlag();
    }

    private void systemController() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            environment.switchUpdateFlag();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            agentController();
            ldaController();
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.F) && Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            System.out.println(touchPos);
        }
    }

    private void agentController() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            environment.spawnInitAgents();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            environment.getAgentList().clear();
            environment.setGoalAgentNum(0);
            environment.setStep(0);
            environment.setUpdateFlag(false);
        }

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.F) && Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            environment.spawnAgent(new Vector2(touchPos.x, touchPos.y));
        }

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.NUM_1) && Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), 0);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.NUM_2) && Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), 1);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.NUM_3) && Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            environment.spawnAgent(new Vector2(touchPos.x, touchPos.y), 2);
        }
    }

    private void ldaController() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            environment.saveLDA();
        }
    }

//    private void input() {
//        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.R)) {
//            environment.loadMap();
//        }
//
//        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.A)) {
//            environment.spawnInitLogAgent();
//        }
//
//        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.B)) {
//            System.out.println("environment = " + environment.getObstacles());
//        }
//
//        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.L)) {
//            environment.writeExperiment();
//            System.out.println("savedLog");
//        }
//    }

    private void setRenderFlag() {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            // agent view
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.V)) {
                RenderAgent.setViewFlag();
            }
            // agent goal line
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.G)) {
                RenderAgent.setMoveGoalLineFlag();
            }
            // agent follow line
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F)) {
                RenderAgent.setFollowLineFlag();
            }
            // fire
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.C)) {
                RenderFire.setRenderFlag();
            }
            // LDA
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.D)) {
                RenderLDA.switchRenderFlag();
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                RenderLDA.setNumTopics(RenderLDA.getNumTopics() + 1);
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                RenderLDA.setNumTopics(RenderLDA.getNumTopics() - 1);
            }
        }
    }
}
