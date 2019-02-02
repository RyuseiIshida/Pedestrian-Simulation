package com.simulation.pedestrian;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {
    public Test() {
        String str = "\"(1, 2)\"";
        System.out.println("str = " + str);
        str = str.replace("\"*", "");
        System.out.println("str = " + str);

    }
    public static void main(String[] args) {
        Test test = new Test();
    }
}
