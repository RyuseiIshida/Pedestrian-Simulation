package com.simulation.pedestrian.agent;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Environment;
import com.simulation.pedestrian.Goal;
import com.simulation.pedestrian.obstacle.Obstacle;
import com.simulation.pedestrian.Parameter;
import com.simulation.pedestrian.potential.PotentialCell;
import com.simulation.pedestrian.util.Vector;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Agent {
    private Environment env;
    private static final float radius = Parameter.AGENT_RADIUS;
    private float viewRadius = Parameter.VIEW_RADIUS;
    private float viewDegree = Parameter.VIEW_DEGREE;
    private float speed = Parameter.AGENT_SPEED;
    private String ID;
    private String stateTag;
    private Vector2 position;
    private Vector2 goal;
    private Vector2 movePos;
    private Vector2 velocity;
    private Agent followAgent;
    private LinkedList<Agent> followers;

    //perception
    private ArrayList<Agent> perceptionAgentList = new ArrayList<>();
    private ArrayList<Agent> perceptionFollowAgentList = new ArrayList<>();
    private String perceptionBeforeStateTag;
    private Vector2 perceptionBeforePos;
    private float perceptionContinueStep = 0;
    private float perceptionContinueDst = 0;
    private float perceptionAllDst = 0;

    //utility weight
    private final float uRandomWalk = Parameter.U_RANDOM_WALK;
    private final float uFollowAgent = Parameter.U_FOLLOW_AGENT;
    private final float uMoveGoal = Parameter.U_MOVE_GOAL;
    private final float alpha = Parameter.ALPHA;
    private final float beta = Parameter.BETA;
    private final float gamma = Parameter.GAMMA;
    private final float delta = Parameter.DELTA;
    private final float epsilon = Parameter.EPSILON;

    float utilityRandomWalk = 0;
    float utilityFollow = 0;
    float utilityMoveGoal = 0;

    private File loadLog;

    public Agent(String id, Environment env, Vector2 position) {
        this.ID = id;
        this.env = env;
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
        this.stateTag = StateTag.moveGoal;
        this.perceptionBeforeStateTag = StateTag.moveGoal;
        this.position = position;
        this.perceptionBeforePos = position;
        this.goal = goal.getCenter();
        this.movePos = goal.getCenter();
        this.velocity = new Vector2(0, 0);
        this.followers = new LinkedList<>();
    }

    public Agent(File logFile) {
        loadLog = logFile;
    }

    public void action() {
        if (loadLog != null) {
            setLogToAgent();
        } else {
            perception();
            utilityFunction();
        }
    }

    private void perception() {
        setPerceptionContinue(); //同じルールをどれくらい継続しているのか
        setPerceptionAgent(); //視界にいるエージェント
        setPerceptionFollowAgent(); //視野内にいる追従できそうなエージェント
        leSetPerceptionFollowAgent(); //追従を辞めた時の後処理
        setPerceptionGoal();
    }

    private void setLogToAgent() {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(loadLog.getPath()))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] valueList = br.readLine().split(",", 0);
                this.stateTag = valueList[1]; // tag
                this.position = strToVector(valueList[2]); // position
                this.velocity = strToVector(valueList[3]); // velocity
                this.movePos = strToVector(valueList[4]); // movePos
                this.goal = strToVector(valueList[5]); // goal
                this.followAgent = env.getAgent(valueList[6]); //followAgent
                this.followAgent.setFollower(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Vector2 strToVector(String str) {
        //(-0.25262105:2.4872038) ログの形式例
        String[] splitStr = str.substring(1, str.length() - 1).split(":", 0);
        float x = Float.valueOf(splitStr[0]);
        float y = Float.valueOf(splitStr[1]);
        return new Vector2(x, y);
    }

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

    private void setPerceptionAgent() {
        perceptionAgentList = new ArrayList<>();
        env.getAgentList().stream()
                .filter(agent -> !agent.equals(this) && isInView(agent.getPosition()))
                .forEach(agent -> perceptionAgentList.add(agent));
    }

    private void setPerceptionFollowAgent() {
        perceptionFollowAgentList = new ArrayList<>();
        ArrayList<Agent> group = env.getCrowd().getMyGroup(this);
        perceptionAgentList.stream()
                //.filter(agent -> !StateTag.follow.equals(agent.stateTag))
                .filter(agent -> group == null)
                .forEach(agent -> perceptionFollowAgentList.add(agent));
    }

    private void leSetPerceptionFollowAgent() {
        if (!StateTag.follow.equals(stateTag) && followAgent != null) {
            followAgent.followers.remove(this);
            followAgent = null;
        }
    }

    private void setPerceptionGoal() {
        for (Goal goal : env.getGoals()) {
            if (isInView(goal.getCenter())
                    || isInView(goal.getLeftButtom())
                    || isInView(goal.getLeftTop())
                    || isInView(goal.getRightButtom())
                    || isInView(goal.getRightTop())
            ) {
                this.goal = goal.getCenter();
                this.movePos = this.goal;
                break;
            }
        }
    }

    private void utilityFunction() {
        utilityRandomWalk = StateTag.randomWalk.equals(stateTag)
                ? uRandomWalk - perceptionContinueStep * gamma
                : uRandomWalk;
        utilityRandomWalk = (utilityRandomWalk > 0.01f) ? utilityRandomWalk : 0.01f; //follow回避

        utilityFollow = StateTag.follow.equals(stateTag)
                ? perceptionAgentList.size() * beta
                : perceptionFollowAgentList.size() * beta;
        //? 1 - perceptionContinueStep * delta
        //: perceptionFollowAgentList.size() * beta;
        utilityFollow = (utilityFollow > 0.8f) ? 0.8f : utilityFollow;

        utilityMoveGoal = StateTag.moveGoal.equals(stateTag)
                ? 1 - perceptionContinueDst * epsilon
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

    private void moveGoal() {
        stateTag = StateTag.moveGoal;
        move(goal);
    }

    private void initRandomWalk() {
        stateTag = StateTag.randomWalk;
        float posX = MathUtils.random(Parameter.SCALE.x);
        float posY = MathUtils.random(Parameter.SCALE.y);
        movePos = new Vector2(posX, posY);
        move(movePos);
    }

    private void randomWalk() {
        if (getObstacleKIMPotential(position) > 0) {
            initRandomWalk();
            return;
        }
        movePos = movePos.add(velocity);
        move(movePos);
    }

    private void initFollowAgent() {
        Optional<Agent> closestAgent = perceptionFollowAgentList.stream()
                .min((a, b) -> new Float(position.dst(a.position))
                        .compareTo(position.dst(b.position)));
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
        if (distance > viewRadius || followers.contains(followAgent)) {
            followAgent.followers.remove(this);
            followAgent = null;
            initRandomWalk();
            return;
        }
        move(movePos);
        stateTag = StateTag.follow;
    }

    private void boidsFollow() {
        if (perceptionFollowAgentList.size() == 0) {
            return;
        }
        Vector2 vector2 = new Vector2();
        vector2.add(cohesion());
        vector2.add(separation());
    }

    private Vector2 cohesion() { //Crowd Average Position
        Vector2 vec = new Vector2();
        //perceptionFollowAgentList;
        return vec;
    }

    private Vector2 separation() {
        Vector2 vec = new Vector2();

        return vec;
    }

    private Vector2 alignment() {
        Vector2 vec = new Vector2();

        return vec;
    }

    private void move(Vector2 movePos) {
        Vector2 direction = Vector.direction(position, movePos);
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
        //System.out.println("pVector = " + pVector);
        pVector.nor();
        direction.set(pVector);
        //direction.add(pVector); //方向を決定
    }

    private float getPotential(float x, float y) {
        float cg = 200;
        float Ug = getGoalKIMPotential(x, y);
        float Uo = getAgentKIMPotential(x, y) + getObstacleKIMPotential(x, y);
        float U = (((1 / cg) * Uo) + 1) * Ug;
        //System.out.println("U = " + U);
        return U;
    }

    private float getGoalKIMPotential(float x, float y) {
        float cg = 200;
        float lg = 1000;
        Vector2 pos = new Vector2(x, y);
        //Vector2 pos = new Vector2(movePos.x, movePos.y);
        double len = pos.dst2(goal);
        double potentialWeight = cg * (1 - Math.exp(-1 * (len / (lg * lg))));
        return (float) potentialWeight;
    }

    private float getAgentKIMPotential(float x, float y) {
        float potentialWight = 0;
        float co = 700;
        float lo = 20;
        Vector2 pos = new Vector2(x, y);
        for (Agent agent : env.getAgentList()) {
            if (!agent.equals(this)) {
                potentialWight += co * Math.exp(-1 * (pos.dst2(agent.position) / (lo * lo)));
            }
        }
        return potentialWight;
    }

    private float getObstacleKIMPotential(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        float potentialWeight = 0;
        float co = 1000;
        float lo = 20;
        for (Obstacle obstacle : env.getObstacles()) {
            for (PotentialCell obstacleCell : obstacle.getObstacleCells()) {
                potentialWeight += co * Math.exp(-1 * (pos.dst2(obstacleCell.getCenterPoint()) / (lo * lo)));
            }
        }
        return potentialWeight;
    }

    private float getObstacleKIMPotential(Vector2 vec) {
        return getObstacleKIMPotential(vec.x, vec.y);
    }

    private boolean isMoved() {
        if (position.dst(movePos) < radius) {
            return true;
        } else {
            return false;
        }
    }

    public float getDirectionDegree() {
        float radian = (float) Math.atan2(velocity.y, velocity.x);
        float degree = (float) Math.toDegrees(radian);
        return degree;
    }

    public boolean isInView(Vector2 targetPos) {
        float targetDistance = position.dst(targetPos);
        float targetRadian = (float) Math.atan2(targetPos.x - position.x, targetPos.y - position.y);
        float targetDegree = (float) Math.toDegrees(targetRadian);
        if (targetDistance < viewRadius && getDirectionDegree() - targetDegree < viewDegree) {
            return true;
        }
        return false;
    }

    public void setFollower(Agent agent) {
        if(agent.equals(this)){
            throw new IllegalArgumentException();
        }
        followers.add(agent);
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

    public Vector2 getVelocity() {
        return velocity;
    }

    public Vector2 getGoal() {
        return goal;
    }

    public Vector2 getMovePos() {
        return movePos;
    }

    public Agent getFollowAgent() {
        return followAgent;
    }

    public LinkedList<Agent> getFollowers() {
        return followers;
    }


    public ArrayList<Agent> getPerceptionAgentList() {
        return perceptionAgentList;
    }

    public ArrayList<Agent> getPerceptionFollowAgentList() {
        return perceptionFollowAgentList;
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

    public float getURandomWalk() {
        return uRandomWalk;
    }

    public float getUFollowAgent() {
        return uFollowAgent;
    }

    public float getUMoveGoal() {
        return uMoveGoal;
    }

    public float getAlpha() {
        return alpha;
    }

    public float getBeta() {
        return beta;
    }

    public float getGamma() {
        return gamma;
    }

    public float getDelta() {
        return delta;
    }

    public float getEpsilon() {
        return epsilon;
    }

    public float getUtilityRandomWalk() {
        return utilityRandomWalk;
    }

    public float getUtilityFollow() {
        return utilityFollow;
    }

    public float getUtilityMoveGoal() {
        return utilityMoveGoal;
    }

    @Override
    public String toString() {
        return "agent" + ID;
    }
}
