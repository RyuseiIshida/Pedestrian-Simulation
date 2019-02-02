package com.simulation.pedestrian;


import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    //Operation
    public static final int MODE = 0;//0はシミュレーション, 1は軌跡描画
    public static final float ENDSTEP = 0; //0は終わらない
    public static final int ATTEMPTSNUM = 1;
    public static boolean ISWRITELOG = true;

    //Environment
    public static final Vector2 SCALE = new Vector2(800 * 5f, 480 * 5f);
    public static final ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(new Goal(1800,180,120,20),
            //new Goal(SCALE.x-100, SCALE.y-150, 100, 150)));
            new Goal(900, 180, 120, 20)));
    public static final float CELLINTERVAL = 10;

    //Potential
    public static final float MAXPOTENTIAL = 10;
    public static final float AGENT_KIMPOTENTIALWEIGHT = 1;
    public static final float AGENT_KIMPOTENTIALRANGE = 45;
    public static final float OBSTACLE_KIMPOTENTIALWEIGHT = 1;
    public static final float OBSTACLE_KIMPOTENTIALRANGE = 10;

    //Agent
    public static final int initAgentNum = 40;
    public static final int goalAgentNum = 40;
    public static final int followNum = 3;
    public static final int moveGroupNum = 2;
    public static final float agentSpeed = 2.5f;
    public static final float agentRadius = 30;
    public static final float viewRadius = 400;
    public static final float viewDegree = 60;
    public static final float stepInterval = 120;
}
