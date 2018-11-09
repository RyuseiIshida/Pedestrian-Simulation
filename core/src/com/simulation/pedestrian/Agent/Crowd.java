package com.simulation.pedestrian.Agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Crowd {
    public static List<List<Agent>> crowds;

    public static void add(Agent agent1, Agent agent2) {
        // Init crowds
        if (crowds == null) {
            crowds.add(new ArrayList<>(Arrays.asList(agent1, agent2)));
        }
        List<Agent> crowd = ifExist(agent1, agent2);
        if (crowd == null) { //集団がなかった場合
            crowds.add(new ArrayList<>(Arrays.asList(agent1, agent2)));
        } else { //集団に属していた場合
            crowd.add(agent1);
            crowd.add(agent2);
            //重複削除
            crowd.stream().distinct().collect(Collectors.toList());
        }
    }

    private static List<Agent> ifExist(Agent agent1, Agent agent2) {
        for (List<Agent> crowd : crowds) {
            if (crowd.contains(agent1) || crowd.contains(agent2)) {
                return crowd;
            }
        }
        return null;
    }

    public static List<List<Agent>> getCrowds() {
        return crowds;
    }


}
