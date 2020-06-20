package com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle;

import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.cell.Cell;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.cell.CellsMap;

import java.util.ArrayList;
import java.util.List;

public abstract class Obstacle {
    private CellsMap cellsMap;
    private List<Cell> obstacleCells;
    private Vector2 startPoint;
    private Vector2 endPoint;
    private Vector2 minPoint;
    private Vector2 maxPoint;

    Obstacle(float x1, float y1, float x2, float y2, CellsMap CellsMap) {
        this.cellsMap = CellsMap;
        this.obstacleCells = new ArrayList<>();
        this.startPoint = new Vector2(x1, y1);
        this.endPoint = new Vector2(x2, y2);
        this.minPoint = new Vector2(Math.min(x1, x2), Math.min(y1, y2));
        this.maxPoint = new Vector2(Math.max(x1, x2), Math.max(y1, y2));
    }

    abstract void setShapeObstacle();

    public CellsMap getCellsMap() {
        return cellsMap;
    }

    public List<Cell> getObstacleCells() {
        return obstacleCells;
    }

    public Vector2 getStartPoint() {
        return startPoint;
    }

    public Vector2 getEndPoint() {
        return endPoint;
    }

    public Vector2 getMaxPoint() {
        return maxPoint;
    }

    public Vector2 getMinPoint() {
        return minPoint;
    }

    @Override
    public String toString() {
        return "obstacle = (startPoint=" + startPoint + ", endPoint=" + endPoint + ")";
    }
}