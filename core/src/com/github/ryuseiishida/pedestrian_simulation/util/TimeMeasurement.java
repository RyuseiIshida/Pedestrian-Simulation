package com.github.ryuseiishida.pedestrian_simulation.util;

public class TimeMeasurement {
    private static long first;
    private static long last;

    public void start() {
        first = System.currentTimeMillis();
    }

    public void stop() {
        last = System.currentTimeMillis();
        System.out.println("time = " + (last - first));
    }
}
