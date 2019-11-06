package com.simulation.pedestrian.obstacle;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.potential.PotentialCell;
import com.simulation.pedestrian.potential.PotentialMap;
import com.simulation.pedestrian.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public abstract class Obstacle {
    private PotentialMap potentialMap;
    private List<PotentialCell> obstacleCells;
    private Vector2 startPoint;
    private Vector2 endPoint;
    private Tuple startIndex;
    private Tuple endIndex;

    protected Obstacle(float x1, float y1, float x2, float y2, PotentialMap potentialMap) {
        this.potentialMap = potentialMap;
        this.obstacleCells = new ArrayList<>();
        this.startPoint = new Vector2(x1, y1);
        this.endPoint = new Vector2(x2, y2);
    }

    abstract void setShapeObstacle();

    public PotentialMap getPotentialMap() {
        return potentialMap;
    }

    public List<PotentialCell> getObstacleCells() {
        return obstacleCells;
    }

    public Vector2 getStartPoint() {
        return startPoint;
    }

    public Vector2 getEndPoint() {
        return endPoint;
    }

    public Tuple getStartIndex() {
        return startIndex;
    }

    public Tuple getEndIndex() {
        return endIndex;
    }

    @Override
    public String toString() {
        return "obstacle = (startPoint=" + startPoint + ", endPoint=" + endPoint + ")";
    }
}