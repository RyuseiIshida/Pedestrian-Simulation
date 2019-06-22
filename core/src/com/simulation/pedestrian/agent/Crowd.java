package com.simulation.pedestrian.agent;

import com.simulation.pedestrian.Environment;

import java.util.ArrayList;
import java.util.LinkedList;

public class Crowd {
    Environment env;

    public Crowd(Environment env) {
        this.env = env;
    }

    public int getCrowdNum() {
        return getGroups(getLeaders()).size();
    }

    public ArrayList<Agent> getMyGroup(Agent agent) {
        ArrayList<ArrayList<Agent>> groups = getGroups(getLeaders());
        for (ArrayList<Agent> group : groups) {
            for (Agent groupAgent : group) {
                if (groupAgent.equals(agent)) {
                    return group;
                }
            }
        }
        return null;
    }

    public ArrayList<Agent> getLeaders() {
        ArrayList<Agent> leaders = new ArrayList<>();
        for (Agent agent : env.getAllAgent()) {
            if (!agent.getFollowers().isEmpty() && agent.getFollowAgent() == null) {
                leaders.add(agent);
            }
        }
        return leaders;
    }

    private ArrayList<ArrayList<Agent>> getGroups(ArrayList<Agent> list) {
        ArrayList<Agent> leaders = (ArrayList<Agent>) list.clone();
        ArrayList<ArrayList<Agent>> groups = new ArrayList<>();
        for (Agent leader : leaders) {
            ArrayList<Agent> group = new ArrayList<>();
            LinkedList<Agent> stack = new LinkedList<>();
            stack.add(leader);
            while (!stack.isEmpty()) {
                LinkedList<Agent> down = (LinkedList<Agent>) stack.getLast().getFollowers().clone();
                for (Agent groupAgent : group) {
                    down.remove(groupAgent);
                }
                if (down.isEmpty()) {
                    group.add(stack.removeLast());
                } else {
                    stack.add(down.getLast());
                }
            }
            groups.add(group);
        }
        return groups;
    }
}