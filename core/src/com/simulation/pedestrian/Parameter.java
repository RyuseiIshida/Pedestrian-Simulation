package com.simulation.pedestrian;


import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.cell.CellsMap;
import com.simulation.pedestrian.goal.Goal;
import com.simulation.pedestrian.obstacle.Line;
import com.simulation.pedestrian.obstacle.Obstacle;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    /** "DefaultSimulation" or "LogSimulation" or "CreateMap" */
    public static String MODE = "DefaultSimulation";

    /** ログの記録 */
    public static boolean ISWRITELOG = false;

    /** シミュレーションの打ち切りステップ */
    public static float ENDSTEP = 0;

    /** シミュレーション試行回数 */
    public static int ATTEMPTSNUM = 0;

    //Environment
    public static Vector2 SCALE = new Vector2(1000 * 10f, 1000 * 10f);
    public static ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(new Goal(1960, 1300, 120, 20)));
    public static int CELL_INTERVAL = 10;

    //Fire
    public static Vector2 FIRE_POINT = new Vector2();

    //potential
    public static float AGENT_KIM_POTENTIAL_WEIGHT = 200;
    public static float AGENT_KIM_POTENTIAL_RANGE = 1000;
    public static float OBSTACLE_KIM_POTENTIAL_WEIGHT = 700;
    public static float OBSTACLE_KIM_POTENTIAL_RANGE = 20;
    public static float POTENTIAL_DELTA = 1f;

    //agent
    public static int INIT_AGENT_NUM = 100;
    public static int GOAL_AGENT_NUM = 0;
    public static float INIT_RANDOM_X1 = 0;
    public static float INIT_RANDOM_X2 = 10000;
    public static float INIT_RANDOM_Y1 = 0;
    public static float INIT_RANDOM_Y2 = 10000;
    //ゴールを知っているエージェントのゴール番号 or "random"
    public static String GOALAGENT_DESTINATION = "0";
    public static float AGENT_SPEED = 2.5f;
    public static float AGENT_RADIUS = 20;
    public static float VIEW_RADIUS_LENGTH = 400;
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
