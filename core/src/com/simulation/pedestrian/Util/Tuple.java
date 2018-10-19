package com.simulation.pedestrian.Util;

import java.util.ArrayList;
import java.util.Arrays;

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

    @Override
    public String toString() {
        return "[" + t1 + " ," + t2 + "]";
    }
}
