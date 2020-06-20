package com.github.ryuseiishida.pedestrian_simulation.environment.object.obstacle;

import com.badlogic.gdx.math.Vector3;
import com.github.ryuseiishida.pedestrian_simulation.environment.object.cell.CellsMap;

public class CreateLineFromMouse {
    private static Vector3 startPoint;
    private static Vector3 endPoint;

    public static Vector3 getStartPoint() {
        return startPoint;
    }

    public static Vector3 getEndPoint() {
        return endPoint;
    }

    public static void setPoint(Vector3 point) {
        if (startPoint == null) {
            startPoint = point;
        } else {
            endPoint = point;
        }
    }

    public static boolean isCompleteLine() {
        return startPoint != null && endPoint != null;
    }

    public static Line getCompleteLine(CellsMap cellsMap) {
        if (startPoint == null || endPoint == null) {
            return null;
        }
        float x1 = startPoint.x;
        float y1 = startPoint.y;
        float x2 = endPoint.x;
        float y2 = endPoint.y;
        startPoint = null;
        endPoint = null;
        return new Line(x1, y1, x2, y2, cellsMap);
    }
}
