package com.simulation.pedestrian.Agent;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Parameter;
import com.simulation.pedestrian.Potential.PotentialCell;
import com.simulation.pedestrian.Potential.PotentialManager;
import com.simulation.pedestrian.Util.Tuple;
import com.simulation.pedestrian.Util.Vector;

import java.util.ArrayList;
import java.util.List;

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
        setPotentialVector(direction);
        //TODO 掛け算じゃなくて足し算でスピードを変化できるようにする
        velocity = direction.scl(speed);
        position.add(velocity);
    }

    public void setPotentialVector(Vector2 direction) {
        List<PotentialCell> nearCell = new ArrayList<>();
        Tuple index = PotentialManager.getEnvPotentialMap().getIndex(position);
        int range = 1;
        //TODO BAG:範囲外のインデックス参照
        for (int i = index.t1 - range; i <= index.t1 + range; i++) {
            for (int j = index.t2 - range; j <= index.t2 + range; j++) {
                nearCell.add(PotentialManager.getEnvPotentialMap().getMatrixPotentialCell(i, j));
            }
        }
        for (PotentialCell potentialCell : nearCell) {
            if (potentialCell.getPotential() != 0) {
                Vector2 pVec = Vector.direction(potentialCell.getCenterPoint(), position);
                float px = 0, py = 0;
                px = pVec.x;
                py = pVec.y;
                direction.add(px, py).nor();
            }
        }
    }


    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity(){
        return velocity;
    }
}
