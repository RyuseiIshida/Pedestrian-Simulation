package com.simulation.pedestrian;

public class Test {
    public Test() {
        Fruit fruit_type = Fruit.Orange;
        System.out.println("fruit_type.getClass() = " + fruit_type.getClass());
        System.out.println(fruit_type);
    }

    public static enum enumTest {
        orange, dest
    }

    public static enum agentEnum {


    }


    public static void main(String[] args) {
        Test tet = new  Test();
    }

    protected enum Fruit {
        Orange,
        Apple,
        Melon,
    }
}
