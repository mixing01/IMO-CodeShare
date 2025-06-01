package edu.put.imo.lab3;

import edu.put.imo.global.SolutionPlotter;
import edu.put.imo.global.TSPSolution;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        TSPAlgorithm moveList = TSPTurboLocalSearch::turboSteep;
        TSPAlgorithm candidateList = TSPCandidateSearch::steepCandidates;
        TSPAlgorithm steepClimber = UpdatedSteep::steep;
        HashMap<String, TSPAlgorithm> algorithmHashMap = new HashMap<>();
        algorithmHashMap.put("Move List", moveList);
        //algorithmHashMap.put("Candidate Moves", candidateList);
        //algorithmHashMap.put("Classic Steep Local Search", steepClimber);

        final int ITER_NUM = 10;
        String pathA = "src/main/resources/data/kroA200.tsp";
        String pathB = "src/main/resources/data/kroB200.tsp";

        for(Map.Entry<String, TSPAlgorithm> entry : algorithmHashMap.entrySet()) {
            measureTimeForAlgo(ITER_NUM, pathA, entry.getValue(), "A", entry.getKey());
            measureTimeForAlgo(ITER_NUM, pathB, entry.getValue(), "B", entry.getKey());
        }

    }
    private static void measureTimeForAlgo(int iterNum, String path, TSPAlgorithm algorithm,
                                          String datasetName, String algoName) throws IOException {
        TSPSolution bestsol = new TSPSolution(path);
        TSPSolution worstsol = new TSPSolution(path);
        int sumCost = 0;
        double bestTime = 1000000;
        double worstTime = 0;
        double sumTime = 0;
        worstsol.cost = 0;
        for(int i = 0; i<iterNum; i++) {
            TSPSolution solution = new TSPSolution(path);
            long start = System.nanoTime();
            algorithm.apply(solution);
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
        System.out.printf("(%s) |%s| Czas (best, mean, worst): %f, %f, %f%n",datasetName, algoName, bestTime, sumTime/iterNum, worstTime);
        System.out.printf("(%s) |%s| Koszt (best, mean, worst): %d, %d, %d%n",datasetName, algoName, bestsol.cost, sumCost/iterNum, worstsol.cost);
        SolutionPlotter.plotTSPSolution(bestsol,"("+datasetName+") "+algoName);
    }

    @FunctionalInterface
    private interface TSPAlgorithm {
        void apply(TSPSolution solution);
    }
}
