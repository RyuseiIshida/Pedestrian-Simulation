package com.simulation.pedestrian;


import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Parameter {
    //Environment
    public static final Vector2 SCALE = new Vector2(800*1.5f, 480*1.5f);
    public static final Goal INITGOAL = new Goal(0,0,30,50);
    public static final float CELLINTERVAL = 50;

    //Potential
    public static final float MAXPOTENTIAL = 0;
    public static final float AGENTPOTENTIAL = 1;
    public static final float OBSTACLEPOTENTIAL = 5;

    //Agent
    public static final int initAgentNum = 10;
    public static final int goalAgentNum = 3;
    public static final int followNum = 2;
    public static final float agentSpeed = 1;
    public static final float agentRadius = 10;
    public static final float viewRadius = 100;
    public static final float viewDegree = 60;
    public static final float stepInterval = 120;
}
