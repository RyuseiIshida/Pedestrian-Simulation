package com.gihutb.ryuseiishida.simulation.evacuation.agent;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gihutb.ryuseiishida.simulation.evacuation.Parameter;
import com.gihutb.ryuseiishida.simulation.evacuation.analysis.LDA.LDA;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.Cell;
import com.gihutb.ryuseiishida.simulation.evacuation.environment.Environment;
import com.gihutb.ryuseiishida.simulation.evacuation.goal.Goal;
import com.gihutb.ryuseiishida.simulation.evacuation.obstacle.Obstacle;
import com.gihutb.ryuseiishida.simulation.evacuation.potential.KimPotentialModel;
import com.gihutb.ryuseiishida.simulation.evacuation.util.UtilVector;
import org.apache.commons.math3.analysis.function.Exp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
     * the simulation {@link Environment} of this agent
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
     * ランダムウォーク効用関数の選好度
     */
    private float UTILITY_PERFORMANCE_RANDOM_WALK = Parameter.U_RANDOM_WALK;

    /**
     * 追従行動効用関数の選好度
     */
    private float UTILITY_PERFORMANCE_FOLLOW_AGENT = Parameter.U_FOLLOW_AGENT;

    /**
     * ゴール移動効用関数の選好度
     */
    private float UTILITY_PERFORMANCE_MOVE_GOAL = Parameter.U_MOVE_GOAL;

    /**
     * 効用関数の重み <i>alpha</i>
     */
    private float UTILITY_ALPHA = Parameter.ALPHA;

    /**
     * 効用関数の重み <i>beta</i>
     */
    private float UTILITY_BETA = Parameter.BETA;

    /**
     * 効用関数の重み <i>gamma</i>
     */
    private float UTILITY_GAMMA = Parameter.GAMMA;

    /**
     * 効用関数の重み <i>delta</i>
     */
    private float UTILITY_DELTA = Parameter.DELTA;

    /**
     * 効用関数の重み <i>epsilon</i>
     */
    private float UTILITY_EPSILON = Parameter.EPSILON;

    /**
     * ランダム行動を発火する効用変数
     **/
    private float utilityRandomWalk = 0;

    /**
     * 追従行動を発火する効用変数
     **/
    private float utilityFollow = 0;

    /**
     * 出口に向かう行動を発火する効用変数
     **/
    private float utilityMoveGoal = 0;

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
        //setObstaclePositionList();
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
        //setObstaclePositionList();
        setObstaclePositionMap2();
    }

    public Agent(File logFile, Environment env) {
        int startIndexID = logFile.getPath().indexOf("agent") + "agent".length();
        int endIndexID = logFile.getPath().indexOf(".txt");
        this.ID = logFile.getPath().substring(startIndexID, endIndexID);
        this.env = env;
        this.potentialModel = new KimPotentialModel(env, this);
        this.followers = new ArrayList<>();
        loadLogFile = logFile;
        initLogToAgent();
        action();
    }

    LinkedList<Vector2> pheromone = new LinkedList<>();

    /**
     * エージェント行動
     */
    public void action() {
        if (loadLogFile != null) {
            setLogToAgent(env.getStep());
        } else {
            perception();
            //utilityFunction();
            //moveFollow();
//            if (!StateTag.randomWalk.equals(stateTag)) {
//                initRandomWalk();
//            } else {
//                randomWalk();
//            }




            if(env.getStep() == 0) {
                initRandomWalk();
            }
            if (goal != null) {
                moveGoal();
            } else {
                moveFollow();
            }
        }
    }

    /**
     * 環境認識
     */
    private void perception() {
        setMentalDst();
        setPerceptionContinue(); //同じルールをどれくらい継続しているのか
        setPerceptionAgent(); //視界にいるエージェント
        setPerceptionFollowAgent(); //視野内にいる追従できそうなエージェント
        resetPerceptionFollowAgent(); //追従を辞めた時の後処理
        setPerceptionGoal();
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
        this.position = UtilVector.strToVector(valueList[2], ID); // position
        this.velocity = UtilVector.strToVector(valueList[3], ID); // velocity
        this.movePos = UtilVector.strToVector(valueList[4], ID); // movePos
        this.goal = UtilVector.strToVector(valueList[5], ID); // goal
        if (!valueList[6].isEmpty()) {
            System.out.println("valueList = " + valueList[6]);
            this.followAgent = env.getAgent(valueList[6]); //followAgent
            System.out.println("follow = " + followAgent);
            this.followAgent.setFollower(this);
        }
        this.followers = strToAgentList(valueList[7]); //followers
        this.perceptionInViewAgentList = strToAgentList(valueList[9]);
        this.perceptionFollowAgentList = strToAgentList(valueList[11]);
        this.perceptionContinueStep = Float.parseFloat(valueList[13]);
        this.perceptionContinueDst = Float.parseFloat(valueList[14]);
        this.perceptionAllDst = Float.parseFloat(valueList[15]);
        this.UTILITY_PERFORMANCE_RANDOM_WALK = Float.parseFloat(valueList[16]);
        this.UTILITY_PERFORMANCE_FOLLOW_AGENT = Float.parseFloat(valueList[17]);
        this.UTILITY_PERFORMANCE_MOVE_GOAL = Float.parseFloat(valueList[18]);
        this.UTILITY_ALPHA = Float.parseFloat(valueList[19]);
        this.UTILITY_BETA = Float.parseFloat(valueList[20]);
        this.UTILITY_GAMMA = Float.parseFloat(valueList[21]);
        this.UTILITY_DELTA = Float.parseFloat(valueList[22]);
        this.UTILITY_EPSILON = Float.parseFloat(valueList[23]);
        this.utilityRandomWalk = Float.parseFloat(valueList[24]);
        this.utilityFollow = Float.parseFloat(valueList[25]);
        this.utilityMoveGoal = Float.parseFloat(valueList[26]);
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
     * どのAgentルールが発火するのかを決める効用関数
     * {@link #randomWalk()}, {@link #followAgent}, {@link #moveGoal()}の三つの行動ルールがある
     */
    private void utilityFunction() {
        utilityRandomWalk = StateTag.randomWalk.equals(stateTag)
                ? UTILITY_PERFORMANCE_RANDOM_WALK - perceptionContinueStep * UTILITY_GAMMA
                : UTILITY_PERFORMANCE_RANDOM_WALK;
        utilityRandomWalk = Math.max(utilityRandomWalk, 0.01f); //follow回避

        utilityFollow = StateTag.follow.equals(stateTag)
                ? perceptionInViewAgentList.size() * UTILITY_BETA
                : perceptionFollowAgentList.size() * UTILITY_BETA;
        utilityFollow = Math.min(utilityFollow, 0.8f);

        utilityMoveGoal = StateTag.moveGoal.equals(stateTag)
                ? 1 - perceptionContinueDst * UTILITY_EPSILON
                : (goal != null ? 1 : -1);

        Map<String, Float> actionMap = new TreeMap<>();
        ArrayList<String> actionList = new ArrayList<>();
        actionMap.put(StateTag.randomWalk, utilityRandomWalk);
        actionMap.put(StateTag.follow, utilityFollow);
        actionMap.put(StateTag.moveGoal, utilityMoveGoal);
        actionMap.entrySet().stream()
                .sorted(Collections.reverseOrder(java.util.Map.Entry.comparingByValue()))
                .forEach(action -> actionList.add(action.getKey()));
        if (StateTag.randomWalk.equals(actionList.get(0))) {
            //System.out.println("utilityRandomWalk = " + utilityRandomWalk);
            if (!StateTag.randomWalk.equals(stateTag)) {
                initRandomWalk();
            } else {
                randomWalk();
            }
        } else if (StateTag.follow.equals(actionList.get(0))) {
            stateTag = StateTag.follow;
            moveFollow();
        } else if (StateTag.moveGoal.equals(actionList.get(0))) {
            //System.out.println("utilityMoveGoal = " + utilityMoveGoal);
            moveGoal();
        }
    }

    /**
     * {@link #goal}に向かって移動する
     */
    private void moveGoal() {
        stateTag = StateTag.moveGoal;
        move(goal);
    }

    /**
     * 行動ルール{@link #randomWalk()}の初期設定を行う
     */
    private void initRandomWalk() {
        stateTag = StateTag.randomWalk;
        float posX = MathUtils.random(Parameter.SCALE.x);
        float posY = MathUtils.random(Parameter.SCALE.y);
        movePos = new Vector2(posX, posY);
        move(movePos);
    }

    private void randomWalk() {
        //if (getObstacleKIMPotential(position) > 0) {
//        if (potentialModel.repulsivePotential(position, potentialModel.getNearObstaclePotentialCells())> 0) {
        if (potentialModel.repulsivePotential(position, env.getObstaclesPosition()) > 0) {
            initRandomWalk();
            return;
        }
        movePos = movePos.add(velocity);
        move(movePos);
    }

    private void initFollowAgent() {
        Optional<Agent> closestAgent = perceptionFollowAgentList.stream()
                .min((a, b) -> Float.compare(position.dst(a.position), position.dst(b.position)));
        followAgent = closestAgent.get();
        if (perceptionFollowAgentList.size() != 0) {
            followAgent.setFollower(this);
            stateTag = StateTag.follow;
        } else {
            throw new NullPointerException();
        }
    }

    private void followAgent() {
        movePos = new Vector2(followAgent.getPosition()).sub(followAgent.getVelocity().scl(2f));
        float distance = position.dst(followAgent.getPosition());
        if (distance > viewRadiusLength || followers.contains(followAgent)) {
            followAgent.followers.remove(this);
            followAgent = null;
            initRandomWalk();
            return;
        }
        move(movePos);
        stateTag = StateTag.follow;
    }

    /**
     * {@link #movePos}の位置に向かって移動する
     *
     * @param movePos 移動座標
     */
    private void move(Vector2 movePos) {
        setMentalPotential();
        Vector2 direction = UtilVector.direction(position, movePos);
        //setPotentialVector(direction);
        setFastPotentialVector(direction);
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

    private void moveFollow() {
        stateTag = StateTag.follow;
        ///setMentalPotential();
        Vector2 direction = UtilVector.direction(position, movePos);
        setFastFollowPotentialVector(direction);
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

    /**
     * @param direction Agent移動方向
     */
    private void setPotentialVector(Vector2 direction) {
        direction.set(potentialModel.getMoveDirection());
    }


    private void setFastPotentialVector(Vector2 direction) {
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
        float Uo = getAgentKIMPotential(x, y) + getFastObstacleKIMPotential(x, y) + getFastFireKIMPotential(x, y);
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
        float lo = Parameter.AGENT_RADIUS + mentalAgentWeight;
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

    //今使ってない
    private float getObstacleKIMPotential(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        float potentialWeight = 0;
        float co = 1200;
        float lo = Parameter.AGENT_RADIUS + mentalAgentWeight;
        System.out.println("lo = " + lo);
        for (Obstacle obstacle : env.getObstacles()) {
            for (Cell obstacleCell : obstacle.getObstacleCells()) {
                double value = -1 * (pos.dst2(obstacleCell.getCenterPoint()) / (lo * lo));
                potentialWeight += co * new Exp().value(value);
            }
        }
        return potentialWeight;
    }


    private float getObstacleKIMPotential(Vector2 vec) {
        return getObstacleKIMPotential(vec.x, vec.y);
    }

    private float getFastFireKIMPotential(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        float potentialWeight = 0;
        float co = 200;
        float lo = env.getFire().getSpreadFireRange();
        double value = -1 * (pos.dst2(env.getFire().getFirePoint()) / (lo * lo));
        potentialWeight += co * new Exp().value(value);
        return potentialWeight;
    }

    private float getFireKIMPotential(Vector2 vec) {
        return getObstacleKIMPotential(vec.x, vec.y);
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
        float targetDegree = (float) Math.toDegrees(targetRadian);
        return targetDistance < viewRadiusLength && getDirectionDegree() - targetDegree < viewDegree;
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
     * @return {@link #UTILITY_PERFORMANCE_RANDOM_WALK}
     */
    public float getURandomWalk() {
        return UTILITY_PERFORMANCE_RANDOM_WALK;
    }

    /**
     * @return {@link #UTILITY_PERFORMANCE_FOLLOW_AGENT}
     */
    public float getUFollowAgent() {
        return UTILITY_PERFORMANCE_FOLLOW_AGENT;
    }

    /**
     * @return {@link #UTILITY_PERFORMANCE_MOVE_GOAL}
     */
    public float getUMoveGoal() {
        return UTILITY_PERFORMANCE_MOVE_GOAL;
    }

    /**
     * @return {@link #UTILITY_ALPHA}
     */
    public float getUTILITY_ALPHA() {
        return UTILITY_ALPHA;
    }

    /**
     * @return {@link #UTILITY_BETA}
     */
    public float getUTILITY_BETA() {
        return UTILITY_BETA;
    }

    /**
     * @return {@link #UTILITY_GAMMA}
     */
    public float getUTILITY_GAMMA() {
        return UTILITY_GAMMA;
    }

    /**
     * @return {@link #UTILITY_DELTA}
     */
    public float getUTILITY_DELTA() {
        return UTILITY_DELTA;
    }

    /**
     * @return {@link #UTILITY_EPSILON}
     */
    public float getUTILITY_EPSILON() {
        return UTILITY_EPSILON;
    }

    /**
     * @return {@link #utilityRandomWalk}
     */
    public float getUtilityRandomWalk() {
        return utilityRandomWalk;
    }

    /**
     * @return {@link #utilityFollow}
     */
    public float getUtilityFollow() {
        return utilityFollow;
    }


    /**
     * @return {@link #utilityMoveGoal}
     */
    public float getUtilityMoveGoal() {
        return utilityMoveGoal;
    }

    /**
     * @return {@link #ID}
     */
    @Override
    public String toString() {
        return "agent" + ID;
    }

    private void setFastFollowPotentialVector(Vector2 direction) {
        Vector2 pVector = new Vector2();
        float delta = 1f;
        pVector.x = -1 * (getFollowPotential(position.x + delta, position.y) - getFollowPotential(position.x, position.y)) / delta;
        pVector.y = -1 * (getFollowPotential(position.x, position.y + delta) - getFollowPotential(position.x, position.y)) / delta;
        //pVector.nor();
        direction.set(pVector);
    }

    private float getFollowPotential(float x, float y) {
        float cg = goal == null ? 100 : 1000;
        //float Ug = getGoalKIMPotential(x, y) + getFollowKimPotential(x, y);
        float Ug = getFollowKimPotential(x, y);
        float Uo = getAgentKIMPotential(x, y) + getFastObstacleKIMPotential(x, y) + getFastFireKIMPotential(x, y);
        float U = (((1 / cg) * Uo) + 1) * Ug;
        //float U = Ug + Uo;
        return U;
    }

    private float getFollowKimPotential(float x, float y) {
        float potentialWeight = 0;
        float cg = goal == null ? 10 : 100;
        float lg = Parameter.VIEW_RADIUS_LENGTH * 2;
        Vector2 pos = new Vector2(x, y);
        for (Agent agent : perceptionInViewAgentList) {
            //potentialWeight = (float) (cg * (1 - new Exp().value(-1 * (pos.dst2(agent.getPosition()) / (lg * lg)))));
            Vector2 followVelocity = UtilVector.direction(agent.getPosition(), agent.getMovePos());
            Vector2 followVector = new Vector2(agent.getPosition()).add(followVelocity);
            potentialWeight = (float) (cg * (1 - new Exp().value(-1 * (pos.dst2(followVector) / (lg * lg)))));
        }
        return potentialWeight;
    }

    private LinkedList<Vector2> obstaclePositionList;
    private LinkedHashMap<Vector2, Float> obstaclePositionMap;
    private float obstacleLo = radius;

    //    private void setObstaclePositionList() {
//        obstaclePositionList = new LinkedList<>();
//        for (Obstacle obstacle : env.getObstacles()) {
//            for (Cell obstacleCell : obstacle.getObstacleCells()) {
//                obstaclePositionList.add(obstacleCell.getCenterPoint());
//            }
//        }
//    }
//
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

//    private float getFastObstacleKIMPotential(float x, float y) {
//        Vector2 pos = new Vector2(x, y);
//        float potentialWeight = 0;
//        float co = 1200;
//        float lo = Parameter.AGENT_RADIUS + mentalWeight;
//        System.out.println("lo = " + lo);
//        for (Vector2 obstaclePos : obstaclePositionList) {
//            double value = -1 * (pos.dst2(obstaclePos) / (lo * lo));
//            potentialWeight += co * new Exp().value(value);
//        }
//        return potentialWeight;
//    }

    private float getFastObstacleKIMPotential(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        float potentialWeight = 0;
        float co = 1200;
        float lo;
        for (Map.Entry<Vector2, Float> entry : obstaclePositionMap.entrySet()) {
            lo = entry.getValue() + mentalAgentWeight;
            double value = -1 * (pos.dst2(entry.getKey()) / (lo * lo));
            potentialWeight += co * new Exp().value(value);
        }
        return potentialWeight;
    }


    private float mentalAgentWeight = 1;
    private float mentalDst = 0;


    private float setMentalDst() {
        mentalDst = perceptionBeforePos.dst(position);
        return mentalDst;
    }

    private float setMentalPotential() {
        //mentalDst = setMentalDst();
        float alpha = 0.1f;
        if (mentalDst < 10) {
            mentalAgentWeight += alpha;
            setMentalObstaclePotential();
        }
        return mentalAgentWeight;
    }

    private void setMentalObstaclePotential() {
        for (Map.Entry<Vector2, Float> entry : obstaclePositionMap.entrySet()) {
            if (isInView(entry.getKey())) {
                entry.setValue(entry.getValue() + 1);
            }
        }
    }

    public static void main(String[] args) {
        createAgentList(Parameter.INIT_AGENT_NUM);
    }

    private static void createAgentList(int agentNum) {
        String path = "core/assets/agentList.txt";
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(path))) {
            for (int i = 0; i < agentNum; i++) {
                float x = MathUtils.random(Parameter.INIT_RANDOM_X.valueA, Parameter.INIT_RANDOM_X.valueB);
                float y = MathUtils.random(Parameter.INIT_RANDOM_Y.valueA, Parameter.INIT_RANDOM_Y.valueB);
                bw.append(x + "," + y);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
