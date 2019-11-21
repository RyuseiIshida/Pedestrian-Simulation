package com.simulation.pedestrian.obstacle;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.environment.Environment;

public class Fire {
    private Vector2 burningPoint;
    private float fireSpeed = 1f;
    private float spreadFireRange;

    public Fire(Vector2 burningPoint) {
        this.burningPoint = burningPoint;
        this.spreadFireRange = this.fireSpeed;
    }

    public void spreadFire(){
        this.spreadFireRange += this.fireSpeed;
    }

    public void getTargetPosToFireDst(Vector2 pos){

    }

    public float getFireSpeed() {
        return fireSpeed;
    }

    public float getSpreadFireRange() {
        return spreadFireRange;
    }

    public Vector2 getBurningPoint() {
        return burningPoint;
    }
}
