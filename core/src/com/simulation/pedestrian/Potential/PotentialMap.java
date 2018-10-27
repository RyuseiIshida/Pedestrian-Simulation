package com.simulation.pedestrian.Potential;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class PotentialMap {
    List<PotentialCell> potentialCells;
    List<List<PotentialCell>> matrixPotentialCells;
    Vector2 scale;
    float maxPotential;
    float cellInterval;

    public PotentialMap(Vector2 scale, float cellInterval, float maxPotential) {
        potentialCells = new ArrayList<>();
        matrixPotentialCells = new ArrayList<>();
        this.scale = scale;
        this.cellInterval = cellInterval;
        this.maxPotential = maxPotential;
        initCell();
    }

    private void initCell() {
        for (int i = 0; i <= scale.x; i += cellInterval) {
            ArrayList<PotentialCell> array = new ArrayList<>();
            for (int j = 0; j <= scale.y; j += cellInterval) {
                PotentialCell cell = new PotentialCell(new Vector2(i, j), cellInterval, maxPotential);
                potentialCells.add(cell);
                array.add(cell);
            }
            matrixPotentialCells.add(array);
        }
    }

    public List<PotentialCell> getPotentialCells() {
        return potentialCells;
    }

    public List<List<PotentialCell>> getMatrixPotentialCells() {
        return matrixPotentialCells;
    }

    public PotentialCell getPotentialCell(Vector2 targetPosition) {
        PotentialCell matchCell = null;
        Tuple index = getIndex(targetPosition);
        return getMatrixPotentialCells().get(index.t1).get(index.t2);
    }

    public PotentialCell getMatrixPotentialCell(int i, int j) {
        return matrixPotentialCells.get(i).get(j);
    }

    public Tuple getIndex(Vector2 targetPosition) {
        PotentialCell matchPotentialCell = null;
        //線形探索
        int x = 0;
        for (int i = 0; i < matrixPotentialCells.size(); i++) {
            if (targetPosition.x < matrixPotentialCells.get(i).get(0).getRightButtomPoint().x) {
                break;
            }
            x++;
        }
        int y = 0;
        for (int i = 0; i < matrixPotentialCells.get(0).size(); i++) {
            if (targetPosition.y < matrixPotentialCells.get(0).get(i).getRightTopPoint().y) {
                break;
            }
            y++;
        }
        return new Tuple(x, y);
    }

    public Tuple getIndex(PotentialCell targetCell){
        return getIndex(targetCell.getCenterPoint());
    }

    public float getCellInterval() {
        return cellInterval;
    }
}
