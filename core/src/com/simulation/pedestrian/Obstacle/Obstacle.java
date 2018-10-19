package com.simulation.pedestrian.Obstacle;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Potential.PotentialCell;
import com.simulation.pedestrian.Potential.PotentialMap;
import com.simulation.pedestrian.Util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class Obstacle {
    PotentialMap potentialMap;
    List<PotentialCell> obstacleCells;
    float potential;
    float cellInterval;

    public Obstacle(float x, float y, float w, float h, PotentialMap potentialMap, float potential) {
        this.potentialMap = potentialMap;
        this.obstacleCells = new ArrayList<>();
        this.potential = potential;
        this.cellInterval = potentialMap.getCellInterval();
        setShapeObstacle(x, y, w, h);
    }

    public Obstacle(Vector2 position, float w, float h, PotentialMap potentialMap, float potential) {
        this(position.x, position.y, w, h, potentialMap, potential);
    }

    public void setShapeObstacle(Vector2 position, float w, float h) {
        PotentialCell startCell = potentialMap.getPotentialCell(position);
        PotentialCell endCell = potentialMap.getPotentialCell(new Vector2(position.x + w, position.y + h));
        Tuple startIndex = potentialMap.getIndex(startCell);
        Tuple endIndex = potentialMap.getIndex(endCell);
        List<List<PotentialCell>> matrixCell = potentialMap.getMatrixPotentialCells();

        for (int i = startIndex.t1; i <= endIndex.t1; i++) {
            for (int j = startIndex.t2; j <= endIndex.t2; j++) {
                matrixCell.get(i).get(j).setObstaclePotential(potential);
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
}