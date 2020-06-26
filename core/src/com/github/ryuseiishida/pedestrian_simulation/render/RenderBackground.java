package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.github.ryuseiishida.pedestrian_simulation.controller.GdxController;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

public class RenderBackground {
    private static boolean renderFlag = true;

    public RenderBackground(Texture texture) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(renderFlag && texture != null) {
            GdxController.getCamera().update();
            GdxController.getBatch().setProjectionMatrix(GdxController.getCamera().combined);
            GdxController.getBatch().begin();
            GdxController.getBatch().draw(texture, 0, 0, Parameter.SCALE.x, Parameter.SCALE.y);
            GdxController.getBatch().end();
        }
    }

    public static void switchRenderFlag() {
        RenderBackground.renderFlag = !RenderBackground.renderFlag;
    }
}