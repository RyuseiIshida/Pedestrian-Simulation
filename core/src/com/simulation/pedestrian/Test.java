package com.simulation.pedestrian;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.Agent.Agent;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
