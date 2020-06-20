package com.github.ryuseiishida.pedestrian_simulation.util;


import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.cell.CellsMap;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.BoxLine;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    /**
     * application version
     */
    public static String version = "1.02";

    /**
     * シミュレーションの終了方法
     * not quit automatically -> "NONE"
     * Agents escape all -> "ESCAPED"
     * Specify end step -> "END_STEP"
     */
    public static String END = "NONE";

    /**
     * シミュレーション終了ステップ
     */
    public static int END_STEP;

    /**
     * ログの記録
     */
    public static boolean IS_WRITE_LOG = false;

    /**
     * LDA コーパス保存ステップ
     * END が"ESCAPED"の場合はそのタイミングが優先される
     * nullの場合はCtr+Lで保存する
     */
    public static int LDA_OUT_PRINT_STEP = END_STEP;

    /**
     * LDA 文書の区切り方
     */
    public static int LDA_SPLIT_STEP = 60;

    //Environment
    //1 pixel = 1 cm
    private static float METER = 100f;
    public static Vector2 SCALE = new Vector2(100 * METER, 100 * METER);
    public static int CELL_INTERVAL = 10;

    //Obstacles
    public static String OBSTACLE_MAP_PATH;

    public static CellsMap ENV_CELLS_MAP = new CellsMap(SCALE, CELL_INTERVAL);

    //非歩行領域
    public static ArrayList<BoxLine> BOX_LIST = new ArrayList<>(Arrays.asList(
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

    //potential
    public static float AGENT_KIM_POTENTIAL_WEIGHT = 200;
    public static float AGENT_KIM_POTENTIAL_RANGE = 1000;
    public static float OBSTACLE_KIM_POTENTIAL_WEIGHT = 700;
    public static float OBSTACLE_KIM_POTENTIAL_RANGE = 20;
    public static float POTENTIAL_DELTA = 1f;

    //agent
    public static int INIT_AGENT_NUM = 100;
    public static int GOAL_AGENT_NUM = 0;
    public static Tuple<Float> INIT_RANDOM_X = new Tuple<>(0f, SCALE.x);
    public static Tuple<Float> INIT_RANDOM_Y = new Tuple<>(0f, SCALE.y);
    //ゴールを知っているエージェントのゴール番号 or "random"
    public static int AGENT_ACTION_INTERVAL = 60; //1stepあたり何回行動できるか
    //public static float AGENT_SPEED = 75f / AGENT_ACTION_INTERVAL; //1.5m/s
    public static float AGENT_SPEED = 250 / AGENT_ACTION_INTERVAL; //2.5m/s
    public static float AGENT_RADIUS = 25; //50cm
    public static float VIEW_RADIUS_LENGTH = 5 * METER;
    public static float VIEW_DEGREE = 50f;
}
