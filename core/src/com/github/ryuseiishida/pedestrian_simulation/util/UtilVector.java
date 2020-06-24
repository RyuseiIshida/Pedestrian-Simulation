package com.github.ryuseiishida.pedestrian_simulation.util;

import com.badlogic.gdx.math.MathUtils;
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

    public static boolean judgeInside(Vector2 startPoint, Vector2 endPoint, Vector2 targetPoint) {
        return startPoint.x <= targetPoint.x && startPoint.y <= targetPoint.y
                && endPoint.x >= targetPoint.x && endPoint.y >= targetPoint.y;
    }

    public static boolean judgeIntersected(Vector2 startPoint, Vector2 endPoint, Vector2 targetStartPoint, Vector2 targetEndPoint) {
        return judgeIntersected(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
                targetStartPoint.x, targetStartPoint.y, targetEndPoint.x, targetEndPoint.y);
    }

    public static boolean judgeIntersected(float ax, float ay, float bx, float by, float cx, float cy, float dx, float dy) {
        float ta = (cx - dx) * (ay - cy) + (cy - dy) * (cx - ax);
        float tb = (cx - dx) * (by - cy) + (cy - dy) * (cx - bx);
        float tc = (ax - bx) * (cy - ay) + (ay - by) * (ax - cx);
        float td = (ax - bx) * (dy - ay) + (ay - by) * (ax - dx);
        return tc * td <= 0 && ta * tb <= 0;
    }

    public static Vector2 getRandomPosition(float startX, float startY, float endX, float endY) {
        float x = MathUtils.random(startX, endX);
        float y = MathUtils.random(startY, endY);
        return new Vector2(x, y);
    }

}
