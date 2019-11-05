package com.simulation.pedestrian.obstacle;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.potential.PotentialMap;
import com.simulation.pedestrian.util.UtilVector;

public class Line extends Obstacle {
    public Line(float x1, float y1, float x2, float y2, PotentialMap potentialMap) {
        super(x1, y1, x2, y2, potentialMap);
        setShapeObstacle();
    }

    @Override
    public void setShapeObstacle() {
        Vector2 direction = UtilVector.direction(startPoint,endPoint);
        Vector2 setPointCell = new Vector2(startPoint);
        while(setPointCell.x < endPoint.x || setPointCell.y < endPoint.y){
            obstacleCells.add(potentialMap.getPotentialCell(setPointCell));
            setPointCell.add(direction);
        }
    }

}
