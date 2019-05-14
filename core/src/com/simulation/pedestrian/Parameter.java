package com.simulation.pedestrian;


import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Obstacle.Obstacle;
import com.simulation.pedestrian.Potential.PotentialMap;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    //Operation
    public static final int MODE = 0;//0はシミュレーション, 1はログを使用する,2は軌跡描画
    public static final boolean ISWRITELOG = false; //ログを記録?
    public static final float ENDSTEP = 60*30; //0は終わらない
    public static final int ATTEMPTSNUM = 1; //試行回数

    //Environment
    public static final Vector2 SCALE = new Vector2(800 * 5f, 480 * 5f);
    public static final ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(new Goal(100, 100, 20, 100),
//            //new Goal(SCALE.x-100, SCALE.y-150, 100, 150)));
            new Goal(3100, 2000, 20, 100)));
    //public static final ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(new Goal(3100, 2000, 20, 100)));
    public static final float CELL_INTERVAL = 10;

    //Potential
    public static final float MAX_POTENTIAL = 10;
    public static final PotentialMap POTENTIALMAP = new PotentialMap(SCALE, CELL_INTERVAL, MAX_POTENTIAL);
    public static final float AGENT_KIM_POTENTIAL_WEIGHT = 1;
    public static final float AGENT_KIM_POTENTIAL_RANGE = 30;
    public static final float OBSTACLE_KIM_POTENTIAL_WEIGHT = 100;
    public static final float OBSTACLE_KIM_POTENTIAL_RANGE = 10;

    //Obstacle
    public static final ArrayList<Obstacle> ALL_OBSTACLE = new ArrayList<>(Arrays.asList(
            //leftLine
            new Obstacle(100, 200, 10, 1900, POTENTIALMAP),
            //rightLine
            new Obstacle(3100, 100, 10, 1900, POTENTIALMAP),
            //bottomLine
            new Obstacle(100, 100, 3000, 10, POTENTIALMAP),
            //TopLine
            new Obstacle(100, 2100, 3000, 10, POTENTIALMAP)

    ));

    //Agent
    public static final int INIT_AGENT_NUM = 50;
    public static final int GOAL_AGENT_NUM = 10;
    public static float INIT_RANDOM_X1 = 150;
    public static float INIT_RANDOM_X2 = 650;
    public static float INIT_RANDOM_Y1 = 200;
    public static float INIT_RANDOM_Y2 = 1500;
    //ゴールを知っているエージェントのゴール番号 or "random"
    public static final String GOALAGENT_DESTINATION = "0";
    public static final float AGENT_SPEED = 2.5f;
    public static final float AGENT_RADIUS = 30;
    public static final float VIEW_RADIUS = 400;
    public static final float VIEW_DEGREE = 160;

    //utility parameter
    public static final float U_RANDOM_WALK = 0.5f;
    public static final float U_FOLLOW_AGENT = 0;
    public static final float U_MOVE_GOAL = 0;
    public static final float ALPHA = 1f;
    public static final float BETA = 0.3f;
    public static final float GAMMA = 0.002f;
    public static final float DELTA = 0.002f;
    public static final float EPSILON = 0.0001f;
}
