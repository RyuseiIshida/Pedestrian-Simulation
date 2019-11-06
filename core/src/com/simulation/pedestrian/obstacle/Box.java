package com.simulation.pedestrian.obstacle;

import com.simulation.pedestrian.potential.PotentialCell;
import com.simulation.pedestrian.potential.PotentialMap;
import com.simulation.pedestrian.util.Tuple;

import java.util.ArrayList;

public class Box extends Obstacle {
    Tuple startIndex;
    Tuple endIndex;
    public Box(float x1, float y1, float x2, float y2, PotentialMap potentialMap){
        super(x1, y1, x2, y2, potentialMap);
        setShapeObstacle();
    }

    @Override
    public void setShapeObstacle() {
        PotentialCell startCell = super.getPotentialMap().getPotentialCell(super.getStartPoint());
        PotentialCell endCell = super.getPotentialMap().getPotentialCell(super.getEndPoint());
        startIndex = super.getPotentialMap().getIndex(startCell);
        endIndex = super.getPotentialMap().getIndex(endCell);
        ArrayList<ArrayList<PotentialCell>> matrixCell = super.getPotentialMap().getMatrixPotentialCells();
        for (int i = startIndex.t1; i <= endIndex.t1; i++) {
            for (int j = startIndex.t2; j <= endIndex.t2; j++) {
                super.getObstacleCells().add(matrixCell.get(i).get(j));
            }
        }
    }

    public Tuple getStartIndex() {
        return startIndex;
    }

    public Tuple getEndIndex() {
        return endIndex;
    }
}
