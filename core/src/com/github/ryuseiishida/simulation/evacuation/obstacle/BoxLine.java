package com.github.ryuseiishida.simulation.evacuation.obstacle;

import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.simulation.evacuation.cell.CellsMap;

import java.util.ArrayList;

public class BoxLine {
    private Vector2 startPoint;
    private Vector2 endPoint;
    private CellsMap cellsMap;

    public BoxLine(float x1, float y1, float x2, float y2, CellsMap cellsMap) {
        startPoint = new Vector2(x1, y1);
        endPoint = new Vector2(x2, y2);
        this.cellsMap = cellsMap;
    }

    public ArrayList<Line> getLines() {
        ArrayList<Line> boxLines = new ArrayList<>();
        //Left Line
        boxLines.add(new Line(startPoint.x, startPoint.y, startPoint.x, endPoint.y, cellsMap));
        //Right Line
        boxLines.add(new Line(endPoint.x, startPoint.y, endPoint.x, endPoint.y, cellsMap));
        //Bottom Line
        boxLines.add(new Line(startPoint.x, startPoint.y, endPoint.x, startPoint.y, cellsMap));
        //Top Line
        boxLines.add(new Line(startPoint.x, endPoint.y, endPoint.x, endPoint.y, cellsMap));
        return boxLines;
    }

    public boolean isPositionInBox(float targetX, float targetY) {
        return targetX > startPoint.x
                && targetY > startPoint.y
                && targetX < endPoint.x
                && targetY < endPoint.y;
    }
}
