package com.gihutb.ryuseiishida.simulation.evacuation.util;

public class Tuple<T> {
    public final T valueA;
    public final T valueB;

    public Tuple(T valueA, T valueB) {
        this.valueA = valueA;
        this.valueB = valueB;
    }

    public T getValueA() {
        return valueA;
    }

    public T getValueB() {
        return valueB;
    }

    @Override
    public String toString() {
        return "[" + valueA + " ," + valueB + "]";
    }
}
