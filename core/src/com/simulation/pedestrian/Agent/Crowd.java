package com.simulation.pedestrian.Agent;

import com.simulation.pedestrian.Environment;

import java.util.ArrayList;
import java.util.LinkedList;

public class Crowd {
    Environment env;

    public Crowd(Environment env) {
        this.env = env;
    }

    //私はどのGroupに所属していますか?
//    public ArrayList<Agent> getGroup(Agent targetAgent) {
//        initGroup();
//        for (ArrayList<Agent> agents : groups) {
//            for (Agent agent : agents) {
//                return agents;
//            }
//        }
//        return null;
//    }

    public int getCrowdNum() {
        return getGroups(getLeaders()).size();
    }

//    private void initGroup() {
//        getLeaders();
//        groups.clear(); //bug
//        getGroups();
//    }

    private ArrayList<Agent> getLeaders() {
        ArrayList<Agent> leaders = new ArrayList<>();
        for (Agent agent : env.getAgents()) {
            if (!agent.getFollowers().isEmpty() && agent.getFollowAgent() == null) {
                leaders.add(agent);
            }
        }
        if(!leaders.isEmpty()) {
            System.out.println("leaders.siq = " + leaders.size());
        }
        return leaders;
    }

    public ArrayList<ArrayList<Agent>> getGroups(ArrayList<Agent> leaders) {
        ArrayList<ArrayList<Agent>> groups = new ArrayList<>();
        for (Agent leader : leaders) {
            ArrayList<Agent> group = new ArrayList<>();
            LinkedList<Agent> stack = new LinkedList<>();
            stack.add(leader);
            while (stack.size() != 0) {
                LinkedList<Agent> down = (LinkedList<Agent>) stack.getLast().getFollowers().clone();
                if (!down.isEmpty()) {
                    stack.add(down.getLast());
                    down.removeLast();
                } else {
                    group.add(stack.removeLast());
                }
            }
            groups.add(group);
        }
        return groups;
    }
}