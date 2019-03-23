package com.simulation.pedestrian;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    //Operation
    public static final int MODE = 0;//0はシミュレーション, 1は軌跡描画
    public static boolean ISWRITELOG = false; //ログを記録?
    public static final float ENDSTEP = 60*25; //0は終わらない
    public static final int ATTEMPTSNUM = 10; //試行回数

    //Environment
    public static final Vector2 SCALE = new Vector2(800 * 5f, 480 * 5f);
    public static final ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(new Goal(2300,180,120,20),
            new Goal(900, 180, 120, 20)));
    public static final float CELLINTERVAL = 10;

    //Potential
    public static final float MAXPOTENTIAL = 10;
    public static final float AGENT_KIMPOTENTIALWEIGHT = 1;
    public static final float AGENT_KIMPOTENTIALRANGE = 30;
    public static final float OBSTACLE_KIMPOTENTIALWEIGHT = 1;
    public static final float OBSTACLE_KIMPOTENTIALRANGE = 10;

    //Agent
    public static final int initAgentNum = 50;
    public static final int goalAgentNum = 50;
    //ゴールを知っているエージェントのゴール番号 or "random"
    public static final String goalAgentDestination = "0";
    public static final int followNum = 3;
    public static final int moveGroupNum = 2;
    public static final float agentSpeed = 2.5f;
    public static final float agentRadius = 30;
    public static final float viewRadius = 400;
    public static final float viewDegree = 160;
    public static final float stepInterval = 120;
}
