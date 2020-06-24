package com.github.ryuseiishida.pedestrian_simulation.render;

import com.github.ryuseiishida.pedestrian_simulation.controller.GDXController;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

public class RenderFont {
    private static boolean renderFlag = true;

    public RenderFont() {
        if(renderFlag) {
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

    public static void switchRenderFlag() {
        RenderFont.renderFlag = !RenderFont.renderFlag;
    }
}
