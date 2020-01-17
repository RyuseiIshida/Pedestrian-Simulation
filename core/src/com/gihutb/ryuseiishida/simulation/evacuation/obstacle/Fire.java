package com.gihutb.ryuseiishida.simulation.evacuation.obstacle;

import com.badlogic.gdx.math.Vector2;
import com.gihutb.ryuseiishida.simulation.evacuation.agent.Agent;

public class Fire {
    private Vector2 firePoint;
    private float fireSpeed = 0f;
    private float spreadFireRange;

    public Fire(Vector2 firePoint) {
        this.firePoint = firePoint;
        this.spreadFireRange = this.fireSpeed;
    }

    public Fire(Vector2 firePoint, float spreadFireRange) {
        this.firePoint = firePoint;
        this.spreadFireRange = spreadFireRange;
    }

    public void spreadFire() {
        this.spreadFireRange += this.fireSpeed;
    }

    public float dstFirePoint(Vector2 targetPos) {
        return firePoint.dst(targetPos);
    }

    public float dstFirePoint(Agent targetAgent) {
        return firePoint.dst(targetAgent.getPosition());
    }

    public float dstFireEdge(Vector2 targetPos) {
        return dstFirePoint(targetPos) - spreadFireRange;
    }

    public float dstFireEdge(Agent agent) {
        return dstFirePoint(agent) - spreadFireRange;
    }

    public float getFireSpeed() {
        return fireSpeed;
    }

    public float getSpreadFireRange() {
        return spreadFireRange;
    }

    public Vector2 getFirePoint() {
        return firePoint;
    }
}
