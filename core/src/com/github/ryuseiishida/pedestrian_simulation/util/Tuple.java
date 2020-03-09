package com.github.ryuseiishida.pedestrian_simulation.util;

public class Tuple<T> {
    public T valueA;
    public T valueB;

    public Tuple(T valueA, T valueB) {
        this.valueA = valueA;
        this.valueB = valueB;
    }

    public T getValueA() {
        return valueA;
    }

    public void setValueA(T valueA) {
        this.valueA = valueA;
    }

    public T getValueB() {
        return valueB;
    }

    public void setValueB(T valueB) {
        this.valueB = valueB;
    }

    @Override
    public String toString() {
        return "[" + valueA + " ," + valueB + "]";
    }
}
