package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.github.ryuseiishida.pedestrian_simulation.GDXController;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

public class RenderFont {
    public RenderFont() {
        Gdx.gl.glClearColor(255, 255, 255, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        GDXController.getCamera().update();
        GDXController.getBatch().setProjectionMatrix(GDXController.getCamera().combined);
        GDXController.getBatch().begin();
        GDXController.getBitmapFont().draw(GDXController.getBatch(),
                "step " + GDXController.getEnvironment().getStep()
                        + "  " + "agent = " + String.format(String.valueOf(GDXController.getEnvironment().getAgentList().size())),
                30, Parameter.SCALE.y - 10);
        GDXController.getBatch().end();
    }
}
