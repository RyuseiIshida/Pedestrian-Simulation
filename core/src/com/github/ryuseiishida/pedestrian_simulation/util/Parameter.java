package com.github.ryuseiishida.pedestrian_simulation.util;

import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.cell.CellsMap;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle.BoxLine;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    /**
     * Application version
     */
    public static String VERSION = "1.14";

    /**
     * Application about message
     */
    public static String ABOUT_MESSAGE = "GitHub @RyuseiIshida\n"
            + "https://github.com/RyuseiIshida/Pedestrian-Simulation";

    /**
     * Application version update history
     */
    public static String VERSION_MESSAGE = "履歴\n"
            + "1.01\t基本的なシミュレーション機能を提供\n"
            + "1.02\t細かなバグを修正およびUIの更新\n"
            + "1.03\tパラメーター変更機能を追加\n"
            + "1.04\t軽微なUIの修正\n"
            + "1.05\t障害物の保存および読み込み機能を追加\n"
            + "1.06\tシミュレーションログの書き出し機能を追加\n"
            + "1.07\t軽微なバグの修正およびシミュレーションログの書き出し\n"
            + "\t\t読込機能にゴールオブジェクトと障害物オブジェクトが対応\n"
            + "1.08\t表示設定機能を更新\n"
            + "1.09\tシミュレーション設定環境の保存・読み込み機能を追加\n"
            + "\t\t(Open/Save Work Space)\n"
            + "1.10\t消しゴム機能の追加\n"
            + "1.11\t\trandom spawn agent 機能を更新\n"
            + "\t\t場所・範囲が指定できるようになった\n"
            + "1.12\tパラメータの書き出し&読み込み機能を追加\n"
            + "\t\t背景の書き出し&読み込み機能を追加\n"
            + "1.13\tトピックデータの描画およびコーパスデータの作成機能を追加\n"
            + "1.14\tトピックデータの作成機能を追加(要Python環境)\n"
            + "1.15(beta)\tAnalysisModeの追加\n"
            + "\n"
            + "次回アップデート予定\n"
            + "1.15\tAnalysisModeの正式リリース";

    /**
     * Simulation termination conditions
     * not end automatically -> "NONE"
     * Agents escape all -> "ESCAPED"
     * Specify end step number(int) -> "1~n"
     */
    public static String END = "NONE";

    /**
     * 　Log recording boolean
     */
    public static boolean IS_WRITE_LOG = false;

    /** analysis mode boolean */
    public static boolean IS_ANALYSIS_MODE = false;

    /**
     * Output path of log file
     */
    public static String WRITE_LOG_PATH;

    /**
     * LDA コーパス保存ステップ
     * END が"ESCAPED"の場合はそのタイミングが優先される
     * nullの場合はCtr+Lで保存する
     */
    public static int LDA_OUT_PRINT_STEP;

    /**
     * LDA 文書の区切り方
     */
    public static int LDA_SPLIT_STEP = 60;

    /**
     * Simulation Scale Size
     */
    public static Vector2 SCALE = new Vector2(100 * 100f, 100 * 100f);

    /**
     * Simulation Background Texture path
     */
    public static String BACKGROUND_TEXTURE_Path;

    /**
     * Simulation Background Texture size
     */
    public static Vector2 BACKGROUND_TEXTURE_SIZE = SCALE;

    /**
     * Size of the cell that calculates obstacles and potential intervals
     */
    public static int CELL_INTERVAL = 5;

    /**
     * Environment Cells Map
     */
    public static CellsMap ENV_CELLS_MAP = new CellsMap(SCALE, CELL_INTERVAL);

    /**
     * Simulation Non-walking area
     */
    public static ArrayList<BoxLine> NON_WALKING_AREA = new ArrayList<>(Arrays.asList());

    /**
     * Init agent speed
     */
    public static float AGENT_SPEED = 4.1f; //2.5m/s

    /**
     * Init agent radius
     */
    public static float AGENT_RADIUS = 25; //50cm

    /**
     * Init agent view radius length
     */
    public static float VIEW_RADIUS_LENGTH = 5 * 100f;

    /**
     * Init view degree
     */
    public static float VIEW_DEGREE = 50f;

    /**
     *
     */
    public static float GOAL_POTENTIAL_WEIGHT = 500;

    /**
     *
     */
    public static float GOAL_POTENTIAL_RANGE = 10000;

    /**
     *
     */
    public static float AGENT_POTENTIAL_WEIGHT = 1000;

    /**
     *
     */
    public static float AGENT_POTENTIAL_RANGE = AGENT_RADIUS;

    /**
     *
     */
    public static float OBSTACLE_POTENTIAL_WEIGHT = 1200;

    /**
     *
     */
    public static float OBSTACLE_POTENTIAL_RANGE = AGENT_RADIUS;

    /**
     * Interval of potential to calculate
     */
    public static float POTENTIAL_DELTA = CELL_INTERVAL;
}
