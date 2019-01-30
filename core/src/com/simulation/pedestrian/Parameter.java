package com.simulation.pedestrian;


import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Parameter {
    //Environment
    public static final Vector2 SCALE = new Vector2(800 * 1.5f, 480 * 1.5f);
    public static final ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(new Goal(0,0,30,50),
            new Goal(SCALE.x-30, SCALE.y-50, 30, 50)));
    public static final float CELLINTERVAL = 5;

    //Potential
    public static final float MAXPOTENTIAL = 10;
    public static final float AGENTPOTENTIAL = 1;
    public static final float OBSTACLEPOTENTIAL = MAXPOTENTIAL;
    public static final int OBSTACLEPOTENTIALRANGE = 1;

    //KIM Potential
    public static final float KIMPOTENTIALWEIGHT = 1f;
    public static final float KIMPOTENTIALRANGE = 10;

    //Agent
    public static final int initAgentNum = 20;
    public static final int goalAgentNum = 0;
    public static final int followNum = 3;
    public static final int moveGroupNum = 2;
    public static final float agentSpeed = 1;
    public static final float agentRadius = 10;
    public static final float viewRadius = 200;
    public static final float viewDegree = 60;
    public static final float stepInterval = 120;
}
