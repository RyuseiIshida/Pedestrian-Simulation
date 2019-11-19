package com.simulation.pedestrian.agent;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.environment.Environment;
import com.simulation.pedestrian.goal.Goal;
import com.simulation.pedestrian.Parameter;
import com.simulation.pedestrian.potential.KimPotentialModel;
import com.simulation.pedestrian.util.UtilVector;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Agent {
    /** the radius of this agent */
    private static final float radius = Parameter.AGENT_RADIUS;

    /** the view radius length of this agent */
    private float viewRadiusLength = Parameter.VIEW_RADIUS_LENGTH;

    /** the view degree of this agent */
    private float viewDegree = Parameter.VIEW_DEGREE;

    /** the move speed of this agent */
    private float speed = Parameter.AGENT_SPEED;

    /** the ID of this agent */
    private String ID;

    /** the state-tag of this agent */
    private String stateTag;

    /** ths position of this agent */
    private Vector2 position;

    /** the position of {@link Goal} */
    private Vector2 goal;

    /** the move position of this agent */
    private Vector2 movePos;

    /** the move velocity of this agent */
    private Vector2 velocity;

    /** the follow agent of this agent */
    private Agent followAgent;

    /** the followers agent of this agent */
    private LinkedList<Agent> followers;

    /** the simulation {@link Environment} of this agent */
    private Environment env;

    /** the Kim potential model is calculate the potential */
    private KimPotentialModel potentialModel;

    /** 現在の視野内の他Agentリスト */
    private LinkedList<Agent> perceptionInViewAgentList = new LinkedList<>();

    /** 現在のフォローされているAgentリスト */
    private LinkedList<Agent> perceptionFollowAgentList = new LinkedList<>();

    /** 前Stepの状態Tag */
    private String perceptionBeforeStateTag;

    /** 前Stepの位置座標 */
    private Vector2 perceptionBeforePos;

    /** 何Stepの同じルールを発火しているかを数える */
    private float perceptionContinueStep = 0;

    /** 同じルールを発火している間、どれだけ移動したか */
    private float perceptionContinueDst = 0;

    /** Agentの総移動距離 */
    private float perceptionAllDst = 0;

    /** ランダムウォーク効用関数の選好度 */
    private float UTILITY_PERFORMANCE_RANDOM_WALK = Parameter.U_RANDOM_WALK;

    /** 追従行動効用関数の選好度 */
    private float UTILITY_PERFORMANCE_FOLLOW_AGENT = Parameter.U_FOLLOW_AGENT;

    /** ゴール移動効用関数の選好度 */
    private float UTILITY_PERFORMANCE_MOVE_GOAL = Parameter.U_MOVE_GOAL;

    /** 効用関数の重み <i>alpha</i> */
    private float UTILITY_ALPHA = Parameter.ALPHA;

    /** 効用関数の重み <i>beta</i> */
    private float UTILITY_BETA = Parameter.BETA;

    /** 効用関数の重み <i>gamma</i> */
    private float UTILITY_GAMMA = Parameter.GAMMA;

    /** 効用関数の重み <i>delta</i> */
    private float UTILITY_DELTA = Parameter.DELTA;

    /** 効用関数の重み <i>epsilon</i> */
    private float UTILITY_EPSILON = Parameter.EPSILON;

    /** ランダム行動を発火する効用変数 **/
    private float utilityRandomWalk = 0;

    /** 追従行動を発火する効用変数 **/
    private float utilityFollow = 0;

    /** 出口に向かう行動を発火する効用変数 **/
    private float utilityMoveGoal = 0;

    /** シミュレーションログファイル **/
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
        this.followers = new LinkedList<>();
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
        this.followers = new LinkedList<>();
    }

    public Agent(File logFile, Environment env) {
        int startIndexID = logFile.getPath().indexOf("agent") + "agent".length();
        int endIndexID = logFile.getPath().indexOf(".txt");
        this.ID = logFile.getPath().substring(startIndexID, endIndexID);
        this.env = env;
        this.potentialModel = new KimPotentialModel(env, this);
        this.followers = new LinkedList<>();
        loadLogFile = logFile;
        initLogToAgent();
        action();
    }

    /**
     * エージェント行動
     */
    public void action() {
        if (loadLogFile != null) {
            setLogToAgent(env.getStep());
        } else {
            perception();
            utilityFunction();
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
    private LinkedList<Agent> strToAgentList(String str) {
        LinkedList<Agent> agentList = new LinkedList<>();
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
        perceptionInViewAgentList = new LinkedList<>();
        env.getAgentList().stream()
                .filter(agent -> !agent.equals(this) && isInView(agent.getPosition()))
                .forEach(agent -> perceptionInViewAgentList.add(agent));
    }

    /**
     * フォロワーを記憶する
     */
    private void setPerceptionFollowAgent() {
        perceptionFollowAgentList = new LinkedList<>();
        ArrayList<Agent> group = Group.getMyGroupAgents(this,env.getAgentList());
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
            //System.out.println("utilityFollow = " + utilityFollow);
            if (!StateTag.follow.equals(stateTag)) {
                initFollowAgent();
            }
            followAgent();
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
        Vector2 direction = UtilVector.direction(position, movePos);
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

    /**
     * @param direction Agent移動方向
     */
    private void setPotentialVector(Vector2 direction) {
        direction.set(potentialModel.getMoveDirection());
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
    public LinkedList<Agent> getFollowers() {
        return followers;
    }

    /**
     * @return {@link #perceptionInViewAgentList}
     */
    public LinkedList<Agent> getPerceptionInViewAgentList() {
        return perceptionInViewAgentList;
    }

    /**
     * @return {@link #perceptionFollowAgentList}
     */
    public LinkedList<Agent> getPerceptionFollowAgentList() {
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
}
