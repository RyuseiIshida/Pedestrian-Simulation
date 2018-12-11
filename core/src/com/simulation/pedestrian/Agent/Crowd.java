package com.simulation.pedestrian.Agent;

import com.simulation.pedestrian.Environment;

import java.util.ArrayList;
import java.util.LinkedList;

public class Crowd {
    Environment env;

    public Crowd(Environment env) {
        this.env = env;
    }

    public int getCrowdNum() {
        ArrayList<Agent> leaders = getLeaders();
        return getGroups(getLeaders()).size();
    }

    private ArrayList<Agent> getLeaders() {
        ArrayList<Agent> leaders = new ArrayList<>();
        for (Agent agent : env.getAgents()) {
            if (!agent.getFollowers().isEmpty() && agent.getFollowAgent() == null) {
                leaders.add(agent);
            }
        }
        return leaders;
    }

    public ArrayList<ArrayList<Agent>> getGroups(ArrayList<Agent> list) {
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
                if(down.isEmpty()){
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