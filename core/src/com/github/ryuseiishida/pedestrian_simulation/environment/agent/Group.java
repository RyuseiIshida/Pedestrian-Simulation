package com.github.ryuseiishida.pedestrian_simulation.environment.agent;

import java.util.ArrayList;

public class Group {
    private static ArrayList<ArrayList<Agent>> groupList;

    public static ArrayList<ArrayList<Agent>> getGroup(ArrayList<Agent> agentList) {
        groupList = new ArrayList<>();
        for (Agent agent : agentList) {
            ArrayList<Agent> nearAgentList = new ArrayList<>(searchLengthAgent(agent, agentList));
            if (!searchGroup(nearAgentList)) {
                groupList.add(nearAgentList);
            }
        }
        return groupList;
    }

    private static ArrayList<Agent> searchLengthAgent(Agent targetAgent, ArrayList<Agent> agentList) {
        //距離閾値
        float value = 20 * 100;
        ArrayList<Agent> group = new ArrayList<>();
        for (Agent agent : agentList) {
            if (targetAgent.getPosition().dst(agent.getPosition()) <= value) {
                group.add(agent);
            }
        }
        return group;
    }

    private static boolean searchGroup(ArrayList<Agent> agentList) {
        for (ArrayList<Agent> group : groupList) {
            for (Agent agent : group) {
                if (agentList.contains(agent)) {
                    return true;
                }
            }
        }
        return false;
    }


}