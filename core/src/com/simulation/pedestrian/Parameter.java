package com.simulation.pedestrian;


import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Obstacle.Obstacle;
import com.simulation.pedestrian.Potential.PotentialMap;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    //Operation
    public static final int MODE = 0;//0はシミュレーション, 1は軌跡描画
    public static final boolean ISWRITELOG = false; //ログを記録?
    public static final float ENDSTEP = 60 * 25; //0は終わらない
    public static final int ATTEMPTSNUM = 10; //試行回数

    //Environment
    public static final Vector2 SCALE = new Vector2(800 * 5f, 480 * 5f);
    public static final ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(new Goal(100,100,20,100),
            //new Goal(SCALE.x-100, SCALE.y-150, 100, 150)));
            new Goal(3100, 2000, 20, 100)));
    public static final float CELLINTERVAL = 10;

    //Potential
    public static final float MAXPOTENTIAL = 10;
    public static final PotentialMap POTENTIALMAP = new PotentialMap(SCALE, CELLINTERVAL, MAXPOTENTIAL);
    public static final float AGENT_KIMPOTENTIALWEIGHT = 1;
    public static final float AGENT_KIMPOTENTIALRANGE = 30;
    public static final float OBSTACLE_KIMPOTENTIALWEIGHT = 100;
    public static final float OBSTACLE_KIMPOTENTIALRANGE = 10;

    //Obstacle
    public static final ArrayList<Obstacle> obstacles = new ArrayList<>(Arrays.asList(
            //leftLine
            new Obstacle(100, 200, 10, 1900, POTENTIALMAP),
            //rightLine
            new Obstacle(3100, 100, 10, 1900, POTENTIALMAP),
            //bottomLine
            new Obstacle(100, 100, 3000, 10, POTENTIALMAP),
            //TopLine
            new Obstacle(100, 2100, 3000, 10,POTENTIALMAP)
    ));

    //Agent
    public static final int initAgentNum = 200;
    public static final int goalAgentNum = 0;
    public static float initAgentRandomPosX1 = 950;
    public static float initAgentRandomPosX2 = 2300;
    public static float initAgentRandomPosY1 = 1300;
    public static float initAgentRandomPosY2 = 1600;
    //ゴールを知っているエージェントのゴール番号 or "random"
    public static final String goalAgentDestination = "0";
    public static final float agentSpeed = 2.5f;
    public static final float agentRadius = 30;
    public static final float viewRadius = 400;
    public static final float viewDegree = 160;

    //utility parameter
    public static final float alpha = -0.003f;
    public static final float beta = -2;
    public static final float gamma = -0.003f;
    public static final float delta = 0.1f;
    public static final float epsilon = -0.0001f;
}
