package com.simulation.pedestrian;


import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.obstacle.Obstacle;
import com.simulation.pedestrian.potential.PotentialMap;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    //Operation
    // "Simulation" or "LogSimulation" or DrawLogAgentLines
    public static String MODE = "Simulation";
    public static boolean ISWRITELOG = false; //ログを記録?
    public static float ENDSTEP = 0; //0は終わらない
    public static int ATTEMPTSNUM = 0; //試行回数

    //Environment
    public static Vector2 SCALE = new Vector2(800 * 5f, 480 * 5f);
    public static ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(new Goal(2300, 180, 120, 20), new Goal(900, 180, 120, 20)));
    //            //new Goal(SCALE.x-100, SCALE.y-150, 100, 150)));
    //        new Goal(3100, 2000, 20, 100)));
    //public static final ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(new Goal(3100, 2000, 20, 100)));
    public static float CELL_INTERVAL = 10;

    //potential
    public static float MAX_POTENTIAL = Float.MAX_VALUE;
    public static PotentialMap POTENTIALMAP = new PotentialMap(SCALE, CELL_INTERVAL, MAX_POTENTIAL);
    public static float AGENT_KIM_POTENTIAL_WEIGHT = 200;
    public static float AGENT_KIM_POTENTIAL_RANGE = 100;
    public static float OBSTACLE_KIM_POTENTIAL_WEIGHT = 700;
    public static float OBSTACLE_KIM_POTENTIAL_RANGE = 10;

    //obstacle
    public static ArrayList<Obstacle> ALL_OBSTACLE = new ArrayList<>(Arrays.asList(
            //leftLine描画
//            new Obstacle(100, 100, 10, 2000, POTENTIALMAP),
//            //rightLine
//            //new obstacle(800, 100, 10, 1400, POTENTIALMAP),
//            new Obstacle(3100, 100, 10, 1900, POTENTIALMAP),
//            //bottomLine
//            new Obstacle(100, 100, 3000, 10, POTENTIALMAP),
//            //TopLine
//            //new obstacle(800,1500,2300, 10, POTENTIALMAP),
//            new Obstacle(100, 2100, 3000, 10, POTENTIALMAP)

            //leftLine
            //new Obstacle(900, 200, 1000, 1500, POTENTIALMAP),
            //rightLine
            new Obstacle(900, 200, 10, 1500, POTENTIALMAP),
            //bottomLine
            new Obstacle(2400, 200, 10, 1500, POTENTIALMAP),
            //TopLine
            new Obstacle(1010, 200, 1290, 10, POTENTIALMAP),
            new Obstacle(900, 1700, 1500, 10, POTENTIALMAP)


    ));

    //agent
    public static int INIT_AGENT_NUM = 20;
    public static int GOAL_AGENT_NUM = 20;
    //    public static float INIT_RANDOM_X1 = 150;
//    public static float INIT_RANDOM_X2 = 650;
//    public static float INIT_RANDOM_Y1 = 200;
//    public static float INIT_RANDOM_Y2 = 1500;
    public static float INIT_RANDOM_X1 = 950;
    public static float INIT_RANDOM_X2 = 2300;
    public static float INIT_RANDOM_Y1 = 1300;
    public static float INIT_RANDOM_Y2 = 1600;
    //ゴールを知っているエージェントのゴール番号 or "random"
    public static String GOALAGENT_DESTINATION = "0";
    public static float AGENT_SPEED = 2.5f;
    public static float AGENT_RADIUS = 30;
    public static float VIEW_RADIUS = 400;
    public static float VIEW_DEGREE = 160;

    //utility parameter
    public static float U_RANDOM_WALK = 0.5f;
    public static float U_FOLLOW_AGENT = 0;
    public static float U_MOVE_GOAL = 0;
    public static float ALPHA = 1f;
    public static float BETA = 0.3f;
    public static float GAMMA = 0.002f;
    public static float DELTA = 0.002f;
    public static float EPSILON = 0.0001f;
}
