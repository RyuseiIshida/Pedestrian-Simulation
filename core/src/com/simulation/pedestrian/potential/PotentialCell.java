package com.simulation.pedestrian.potential;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.cell.Cell;

public class PotentialCell extends Cell {
    float maxPotential;
    float agentPotential;
    float obstaclePotential;
    float interval;

    public PotentialCell(Vector2 position, float interval, float maxPotential) {
        super(position, interval);
        this.maxPotential = maxPotential;
        this.interval = interval;
        agentPotential = 0;
        obstaclePotential = 0;
    }

    public void setAgentPotential(float agentPotential) {
        this.agentPotential = agentPotential;
    }

    public void addAgentPotential(float agentPotential) {
        this.agentPotential += agentPotential;
        if(agentPotential > maxPotential) {
            this.agentPotential = maxPotential;
        }
    }

    public float getAgentPotential() {
        return agentPotential;
    }

    public void setObstaclePotential(float obstaclePotential) {
        this.obstaclePotential = obstaclePotential;
    }

    public void addObstaclePotential(float potential) {
        obstaclePotential += potential;
        if(obstaclePotential > maxPotential){
            obstaclePotential = maxPotential;
        }
    }

    public float getObstaclePotential() {
        return obstaclePotential;
    }

    public float getPotential() {
        return agentPotential + obstaclePotential;
    }
}
