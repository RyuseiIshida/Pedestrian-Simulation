package com.simulation.pedestrian.obstacle;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.potential.PotentialCell;
import com.simulation.pedestrian.potential.PotentialMap;
import com.simulation.pedestrian.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public abstract class Obstacle {
    protected PotentialMap potentialMap;
    protected List<PotentialCell> obstacleCells;
    protected Vector2 startPoint;
    protected Vector2 endPoint;
    protected Tuple startIndex;
    protected Tuple endIndex;

    public Obstacle(float x1, float y1, float x2, float y2, PotentialMap potentialMap) {
        this.potentialMap = potentialMap;
        this.obstacleCells = new ArrayList<>();
        this.startPoint = new Vector2(x1, y1);
        this.endPoint = new Vector2(x2, y2);
    }

    abstract void setShapeObstacle();

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
        return "obstacle = (startPoint=" + startPoint + ", endPoint=" + endPoint + ")";
    }
}