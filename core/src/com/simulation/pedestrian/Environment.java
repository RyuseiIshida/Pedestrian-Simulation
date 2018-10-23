package com.simulation.pedestrian;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Agent.Agent;
import com.simulation.pedestrian.Obstacle.Obstacle;
import com.simulation.pedestrian.Potential.PotentialMap;

import java.util.ArrayList;
import java.util.List;

public class Environment {
    private float step;
    private Vector2 scale = Parameter.SCALE;
    private PotentialMap envPotentialMap;
    private float cellInterval = Parameter.CELLINTERVAL;
    private float maxPotential = Parameter.MAXPOTENTIAL;
    private float obstaclePotential = Parameter.OBSTACLEPOTENTIAL;
    private List<Agent> agents;

    public Environment(){
        step = 0;
        agents = new ArrayList<>();
        envPotentialMap = new PotentialMap(scale, cellInterval, maxPotential);
        new Obstacle(150, 150, 300, 100, envPotentialMap, obstaclePotential);
    }

    public void update() {
        agents.stream()
                .parallel()
                .forEach(agent -> {
                    try {
                        agent.action();
                    } catch (final Exception l_exception) {
                    }
                });
        step++;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public float getStep() {
        return step;
    }

    //Potential

    public PotentialMap getEnvPotentialMap() {
        return envPotentialMap;
    }

    //Agent

    public void spawnAgent1(Vector2 pos){
        agents.add(new Agent(this, pos));
    }

    public void spawnAgent2(Vector2 pos){
        agents.add(new Agent(this, pos, Parameter.GOAL));
    }

    public List<Agent> getAgents() {
        return agents;
    }
}
