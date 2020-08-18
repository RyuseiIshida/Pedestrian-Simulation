package com.github.ryuseiishida.pedestrian_simulation.environment.object.agent;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Group {
    private static ArrayList<ArrayList<Agent>> groupList;
    private static ArrayList<ArrayList<Vector2>> groupList2;

    public static ArrayList<ArrayList<Vector2>> getGroup(ArrayList<Vector2> positions) {
        groupList2 = new ArrayList<>();
        for (Vector2 positionElement : positions) {
            ArrayList<Vector2> nearPositions = new ArrayList<>(searchLengthAgent(positionElement, positions));
            if (!searchGroup(nearPositions)) {
                groupList2.add(nearPositions);
            }
        }
        return groupList2;
    }

    private static ArrayList<Vector2> searchLengthAgent(Vector2 targetPosition, ArrayList<Vector2> positions) {
        //距離閾値
        float value = 20 * 100;
        ArrayList<Vector2> group = new ArrayList<>();
        for (Vector2 positionsElement : positions) {
            if (positionsElement.dst(targetPosition) <= value) {
                group.add(positionsElement);
            }
        }
        return group;
    }

    private static boolean searchGroup(ArrayList<Vector2> positions) {
        for (ArrayList<Vector2> group2 : groupList2) {
            for (Vector2 position : group2) {
                if(positions.contains(position)) {
                    return true;
                }
            }
        }
        return false;
    }

}