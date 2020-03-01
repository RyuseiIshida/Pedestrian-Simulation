package com.gihutb.ryuseiishida.simulation.evacuation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gihutb.ryuseiishida.simulation.evacuation.environment.Environment;
import com.gihutb.ryuseiishida.simulation.evacuation.render.RenderAgent;
import com.gihutb.ryuseiishida.simulation.evacuation.render.RenderFire;
import com.gihutb.ryuseiishida.simulation.evacuation.render.RenderLDA;

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
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            environment.switchUpdateFlag();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            agentController();
        }
    }

    private void agentController() {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
            environment.spawnInitAgents();
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.D)) {
            environment.getAgentList().clear();
            environment.setStep(0);
            environment.setGoalAgentNum(0);
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
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.V)) {
                RenderAgent.setRenderViewFlag();
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.G)) {
                RenderAgent.setRenderMoveGoalLineFlag();
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F)) {
                RenderAgent.setRenderFollowLineFlag();
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.C)) {
                RenderFire.setRenderFlag();
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.D)) {
                RenderLDA.switchRenderFlag();
            }
        }
    }
}
