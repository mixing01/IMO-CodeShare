package edu.put.imo.lab7;

import edu.put.imo.global.TSPSolution;
import edu.put.imo.global.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static edu.put.imo.global.Util.isNumeric;

public class PointSplitter {
    public static TSPSolution splitFromFile(String filepath) throws FileNotFoundException {
        int maxX = -1;
        int minX = 1000000000;
        File f = new File(filepath);
        Scanner sc = new Scanner(f);
        ArrayList<int[]> points = new ArrayList<>();
        while(sc.hasNext()) {
            String line = sc.nextLine();
            if(isNumeric(line.substring(0,1))) {
                String[] split = line.split(" ");
                int x = Integer.parseInt(split[1]);
                int y = Integer.parseInt(split[2]);
                if(x > maxX)
                    maxX = x;
                if (x < minX)
                    minX = x;
                int[] point = {x, y};
                points.add(point);
            }
        }
        sc.close();
        int splitPoint = minX + (maxX-minX)/2;
        int size = points.size();
        Integer[][] distanceMatrix = new Integer[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = i; j < size; j++) {
                if(i==j) {
                    distanceMatrix[i][j] = 0;
                }
                else {
                    distanceMatrix[i][j] = Util.getDistance(points.get(i),points.get(j));
                    distanceMatrix[j][i] = Util.getDistance(points.get(j),points.get(i));
                }
            }
        }
        ArrayList<Integer> pointsLeft = new ArrayList<>();
        ArrayList<Integer> pointsRight = new ArrayList<>();

        for(int i = 0; i<points.size(); i++) {
            if(points.get(i)[0] <= splitPoint) {
                pointsLeft.add(i);
            }
            else {
                pointsRight.add(i);
            }
        }

        while(pointsLeft.size() > pointsRight.size()+1) {
            int maxXTemp = -1;
            int maxI = -1;
            for(int i: pointsLeft) {
                if(points.get(i)[0] > maxXTemp) {
                    maxXTemp = points.get(i)[0];
                    maxI = i;
                }
            }
            pointsLeft.remove((Integer) maxI);
            pointsRight.add(maxI);
        }

        while(pointsRight.size() > pointsLeft.size()+1) {
            int minXTemp = 1000000;
            int minI = -1;
            for(int i: pointsRight) {
                if(points.get(i)[0] < minXTemp) {
                    minXTemp = points.get(i)[0];
                    minI = i;
                }
            }
            pointsRight.remove((Integer) minI);
            pointsLeft.add(minI);
        }
        TSPSolution solution = new TSPSolution(pointsLeft, pointsRight, -1, distanceMatrix, points);
        solution.updateCost();
        return solution;
    }

}
