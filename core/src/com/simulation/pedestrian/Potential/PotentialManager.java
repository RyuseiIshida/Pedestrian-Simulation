package com.simulation.pedestrian.Potential;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Agent.Agent;
import com.simulation.pedestrian.Obstacle.Obstacle;
import com.simulation.pedestrian.Parameter;
import com.simulation.pedestrian.Util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class PotentialManager {
    private static PotentialMap envPotentialMap;
    private static float obstaclePotential = Parameter.OBSTACLEPOTENTIAL;
    private static Vector2 scale = Parameter.SCALE;
    private static float cellInterval = Parameter.CELLINTERVAL;
    private static float maxPotential = Parameter.MAXPOTENTIAL;


    public PotentialManager() {
        envPotentialMap = new PotentialMap(scale, cellInterval, maxPotential);
        new Obstacle(150, 150, 300, 100, envPotentialMap, obstaclePotential);
    }

    public static PotentialMap getEnvPotentialMap() {
        return envPotentialMap;
    }

    public static void setAgentPotential(Agent agent){
       Vector2 position = agent.getPosition();
       envPotentialMap.getPotentialCell(position).setAgentPotential(1);
    }

}
