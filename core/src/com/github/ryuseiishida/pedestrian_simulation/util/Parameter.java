package com.github.ryuseiishida.pedestrian_simulation.util;


import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.cell.CellsMap;
import com.github.ryuseiishida.pedestrian_simulation.goal.Goal;
import com.github.ryuseiishida.pedestrian_simulation.obstacle.BoxLine;
import com.github.ryuseiishida.pedestrian_simulation.obstacle.Line;
import com.github.ryuseiishida.pedestrian_simulation.obstacle.Obstacle;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    /**
     * "DefaultSimulation" or "LogSimulation" or "CreateMap"
     */
    public static String MODE = "DefaultSimulation";

    /**
     * シミュレーションループ回数
     */
    public static int LOOP_NUM = 10;

    /**
     * シミュレーション終了ステップ
     */
    public static int END_STEP = 1000;

    /**
     * ログの記録
     */
    public static boolean IS_WRITE_LOG = true;
    
    public static boolean IS_WRITE_LOG_AGENT = true;
    
    public static boolean IS_WRITE_LOG_MACRO = false;

    /**
     * LDA コーパス保存ステップ
     * nullの場合はCtr+Lで保存する
     */
    public static int LDA_OUT_PRINT_STEP = 1000;

    /**
     * LDA 文書の区切り方
     */
    public static int LDA_SPLIT_STEP = 60;



    //Environment
    //1 pixel = 1 cm
    private static float METER = 100f;
    public static Vector2 SCALE = new Vector2(100 * METER, 100 * METER);
    public static ArrayList<Goal> GOALS = new ArrayList<>(Arrays.asList(
            new Goal(0, 200, 200, 200 + 200)
                        //new Goal(SCALE.x - 200, SCALE.y - 1400, 200, 200 + 200)
    ));
    public static int CELL_INTERVAL = 10;

    //Fire
    public static Vector2 FIRE_POINT = new Vector2();

    //Obstacles
    public static CellsMap ENV_CELLS_MAP = new CellsMap(SCALE, CELL_INTERVAL);

    public static ArrayList<BoxLine> Boxes = new ArrayList<>(Arrays.asList(
//            // 左上
//            new BoxLine(1200, 6200, 4500, 8000, ENV_CELLS_MAP),
//            //右上
//            new BoxLine(5700, 6200, 9000, 8000, ENV_CELLS_MAP),
//            // 左下
//            new BoxLine(1200, 1200, 4500, 3000, ENV_CELLS_MAP),
//            //右下
//            new BoxLine(5700, 1200, 9000, 3000, ENV_CELLS_MAP),
//            //中央左
//            new BoxLine(1200, 3700, 4500, 5500, ENV_CELLS_MAP),
//            //中央右
//            new BoxLine(5700, 3700, 9000, 5500, ENV_CELLS_MAP)
    ));

    public static ArrayList<Obstacle> OBSTACLES = new ArrayList<>(Arrays.asList(
            //left wall line
            new Line(200, 600, 200, SCALE.y - 1000, ENV_CELLS_MAP),
            //right wall line
            new Line(SCALE.x - 200, 200, SCALE.x - 200, SCALE.y - 1000, ENV_CELLS_MAP),
            //new Line(SCALE.x - 200, 200, SCALE.x - 200, SCALE.y - 1400, ENV_CELLS_MAP),
            // bottom wall line
            new Line(200, 200, SCALE.x - 200, 200, ENV_CELLS_MAP),
            // top wall line
            new Line(200, SCALE.y - 1000, SCALE.x - 200, SCALE.y - 1000, ENV_CELLS_MAP)
    ));

    public static Line experienceOLINE = new Line(0, 0, 0, 0, ENV_CELLS_MAP);

    //potential
    public static float AGENT_KIM_POTENTIAL_WEIGHT = 200;
    public static float AGENT_KIM_POTENTIAL_RANGE = 1000;
    public static float OBSTACLE_KIM_POTENTIAL_WEIGHT = 700;
    public static float OBSTACLE_KIM_POTENTIAL_RANGE = 20;
    public static float POTENTIAL_DELTA = 1f;

    //agent
    public static int INIT_AGENT_NUM = 100;
    public static int GOAL_AGENT_NUM = 30;
    public static Tuple<Float> INIT_RANDOM_X = new Tuple<>(300f, SCALE.x - 300);
    public static Tuple<Float> INIT_RANDOM_Y = new Tuple<>(300f, SCALE.y - 1100);
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
