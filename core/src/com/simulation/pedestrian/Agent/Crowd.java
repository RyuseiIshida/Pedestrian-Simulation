package com.simulation.pedestrian.Agent;

import com.simulation.pedestrian.Environment;

import java.util.ArrayList;
import java.util.LinkedList;

public class Crowd {
    Environment env;
    ArrayList<ArrayList<Agent>> groups = new ArrayList<>();
    ArrayList<Agent> leaders = new ArrayList<>();

    public Crowd(Environment env) {
        this.env = env;
    }

    //私はどのGroupに所属していますか?
    public ArrayList<Agent> getGroup(Agent targetAgent) {
        initGroup();
        for (ArrayList<Agent> agents : groups) {
            for (Agent agent : agents) {
                return agents;
            }
        }
        return null;
    }

    public int getCrowdNum() {
        initGroup();
        return groups.size();
    }

    private void initGroup() {
        leaders.clear();
        setLeaders();
        groups.clear();
        setGroups();
    }

    private void setLeaders() {
        for (Agent agent : env.getAgents()) {
            if (!agent.getFollowerAgent().isEmpty() && agent.getFollowAgent() == null) {
                leaders.add(agent);
            }
        }
    }

    public void setGroups() {
        for (Agent leader : leaders) {
            ArrayList<Agent> group = new ArrayList<>();
            LinkedList<Agent> stack = new LinkedList<>();
            stack.add(leader);
            while (stack.size() != 0) {
                LinkedList<Agent> down = stack.getLast().getFollowerAgent();
                if (!down.isEmpty()) {
                    stack.add(down.getLast());
                    down.removeLast();
                } else {
                    group.add(stack.removeLast());
                }
            }
            groups.add(group);
        }
    }
}