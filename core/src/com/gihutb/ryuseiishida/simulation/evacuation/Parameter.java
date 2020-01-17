package com.gihutb.ryuseiishida.simulation.evacuation;


import com.badlogic.gdx.math.Vector2;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.CellsMap;
import com.gihutb.ryuseiishida.simulation.evacuation.goal.Goal;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.Line;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.Obstacle;
import com.gihutb.ryuseiishida.simulation.evacuation.util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    /**
     * "DefaultSimulation" or "LogSimulation" or "CreateMap"
     */
    public static String MODE = "DefaultSimulation";

    /**
     * ログの記録
     */
    public static boolean ISWRITELOG = false;

    /**
     * シミュレーションの打ち切りステップ
     */
    public static float ENDSTEP = 0;

    /**
     * シミュレーション試行回数
     */
    public static int ATTEMPTSNUM = 0;

    //Environment
    //1 pixel = 1 cm
    private static float METER = 100f;
    //public static Vector2 SCALE = new Vector2(120 * METER, 120 * METER);
    public static Vector2 SCALE = new Vector2(100 * METER, 100 * METER);
    public static ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(
            //new Goal(8900, 4000, 100, 2000)
            //new Goal(-1000, -4000, 0, 0)
            //new Goal(9000 + 200, SCALE.y / 2 + 3000, 0, 0)
            //new Goal(9000 + 200, SCALE.y / 2 - 5000, 100, 2000)
    ));
    public static int CELL_INTERVAL = 10;

    //Fire
    public static Vector2 FIRE_POINT = new Vector2();

    //Obstacles
    public static CellsMap cellsMap = new CellsMap(SCALE, CELL_INTERVAL);
    public static ArrayList<Obstacle> OBSTACLES = new ArrayList<>(Arrays.asList(
            //left wall line
            new Line(0 - CELL_INTERVAL, 0, 0, SCALE.y, cellsMap),
            //right wall line
            new Line(SCALE.x, 0, SCALE.x, SCALE.y, cellsMap),
            // bottom wall line
            new Line(0 - CELL_INTERVAL, 0, SCALE.x, 0, cellsMap),
            // top wall line
            new Line(0, SCALE.y, SCALE.x, SCALE.y, cellsMap)

//            new Line(0, 0, 0, 2000, cellsMap),
//            //new Line(5000+300, SCALE.y / 2 - 1000, 5000 + 300, SCALE.y / 2 + 1000, cellsMap),
//            new Line(0, 0, 9000, 0, cellsMap),
//            new Line(0, 2000, 3000, 2000, cellsMap),
//            new Line(3000, 2000, 3000, 6000, cellsMap),
//            new Line(3000, 6000, 9000, 6000, cellsMap),
//            new Line(5000, 4000, 9000, 4000, cellsMap),
//            new Line(4999, 2000, 4999, 4000, cellsMap),
//            new Line(5000, 2000, 9000, 2000, cellsMap),
//            new Line(9000, 0, 9000, 2000, cellsMap)
//
//            new Line(0, 0, 0, 2000, cellsMap),
//            new Line(0, 0, 9000, 0, cellsMap),
//            new Line(0, 2000, 3000, 2000, cellsMap),
//            new Line(5000, 2000, 9000, 2000, cellsMap),
//            new Line(9000, 0, 9000, 2000, cellsMap),
//            new Line(3000, 2000, 3000, 2999, cellsMap),
//            new Line(3000, 2999, 9000, 6000, cellsMap),
//            new Line(5000, 2000, 9000, 4000, cellsMap)
    ));

    public static Line experienceOLINE = new Line(5000, 0, 5000, 2000, cellsMap);

    //potential
    public static float AGENT_KIM_POTENTIAL_WEIGHT = 200;
    public static float AGENT_KIM_POTENTIAL_RANGE = 1000;
    public static float OBSTACLE_KIM_POTENTIAL_WEIGHT = 700;
    public static float OBSTACLE_KIM_POTENTIAL_RANGE = 20;
    public static float POTENTIAL_DELTA = 1f;

    //agent
    public static int INIT_AGENT_NUM = 300;
    public static int GOAL_AGENT_NUM = 0;
    //public static Tuple<Float> INIT_RANDOM_X = new Tuple<>(50f, 19f * METER);
    public static Tuple<Float> INIT_RANDOM_X = new Tuple<>(50f, SCALE.x);
    //public static Tuple<Float> INIT_RANDOM_X = new Tuple<>(300f, SCALE.x - 300f);
    //public static Tuple<Float> INIT_RANDOM_Y = new Tuple<>(50f,19f * METER);
    public static Tuple<Float> INIT_RANDOM_Y = new Tuple<>(50f,SCALE.y);
    //public static Tuple<Float> INIT_RANDOM_Y = new Tuple<>(300f, SCALE.y - 300f);
    //ゴールを知っているエージェントのゴール番号 or "random"
    public static String GOAL_AGENT_DESTINATION = "1";
    private static int AGENT_ACTION_INTERVAL = 10; //1秒あたり何回行動できるか
    public static float AGENT_SPEED = 150f / AGENT_ACTION_INTERVAL; //1.5m/s
    public static float AGENT_RADIUS = 50f; //50cm
    public static float VIEW_RADIUS_LENGTH = 20 * METER;
    public static float VIEW_DEGREE = 100f;

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
