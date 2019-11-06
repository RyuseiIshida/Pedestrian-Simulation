package com.simulation.pedestrian;


import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.goal.Goal;
import com.simulation.pedestrian.obstacle.Box;
import com.simulation.pedestrian.obstacle.Line;
import com.simulation.pedestrian.obstacle.Obstacle;
import com.simulation.pedestrian.potential.PotentialMap;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    // "DefaultSimulation" or "LogSimulation" or "CreateMap"
    public static String MODE = "DefaultSimulation";
    public static boolean ISWRITELOG = false; //ログを記録?
    public static float ENDSTEP = 0; //0は終わらない
    public static int ATTEMPTSNUM = 0; //試行回数

    //Environment
    public static Vector2 SCALE = new Vector2(1000 * 10f, 1000 * 10f);
    public static ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(new Goal(1960, 1300, 120, 20)));
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
    ));

    //agent
    public static int INIT_AGENT_NUM = 20;
    public static int GOAL_AGENT_NUM = 20;
    public static float INIT_RANDOM_X1 = 950;
    public static float INIT_RANDOM_X2 = 2300;
    public static float INIT_RANDOM_Y1 = 1300;
    public static float INIT_RANDOM_Y2 = 1600;
    //ゴールを知っているエージェントのゴール番号 or "random"
    public static String GOALAGENT_DESTINATION = "0";
    public static float AGENT_SPEED = 2.5f;
    public static float AGENT_RADIUS = 20;
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
