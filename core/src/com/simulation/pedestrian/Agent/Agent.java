package com.simulation.pedestrian.Agent;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Environment;
import com.simulation.pedestrian.Goal;
import com.simulation.pedestrian.Obstacle.Obstacle;
import com.simulation.pedestrian.Parameter;
import com.simulation.pedestrian.Potential.PotentialCell;
import com.simulation.pedestrian.Util.Vector;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        float delta = 1f;
        float vectorWeight = 100f;
        //grad(P)
        pVector.x = -1 * (getPotential(position.x + delta, position.y) - getPotential(position.x, position.y)) / delta;
        pVector.y = -1 * (getPotential(position.x, position.y + delta) - getPotential(position.x, position.y)) / delta;
        //pVector.scl(vectorWeight);
        pVector.nor();
        direction.add(pVector);
    }

    private float getPotential(float x, float y) {
        return getAgentKIMPotential(x, y) + getObstaclePotential(x, y);
        //return getAgentDefaultPotential (x, y);
    }

    private float getAgentDefaultPotential(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        float potential = 0;
        for (Agent agent : env.getAgents()) {
            if (!agent.equals(this)) {
                potential += pos.dst(agent.position);
            }
        }
        if(potential <= 0.01){
            potential = 0;
        }
        return potential;
    }

    private float getAgentKIMPotential(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        float potential = 0;
        for (Agent agent : env.getAgents()) {
            if (!agent.equals(this)) {
                //potential += pos.dst(agent.position);
                potential += (float) (Math.exp(-1 * (pos.dst2(agent.position) / (Parameter.KIMPOTENTIALRANGE * Parameter.KIMPOTENTIALRANGE))));
            }
        }
        potential *= Parameter.KIMPOTENTIALWEIGHT;
        if(potential <= 0.01){
            potential = 0;
        }
        return potential;
    }

    private float getObstaclePotential(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        float potential = 0;
        float co = Parameter.KIMPOTENTIALWEIGHT;
        float lo = Parameter.KIMPOTENTIALRANGE;
        for (Obstacle obstacle : env.getObstacles()) {
            for (PotentialCell obstacleCell : obstacle.getObstacleCells()) {
                if (obstacleCell.getCenterPoint().dst(position) <= 10)
                    //potential += pos.dst(obstacleCell.getCenterPoint());
                    potential += (float) Math.exp(-1 * (pos.dst2(obstacleCell.getCenterPoint()) / (lo * lo)));
            }
        }
        potential *= co;
        return potential;
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

    public Agent getFollowAgent() {
        return followAgent;
    }

    public LinkedList<Agent> getFollowers() {
        return followers;
    }


    public Environment getEnv() {
        return env;
    }

    public void writerCSV() {
        // Header
        //   0   1     2         3       4     5        6         7
        // step tag position velocity movepos goal followAgent followers
        String path = env.getLogPath() + "/" + ID + ".txt";
        try {
            if (!(new File(path).exists())) {
                CSVPrinter printer = new CSVPrinter(new FileWriter(path), CSVFormat.DEFAULT);
                printer.printRecord("step", "tag", "position", "velocity", "movepos", "goal", "followAgent", "followers");
                printer.close();
            }
            Reader reader = Files.newBufferedReader(Paths.get(path));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            ArrayList<CSVRecord> csvRecord = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                csvRecord.add(record);
            }
            CSVPrinter printer = new CSVPrinter(new FileWriter(path), CSVFormat.DEFAULT);
            for (CSVRecord record : csvRecord) {
                printer.printRecord(
                        record.get(0), record.get(1),
                        record.get(2), record.get(3),
                        record.get(4), record.get(5),
                        record.get(6), record.get(7));
            }
            printer.printRecord(env.getStep(), stateTag, position, velocity, movePos, goal, followAgent, followers);
            printer.close();
            csvParser.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
