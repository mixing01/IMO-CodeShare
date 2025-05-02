package edu.put.imo.lab3;

import edu.put.imo.global.SolutionPlotter;
import edu.put.imo.global.TSPSolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) throws IOException {
        final int ITER_NUM = 10;
        String pathA = "src/main/resources/data/kroA200.tsp";
        String pathB = "src/main/resources/data/kroB200.tsp";
        TSPSolution bestsol = new TSPSolution(pathA);
        TSPSolution worstsol = new TSPSolution(pathA);
        int sumCost = 0;
        double bestTime = 1000000;
        double worstTime = 0;
        double sumTime = 0;
        worstsol.cost = 0;
        worstTime = 0;
        sumTime = 0;
        for(int i = 0; i < ITER_NUM; i++) {
            TSPSolution solution = new TSPSolution(pathA);
            long start = System.nanoTime();
            TSPCandidateSearch.steepCandidates(solution);
            long end = System.nanoTime();
            double elapsedTime = (double) (end - start)/1_000_000_000;
            if(solution.cost < bestsol.cost)
                bestsol = solution;
            if (solution.cost > worstsol.cost) {
                worstsol = solution;
            }
            sumCost += solution.cost;

            if(elapsedTime < bestTime)
                bestTime = elapsedTime;
            if (elapsedTime > worstTime) {
                worstTime = elapsedTime;
            }
            sumTime += elapsedTime;
        }
        System.out.printf("(A) Czas (best, mean, worst): %f, %f, %f%n",bestTime, sumTime/ITER_NUM, worstTime);
        System.out.printf("(A) Koszt (best, mean, worst): %d, %d, %d%n",bestsol.cost, sumCost/ITER_NUM, worstsol.cost);
        SolutionPlotter.plotTSPSolution(bestsol,"(A) Move List");
    }
}
