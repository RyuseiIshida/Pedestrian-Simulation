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
    public static String VERSION = "1.10";

    /**
     * application about message
     */
    public static String ABOUT_MESSAGE = "GitHub @RyuseiIshida\n" + "https://github.com/RyuseiIshida/Pedestrian-Simulation";

    /**
     * application version message
     */
    public static String VERSION_MESSAGE = "履歴\n" +
            "1.01\t基本的なシミュレーション機能を提供\n" +
            "1.02\t細かなバグを修正およびUIの更新\n" +
            "1.03\tパラメーター変更機能を追加\n" +
            "1.04\t軽微なUIの修正\n" +
            "1.05\t障害物の保存および読み込み機能を追加\n" +
            "1.06\tシミュレーションログの書き出し機能を追加\n" +
            "1.07\t軽微なバグの修正およびシミュレーションログの書き出し・読み込み機能にゴールオブジェクトと障害物オブジェクトが対応\n" +
            "1.08\t表示設定機能を更新\n" +
            "1.09\tシミュレーション設定環境の保存・読み込み機能を追加(Open/Save Work Space)\n" +
            "1.10\t消しゴム機能の追加\n" +
            "\n" +
            "次回アップデート\n" +
            "1.1x\t軽微な修正";

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

    /** ログの出力先 */
    public static String WRITE_LOG_PATH;

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
    public static Vector2 BACKGROUND_TEXTURE_SIZE = SCALE;

    //Obstacles
    public static String OBSTACLE_MAP_PATH;

    public static CellsMap ENV_CELLS_MAP = new CellsMap(SCALE, CELL_INTERVAL);

    //非歩行領域
    public static ArrayList<BoxLine> BOX_LIST = new ArrayList<>(Arrays.asList());

    //agent
    public static int INIT_AGENT_NUM = 100;
    public static int GOAL_AGENT_NUM = 0;
    public static Tuple<Float> INIT_RANDOM_X = new Tuple<>(0f, SCALE.x);
    public static Tuple<Float> INIT_RANDOM_Y = new Tuple<>(0f, SCALE.y);
    //public static float AGENT_SPEED = 75f / AGENT_ACTION_INTERVAL; //1.5m/s
    public static float AGENT_SPEED = 4.1f; //2.5m/s
    public static float AGENT_RADIUS = 25; //50cm
    public static float VIEW_RADIUS_LENGTH = 5 * METER;
    public static float VIEW_DEGREE = 50f;

    //potential
    public static float GOAL_POTENTIAL_WEIGHT = 500;
    public static float GOAL_POTENTIAL_RANGE = 10000;
    public static float AGENT_POTENTIAL_WEIGHT = 1000;
    public static float AGENT_POTENTIAL_RANGE = AGENT_RADIUS;
    public static float OBSTACLE_POTENTIAL_WEIGHT = 1200;
    public static float OBSTACLE_POTENTIAL_RANGE = AGENT_RADIUS;
    public static float POTENTIAL_DELTA = CELL_INTERVAL;
}
