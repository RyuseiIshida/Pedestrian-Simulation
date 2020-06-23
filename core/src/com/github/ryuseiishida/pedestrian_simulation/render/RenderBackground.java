package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.github.ryuseiishida.pedestrian_simulation.GDXController;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

public class RenderBackground {
    public RenderBackground(Texture texture) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        GDXController.getCamera().update();
        GDXController.getBatch().setProjectionMatrix(GDXController.getCamera().combined);
        GDXController.getBatch().begin();
        GDXController.getBatch().draw(texture, 0, 0, Parameter.SCALE.x, Parameter.SCALE.y);
        GDXController.getBatch().end();
    }
}