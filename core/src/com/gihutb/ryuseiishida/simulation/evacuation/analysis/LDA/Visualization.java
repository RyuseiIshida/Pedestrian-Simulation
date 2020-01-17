package com.gihutb.ryuseiishida.simulation.evacuation.analysis.LDA;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.Cell;

import java.util.ArrayList;
import java.util.HashMap;

public class Visualization {
    public static void renderCell(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        for (Cell cell : LDA.getPositionMap().getCells()) {
            shapeRenderer.rect(cell.getLeftBottomPoint().x, cell.getLeftBottomPoint().y, cell.getCellInterval(), cell.getCellInterval());
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (Cell cell : LDA.getPositionMap().getCells()) {
            shapeRenderer.line(cell.getLeftBottomPoint(), cell.getLeftTopPoint());
            shapeRenderer.line(cell.getLeftBottomPoint(), cell.getRightBottomPoint());
            shapeRenderer.line(cell.getRightBottomPoint(), cell.getRightTopPoint());
            shapeRenderer.line(cell.getLeftTopPoint(), cell.getRightTopPoint());
        }
        shapeRenderer.end();
    }

    public static void renderCellIndex(Batch batch, BitmapFont bitmapFont) {
        batch.begin();
        //batch.draw(texture, 0, 0);
        for (Cell cell : LDA.getPositionMap().getCells()) {
            bitmapFont.draw(batch, String.valueOf(LDA.getPositionMap().getCells().indexOf(cell)), cell.getCenterPoint().x, cell.getCenterPoint().y);
        }
        batch.end();
    }

    public static void renderTopic(ShapeRenderer shapeRenderer, ArrayList<String> list) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);
        for (String s : list) {
            int pos = Integer.parseInt(s.substring(0,2));
            int dir = Integer.parseInt(s.substring(2,4));
            float x = LDA.getPositionMap().getCells().get(pos).getCenterPoint().x;
            float y = LDA.getPositionMap().getCells().get(pos).getCenterPoint().y;
            shapeRenderer.circle(x,y, 100);
        }
        shapeRenderer.end();
    }
}
