package com.simulation.pedestrian;

import com.badlogic.gdx.math.Vector2;

public class Goal {
    private Vector2 position;
    private float width;
    private float height;

    public Goal(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getPositionX() {
        return position.x;
    }

    public float getPositionY() {
        return position.y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Vector2 getCenter() {
        return new Vector2(position).add(width / 2, height / 2);
    }

    public Vector2 getLeftButtom() {
        return position;
    }

    public Vector2 getLeftTop() {
        return new Vector2(getPositionX(), getPositionY() + height);
    }

    public Vector2 getRightButtom() {
        return new Vector2(getPositionX() + width, getPositionY());
    }

    public Vector2 getRightTop() {
        return new Vector2(getPositionX() + width, getPositionY() + height);
    }

    @Override
    public String toString() {
        return "Goal = (x=" + position.x + ", y=" + position.y + ", w=" + width + ", h=" + height + ")";
    }

}
