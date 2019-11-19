package com.simulation.pedestrian.obstacle;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.cell.CellsMap;
import com.simulation.pedestrian.util.UtilVector;

public class Line extends Obstacle {
    public Line(float x1, float y1, float x2, float y2, CellsMap cellsMap) {
        super(x1, y1, x2, y2, cellsMap);
        setShapeObstacle();
    }

    @Override
    public void setShapeObstacle() {
        Vector2 direction = UtilVector.direction(super.getStartPoint(), super.getEndPoint());
        Vector2 setPointCell = new Vector2(super.getStartPoint());
        while ((int) setPointCell.dst(getEndPoint()) != 0) {
            super.getObstacleCells().add(super.getCellsMap().getCell(setPointCell));
            setPointCell.add(direction);
        }
    }

}
