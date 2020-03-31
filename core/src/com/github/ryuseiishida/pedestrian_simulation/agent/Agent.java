package com.github.ryuseiishida.pedestrian_simulation.agent;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.cell.Cell;
import com.github.ryuseiishida.pedestrian_simulation.goal.Goal;
import com.github.ryuseiishida.pedestrian_simulation.obstacle.Obstacle;
import com.github.ryuseiishida.pedestrian_simulation.potential.KimPotentialModel;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;
import com.github.ryuseiishida.pedestrian_simulation.util.UtilVector;
import org.apache.commons.math3.analysis.function.Exp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
    private Vector2 goal;

    /**
     * the move position of this agent
     */
    private Vector2 movePos;

    /**
     * the move velocity of this agent
     */
    private Vector2 velocity;

    /**
     * the follow agent of this agent
     */
    private Agent followAgent;

    /**
     * the followers agent of this agent
     */
    private ArrayList<Agent> followers;

    /**
     * the pedestrian_simulation {@link Environment} of this agent
     */
    private Environment env;

    /**
     * the Kim potential model is calculate the potential
     */
    private KimPotentialModel potentialModel;

    /**
     * 現在の視野内の他Agentリスト
     */
    private ArrayList<Agent> perceptionInViewAgentList = new ArrayList<>();

    /**
     * 現在のフォローされているAgentリスト
     */
    private ArrayList<Agent> perceptionFollowAgentList = new ArrayList<>();

    /**
     * 前Stepの状態Tag
     */
    private String perceptionBeforeStateTag;

    /**
     * 前Stepの位置座標
     */
    private Vector2 perceptionBeforePos;

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
     * シミュレーションログファイル
     **/
    private File loadLogFile;

    public Agent(String id, Environment env, Vector2 position) {
        this.ID = id;
        this.env = env;
        this.potentialModel = new KimPotentialModel(env, this);
        this.stateTag = StateTag.none;
        this.perceptionBeforeStateTag = StateTag.none;
        this.position = position;
        this.perceptionBeforePos = position;
        this.movePos = position;
        this.velocity = new Vector2(0, 0);
        this.followers = new ArrayList<>();
        setObstaclePositionMap();
    }

    public Agent(String id, Environment env, Vector2 position, Goal goal) {
        this.ID = id;
        this.env = env;
        this.potentialModel = new KimPotentialModel(env, this);
        this.stateTag = StateTag.moveGoal;
        this.perceptionBeforeStateTag = StateTag.moveGoal;
        this.position = position;
        this.perceptionBeforePos = position;
        this.goal = goal.getCenter();
        this.movePos = goal.getCenter();
        this.velocity = new Vector2(0, 0);
        this.followers = new ArrayList<>();
        setObstaclePositionMap2();
    }

    public Agent(File logFile, Environment env) {
        int startIndexID = logFile.getPath().indexOf("agent") + "agent".length();
        int endIndexID = logFile.getPath().indexOf(".txt");
        this.ID = logFile.getPath().substring(startIndexID, endIndexID);
        this.env = env;
        loadLogFile = logFile;
        initLogToAgent();
    }

    /**
     * エージェント行動
     */
    public void action() {
        if (loadLogFile != null) {
            setLogToAgent(env.getStep());
        } else {
            perception();
            if (goal != null) {
                moveGoal();
            } else {
                ex();
            }

        }
    }

    /**
     * 環境認識
     */
    private void perception() {
        setPerceptionContinue(); //同じルールをどれくらい継続しているのか
        setPerceptionAgent(); //視界にいるエージェント
        setPerceptionFollowAgent(); //視野内にいる追従できそうなエージェント
        resetPerceptionFollowAgent(); //追従を辞めた時の後処理
        setPerceptionGoal();
    }

    private void ex() {
        if (isRandomWalkAgent()) {
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

    private boolean isFollowNearGroup(ArrayList<Agent> tmpFollowAgents) {
        ArrayList<Agent> isGroup = new ArrayList<>();
        for (ArrayList<Agent> group : Group.getGroup3(env.getAgentList())) {
            for (Agent agent : group) {
                if (this.equals(agent)) {
                    isGroup = group;
                }
            }
        }
        for (Agent tmpFollowAgent : tmpFollowAgents) {
            if (tmpFollowAgent.getStateTag().equals(StateTag.follow)) {
                if (isGroup.contains(tmpFollowAgent)) {
                    return true;
                }
            }
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
                int step = Integer.parseInt(line.substring(0, line.indexOf(",")));
                log.put(step, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setLogToAgent(int step) {
        String strValue = log.get(step);
        if (strValue == null) {
            return;
        }
        String[] valueList = log.get(step).split(",", 0);
        int logStep = Integer.parseInt(valueList[0]);
        this.stateTag = valueList[1]; // tag
        this.position = UtilVector.strToVector(valueList[2]); // position
//        this.velocity = UtilVector.strToVector(valueList[3]); // velocity
//        this.movePos = UtilVector.strToVector(valueList[4]); // movePos
//        this.goal = UtilVector.strToVector(valueList[5]); // goal
//        if (!valueList[6].isEmpty()) {
//            System.out.println("valueList = " + valueList[6]);
//            this.followAgent = env.getAgent(valueList[6]); //followAgent
//            System.out.println("follow = " + followAgent);
//            this.followAgent.setFollower(this);
//        }
//        this.followers = strToAgentList(valueList[7]); //followers
//        this.perceptionInViewAgentList = strToAgentList(valueList[9]);
//        this.perceptionFollowAgentList = strToAgentList(valueList[11]);
//        this.perceptionContinueStep = Float.parseFloat(valueList[13]);
//        this.perceptionContinueDst = Float.parseFloat(valueList[14]);
//        this.perceptionAllDst = Float.parseFloat(valueList[15]);
        this.perceptionBeforePos = this.position;
        this.perceptionBeforeStateTag = this.stateTag;
    }

    /**
     * StringのエージェントリストをAgent型リストに変換
     *
     * @param str String型のエージェントリスト
     * @return Agent型リスト
     */
    private ArrayList<Agent> strToAgentList(String str) {
        ArrayList<Agent> agentList = new ArrayList<>();
        //例 [] or [agent4: agent7: agent8: agent20]
        String tmpStr = str.replace("[", "").replace("]", "").replace(" ", "");
        if (!tmpStr.isEmpty()) {
            // agent4:agent:7:agent8:agent20
            String[] splitStr = tmpStr.replace("agent", "").split(":", 0);
            for (String strAgent : splitStr) {
                agentList.add(env.getAgent(strAgent));
            }
        }
        return agentList;
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
     * フォロワーを記憶する
     */
    private void setPerceptionFollowAgent() {
        perceptionFollowAgentList = new ArrayList<>();
        ArrayList<Agent> group = Group.getMyGroupAgents(this, env.getAgentList());
        perceptionInViewAgentList.stream()
                .filter(agent -> group == null)
                .forEach(agent -> perceptionFollowAgentList.add(agent));
    }

    /**
     * フォローする相手がいない時に情報をリセットする
     */
    private void resetPerceptionFollowAgent() {
        if (!StateTag.follow.equals(stateTag) && followAgent != null) {
            followAgent.followers.remove(this);
            followAgent = null;
        }
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
                this.goal = goal.getCenter();
                this.movePos = this.goal;
                break;
            }
        }
    }

    /**
     * {@link #goal}に向かって移動する
     */
    private void moveGoal() {
        stateTag = StateTag.moveGoal;
        movePos = goal;
        move(goal);
    }

    /**
     * ランダムな方向に移動する
     */
    private void randomWalk() {
        if (!stateTag.equals(StateTag.randomWalk) || potentialModel.repulsivePotential(position, env.getObstaclesPosition()) > 0) {
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
        setPotentialVector(direction);
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


    private void setPotentialVector(Vector2 direction) {
        Vector2 pVector = new Vector2();
        float delta = 1f;
        pVector.x = -1 * (getPotential(position.x + delta, position.y) - getPotential(position.x, position.y)) / delta;
        pVector.y = -1 * (getPotential(position.x, position.y + delta) - getPotential(position.x, position.y)) / delta;
        pVector.nor();
        direction.set(pVector);
    }


    private float getPotential(float x, float y) {
        float cg = 200;
        float Ug = getGoalKIMPotential(x, y);
        //float Uo = getAgentKIMPotential(x, y) + getObstacleKIMPotential(x, y);
        //float Uo = getAgentKIMPotential(x, y) + getFastObstacleKIMPotential(x, y);

        float Uo = getAgentKIMPotential(x, y) + getObstacleKIMPotential(x, y) + getFireKIMPotential(x, y);
        //float U = (((1 / cg) * Uo) + 1) * Ug;
        float U = Ug + Uo;
        return U;
    }

    private float getGoalKIMPotential(float x, float y) {
        float cg = 500;
        float lg = Parameter.SCALE.x;
        Vector2 pos = new Vector2(x, y);
        double len = pos.dst2(movePos);
        double potentialWeight = cg * (1 - new Exp().value(-1 * (len / (lg * lg))));
        return (float) potentialWeight;
    }

    private float getAgentKIMPotential(float x, float y) {
        float potentialWight = 0;
        float co = 1000;
        float lo = Parameter.AGENT_RADIUS;
        //float lo = Parameter.AGENT_RADIUS;
        //System.out.println("lo = " + lo);
        Vector2 pos = new Vector2(x, y);
        for (Agent agent : env.getAgentList()) {
            if (!agent.equals(this)) {
                potentialWight += co * new Exp().value(-1 * (pos.dst2(agent.position) / (lo * lo)));
            }
        }
        return potentialWight;
    }

    private float getObstacleKIMPotential(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        float potentialWeight = 0;
        float co = 1200;
        float lo;
        for (Map.Entry<Vector2, Float> entry : obstaclePositionMap.entrySet()) {
            lo = entry.getValue();
            double value = -1 * (pos.dst2(entry.getKey()) / (lo * lo));
            potentialWeight += co * new Exp().value(value);
        }
        return potentialWeight;
    }

    private float getFireKIMPotential(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        float potentialWeight = 0;
        float co = 200;
        float lo = env.getFire().getSpreadFireRange();
        double value = -1 * (pos.dst2(env.getFire().getFirePoint()) / (lo * lo));
        potentialWeight += co * new Exp().value(value);
        return potentialWeight;
    }

    ArrayList<Agent> beforeFollowAgent;

    private void moveFollow() {
        velocityFollow();
        stateTag = StateTag.follow;
        beforeFollowAgent = perceptionInViewAgentList;
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
        pVector.x = -1 * (getFollowKimPotential(position.x + delta, position.y) - getFollowKimPotential(position.x, position.y)) / delta;
        pVector.y = -1 * (getFollowKimPotential(position.x, position.y + delta) - getFollowKimPotential(position.x, position.y)) / delta;
        pVector.nor();
        velocity.set(pVector);
    }

    private void setFollowPotentialVector(Vector2 direction) {
        Vector2 pVector = new Vector2();
        float delta = 1f;
        pVector.x = -1 * (getFollowPotential(position.x + delta, position.y) - getFollowPotential(position.x, position.y)) / delta;
        pVector.y = -1 * (getFollowPotential(position.x, position.y + delta) - getFollowPotential(position.x, position.y)) / delta;
        pVector.nor();
        direction.set(pVector);
    }

    private float getFollowPotential(float x, float y) {
        float cg = goal == null ? 100 : 1000;
        //float Ug = getGoalKIMPotential(x, y) + getFollowKimPotential(x, y);
        float Ug = getFollowKimPotential(x, y);
        float Uo = getAgentKIMPotential(x, y) + getObstacleKIMPotential(x, y) + getFireKIMPotential(x, y);
        //float U = (((1 / cg) * Uo) + 1) * Ug;
        float U = Ug + Uo;
        return U;
    }

    private float getFollowKimPotential(float x, float y) {
        float potentialWeight = 0;
        float cg = goal == null ? 10 : 100;
        float lg = Parameter.VIEW_RADIUS_LENGTH * 2;
        Vector2 pos = new Vector2(x, y);
        for (Agent agent : perceptionInViewAgentList) {
            Vector2 followVelocity = UtilVector.direction(agent.getPosition(), agent.getMovePos());
            Vector2 followVector = new Vector2(agent.getPosition()).add(followVelocity);
            potentialWeight = (float) (cg * (1 - new Exp().value(-1 * (pos.dst2(followVector) / (lg * lg)))));
        }
        return potentialWeight;
    }

    private float getAgentKIMPotential2(float x, float y) {
        float potentialWight = 0;
        float co = 1000;
        float lo = Parameter.AGENT_RADIUS;
        //float lo = Parameter.AGENT_RADIUS;
        //System.out.println("lo = " + lo);
        Vector2 pos = new Vector2(x, y);
        for (Agent agent : env.getAgentList()) {
            if (!agent.equals(this) && !StateTag.follow.equals(agent.getStateTag())) {
                potentialWight += co * new Exp().value(-1 * (pos.dst2(agent.position) / (lo * lo)));
            }
        }
        return potentialWight;
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

    /**
     * フォローするAgentをセットする
     *
     * @param agent フォローするAgent
     */
    private void setFollower(Agent agent) {
        if (agent.equals(this)) {
            throw new IllegalArgumentException();
        }
        followers.add(agent);
    }

    /**
     * AgentのIDを返す
     *
     * @return {@link #ID}
     */
    public String getID() {
        return ID;
    }

    /**
     * Agentの状態タグを返す
     *
     * @return {@link #stateTag}
     */
    public String getStateTag() {
        return stateTag;
    }

    /**
     * Agentの位置を返す
     *
     * @return {@link #position}
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Agentの移動方向を返す
     *
     * @return {@link #velocity}
     */
    public Vector2 getVelocity() {
        return velocity;
    }

    /**
     * Agentが知っている{@link Goal}を返す
     *
     * @return {@link Goal}
     */
    public Vector2 getGoal() {
        return goal;
    }

    /**
     * Agentの移動先を返す
     *
     * @return {@link #movePos}
     */
    public Vector2 getMovePos() {
        return movePos;
    }

    /**
     * AgentがフォローしているAgentを返す
     *
     * @return {@link #followAgent}
     */
    public Agent getFollowAgent() {
        return followAgent;
    }

    /**
     * 他Agentにフォローされているリストを返す
     *
     * @return {@link #followers}
     */
    public ArrayList<Agent> getFollowers() {
        return followers;
    }

    /**
     * @return {@link #perceptionInViewAgentList}
     */
    public ArrayList<Agent> getPerceptionInViewAgentList() {
        return perceptionInViewAgentList;
    }

    /**
     * @return {@link #perceptionFollowAgentList}
     */
    public ArrayList<Agent> getPerceptionFollowAgentList() {
        return perceptionFollowAgentList;
    }


    /**
     * @return {@link #perceptionContinueStep}
     */
    public float getPerceptionContinueStep() {
        return perceptionContinueStep;
    }

    /**
     * @return {@link #perceptionContinueDst}
     */
    public float getPerceptionContinueDst() {
        return perceptionContinueDst;
    }

    /**
     * @return {@link #perceptionAllDst}
     */
    public float getPerceptionAllDst() {
        return perceptionAllDst;
    }

    /**
     * @return {@link #ID}
     */
    @Override
    public String toString() {
        return "agent" + ID;
    }

    private LinkedHashMap<Vector2, Float> obstaclePositionMap;
    private float obstacleLo = radius;

    private void setObstaclePositionMap2() {
        setObstaclePositionMap();
        for (Cell obstacleCell : Parameter.experienceOLINE.getObstacleCells()) {
            //obstaclePositionList.add(obstacleCell.getCenterPoint());
            obstaclePositionMap.put(obstacleCell.getCenterPoint(), obstacleLo);
        }
    }

    private void setObstaclePositionMap() {
        obstaclePositionMap = new LinkedHashMap<>();
        for (Obstacle obstacle : env.getObstacles()) {
            for (Cell obstacleCell : obstacle.getObstacleCells()) {
                obstaclePositionMap.put(obstacleCell.getCenterPoint(), obstacleLo);
            }
        }
    }
}
