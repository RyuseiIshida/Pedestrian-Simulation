package com.simulation.pedestrian.Agent;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Environment;
import com.simulation.pedestrian.Goal;
import com.simulation.pedestrian.Obstacle.Obstacle;
import com.simulation.pedestrian.Parameter;
import com.simulation.pedestrian.Potential.PotentialCell;
import com.simulation.pedestrian.Util.Vector;

import java.util.ArrayList;
import java.util.LinkedList;

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
        this.goal = goal.getCenter();
        this.movePos = goal.getCenter();
        this.velocity = new Vector2(0, 0);
        this.followers = new LinkedList<>();
    }


    public void action() throws Exception {
        decisionMaking();
        move(movePos);
        //writerCSV();
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
        direction.nor();
        velocity = direction.scl(speed);

        Vector2 tmpPos = new Vector2(position);
        tmpPos.add(velocity);
        if (tmpPos.x >= 0 + Parameter.agentRadius && tmpPos.x <= Parameter.SCALE.x - Parameter.agentRadius) {
            position.x = tmpPos.x;
        }
        if (tmpPos.y >= 0 + Parameter.agentRadius && tmpPos.y <= Parameter.SCALE.y - Parameter.agentRadius) {
            position.y = tmpPos.y;
        }
    }

    private void setPotentialVector(Vector2 direction) {
        Vector2 pVector = new Vector2();
        float delta = 1;
        pVector.x = -1 * (getPotential(position.x + delta, position.y) - getPotential(position.x, position.y)) / delta;
        pVector.y = -1 * (getPotential(position.x, position.y + delta) - getPotential(position.x, position.y)) / delta;
        //pVector.scl(vectorWeight);
        pVector.nor();
        direction.add(pVector);
    }

    private float getPotential(float x, float y) {
        return getAgentKIMPotential(x, y) + getObstacleDSTPotential(x, y);
    }

    private float getAgentDSTPotential(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        float potential = 0;
        for (Agent agent : env.getAgents()) {
            if (!agent.equals(this)) {
                potential += pos.dst(agent.position);
            }
        }
        if (potential <= 0.01) {
            potential = 0;
        }
        return potential;
    }

    private float getAgentKIMPotential(float x, float y) {
        float potentialWight = 0;
        float co = Parameter.AGENT_KIMPOTENTIALWEIGHT;
        float lo = Parameter.AGENT_KIMPOTENTIALRANGE;
        Vector2 pos = new Vector2(x, y);
        for (Agent agent : env.getAgents()) {
            if (!agent.equals(this)) {
                potentialWight += co * Math.exp(-1 * (pos.dst2(agent.position) / (lo * lo)));
            }
        }
        if (potentialWight <= 0.01) {
            potentialWight = 0;
        }
        return potentialWight;
    }

    private float getObstacleDSTPotential(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        float potentialWeight = 0;
        float co = Parameter.OBSTACLE_KIMPOTENTIALWEIGHT;
        float lo = Parameter.OBSTACLE_KIMPOTENTIALRANGE;
        for (Obstacle obstacle : env.getObstacles()) {
            for (PotentialCell obstacleCell : obstacle.getObstacleCells()) {
                potentialWeight += co * Math.exp(-1 * (pos.dst2(obstacleCell.getCenterPoint()) / (lo * lo)));
            }
        }
        return potentialWeight;
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
        ArrayList<Agent> list = new ArrayList<>();
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
        ArrayList<Agent> list = new ArrayList<>();
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
        //movePos = followAgent.getPosition();
        movePos = new Vector2(followAgent.getPosition()).sub(followAgent.getVelocity().scl(2f));
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

}
