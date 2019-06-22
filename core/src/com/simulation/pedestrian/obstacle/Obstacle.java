package com.simulation.pedestrian.obstacle;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.potential.PotentialCell;
import com.simulation.pedestrian.potential.PotentialMap;
import com.simulation.pedestrian.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class Obstacle {
    private PotentialMap potentialMap;
    private List<PotentialCell> obstacleCells;
    private float x;
    private float y;
    private float width;
    private float height;
    private Tuple startIndex;
    private Tuple endIndex;

    public Obstacle(float x, float y, float w, float h, PotentialMap potentialMap) {
        this.potentialMap = potentialMap;
        this.obstacleCells = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
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

    @Override
    public String toString() {
        return "obstacle = (x=" + x + ", y=" + y + ", w=" + width + ", h=" + height + ")";
    }
}