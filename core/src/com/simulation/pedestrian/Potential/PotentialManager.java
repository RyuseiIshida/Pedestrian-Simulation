package com.simulation.pedestrian.Potential;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Obstacle.Obstacle;
import com.simulation.pedestrian.Parameter;
import com.simulation.pedestrian.Util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class PotentialManager {
    private static PotentialMap envPotentialMap;
    private static PotentialMap obstaclesPotentialMap;
    private static List<Obstacle> obstacles = new ArrayList<>();
    private static float obstaclePotential = Parameter.OBSTACLEPOTENTIAL;
    private static PotentialMap agentsPotentialMap;
    private static Vector2 scale = Parameter.SCALE;
    private static float cellInterval = Parameter.CELLINTERVAL;
    private static float maxPotential = Parameter.MAXPOTENTIAL;


    public PotentialManager() {
        envPotentialMap = new PotentialMap(scale, cellInterval, maxPotential);
        setObstacle(150, 150, 300, 100);
    }

    public static void setObstacle(int x, int y, int width, int height){
        obstaclesPotentialMap = new PotentialMap(scale, cellInterval, maxPotential);
        Obstacle obstacle = new Obstacle(x, y, width, height, obstaclesPotentialMap, obstaclePotential );
        for (PotentialCell cell : obstacle.getObstacleCells()) {
            Tuple index = obstaclesPotentialMap.getIndex(cell);
            obstaclesPotentialMap.getMatrixPotentialCell(index.t1, index.t2).setObstaclePotential(obstaclePotential);
        }
        obstacles.add(obstacle);
    }

    public static List<Obstacle> getObstacles() {
        return obstacles;
    }
}
