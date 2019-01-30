package com.simulation.pedestrian;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Agent.Agent;
import com.simulation.pedestrian.Agent.Crowd;
import com.simulation.pedestrian.Obstacle.Obstacle;
import com.simulation.pedestrian.Potential.PotentialCell;
import com.simulation.pedestrian.Potential.PotentialMap;
import com.simulation.pedestrian.Util.Tuple;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Environment {
    private float step;
    private Vector2 scale = Parameter.SCALE;
    private PotentialMap envPotentialMap;
    private float cellInterval = Parameter.CELLINTERVAL;
    private float maxPotential = Parameter.MAXPOTENTIAL;
    private float obstaclePotential = Parameter.OBSTACLEPOTENTIAL;
    private int obstaclePotentialRange = Parameter.OBSTACLEPOTENTIALRANGE;
    private List<Goal> goals = new ArrayList<>(Parameter.GOALS);
    private List<Obstacle> obstacles;
    private List<Agent> agents;
    private Crowd crowd;
    private int agentCounter;
    private String logPath;

    public Environment() {
        step = 0;
        agents = new ArrayList<>();
        agentCounter = 0;
        crowd = new Crowd(this);
        spawnInitAgents();
        envPotentialMap = new PotentialMap(scale, cellInterval, maxPotential);
        obstacles = new ArrayList<>();
        //obstacles.add(new Obstacle(150, 150, 300, 100, envPotentialMap, obstaclePotential));
        //setEdgePotential();
        setObstaclePotential();

        //initLogDir();
    }

    public void initLogDir() {
        String path = new File(".").getAbsoluteFile().getParent();
        path += "/core/src/com/simulation/pedestrian/Log/";
        LocalDateTime ldt = LocalDateTime.now();
        logPath = path + ldt;
        new File(logPath).mkdir();
    }

    public String getLogPath() {
        return logPath;
    }

    public void update() {
        agents.stream()
                .parallel()
                .forEach(agent -> {
                    try {
                        agent.action();
                    } catch (final Exception l_exception) {
                    }
                });
        //TODO ポテンシャル関数導入
        //setAgentPotential();
        setAgentKimPotentialCell();
        ifAgentInGoal();
        step++;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public float getStep() {
        return step;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    private void ifAgentInGoal() {
        Iterator<Agent> iterator = agents.iterator();
        while (iterator.hasNext()) {
            Agent agent = iterator.next();
            for (Goal goal : goals) {
                float aPosX = agent.getPosition().x;
                float aPosY = agent.getPosition().y;
                if (aPosX > goal.getPositionX()
                        && aPosY > goal.getPositionY()
                        && aPosX < goal.getRightTop().x
                        && aPosY < goal.getRightTop().y) {
                    iterator.remove();
                }
            }
        }
    }

    //Potential

    public PotentialMap getEnvPotentialMap() {
        return envPotentialMap;
    }

    private void setEdgePotential() {
        //Bottom
        for (int i = 0; i < envPotentialMap.getLastIndex().t1; i++) {
            envPotentialMap.getMatrixPotentialCell(i, 0).setObstaclePotential(obstaclePotential);
        }
        //Top
        for (int i = 0; i < envPotentialMap.getLastIndex().t1; i++) {
            envPotentialMap.getMatrixPotentialCell(i, envPotentialMap.getLastIndex().t2).setObstaclePotential(obstaclePotential);
        }
        //Left
        for (int j = 0; j < envPotentialMap.getLastIndex().t2; j++) {
            envPotentialMap.getMatrixPotentialCell(0, j).setObstaclePotential(obstaclePotential);
        }
        //Right
        for (int j = 0; j < envPotentialMap.getLastIndex().t2; j++) {
            envPotentialMap.getMatrixPotentialCell(envPotentialMap.getLastIndex().t1, j).setObstaclePotential(obstaclePotential);
        }
    }

    private void setAgentKimPotentialCell() {
        float weightPotential;
        float co = Parameter.KIMPOTENTIALWEIGHT;
        float lo = Parameter.KIMPOTENTIALRANGE;
        for (PotentialCell cell : envPotentialMap.getPotentialCells()) {
            cell.setAgentPotential(0); //前ステップ時のポテンシャルセルを初期化
            weightPotential = 0;
            for (Agent agent : agents) {
                float kimPotential = (float) (Math.exp(-1 * (agent.getPosition().dst2(cell.getCenterPoint()) / (lo * lo))));
                //float movePotential = (float) (Math.exp(-1 * ( /(lo*lo))));
                weightPotential += kimPotential;
            }
            cell.setAgentPotential(co*weightPotential);
        }
    }


    public Vector2 getAgentGrad(Vector2 targetPos) {
        if (targetPos.x < 0 || targetPos.x > Parameter.SCALE.x
                || targetPos.y < 0 || targetPos.y > Parameter.SCALE.y) {
            return new Vector2(0, 0);
        }
        Vector2 gradVec = new Vector2();
        float delta = Parameter.CELLINTERVAL/2;
        PotentialCell targetCell = envPotentialMap.getPotentialCell(targetPos);
        PotentialCell deltaXCell = envPotentialMap.getPotentialCell(targetPos.x + delta, targetPos.y);
        PotentialCell deltaYCell = envPotentialMap.getPotentialCell(targetPos.x, targetPos.y + delta);
        if (targetCell.equals(deltaXCell) || targetCell.equals(deltaYCell)) {
            System.out.println("重なりポテンシャルセル");
        }
        gradVec.x = -(deltaXCell.getAgentPotential() - targetCell.getAgentPotential() / delta);
        gradVec.y = -(deltaYCell.getAgentPotential() - targetCell.getAgentPotential() / delta);
        float v = (float) Math.sqrt(gradVec.x * gradVec.x + gradVec.y * gradVec.y);
        //gradVec.x *= 5;
        //gradVec.y *= 5;
        gradVec.x /= v;
        gradVec.y /= v;
        return gradVec;
    }

    private void setLeaderPotential() {
        for (Agent leader : crowd.getLeaders()) {
            Vector2 movePoint = new Vector2(leader.getPosition().x + leader.getVelocity().x,
                    leader.getPosition().y + leader.getVelocity().y);
            PotentialCell targetCell = envPotentialMap.getPotentialCell(movePoint);
            targetCell.setAgentPotential(Parameter.AGENTPOTENTIAL);
        }
    }

    private void setObstaclePotential() {
        for (Obstacle obstacle : obstacles) {
            Tuple startIndex = obstacle.getStartIndex();
            Tuple endIndex = obstacle.getEndIndex();
            int range = 0;
            for (int i = startIndex.t1 - range; i <= endIndex.t1 + range; i++) {
                for (int j = startIndex.t2 - range; j <= endIndex.t2 + range; j++) {
                    if (startIndex.t1 > i || startIndex.t2 > j || endIndex.t1 < i || endIndex.t2 < j) {
                        manhattanDistance(startIndex, endIndex, new Tuple(i, j));
                        envPotentialMap.getMatrixPotentialCell(i, j).setObstaclePotential(range);
                    } else {
                        envPotentialMap.getMatrixPotentialCell(i, j).setObstaclePotential(obstaclePotential);
                    }
                }
            }
            setInterpolationPotential();
        }
        getObstaclePotentialInfo();
    }

    private void setInterpolationPotential() {
        for (Obstacle obstacle : obstacles) {
            int range = obstaclePotentialRange;
            for (int i = obstacle.getStartIndex().t1 - range; i <= obstacle.getEndIndex().t1 + range; i++) {
                for (int j = obstacle.getStartIndex().t2 - range; j <= obstacle.getEndIndex().t2 + range; j++) {
                    if (
                            i >= 0
                                    && j >= 0
                                    && i <= envPotentialMap.getLastIndex().t1
                                    && j <= envPotentialMap.getLastIndex().t2
                    ) {
                        float u = meguroFunc(obstacle.getStartIndex().t1, obstacle.getStartIndex().t2, i, j);
                        envPotentialMap.getMatrixPotentialCell(i, j).addObstaclePotential(u);
                    }
                }
            }
        }
    }

    private float meguroFunc(int x, int y, int xi, int yi) {
        double sigma = 1; //パラメータ
        double distance = Math.sqrt(Math.pow(x - xi, 2) + Math.pow(y - yi, 2));
        return (float) Math.exp(-1 * Math.pow(distance / sigma, 2));
    }

    private void getObstaclePotentialInfo() {
        for (PotentialCell potentialCell : envPotentialMap.getPotentialCells()) {
            if (potentialCell.getObstaclePotential() != 0) {
                System.out.println(envPotentialMap.getIndex(potentialCell) + " = " + potentialCell.getObstaclePotential());
            }
        }
    }

    public void spawnObstacle(Vector2 pos) {
        obstacles.add(new Obstacle(pos.x, pos.y, Parameter.CELLINTERVAL, Parameter.CELLINTERVAL, envPotentialMap, obstaclePotential));
        setObstaclePotential();
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    private int manhattanDistance(Tuple startIndex, Tuple endIndex, Tuple targetIndex) {
        int minDistance = 0;
        for (Obstacle obstacle : obstacles) {
            for (int i = startIndex.t1; i <= endIndex.t2; i++) {
                for (int j = startIndex.t2; j <= endIndex.t2; j++) {
                    int x = Math.abs(targetIndex.t1 - startIndex.t1);
                    int y = Math.abs(targetIndex.t2 - startIndex.t2);
                    int tmpDistance = x + y;
                    if (minDistance == 0) minDistance = tmpDistance;
                    minDistance = minDistance > tmpDistance ? tmpDistance : minDistance;
                }
            }
        }
        return minDistance;
    }

    //Agent

    public void spawnInitAgents() {
        for (int i = 0; i < Parameter.initAgentNum; i++) {
            float x = MathUtils.random(0, scale.x);
            float y = MathUtils.random(0, scale.y);
            Vector2 position = new Vector2(x, y);
            if (i < Parameter.goalAgentNum) {
                agents.add(new Agent(String.valueOf(++agentCounter), this, position, goals.get(0)));
            } else {
                agents.add(new Agent(String.valueOf(++agentCounter), this, position));
            }
        }
    }

    public void spawnAgent1(Vector2 pos) {
        agents.add(new Agent(String.valueOf(++agentCounter), this, pos));
    }

    public void spawnAgent2(Vector2 pos, int goalIndex) {
        agents.add(new Agent(String.valueOf(++agentCounter), this, pos, goals.get(goalIndex)));
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public Crowd getCrowd() {
        return crowd;
    }

    //    public static void main(String[] args) {
//        Environment env = new Environment();
//        ArrayList<Agent> agents = new ArrayList<>();
//        agents.add(new Agent("0", env, new Vector2()));
//        agents.add(new Agent("1", env, new Vector2()));
//        agents.add(new Agent("2", env, new Vector2()));
//        Crowd.add(agents.get(0),agents.get(1));
//        int i = 0;
//        for (List<Agent> list : Crowd.getCrowds()) {
//            System.out.println("-集団" + ++i + "-");
//            for (Agent agent : list) {
//                System.out.println(agent.getID());
//            }
//
//        }
//    }
}
