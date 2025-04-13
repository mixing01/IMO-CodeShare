package edu.put.imo.lab3;

import edu.put.imo.global.SolutionPlotter;
import edu.put.imo.global.TSPSolution;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String pathA = "src/main/resources/data/kroA200.tsp";
        String pathB = "src/main/resources/data/kroB200.tsp";
        TSPSolution bestsol = new TSPSolution(pathA);
        TSPSolution worstsol = new TSPSolution(pathA);
        int sumCost = 0;
        double bestTime = 1000000;
        double worstTime = 0;
        double sumTime = 0;
        worstsol.cost = 0;
        for(int i = 0; i < 100; i++) {
            TSPSolution solution = new TSPSolution(pathA);
            long start = System.nanoTime();
            solution = TSPCandidateSearch.steepCandidates(solution, 10);
            long end = System.nanoTime();
            double elapsedTime = (double) (end - start)/1_000_000_000;
            if(solution.cost < bestsol.cost)
                bestsol = solution;
            else if (solution.cost > worstsol.cost) {
                worstsol = solution;
            }
            sumCost += solution.cost;

            if(elapsedTime < bestTime)
                bestTime = elapsedTime;
            else if (elapsedTime > worstTime) {
                worstTime = elapsedTime;
            }
            sumTime += elapsedTime;
        }
        System.out.printf("(A) Czas (best, mean, worst): %f, %f, %f%n",bestTime, sumTime/100, worstTime);
        System.out.printf("(A) Koszt (best, mean, worst): %d, %d, %d%n",bestsol.cost, sumCost/100, worstsol.cost);
        SolutionPlotter.plotTSPSolution(bestsol,"(A) Candidate Search");


        bestsol = new TSPSolution(pathB);
        worstsol = new TSPSolution(pathB);
        sumCost = 0;
        bestTime = 1000000;
        worstTime = 0;
        sumTime = 0;
        worstsol.cost = 0;
        for(int i = 0; i < 100; i++) {
            TSPSolution solution = new TSPSolution(pathA);
            long start = System.nanoTime();
            solution = TSPCandidateSearch.steepCandidates(solution, 10);
            long end = System.nanoTime();
            double elapsedTime = (double) (end - start)/1_000_000_000;
            if(solution.cost < bestsol.cost)
                bestsol = solution;
            else if (solution.cost > worstsol.cost) {
                worstsol = solution;
            }
            sumCost += solution.cost;

            if(elapsedTime < bestTime)
                bestTime = elapsedTime;
            else if (elapsedTime > worstTime) {
                worstTime = elapsedTime;
            }
            sumTime += elapsedTime;
        }
        System.out.printf("(B) Czas (best, mean, worst): %f, %f, %f%n",bestTime, sumTime/100, worstTime);
        System.out.printf("(B) Koszt (best, mean, worst): %d, %d, %d%n",bestsol.cost, sumCost/100, worstsol.cost);
        SolutionPlotter.plotTSPSolution(bestsol,"(B) Candidate search");
        /*
        TSPSolution bestsol = new TSPSolution(pathA);
        TSPSolution worstsol = new TSPSolution(pathA);
        int sumCost = 0;
        double bestTime = 1000000;
        double worstTime = 0;
        double sumTime = 0;
        worstsol.cost = 0;
        for(int i = 0; i < 100; i++) {
            System.out.println("Iter"+i);
            TSPSolution solution = new TSPSolution(pathA);
            long start = System.nanoTime();
            int code = TSPTurboLocalSearch.turboSteep(solution);
            long end = System.nanoTime();
            if(code == -1) {
                i--;
                continue;
            }
            double elapsedTime = (double) (end - start)/1_000_000_000;
            if(solution.cost < bestsol.cost)
                bestsol = solution;
            else if (solution.cost > worstsol.cost) {
                worstsol = solution;
            }
            sumCost += solution.cost;

            if(elapsedTime < bestTime)
                bestTime = elapsedTime;
            else if (elapsedTime > worstTime) {
                worstTime = elapsedTime;
            }
            sumTime += elapsedTime;
        }
        System.out.printf("(A) Czas (best, mean, worst): %f, %f, %f%n",bestTime, sumTime/100, worstTime);
        System.out.printf("(A) Koszt (best, mean, worst): %d, %d, %d%n",bestsol.cost, sumCost/100, worstsol.cost);
        SolutionPlotter.plotTSPSolution(bestsol,"(A) Move List");

        */
        bestsol = new TSPSolution(pathB);
        worstsol = new TSPSolution(pathB);
        sumCost = 0;
        bestTime = 1000000;
        worstTime = 0;
        sumTime = 0;
        worstsol.cost = 0;
        for(int i = 0; i < 100; i++) {
            TSPSolution solution = new TSPSolution(pathA);
            long start = System.nanoTime();
            int code = TSPTurboLocalSearch.turboSteep(solution);
            long end = System.nanoTime();
            if(code == -1) {
                i--;
                continue;
            }
            double elapsedTime = (double) (end - start)/1_000_000_000;
            if(solution.cost < bestsol.cost)
                bestsol = solution;
            else if (solution.cost > worstsol.cost) {
                worstsol = solution;
            }
            sumCost += solution.cost;

            if(elapsedTime < bestTime)
                bestTime = elapsedTime;
            else if (elapsedTime > worstTime) {
                worstTime = elapsedTime;
            }
            sumTime += elapsedTime;
        }
        System.out.printf("(B) Czas (best, mean, worst): %f, %f, %f%n",bestTime, sumTime/100, worstTime);
        System.out.printf("(B) Koszt (best, mean, worst): %d, %d, %d%n",bestsol.cost, sumCost/100, worstsol.cost);
        SolutionPlotter.plotTSPSolution(bestsol,"(B) Move List");

    }
}
