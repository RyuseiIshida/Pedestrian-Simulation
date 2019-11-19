package com.simulation.pedestrian.agent;

import java.util.ArrayList;
import java.util.LinkedList;

public class Group {

    /**
     * returns a list of agents by following
     * フォローによってできたエージェントのグループリストを返す
     * @param agentList all agents
     * @return agent group
     */
    private static ArrayList<ArrayList<Agent>> getGroups(ArrayList<Agent> agentList) {
        ArrayList<Agent> leaders = (ArrayList<Agent>) agentList.clone();
        ArrayList<ArrayList<Agent>> groups = new ArrayList<>();
        for (Agent leader : leaders) {
            ArrayList<Agent> group = new ArrayList<>();
            LinkedList<Agent> stack = new LinkedList<>();
            stack.add(leader);
            while (!stack.isEmpty()) {
                //downにstackの一番うしろのフォロワーを入れる
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

    /**
     * returns a list of leader agents in a group
     * グループ内のリーダーエージェントリストを返す
     * @param agentList all agents
     * @return leader agent list
     */
    public static ArrayList<Agent> getLeaders(ArrayList<Agent> agentList) {
        ArrayList<Agent> leaders = new ArrayList<>();
        for (Agent agent : agentList) {
            if (!agent.getFollowers().isEmpty() && agent.getFollowAgent() == null) {
                leaders.add(agent);
            }
        }
        return leaders;
    }

    /**
     * returns size of agent group
     * エージェントグループのサイズ
     * @param agentList all agents
     * @return group size
     */
    public static int getGroupNum(ArrayList<Agent> agentList) {
        return getGroups(getLeaders(agentList)).size();
    }

    /**
     * returns the Agent list of the group to which the passed Agent belongs
     * 渡されたAgentの所属するグループのAgentリストを返す
     * @param agent agents in the group
     * @param agentList all agents
     * @return agent list in the group
     */
    public static ArrayList<Agent> getMyGroupAgents(Agent agent, ArrayList<Agent> agentList) {
        ArrayList<ArrayList<Agent>> groups = getGroups(getLeaders(agentList));
        for (ArrayList<Agent> group : groups) {
            for (Agent groupAgent : group) {
                if (groupAgent.equals(agent)) {
                    return group;
                }
            }
        }
        return null;
    }
}