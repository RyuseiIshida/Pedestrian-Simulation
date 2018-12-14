package com.simulation.pedestrian.Agent;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Environment;
import com.simulation.pedestrian.Goal;
import com.simulation.pedestrian.Parameter;
import com.simulation.pedestrian.Potential.PotentialCell;
import com.simulation.pedestrian.Util.Tuple;
import com.simulation.pedestrian.Util.Vector;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Agent {
    private Environment env;
    private static final float radius = Parameter.agentRadius;
    private float viewRadius = Parameter.viewRadius;
    private float viewDegree = Parameter.viewDegree;
    private float speed = Parameter.agentSpeed;
    private String ID;
    private String stateTag;
    private Vector2 position;
    private Vector2 goal;
    private Vector2 movePos;
    private Vector2 velocity;
    private Agent followAgent;
    private LinkedList<Agent> followers;

    public Agent(String id, Environment env, Vector2 position) {
        this.ID = id;
        this.env = env;
        this.stateTag = StateTag.none;
        this.position = position;
        this.movePos = position;
        this.velocity = new Vector2(0, 0);
        this.followers = new LinkedList<>();
    }

    public Agent(String id, Environment env, Vector2 position, Goal goal) {
        this.ID = id;
        this.env = env;
        this.stateTag = StateTag.moveGoal;
        this.position = position;
        this.goal = goal.getPosition();
        this.movePos = goal.getCenter();
        this.velocity = new Vector2(0, 0);
        this.followers = new LinkedList<>();
    }


    public void action() throws Exception {
        decisionMaking();
        move(movePos);
    }

    //意思決定
    private void decisionMaking() {
        ifGoalInView();
        if (stateTag == StateTag.follow) {
            followAgent();
        }
        if (env.getStep() % Parameter.stepInterval == 0
                && stateTag != StateTag.moveGoal
                && stateTag != StateTag.follow
                ) {
            int random = MathUtils.random(0, 2);
            switch (random) {
                case 0:
                    randomWalk();
                    break;
                case 1:
                    judgeCrowd();
                    break;
                case 2:
                    moveGroupPosition();
                    break;
            }
        }
    }

    private void move(Vector2 movePos) {
        Vector2 direction = Vector.direction(position, movePos);
        setPotentialVector(direction);
        velocity = direction.scl(speed);
        position.add(velocity);
    }

    private void setPotentialVector(Vector2 direction) {
        List<PotentialCell> nearCell = new ArrayList<>();
        Tuple index = env.getEnvPotentialMap().getIndex(position);
        //TODO 後でrange変更
        int range = 10;
        for (int i = index.t1 - range; i <= index.t1 + range; i++) {
            for (int j = index.t2 - range; j <= index.t2 + range; j++) {
                if (!(index.t1 == i && index.t2 == j)
                        && i >= 0
                        && j >= 0
                        && i <= env.getEnvPotentialMap().getLastIndex().t1
                        && j <= env.getEnvPotentialMap().getLastIndex().t2
                        ) {
                    nearCell.add(env.getEnvPotentialMap().getMatrixPotentialCell(i, j));
                }
            }
        }
        for (PotentialCell potentialCell : nearCell) {
            if (potentialCell.getPotential() != 0) {
                Vector2 pVec = Vector.direction(potentialCell.getCenterPoint(), position);
                direction.add(pVec.x, pVec.y).nor();
            }
        }
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

//    public boolean isInView(Vector2 targetPos) {
//        float targetDistance = position.dst(targetPos);
//        float targetRadian = (float) Math.atan2(targetPos.x - position.x, targetPos.y - position.y);
//        float targetDegree = (float) Math.toDegrees(targetRadian);
//        //if (targetDistance < viewRadius && getDirectionDegree() - targetDegree < viewDegree / 2) {
//        if (targetDistance < viewRadius && Math.abs(getDirectionDegree() - targetDegree) < viewDegree / 2) {
//            return true;
//        }
//        return false;
//    }

    public boolean isInView(Vector2 targetPos) {
        float targetDistance = position.dst(targetPos);
        float targetRadian = (float) Math.atan2(targetPos.x - position.x, targetPos.y - position.y);
        float targetDegree = (float) Math.toDegrees(targetRadian);
        if (targetDistance < viewRadius && getDirectionDegree() - targetDegree < viewDegree) {
            return true;
        }
        return false;
    }

    private void ifGoalInView() {//視野内にゴールが入った場合
        for (Goal goal : env.getGoals()) {
            if (isInView(goal.getCenter())
                    || isInView(goal.getLeftButtom())
                    || isInView(goal.getLeftTop())
                    || isInView(goal.getRightButtom())
                    || isInView(goal.getRightTop())
                    ) {
                this.stateTag = StateTag.moveGoal;
                this.goal = goal.getCenter();
                this.movePos = this.goal;
                break;
            }
        }
    }

    private void randomWalk() {
        stateTag = StateTag.randomWalk;
        float posX = MathUtils.random(Parameter.SCALE.x);
        float posY = MathUtils.random(Parameter.SCALE.y);
        movePos = new Vector2(posX, posY);
    }

    private void judgeCrowd() {
        List<Agent> list = new ArrayList<>();
        for (Agent agent : env.getAgents()) {
            if (!agent.equals(this)) {
                ArrayList<Agent> group = env.getCrowd().getMyGroup(this);
                if (isInView(agent.getPosition())
                        //&& stateTag != StateTag.follow
                        //&& agent.getStateTag() != StateTag.follow
                        && group == null
                        ) {
                    list.add(agent);
                }

            }
        }
        if (list.size() >= Parameter.followNum) {
            stateTag = StateTag.follow;
            Agent agent = list.get(0);
            for (Agent follow : list) {
                if (follow.getStateTag() == StateTag.moveGoal) {
                    agent = follow;
                    break;
                }
                agent = follow;
            }
            followAgent = agent;
            agent.setFollower(this);
        } else {
            randomWalk();
        }
    }

    private void moveGroupPosition() {
        List<Agent> list = new ArrayList<>();
        ArrayList<Agent> group = env.getCrowd().getMyGroup(this);
        for (Agent agent : env.getAgents()) {
            if (!agent.equals(this)
                    && isInView(agent.getPosition())
                    && group == null) {
                list.add(agent);
            }
        }
        if (list.size() >= Parameter.moveGroupNum) {
            movePos = list.get(0).getPosition();
            stateTag = StateTag.moveGroupPosition;
        } else {
            randomWalk();
        }
    }

    public void setFollower(Agent follower) {
        followers.add(follower);
    }

    private void followAgent() {
        movePos = followAgent.getPosition();
        float distance = position.dst(followAgent.getPosition());
        if (distance > 200) {
            stateTag = StateTag.none;
            followAgent = null;
            followers.remove(this);
        }
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

    public Vector2 getGoal() {
        return goal;
    }

    public Agent getFollowAgent() {
        return followAgent;
    }

    public LinkedList<Agent> getFollowers() {
        return followers;
    }


    public Environment getEnv() {
        return env;
    }
}
