package com.simulation.pedestrian.cell;

import com.badlogic.gdx.math.Vector2;

public class Cell {
    private float cellInterval;
    private Vector2 leftBottomPoint;
    private Vector2 leftTopPoint;
    private Vector2 rightBottomPoint;
    private Vector2 rightTopPoint;
    private Vector2 centerPoint;

    public Cell(Vector2 position, float interval) {
        this.cellInterval = interval;
        leftBottomPoint = new Vector2(position);
        leftTopPoint = new Vector2(position.x, position.y + interval);
        rightBottomPoint = new Vector2(position.x + interval, position.y);
        rightTopPoint = new Vector2(position.x + interval, position.y + interval);
        centerPoint = new Vector2(position.x + interval / 2, position.y + interval / 2);
    }

    public float getCellInterval() {
        return cellInterval;
    }

    public Vector2 getLeftBottomPoint() {
        return leftBottomPoint;
    }

    public Vector2 getLeftTopPoint() {
        return leftTopPoint;
    }

    public Vector2 getRightBottomPoint() {
        return rightBottomPoint;
    }

    public Vector2 getRightTopPoint() {
        return rightTopPoint;
    }

    public Vector2 getCenterPoint() {
        return centerPoint;
    }

}
