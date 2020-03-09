package com.gihub.ryuseiishida.simulation.evacuation.util;

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
