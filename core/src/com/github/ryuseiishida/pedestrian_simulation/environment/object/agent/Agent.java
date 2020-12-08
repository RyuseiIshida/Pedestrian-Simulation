package com.github.ryuseiishida.pedestrian_simulation.environment.object.agent;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.Goal;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;
import com.github.ryuseiishida.pedestrian_simulation.util.UtilVector;
import org.apache.commons.math3.analysis.function.Exp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Agent {
    /**
     * the radius of this agent
     */
    private static final float radius = Parameter.AGENT_RADIUS;

    /**
     * the view radius length of this agent
     */
    private float viewRadiusLength = Parameter.VIEW_RADIUS_LENGTH;

    /**
     * the view degree of this agent
     */
    private float viewDegree = Parameter.VIEW_DEGREE;

    /**
     * the move speed of this agent
     */
    private float speed = Parameter.AGENT_SPEED;

    /**
     * the ID of this agent
     */
    private String ID;

    /**
     * the state-tag of this agent
     */
    private String stateTag;

    /**
     * ths position of this agent
     */
    private Vector2 position;

    /**
     * the position of {@link Goal}
     */
    private Goal goal;

    /**
     * the move position of this agent
     */
    private Vector2 movePos;

    /**
     * the move velocity of this agent
     */
    private Vector2 velocity;

    /**
     * the pedestrian_simulation {@link Environment} of this agent
     */
    private Environment env;

    /**
     * 現在の視野内の他Agentリスト
     */
    private ArrayList<Agent> perceptionInViewAgentList = new ArrayList<>();

    /**
     * 前Stepの状態Tag
     */
    private String perceptionBeforeStateTag;

    /**
     * 前Stepの位置座標
     */
    private Vector2 perceptionBeforePos;

    /**
     * 前stepの移動方向
     */
    private Vector2 perceptionBeforeVelocity;

    /**
     * 何Stepの同じルールを発火しているかを数える
     */
    private float perceptionContinueStep = 0;

    /**
     * 同じルールを発火している間、どれだけ移動したか
     */
    private float perceptionContinueDst = 0;

    /**
     * Agentの総移動距離
     */
    private float perceptionAllDst = 0;

    /**
     * 行動ログ
     */
    private ArrayList<String> logList = new ArrayList<>();

    /**
     * シミュレーションログファイル
     **/
    private File loadLogFile;

    public Agent(String id, Vector2 position) {
        this.ID = id;
        this.env = Environment.getInstance();
        this.stateTag = StateTag.none;
        this.perceptionBeforeStateTag = StateTag.none;
        this.position = position;
        this.perceptionBeforePos = position;
        this.movePos = position;
        this.velocity = new Vector2(0, 0);
        this.perceptionBeforeVelocity = velocity;
    }

    public Agent(String id, Vector2 position, float speed) {
        this.ID = id;
        this.env = Environment.getInstance();
        this.speed = speed;
        this.stateTag = StateTag.none;
        this.perceptionBeforeStateTag = StateTag.none;
        this.position = position;
        this.perceptionBeforePos = position;
        this.movePos = position;
        this.velocity = new Vector2(0, 0);
        this.perceptionBeforeVelocity = velocity;
        this.speed = speed;
    }

    public Agent(String id, Vector2 position, Goal goal) {
        this.ID = id;
        this.env = Environment.getInstance();
        this.stateTag = StateTag.moveGoal;
        this.perceptionBeforeStateTag = StateTag.moveGoal;
        this.position = position;
        this.perceptionBeforePos = position;
        this.goal = goal;
        this.movePos = goal.getCenter();
        this.velocity = new Vector2(0, 0);
        this.perceptionBeforeVelocity = velocity;
    }

    public Agent(String id, Vector2 position, float speed, Goal goal) {
        this.ID = id;
        this.env = Environment.getInstance();
        this.speed = speed;
        this.stateTag = StateTag.moveGoal;
        this.perceptionBeforeStateTag = StateTag.moveGoal;
        this.position = position;
        this.perceptionBeforePos = position;
        this.goal = goal;
        this.movePos = goal.getCenter();
        this.velocity = new Vector2(0, 0);
        this.perceptionBeforeVelocity = velocity;
    }

    public Agent(File logFile) {
        int startIndexID = logFile.getPath().indexOf("agent") + "agent".length();
        int endIndexID = logFile.getPath().indexOf(".txt");
        this.ID = logFile.getPath().substring(startIndexID, endIndexID);
        this.env = Environment.getInstance();
        loadLogFile = logFile;
        initLogToAgent();
    }

    /**
     * エージェント行動
     */
    public void action() {
        if (loadLogFile != null) {
            setLogToAgent(env.getStep());
        } else if (goal != null) {
            perception();
            moveGoal();
        } else {
            perception();
            nonGoalRule();
        }
        ifSetLog();
    }

    public void ifSetLog() {
        if (Parameter.IS_WRITE_LOG) {
            String log = String.format("%d %s %s %s",
                    env.getStep(),
                    stateTag,
                    position,
                    velocity);
            logList.add(log);
            if (Environment.getInstance().getStep() == 1) {
                log = String.format("%d %s %s %s",
                        0,
                        perceptionBeforeStateTag,
                        perceptionBeforePos,
                        perceptionBeforeVelocity);
                logList.add(0, log);
            }
        }
    }

    public ArrayList<String> getLogList() {
        return logList;
    }

    public void resetLogList() {
        logList = new ArrayList<>();
    }

    /**
     * 環境認識
     */
    private void perception() {
        setPerceptionContinue(); //同じルールをどれくらい継続しているのか
        setPerceptionAgent(); //視界にいるエージェント
        setPerceptionGoal();
    }

    private void nonGoalRule() {
        if (isMoveGoalAgent()) {
            moveFollow();
        } else {
            randomWalk();
        }
    }

    private boolean isRandomWalkAgent() {
        for (Agent agent : perceptionInViewAgentList) {
            return !agent.getStateTag().equals(StateTag.follow);
        }
        return false;
    }

    private boolean isMoveGoalAgent() {
        for (Agent agent : perceptionInViewAgentList) {
            return agent.getStateTag().equals(StateTag.moveGoal);
        }
        return false;
    }

    private Map<Integer, String> log = new HashMap<>();

    /**
     * ログInit
     */
    private void initLogToAgent() {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(loadLogFile.getPath()))) {
            String line;
            br.readLine(); //ヘッダーを抜かす処理
            while ((line = br.readLine()) != null) {
                int step = Integer.parseInt(line.substring(0, line.indexOf(" ")));
                log.put(step, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setLogToAgent(env.getStep());
    }

    private void setLogToAgent(int step) {
        String strValue = log.get(step);
        if (strValue == null) {
            stateTag = StateTag.escaped;
            return;
        }
        String[] valueList = log.get(step).split(" ", 0);
        int logStep = Integer.parseInt(valueList[0]);
        this.stateTag = valueList[1]; // tag
        this.position = UtilVector.strToVector(valueList[2]); // position
        this.velocity = UtilVector.strToVector(valueList[3]); // velocity
    }

    /**
     * Agentがどの程度同じ行動をしているかをセットする
     */
    private void setPerceptionContinue() {
        if (perceptionBeforeStateTag.equals(stateTag)) {
            perceptionContinueStep++;
            perceptionContinueDst += position.dst(perceptionBeforePos);
        } else { //ルール変更でリセット
            perceptionContinueStep = 0;
            perceptionContinueDst = 0;
        }
        perceptionAllDst += position.dst(perceptionBeforePos);
        //次stepへの後処理
        perceptionBeforeStateTag = stateTag;
        perceptionBeforePos = new Vector2(position);
        perceptionBeforeVelocity = new Vector2(velocity);
    }

    /**
     * Agent視野内の他エージェントを認識し記憶する
     */
    private void setPerceptionAgent() {
        perceptionInViewAgentList = new ArrayList<>();
        env.getAgentList().stream()
                .filter(agent -> !agent.equals(this) && isInView(agent.getPosition()))
                .forEach(agent -> perceptionInViewAgentList.add(agent));
    }

    /**
     * 視野内にGoalがあった場合に、{@link #goal}と{@link #movePos}に設定する
     */
    private void setPerceptionGoal() {
        for (Goal goal : env.getGoals()) {
            if (isInView(goal.getCenter())
                    || isInView(goal.getLeftBottom())
                    || isInView(goal.getLeftTop())
                    || isInView(goal.getRightBottom())
                    || isInView(goal.getRightTop())
            ) {
                this.goal = goal;
                this.movePos = this.goal.getCenter();
                break;
            }
        }
    }

    /**
     * {@link #goal}に向かって移動する
     */
    private void moveGoal() {
        stateTag = StateTag.moveGoal;
        movePos = goal.getCenter();
        move(goal.getCenter());
    }

    /**
     * ランダムな方向に移動する
     */
    private void randomWalk() {
        if (!stateTag.equals(StateTag.randomWalk) || calcObstaclePotential(position.x, position.y) > 0) {
            stateTag = StateTag.randomWalk;
            float posX = MathUtils.random(Parameter.SCALE.x);
            float posY = MathUtils.random(Parameter.SCALE.y);
            movePos = new Vector2(posX, posY);
        }
        movePos = movePos.add(velocity);
        move(movePos);
    }

    /**
     * {@link #movePos}の位置に向かって移動する
     *
     * @param movePos 移動座標
     */
    private void move(Vector2 movePos) {
        Vector2 direction = UtilVector.direction(position, movePos);
        //setPotentialVector(direction);
        calcPotentialVector(direction);
        direction.nor();
        velocity = direction.scl(speed);
        Vector2 tmpPos = new Vector2(position);
        tmpPos.add(velocity);
        if (tmpPos.x >= 0 + Parameter.AGENT_RADIUS && tmpPos.x <= Parameter.SCALE.x - Parameter.AGENT_RADIUS) {
            position.x = tmpPos.x;
        }
        if (tmpPos.y >= 0 + Parameter.AGENT_RADIUS && tmpPos.y <= Parameter.SCALE.y - Parameter.AGENT_RADIUS) {
            position.y = tmpPos.y;
        }
    }

    private void calcPotentialVector(Vector2 direction) {
        Vector2 pVector = new Vector2();
        float delta = Parameter.POTENTIAL_DELTA;
        pVector.x = -1 * (calcPotential(position.x + delta, position.y) - calcPotential(position.x, position.y)) / delta;
        pVector.y = -1 * (calcPotential(position.x, position.y + delta) - calcPotential(position.x, position.y)) / delta;
        pVector.nor();
        direction.set(pVector);
    }

    private float calcPotential(float x, float y) {
        float Ug = calcGoalPotential(x, y);
        float Uo = calcAgentPotential(x, y) + calcObstaclePotential(x, y);
        float U = Ug + Uo;
        return U;
    }

    private float calcGoalPotential(float x, float y) {
        float cg = Parameter.GOAL_POTENTIAL_WEIGHT;
        float lg = Parameter.GOAL_POTENTIAL_RANGE;
        Vector2 pos = new Vector2(x, y);
        double len = pos.dst2(movePos);
        double potentialWeight = cg * (1 - new Exp().value(-1 * (len / (lg * lg))));
        return (float) potentialWeight;
    }

    private float calcAgentPotential(float x, float y) {
        float potentialWight = 0;
        float co = Parameter.AGENT_POTENTIAL_WEIGHT;
        float lo = Parameter.AGENT_POTENTIAL_RANGE;
        Vector2 pos = new Vector2(x, y);
        for (Agent agent : env.getAgentList()) {
            if (!agent.equals(this)) {
                potentialWight += co * new Exp().value(-1 * (pos.dst2(agent.position) / (lo * lo)));
            }
        }
        return potentialWight;
    }

    private float calcObstaclePotential(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        float potentialWeight = 0;
        float co = Parameter.OBSTACLE_POTENTIAL_WEIGHT;
        float lo = Parameter.OBSTACLE_POTENTIAL_RANGE;
        for (Vector2 obstaclePosition : env.getObstaclesPosition().keySet()) {
            double value = -1 * (pos.dst2(obstaclePosition) / (lo * lo));
            potentialWeight += co * new Exp().value(value);
        }
        return potentialWeight;
    }

    private void moveFollow() {
        velocityFollow();
        stateTag = StateTag.follow;
        Vector2 direction = UtilVector.direction(position, movePos);
        setFollowPotentialVector(direction);
        Vector2 tmpPos = new Vector2(position);
        tmpPos.add(direction.scl(speed));
        if (tmpPos.x >= 0 + Parameter.AGENT_RADIUS && tmpPos.x <= Parameter.SCALE.x - Parameter.AGENT_RADIUS) {
            position.x = tmpPos.x;
        }
        if (tmpPos.y >= 0 + Parameter.AGENT_RADIUS && tmpPos.y <= Parameter.SCALE.y - Parameter.AGENT_RADIUS) {
            position.y = tmpPos.y;
        }
    }

    private void velocityFollow() {
        Vector2 pVector = new Vector2();
        float delta = 1f;
        pVector.x = -1 * (calcFollowKimPotential(position.x + delta, position.y) - calcFollowKimPotential(position.x, position.y)) / delta;
        pVector.y = -1 * (calcFollowKimPotential(position.x, position.y + delta) - calcFollowKimPotential(position.x, position.y)) / delta;
        pVector.nor();
        velocity.set(pVector);
    }

    private void setFollowPotentialVector(Vector2 direction) {
        Vector2 pVector = new Vector2();
        float delta = 1f;
        pVector.x = -1 * (calcFollowPotential(position.x + delta, position.y) - calcFollowPotential(position.x, position.y)) / delta;
        pVector.y = -1 * (calcFollowPotential(position.x, position.y + delta) - calcFollowPotential(position.x, position.y)) / delta;
        pVector.nor();
        direction.set(pVector);
    }

    private float calcFollowPotential(float x, float y) {
        float cg = goal == null ? 100 : 1000;
        //float Ug = getGoalKIMPotential(x, y) + getFollowKimPotential(x, y);
        float Ug = calcFollowKimPotential(x, y);
        float Uo = calcAgentPotential(x, y) + calcObstaclePotential(x, y);
        //float U = (((1 / cg) * Uo) + 1) * Ug;
        float U = Ug + Uo;
        return U;
    }

    private float calcFollowKimPotential(float x, float y) {
        float potentialWeight = 0;
        float cg = 100;
        float lg = Parameter.VIEW_RADIUS_LENGTH * 100;
        Vector2 pos = new Vector2(x, y);
        for (Agent agent : perceptionInViewAgentList) {
            if (agent.getStateTag().equals(StateTag.moveGoal)) {
                Vector2 followVelocity = UtilVector.direction(agent.getPosition(), agent.getMovePos());
                Vector2 followVector = new Vector2(agent.getPosition()).add(followVelocity);
                potentialWeight = (float) (cg * (1 - new Exp().value(-1 * (pos.dst2(followVector) / (lg * lg)))));
            }
        }
        return potentialWeight;
    }

    /**
     * Agentの移動方向を返す
     *
     * @return 移動方向
     */
    public float getDirectionDegree() {
        float radian = (float) Math.atan2(velocity.y, velocity.x);
        return (float) Math.toDegrees(radian);
    }

    /**
     * @param targetPos 視野内か判定するオブジェクトの位置
     * @return オブジェクトがAgentの視野内かどうか
     */
    private boolean isInView(Vector2 targetPos) {
        float targetDistance = position.dst(targetPos);
        float targetRadian = (float) Math.atan2(targetPos.x - position.x, targetPos.y - position.y);
        //ここで自分から見てターゲット何度にあるかがわかる
        float targetDegree = (float) Math.toDegrees(targetRadian);
        float differentDegree = Math.abs(getDirectionDegree() - targetDegree);
        return targetDistance < viewRadiusLength && differentDegree < viewDegree;
    }

    public String getID() {
        return ID;
    }

    public String getStateTag() {
        return stateTag;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getSpeed() {
        return speed;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Goal getGoal() {
        return goal;
    }

    public Vector2 getMovePos() {
        return movePos;
    }

    public ArrayList<Agent> getPerceptionInViewAgentList() {
        return perceptionInViewAgentList;
    }

    public float getPerceptionContinueStep() {
        return perceptionContinueStep;
    }

    public float getPerceptionContinueDst() {
        return perceptionContinueDst;
    }

    public float getPerceptionAllDst() {
        return perceptionAllDst;
    }

    @Override
    public String toString() {
        return "agent" + ID;
    }
}