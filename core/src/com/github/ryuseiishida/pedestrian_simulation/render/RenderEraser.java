package com.github.ryuseiishida.pedestrian_simulation.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.ryuseiishida.pedestrian_simulation.GDXController;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

public class RenderEraser {
    private static Camera camera = GDXController.getCamera();
    private static ShapeRenderer shapeRenderer = GDXController.getShapeRenderer();
    public static float width = Parameter.SCALE.x * 0.03f;
    public static float height = Parameter.SCALE.y * 0.03f;

    public RenderEraser(float x, float y) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x - width / 2, y - height / 2, width, height);
        shapeRenderer.end();
    }
}
