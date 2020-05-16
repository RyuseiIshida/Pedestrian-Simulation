package com.github.ryuseiishida.pedestrian_simulation.environment;

import com.github.ryuseiishida.pedestrian_simulation.obstacle.Line;
import com.github.ryuseiishida.pedestrian_simulation.obstacle.Obstacle;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LoadMap {
    private static final String MAP_PATH = "core/assets/obstacle_map.txt";

    //TODO Environmentインスタンス生成時のSCALEとobstacle_map.txtのSCALEとの突合せ
    public static void setObstacle(ArrayList<Obstacle> obstacles) {
        if (!new File(MAP_PATH).exists()) {
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(Paths.get(MAP_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] points = line.split(",");
                obstacles.add(
                        new Line(
                                Float.parseFloat(points[0]),
                                Float.parseFloat(points[1]),
                                Float.parseFloat(points[2]),
                                Float.parseFloat(points[3]),
                                Parameter.ENV_CELLS_MAP));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
