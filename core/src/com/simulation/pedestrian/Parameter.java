package com.simulation.pedestrian;


import com.badlogic.gdx.math.Vector2;

public final class Parameter {
    //Environment
    public static final Vector2 SCALE = new Vector2(800*1.5f, 480*1.5f);
    public static final Vector2 GOAL = new Vector2(0, 0);
    public static final float CELLINTERVAL = 10;

    //Potential
    public static final float MAXPOTENTIAL = 0;

    //Agent
    public static final int initAgentNum = 0;
    public static final int goalAgentNum = 0;
    public static final float agentSpeed = 1;
    public static final float agentRadius = 10;
    public static final float viewMax = 0;
    public static final float viewDegree = 0;
}
