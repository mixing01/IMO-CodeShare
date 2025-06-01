package edu.put.imo.lab7;

import edu.put.imo.global.SolutionPlotter;
import edu.put.imo.global.TSPSolution;
import edu.put.imo.lab4.Pair;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        //Pair<TSPSolution, Integer> pair = LargeNeighbourhoodSplit.largeNeighbourhoodSplit("src/main/resources/data/kroB200.tsp",10);
        //System.out.println(pair.getFirst().cycle1.size());
        //System.out.println(pair.getFirst().cycle2.size());
        //System.out.println(pair.getFirst().cost);

        Algorithm iteratedSplit = IteratedSplit::iteratedLocalSearch;
        Algorithm largeNeighbourhoodSplit = LargeNeighbourhoodSplit::largeNeighbourhoodSplit;

        HashMap<String, Algorithm> algorithmHashMap = new HashMap<>();
        algorithmHashMap.put("Better ILS", iteratedSplit);
        algorithmHashMap.put("Better LNS", largeNeighbourhoodSplit);
        final int ITER_NUM = 10;
        String pathA = "src/main/resources/data/kroA200.tsp";
        String pathB = "src/main/resources/data/kroB200.tsp";
        for(Map.Entry<String, Algorithm> entry : algorithmHashMap.entrySet()) {
            measureTimeForAlgo(ITER_NUM, pathA, entry.getValue(), "A", entry.getKey(), 255);
            measureTimeForAlgo(ITER_NUM, pathB, entry.getValue(), "B", entry.getKey(), 255);
        }

    }

    private static double measureTimeForAlgo(int iterNum, String path, Algorithm algorithm,
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
            Pair solIter = algorithm.apply(path, maxTime);
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
    private interface Algorithm {
        Pair<TSPSolution, Integer> apply(String path, double maxTime) throws IOException;
    }
}
