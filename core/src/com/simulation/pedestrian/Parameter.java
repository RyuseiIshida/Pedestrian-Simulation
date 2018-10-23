package com.simulation.pedestrian;


import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Parameter {
    //Environment
    public static final Vector2 SCALE = new Vector2(800*1.5f, 480*1.5f);
    public static final Goal GOAL1 = new Goal(10,10,30,50);
    public static final List<Goal> GOALS = new ArrayList<>(Arrays.asList(GOAL1));
    public static final float CELLINTERVAL = 5;

    //Potential
    public static final float MAXPOTENTIAL = 0;
    public static final float OBSTACLEPOTENTIAL = 5;

    //Agent
    public static final int initAgentNum = 0;
    public static final int goalAgentNum = 0;
    public static final float agentSpeed = 1;
    public static final float agentRadius = 10;
    public static final float viewMax = 0;
    public static final float viewDegree = 0;
    public static final float stepInterval = 120;
}
