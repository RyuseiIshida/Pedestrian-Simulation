package com.github.ryuseiishida.pedestrian_simulation.environment.object.cell;

import com.badlogic.gdx.math.Vector2;

/**
 * This class is the base class of the CellsMap class.
 */
public class Cell {
    /**
     * the interval of this cell
     */
    private float cellInterval;

    /**
     * the left-bottom position of this cell
     */
    private Vector2 leftBottomPoint;

    /**
     * the left-top position of this cell
     */
    private Vector2 leftTopPoint;

    /**
     * the right-bottom position of this cell
     */
    private Vector2 rightBottomPoint;

    /**
     * the right-top position of this cell
     */
    private Vector2 rightTopPoint;

    /**
     * the center-point of this cell
     */
    private Vector2 centerPoint;

    /**
     * This class is instantiated by the CellsMap class.
     *
     * @param position cell position
     * @param interval cell interval
     */
    Cell(Vector2 position, float interval) {
        this.cellInterval = interval;
        leftBottomPoint = new Vector2(position);
        leftTopPoint = new Vector2(position.x, position.y + interval);
        rightBottomPoint = new Vector2(position.x + interval, position.y);
        rightTopPoint = new Vector2(position.x + interval, position.y + interval);
        centerPoint = new Vector2(position.x + interval / 2, position.y + interval / 2);
    }

    /**
     * return the interval of this cell
     *
     * @return cell interval
     */
    public float getCellInterval() {
        return cellInterval;
    }

    /**
     * return the left-bottom position of this cell
     *
     * @return left bottom position
     */
    public Vector2 getLeftBottomPoint() {
        return leftBottomPoint;
    }

    /**
     * return the left-top position of this cell
     *
     * @return left-top position
     */
    public Vector2 getLeftTopPoint() {
        return leftTopPoint;
    }

    /**
     * return the right-bottom position of this cell
     *
     * @return right-bottom position
     */
    public Vector2 getRightBottomPoint() {
        return rightBottomPoint;
    }

    /**
     * return the right-top position of this cell
     *
     * @return right-top position
     */
    public Vector2 getRightTopPoint() {
        return rightTopPoint;
    }

    /**
     * return the center position of this cell
     *
     * @return center position
     */
    public Vector2 getCenterPoint() {
        return centerPoint;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Cell other = (Cell) obj;
        if ((int) centerPoint.x == (int) other.getCenterPoint().x) return false;
        if ((int) centerPoint.y == (int) other.getCenterPoint().y) return false;
        return false;
    }

}
