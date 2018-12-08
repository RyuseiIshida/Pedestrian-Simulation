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
        for (ArrayList<Agent> agents : getGroups()) {
            for (Agent agent : agents) {
                if (targetAgent.equals(agent)){
                    return agents;
                }
            }
        }
        return null;
    }


    public void setLeaders() {
        ArrayList<Agent> leaders = new ArrayList<>();
        for (Agent agent : env.getAgents()) {
            if (!agent.getFollowerAgent().isEmpty() && agent.getFollowAgent() == null) {
                leaders.add(agent);
            }
        }
    }

    public ArrayList<ArrayList<Agent>> getGroups() {
        return groups;
    }

    //FIFO
    public void setGroups(ArrayList<Agent> leaders) {
        for (Agent leader : leaders) {
            ArrayList<Agent> group = new ArrayList<>();
            LinkedList<Agent> FIFO = new LinkedList<>();
            FIFO.add(leader);
            for (int i = 0; i < env.getAgents().size(); i++) {
                ArrayList<Agent> down = FIFO.getLast().getFollowerAgent();
                if (down.size() != 0) {
                    //グループ内にいないやつをadd
                    for (Agent agent : down) {
                        if (!group.contains(agent)) {
                            FIFO.add(down.get(down.size()));
                            break;
                        } else {
                            group.add(FIFO.removeLast());
                        }
                    }
                } else {
                    group.add(FIFO.removeLast());
                }
            }
            groups.add(group);
        }
    }

//    public static Agent getLeader(Agent agent) {
//        //先頭を取得
//        Agent follow = agent.getFollowAgent();
//        if (follow != null) {
//            getLeader(agent);
//        }
//        return agent;
//    }

//    public static void setGroup(ArrayList<Agent> group, ArrayList<Agent> followers) {
//        for (Agent follower : followers) {
//            if (follower != null) {
//                group.add(follower);
//                setGroup(group, follower.getFollowerAgent());
//            }
//        }
//    }
}
