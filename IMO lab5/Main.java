package edu.put.imo.lab5;

import edu.put.imo.global.SolutionPlotter;
import edu.put.imo.global.TSPSolution;
import edu.put.imo.lab4.IteratedLocalSearch;
import edu.put.imo.lab4.LargeNeighbourhoodSearch;
import edu.put.imo.lab4.MultipleStartLocalSearch;
import edu.put.imo.lab4.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        GeneticVariation geneticBase = Genetic::geneticBase;
        GeneticVariation geneticBaseLS = Genetic::geneticBaseLS;
        GeneticVariation geneticPermutation = Genetic::geneticPermutation;
        GeneticVariation geneticPermutationLS = Genetic::geneticPermutationLS;

        HashMap<String, GeneticVariation> algorithmHashMap = new HashMap<>();
        algorithmHashMap.put("Genetic Base (No LS)", geneticBase);
        algorithmHashMap.put("Genetic Base (With LS)", geneticBaseLS);
        algorithmHashMap.put("Genetic Permutation (No LS)", geneticPermutation);
        algorithmHashMap.put("Genetic Permutation (With LS)", geneticPermutationLS);
        final int ITER_NUM = 10;
        final int POP_NUM = 20;
        String pathA = "src/main/resources/data/kroA200.tsp";
        String pathB = "src/main/resources/data/kroB200.tsp";
        for(Map.Entry<String, GeneticVariation> entry : algorithmHashMap.entrySet()) {
            measureTimeForAlgo(ITER_NUM, POP_NUM, pathA, entry.getValue(), "A", entry.getKey(), 255);
            measureTimeForAlgo(ITER_NUM, POP_NUM, pathB, entry.getValue(), "B", entry.getKey(), 255);
        }
    }

    private static double measureTimeForAlgo(int iterNum, int popNum, String path, GeneticVariation algorithm,
                                             String datasetName, String algoName, double maxTime) throws IOException {
        TSPSolution bestsol = new TSPSolution(path);
        TSPSolution worstsol = new TSPSolution(path);
        int sumCost = 0;
        double bestTime = 1000000;
        double worstTime = 0;
        double sumTime = 0;
        worstsol.cost = 0;
        int sumOfIters = 0;
        for(int i = 0; i<iterNum; i++) {
            long start = System.nanoTime();
            Pair solIter = algorithm.apply(popNum, path, maxTime);
            long end = System.nanoTime();
            TSPSolution solution = (TSPSolution) solIter.getFirst();
            sumOfIters += (Integer) solIter.getSecond();
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
        System.out.printf("(%s) |%s| Iteracje: %d%n",datasetName, algoName, sumOfIters);
        System.out.printf("(%s) |%s| Czas (best, mean, worst): %f, %f, %f%n",datasetName, algoName, bestTime, sumTime/iterNum, worstTime);
        System.out.printf("(%s) |%s| Koszt (best, mean, worst): %d, %d, %d%n",datasetName, algoName, bestsol.cost, sumCost/iterNum, worstsol.cost);
        SolutionPlotter.plotTSPSolution(bestsol,"("+datasetName+") "+algoName);
        return sumTime/iterNum;
    }

    @FunctionalInterface
    private interface GeneticVariation {
        Pair<TSPSolution, Integer> apply(int popSize, String path, double maxTime) throws IOException;
    }
}
