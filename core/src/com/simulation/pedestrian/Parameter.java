package com.simulation.pedestrian;


import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Obstacle.Obstacle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Parameter {
    //Environment
    public static final Vector2 SCALE = new Vector2(800 * 5f, 480 * 5f);
    public static final ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(new Goal(1800,100,120,20),
            //new Goal(SCALE.x-100, SCALE.y-150, 100, 150)));
            new Goal(900, 100, 120, 20)));
    public static final float CELLINTERVAL = 10;

    //Potential
    public static final float MAXPOTENTIAL = 10;
    public static final float AGENT_KIMPOTENTIALWEIGHT = 5;
    public static final float AGENT_KIMPOTENTIALRANGE = 50;
    public static final float OBSTACLE_KIMPOTENTIALWEIGHT = 1;
    public static final float OBSTACLE_KIMPOTENTIALRANGE = 15;

    //Agent
    public static final int initAgentNum = 30;
    public static final int goalAgentNum = 30;
    public static final int followNum = 3;
    public static final int moveGroupNum = 2;
    public static final float agentSpeed = 2.5f;
    public static final float agentRadius = 30;
    public static final float viewRadius = 400;
    public static final float viewDegree = 60;
    public static final float stepInterval = 120;
}
