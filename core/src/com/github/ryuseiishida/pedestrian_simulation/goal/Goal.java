package com.github.ryuseiishida.pedestrian_simulation.goal;

import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.agent.Agent;

public class Goal {
    private Vector2 position;
    private float width;
    private float height;

    public Goal(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
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

    public Vector2 getLeftBottom() {
        return position;
    }

    public Vector2 getLeftTop() {
        return new Vector2(getPositionX(), getPositionY() + height);
    }

    public Vector2 getRightBottom() {
        return new Vector2(getPositionX() + width, getPositionY());
    }

    public Vector2 getRightTop() {
        return new Vector2(getPositionX() + width, getPositionY() + height);
    }

    public boolean isAgentInGoal(Agent agent) {
        float aPosX = agent.getPosition().x;
        float aPosY = agent.getPosition().y;
        if (aPosX > position.x
                && aPosY > position.y
                && aPosX < getRightTop().x
                && aPosY < getRightTop().y) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "goal = (x=" + position.x + ", y=" + position.y + ", w=" + width + ", h=" + height + ")";
    }

}
