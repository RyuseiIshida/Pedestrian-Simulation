package com.gihutb.ryuseiishida.simulation.evacuation.util;


import com.badlogic.gdx.math.Vector2;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.CellsMap;
import com.gihutb.ryuseiishida.simulation.evacuation.goal.Goal;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.BoxLine;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.Line;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.Obstacle;

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
    public static boolean IS_WRITE_LOG = false;

    //Environment
    //1 pixel = 1 cm
    private static float METER = 100f;
    public static Vector2 SCALE = new Vector2(100 * METER, 100 * METER);
    public static ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(
            new Goal(0, 200, 200, 200 + 200)
//            new Goal(8900, 4000, 100, 2000)
//            new Goal(-1000, -4000, 0, 0)
//            new Goal(9000 + 200, SCALE.y / 2 + 3000, 0, 0)
//            new Goal(9000 + 200, SCALE.y / 2 - 5000, 100, 2000)
    ));
    public static int CELL_INTERVAL = 10;

    //Fire
    public static Vector2 FIRE_POINT = new Vector2();

    //Obstacles
    public static CellsMap cellsMap = new CellsMap(SCALE, CELL_INTERVAL);

    public static ArrayList<BoxLine> Boxes = new ArrayList<>(Arrays.asList(
//            // 左上
//            new BoxLine(1200, 6200, 4500, 8000, cellsMap),
//            //右上
//            new BoxLine(5700, 6200, 9000, 8000, cellsMap),
//            // 左下
//            new BoxLine(1200, 1200, 4500, 3000, cellsMap),
//            //右下
//            new BoxLine(5700, 1200, 9000, 3000, cellsMap),
//            //中央左
//            new BoxLine(1200, 3700, 4500, 5500, cellsMap),
//            //中央右
//            new BoxLine(5700, 3700, 9000, 5500, cellsMap)
    ));

    public static ArrayList<Obstacle> OBSTACLES = new ArrayList<>(Arrays.asList(
//            //left wall line
//            new Line(0 - CELL_INTERVAL, 0, 0, SCALE.y, cellsMap),
//            //right wall line
//            new Line(SCALE.x, 0, SCALE.x, SCALE.y, cellsMap),
//            // bottom wall line
//            new Line(0 - CELL_INTERVAL, 0, SCALE.x, 0, cellsMap),
//            // top wall line
//            new Line(0, SCALE.y, SCALE.x, SCALE.y, cellsMap),

            //left wall line
            new Line(200, 600, 200, SCALE.y - 1000, cellsMap),
            //right wall line
            new Line(SCALE.x - 200, 200, SCALE.x - 200, SCALE.y - 1000, cellsMap),
            // bottom wall line
            new Line(200, 200, SCALE.x - 200, 200, cellsMap),
            // top wall line
            new Line(200, SCALE.y - 1000, SCALE.x - 200, SCALE.y - 1000, cellsMap)
//
//            // 左下
//            // left
//            new Line(1200, 1200, 1200, 3000, cellsMap),
//            //right
//            new Line(4000, 1200, 4000, 3000, cellsMap),
//            //bottom
//            new Line(1200, 1200, 4000, 1200, cellsMap),
//            //top
//            new Line(1200, 3000, 4000, 3000, cellsMap),
//
//            // 左上
//            // left
//            new Line(1200, 6200, 1200, 8000, cellsMap),
//            //right
//            new Line(4000, 6200, 4000, 8000, cellsMap),
//            //bottom
//            new Line(1200, 6200, 4000, 6200, cellsMap),
//            //top
//            new Line(1200, 8000, 4000, 8000, cellsMap),
//
//            //右上
//            new Line(6200, 6200, 6200, 8000, cellsMap),
//            //right
//            new Line(9000, 6200, 9000, 8000, cellsMap),
//            //bottom
//            new Line(6200, 6200, 9000, 6200, cellsMap),
//            //top
//            new Line(6200, 8000, 9000, 8000, cellsMap),
//
//
//            //右下
//            new Line(6200, 1200, 6200, 3000, cellsMap),
//            //right
//            new Line(9000, 1200, 9000, 3000, cellsMap),
//            //bottom
//            new Line(6200, 1200, 9000, 1200, cellsMap),
//            //top
//            new Line(6200, 3000, 9000, 3000, cellsMap),
//
//            //中央右
//            new Line(6200, 3700, 6200, 5500, cellsMap),
//            //right
//            new Line(9000, 3700, 9000, 5500, cellsMap),
//            //bottom
//            new Line(6200, 3700, 9000, 3700, cellsMap),
//            //top
//            new Line(6200, 5500, 9000, 5500, cellsMap),
//
//            //中央左
//            new Line(1200, 3700, 1200, 5500, cellsMap),
//            //right
//            new Line(4000, 3700, 4000, 5500, cellsMap),
//            //bottom
//            new Line(1200, 3700, 4000, 3700, cellsMap),
//            //top
//            new Line(1200, 5500, 4000, 5500, cellsMap)
    ));

    public static Line experienceOLINE = new Line(0, 0, 0, 0, cellsMap);

    //potential
    public static float AGENT_KIM_POTENTIAL_WEIGHT = 200;
    public static float AGENT_KIM_POTENTIAL_RANGE = 1000;
    public static float OBSTACLE_KIM_POTENTIAL_WEIGHT = 700;
    public static float OBSTACLE_KIM_POTENTIAL_RANGE = 20;
    public static float POTENTIAL_DELTA = 1f;

    //agent
    public static int INIT_AGENT_NUM = 100;
    public static int GOAL_AGENT_NUM = 30;
    public static Tuple<Float> INIT_RANDOM_X = new Tuple<>(200f, SCALE.x - 300);
    public static Tuple<Float> INIT_RANDOM_Y = new Tuple<>(200f, SCALE.y - 1100);
    //ゴールを知っているエージェントのゴール番号 or "random"
    public static String GOAL_AGENT_DESTINATION = "1";
    private static int AGENT_ACTION_INTERVAL = 10; //1秒あたり何回行動できるか
    public static float AGENT_SPEED = 150f / AGENT_ACTION_INTERVAL; //1.5m/s
    public static float AGENT_RADIUS = 50f; //50cm
    public static float VIEW_RADIUS_LENGTH = 10 * METER;
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
