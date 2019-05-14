package com.simulation.pedestrian;


import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Obstacle.Obstacle;
import com.simulation.pedestrian.Potential.PotentialMap;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    //Operation
    public static final int MODE = 1;//0はシミュレーション, 1はログを使用する,2は軌跡描画
    public static final boolean ISWRITELOG = true; //ログを記録?
    public static final float ENDSTEP = 60*30; //0は終わらない
    public static final int ATTEMPTSNUM = 1; //試行回数

    //Environment
    public static final Vector2 SCALE = new Vector2(800 * 5f, 480 * 5f);
    public static final ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(new Goal(100, 100, 20, 100),
//            //new Goal(SCALE.x-100, SCALE.y-150, 100, 150)));
            new Goal(3100, 2000, 20, 100)));
    //public static final ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(new Goal(3100, 2000, 20, 100)));
    public static final float CELLINTERVAL = 10;

    //Potential
    public static final float MAXPOTENTIAL = 10;
    public static final PotentialMap POTENTIALMAP = new PotentialMap(SCALE, CELLINTERVAL, MAXPOTENTIAL);
    public static final float AGENT_KIMPOTENTIALWEIGHT = 1;
    public static final float AGENT_KIMPOTENTIALRANGE = 30;
    public static final float OBSTACLE_KIMPOTENTIALWEIGHT = 100;
    public static final float OBSTACLE_KIMPOTENTIALRANGE = 10;

    //Obstacle
    //Obstacle
    public static final ArrayList<Obstacle> obstacles = new ArrayList<>(Arrays.asList(
            //leftLine
            new Obstacle(100, 200, 10, 1900, POTENTIALMAP),
            //new Obstacle(100, 100, 10, 2000, POTENTIALMAP),
            //new Obstacle(700, 100, 10, 1400, POTENTIALMAP),
            //rightLine
            new Obstacle(3100, 100, 10, 1900, POTENTIALMAP),
            //bottomLine
            new Obstacle(100, 100, 3000, 10, POTENTIALMAP),
            //TopLine
            //new Obstacle(700, 1500, 2400, 10, POTENTIALMAP),
            new Obstacle(100, 2100, 3000, 10, POTENTIALMAP)

    ));

    //Agent
    public static final int initAgentNum = 50;
    public static final int goalAgentNum = 10;
    public static float initAgentRandomPosX1 = 150;
    public static float initAgentRandomPosX2 = 650;
    public static float initAgentRandomPosY1 = 200;
    public static float initAgentRandomPosY2 = 1500;
    //ゴールを知っているエージェントのゴール番号 or "random"
    public static final String goalAgentDestination = "0";
    public static final float agentSpeed = 2.5f;
    public static final float agentRadius = 30;
    public static final float viewRadius = 400;
    public static final float viewDegree = 160;

    //utility parameter
    public static final float uRandomWalk = 0.5f;
    public static final float uFollowAgent = 0;
    public static final float uMoveGoal = 0;
    public static final float alpha = 1f;
    public static float beta = 0.3f;
    public static final float gamma = 0.002f;
    public static final float delta = 0.002f;
    public static final float epsilon = 0.0001f;
}
