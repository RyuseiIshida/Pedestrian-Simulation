package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

public class Font {
    public Font(Batch batch, BitmapFont bitmapFont, Camera camera, Environment environment) {
        font(batch, bitmapFont, camera, environment);
    }

    public Font(Batch batch, BitmapFont bitmapFont, Texture texture, Camera camera, Environment environment) {
        font(batch, bitmapFont, camera, texture, environment);
    }

    public static void font(Batch batch, BitmapFont bitmapFont, Camera camera, Environment environment) {
        Gdx.gl.glClearColor(255, 255, 255, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        bitmapFont.draw(batch,
                "step " + environment.getStep()
                        + "  " + "agent = " + String.format(String.valueOf(environment.getAgentList().size())),
//                        + "  " + "groupNum= " + String.format(String.valueOf(Group.getGroupNum(environment.getAgentList())))
//                        + "  " + "goalNum= " + String.format(String.valueOf(environment.getGoalAgentNum())),
                30, Parameter.SCALE.y - 10);
        batch.end();
    }

    public static void font(Batch batch, BitmapFont bitmapFont, Camera camera, Texture texture, Environment environment) {
        Gdx.gl.glClearColor(255, 255, 255, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        //文字の描画
        batch.begin();
//        batch.draw(texture, 0, 0);
        batch.draw(texture, 0, 0, Parameter.BACKGROUND_TEXTURE_SIZE.x, Parameter.BACKGROUND_TEXTURE_SIZE.y);
        bitmapFont.draw(batch,
                "time " + environment.getStep()
                        + "  " + "agent = " + String.format(String.valueOf(environment.getAgentList().size())),
//                        + "  " + "groupNum= " + String.format(String.valueOf(Group.getGroupNum(environment.getAgentList())))
//                        + "  " + "goalNum= " + String.format(String.valueOf(environment.getGoalAgentNum())),
                30, Parameter.SCALE.y - 10);
        batch.end();
    }
}
