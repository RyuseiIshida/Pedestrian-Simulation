package com.github.ryuseiishida.pedestrian_simulation.render;

import com.github.ryuseiishida.pedestrian_simulation.controller.GdxController;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

public class RenderFont {
    private static boolean renderFlag = true;

    public RenderFont() {
        if(renderFlag) {
            GdxController.getCamera().update();
            GdxController.getBatch().setProjectionMatrix(GdxController.getCamera().combined);
            GdxController.getBatch().begin();
            GdxController.getBitmapFont().draw(GdxController.getBatch(),
                    "step " + Environment.getInstance().getStep()
                            + "  " + "agent = " + String.format(String.valueOf(Environment.getInstance().getAgentList().size())),
                    30, Parameter.SCALE.y - 10);
            GdxController.getBatch().end();
        }
    }

    public static void switchRenderFlag() {
        RenderFont.renderFlag = !RenderFont.renderFlag;
    }
}
