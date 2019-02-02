package com.simulation.pedestrian.Obstacle;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Potential.PotentialCell;
import com.simulation.pedestrian.Potential.PotentialMap;
import com.simulation.pedestrian.Util.Tuple;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Obstacle {
    private PotentialMap potentialMap;
    private List<PotentialCell> obstacleCells;
    private Tuple startIndex;
    private Tuple endIndex;

    public Obstacle(float x, float y, float w, float h, PotentialMap potentialMap) {
        this.potentialMap = potentialMap;
        this.obstacleCells = new ArrayList<>();
        setShapeObstacle(x, y, w, h);
    }

    public Obstacle(Vector2 position, float w, float h, PotentialMap potentialMap) {
        this(position.x, position.y, w, h, potentialMap);
    }

    public Obstacle(Vector2 position, Vector2 WH, PotentialMap potentialMap) {
        this(position.x, position.y, WH.x, WH.y, potentialMap);
    }

    public void setShapeObstacle(Vector2 position, float w, float h) {
        PotentialCell startCell = potentialMap.getPotentialCell(position);
        PotentialCell endCell = potentialMap.getPotentialCell(new Vector2(position.x + w, position.y + h));
        startIndex = potentialMap.getIndex(startCell);
        endIndex = potentialMap.getIndex(endCell);
        ArrayList<ArrayList<PotentialCell>> matrixCell = potentialMap.getMatrixPotentialCells();
        for (int i = startIndex.t1; i <= endIndex.t1; i++) {
            for (int j = startIndex.t2; j <= endIndex.t2; j++) {
                //matrixCell.get(i).get(j).setObstaclePotential(potential);
                obstacleCells.add(matrixCell.get(i).get(j));
            }
        }
    }

    public void setShapeObstacle(float x, float y, float w, float h) {
        setShapeObstacle(new Vector2(x, y), w, h);
    }

    public List<PotentialCell> getObstacleCells() {
        return obstacleCells;
    }

    public Tuple getStartIndex() {
        return startIndex;
    }

    public Tuple getEndIndex() {
        return endIndex;
    }
}