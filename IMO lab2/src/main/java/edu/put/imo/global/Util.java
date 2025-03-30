package edu.put.imo.global;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

public class Util {
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
