package edu.put.imo.lab6;

import edu.put.imo.global.TSPSolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ResultReader {
    public static ArrayList<TSPSolution> readGreedyResults(String filepath) throws FileNotFoundException {
        ArrayList<TSPSolution> solutions = new ArrayList<>();
        try(Scanner sc = new Scanner(new File(filepath))) {
            while(sc.hasNext()) {
                String cycle1Str = sc.nextLine();
                String cycle2Str = sc.nextLine();
                String costStr = sc.nextLine();
                sc.nextLine();

                cycle1Str = cycle1Str.substring(1,cycle1Str.length()-1);
                cycle2Str = cycle2Str.substring(1,cycle2Str.length()-1);
                List<String> cycle1List = new ArrayList<>(Arrays.asList(cycle1Str.split(", ")));
                List<String> cycle2List = new ArrayList<>(Arrays.asList(cycle2Str.split(", ")));

                ArrayList<Integer> cycle1 = new ArrayList<>(cycle1List.stream().map(Integer::parseInt).toList());
                ArrayList<Integer> cycle2 = new ArrayList<>(cycle2List.stream().map(Integer::parseInt).toList());
                int cost = Integer.parseInt(costStr);

                solutions.add(new TSPSolution(cycle1, cycle2, cost));
            }
        }

        return solutions;
    }

    public static TSPSolution readBenchmarkResults(String filepath) throws FileNotFoundException {
        TSPSolution solution;
        try(Scanner sc = new Scanner(new File(filepath))) {
            String cycle1Str = sc.nextLine();
            String cycle2Str = sc.nextLine();
            String costStr = sc.nextLine();
            sc.nextLine();

            cycle1Str = cycle1Str.substring(1,cycle1Str.length()-1);
            cycle2Str = cycle2Str.substring(1,cycle2Str.length()-1);
            List<String> cycle1List = new ArrayList<>(Arrays.asList(cycle1Str.split(", ")));
            List<String> cycle2List = new ArrayList<>(Arrays.asList(cycle2Str.split(", ")));

            ArrayList<Integer> cycle1 = new ArrayList<>(cycle1List.stream().map(Integer::parseInt).toList());
            ArrayList<Integer> cycle2 = new ArrayList<>(cycle2List.stream().map(Integer::parseInt).toList());
            int cost = Integer.parseInt(costStr);

            solution = new TSPSolution(cycle1, cycle2, cost);
        }

        return solution;
    }
}
