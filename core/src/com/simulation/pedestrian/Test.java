package com.simulation.pedestrian;

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
        Calendar  calendar = Calendar.getInstance();

        System.out.println(calendar.getTime());
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd_hhmm");
        String formatData = format.format(calendar.getTime());
        System.out.println("formatData = " + formatData);

    }



    public static void main(String[] args) {
        Test test = new Test();
    }
}
