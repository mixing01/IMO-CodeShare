package edu.put.imo.global;

import edu.put.imo.lab2.NeighbourType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

import static edu.put.imo.global.Util.isNumeric;

public class TSPSolution {
    public List<Integer> cycle1;
    public List<Integer> cycle2;
    public int cost;
    public int[][] distanceMatrix;

    public List<int[]> points;

    public NeighbourType neighbourType;

    public TSPSolution(String filepath) throws FileNotFoundException {
        readPointsFromFile(filepath);

        cycle1 = new ArrayList<>();
        cycle2 = new ArrayList<>();
        ArrayList<Integer> remaining = new ArrayList<>(IntStream.range(0, points.size()).boxed().toList());
        Collections.shuffle(remaining);
        for (int i = 0; i < remaining.size(); i++) {
            if(i % 2 == 0) {
                cycle1.add(remaining.get(i));
            }
            else {
                cycle2.add(remaining.get(i));
            }
        }
        calculateGoalFunction();
    }

    public TSPSolution(String filepath, DataType dataType, int iter) throws FileNotFoundException {
        readPointsFromFile(filepath);

        File file = null;
        if(dataType == DataType.A) {
            file = new File("src/main/resources/data/hr_data/kroA200HR"+iter+".tsp");
        } else if (dataType == DataType.B) {
            file = new File("src/main/resources/data/hr_data/kroB200HR"+iter+".tsp");
        }
        cycle1 = new ArrayList<>();
        cycle2 = new ArrayList<>();
        assert file != null;
        Scanner sc = new Scanner(file);
        while(sc.hasNext()) {
            String line = sc.nextLine();
            if(isNumeric(line.substring(0,1))) {
                String[] split = line.split(" ");
                cycle1.add(Integer.parseInt(split[1]));
                cycle2.add(Integer.parseInt(split[2]));
            }
        }
        sc.close();
        calculateGoalFunction();
    }
    public TSPSolution(List<Integer> c1, List<Integer> c2, int c, int[][] distanceMatrix, List<int[]> points, NeighbourType neighbourType) {
        this.cycle1 = new ArrayList<>(c1);
        this.cycle2 = new ArrayList<>(c2);
        this.cost = c;
        this.distanceMatrix = distanceMatrix;
        this.points = points;
        this.neighbourType = neighbourType;
    }

    public TSPSolution cloneSolution() {
        return new TSPSolution(new ArrayList<>(cycle1), new ArrayList<>(cycle2), cost, distanceMatrix, points, neighbourType);
    }

    private void calculateGoalFunction(){
        for(int i = 0; i < cycle1.size()-1; i++) {
            cost += distanceMatrix[cycle1.get(i)][cycle1.get(i+1)];
        }
        cost += distanceMatrix[cycle1.get((cycle1.size()-1))][cycle1.get(0)];

        for(int i = 0; i < cycle2.size()-1; i++) {
            cost += distanceMatrix[cycle2.get(i)][cycle2.get(i+1)];
        }
        cost += distanceMatrix[cycle2.get((cycle2.size()-1))][cycle2.get(0)];
    }

    private void readPointsFromFile(String filepath) throws FileNotFoundException {
        File f = new File(filepath);
        Scanner sc = new Scanner(f);
        points = new ArrayList<>();
        while(sc.hasNext()) {
            String line = sc.nextLine();
            if(isNumeric(line.substring(0,1))) {
                String[] split = line.split(" ");
                int[] point = {Integer.parseInt(split[1]), Integer.parseInt(split[2])};
                points.add(point);
            }
        }
        sc.close();

        int size = points.size();
        distanceMatrix = new int[size][size];
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
    }
}
