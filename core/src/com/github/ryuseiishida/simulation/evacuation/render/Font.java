package com.github.ryuseiishida.simulation.evacuation.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.github.ryuseiishida.simulation.evacuation.environment.Environment;
import com.github.ryuseiishida.simulation.evacuation.util.Parameter;

public class Font {
    public Font(Batch batch, BitmapFont bitmapFont, Camera camera, Environment environment) {
        renderFont(batch, bitmapFont, camera, environment);
    }

    public static void renderFont(Batch batch, BitmapFont bitmapFont, Camera camera, Environment environment) {
        Gdx.gl.glClearColor(255, 255, 255, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        //文字の描画
        batch.begin();
        //batch.draw(texture, 0, 0);
        bitmapFont.draw(batch,
                "time " + environment.getStep()
                        + "  " + "agentNum = " + String.format(String.valueOf(environment.getAgentList().size())),
//                        + "  " + "groupNum= " + String.format(String.valueOf(Group.getGroupNum(environment.getAgentList())))
//                        + "  " + "goalNum= " + String.format(String.valueOf(environment.getGoalAgentNum())),
                30, Parameter.SCALE.y - 10);
        batch.end();
    }
}
