package com.gihutb.ryuseiishida.simulation.evacuation.obstacle;

import com.gihutb.ryuseiishida.simulation.evacuation.agent.Agent;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.Cell;
import com.gihutb.ryuseiishida.simulation.evacuation.util.Tuple;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.CellsMap;

import java.util.ArrayList;

public class Box extends Obstacle {
    private Tuple<Integer> startIndex;
    private Tuple<Integer> endIndex;

    public Box(float x1, float y1, float x2, float y2, CellsMap cellsMap) {
        super(x1, y1, x2, y2, cellsMap);
        setShapeObstacle();
    }

    @Override
    public void setShapeObstacle() {
        Cell startCell = super.getCellsMap().getCell(super.getStartPoint());
        Cell endCell = super.getCellsMap().getCell(super.getEndPoint());
        startIndex = super.getCellsMap().getIndex(startCell);
        endIndex = super.getCellsMap().getIndex(endCell);
        ArrayList<ArrayList<Cell>> matrixCell = super.getCellsMap().getMatrixCells();
        for (int i = startIndex.valueA; i <= endIndex.valueA; i++) {
            for (int j = startIndex.valueB; j <= endIndex.valueB; j++) {
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

    public boolean isPositionInBox(float targetX, float targetY) {
        if (targetX > super.getStartPoint().x
                && targetY > super.getStartPoint().y
                && targetX < super.getEndPoint().x
                && targetY < super.getEndPoint().y) {
            return true;
        }
        return false;
    }
}
