package com.simulation.pedestrian.Agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Crowd {
    public static List<List<Agent>> crowds = new ArrayList<>();

    public static void add(Agent agent1, Agent agent2) {
        // Init crowds
        if (crowds == null) {
            crowds.add(new ArrayList<>(Arrays.asList(agent1, agent2)));
        }
        List<Agent> crowd = getCrowd(agent1, agent2);
        if (crowd == null) {
            crowds.add(new ArrayList<>(Arrays.asList(agent1, agent2)));
        } else {
            crowd.add(agent1);
            crowd.add(agent2);
            //重複削除
            crowd.stream().distinct().collect(Collectors.toList());
        }
    }

    private static List<Agent> getCrowd(Agent agent1, Agent agent2) {
        for (List<Agent> crowd : crowds) {
            if (crowd.contains(agent1) || crowd.contains(agent2)) {
                return crowd;
            }
        }
        return null;
    }

    public static List<Agent> getCrowd(Agent agent) {
        for (List<Agent> crowd : crowds) {
            if (crowd.contains(agent)) {
                return crowd;
            }
        }
        return null;
    }

}
