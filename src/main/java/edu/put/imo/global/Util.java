package edu.put.imo.global;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Util {
    public static void reverseSegment(List<Integer> cycle, int i, int j) {
        int b = cycle.size();
        boolean met = false;
        i = (i+1)%b;
        if (i==j)
            met = true;
        j = ((j-1)%b + b) % b;
        if (i==j)
            met = true;
        while (!met) {
            Collections.swap(cycle, i, j);
            i = (i+1)%b;
            if (i==j)
                met = true;
            j = ((j-1)%b + b) % b;
            if (i==j)
                met = true;
        }
        //System.out.println("REVERSED");
    }
    static Random random = new Random();
    private Util(){}
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static String getTimeString() {
        return (LocalDate.now()+"_"+LocalTime.now()).replace(':','-');
    }

    public static int getRandomInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static int getDistance(int[] pointA, int[] pointB) {
        return (int) Math.round(Math.sqrt(Math.pow((double) pointB[0]-pointA[0],2)+Math.pow((double) pointB[1]-pointA[1],2)));
    }
}
