package com.simulation.pedestrian.Agent;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Parameter;
import com.simulation.pedestrian.Util.Vector;

public class Agent {
    private static final float radius = Parameter.agentRadius;
    private float speed = Parameter.agentSpeed;
    private String stateTag;
    private Vector2 position;
    private Vector2 goal;
    private Vector2 movePos;
    private Vector2 velocity;

    public Agent(Vector2 position) {
        this.position = position;
        this.movePos = position;
    }

    public Agent(Vector2 position, Vector2 goal) {
        this.stateTag = StateTag.moveGoal;
        this.position = position;
        this.goal = goal;
        this.movePos = goal;
    }


    public void action() throws Exception {
        move();
    }

    public void move() {
        Vector2 direction = Vector.direction(position, movePos);
        //TODO 掛け算じゃなくて足し算でスピードを変化できるようにする
        velocity = direction.scl(speed);
        position.add(velocity);
    }


    public Vector2 getPosition() {
        return position;
    }
}
