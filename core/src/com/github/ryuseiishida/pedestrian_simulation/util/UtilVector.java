package com.github.ryuseiishida.pedestrian_simulation.util;

import com.badlogic.gdx.math.Vector2;

public final class UtilVector extends Vector2 {

    public static Vector2 direction(Vector2 vec1, Vector2 vec2) {
        Vector2 subVec = new Vector2(vec2).sub(vec1);
        return new Vector2(subVec).nor();
    }

    public static Vector2 sub(Vector2 v1, Vector2 v2) {
        return new Vector2(v2.x - v1.x, v2.y - v1.y);
    }

    public static Vector2 strToVector(String str) {
        String[] splitStr = {};
        if (str.contains(",")) {
            splitStr = str.substring(1, str.length() - 1).split(",", 0);
        } else if (str.contains(":")) {
            splitStr = str.substring(1, str.length() - 1).split(":", 0);
        }
        float x = Float.parseFloat(splitStr[0]);
        float y = Float.parseFloat(splitStr[1]);
        return new Vector2(x, y);
    }
}
