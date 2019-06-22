package com.simulation.pedestrian.util;

public class Tuple {
    public int t1;
    public int t2;

    public Tuple() {
        this.t1 = 0;
        this.t1 = 0;
    }

    public Tuple(int t1, int t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public void setT1(int t1) {
        this.t1 = t1;
    }

    public void setT2(int t2) {
        this.t2 = t2;
    }

    public int getT1() {
        return t1;
    }

    public int getT2() {
        return t2;
    }

    @Override
    public String toString() {
        return "[" + t1 + " ," + t2 + "]";
    }
}
