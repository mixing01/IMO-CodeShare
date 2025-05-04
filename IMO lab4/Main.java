package edu.put.imo.lab4;

import edu.put.imo.global.SolutionPlotter;
import edu.put.imo.global.TSPSolution;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        TSPAlgorithm multipleStartLocalSearch = MultipleStartLocalSearch::multipleStartLocalSearch;
        TSPAlgorithm iteratedLocalSearch = IteratedLocalSearch::iteratedLocalSearch;
        TSPAlgorithm largeNeighbourhoodSearchNoLS = LargeNeighbourhoodSearch::largeNeighbourhoodSearchNoLS;
        TSPAlgorithm largeNeighbourhoodSearchWithLS = LargeNeighbourhoodSearch::largeNeighbourhoodSearchWithLS;



        HashMap<String, TSPAlgorithm> algorithmHashMap = new HashMap<>();
        //algorithmHashMap.put("Multiple Start Local Search", multipleStartLocalSearch);
        algorithmHashMap.put("Large Neighbourhood Search (No LS)", largeNeighbourhoodSearchNoLS);
        algorithmHashMap.put("Large Neighbourhood Search (With LS)", largeNeighbourhoodSearchWithLS);
        final int ITER_NUM = 5;
        String pathA = "src/main/resources/data/kroA200.tsp";
        String pathB = "src/main/resources/data/kroB200.tsp";
        //double timeA = measureTimeForAlgo(ITER_NUM, pathA, multipleStartLocalSearch, "A", "Multiple Start Local Search", -1);
        //double timeB = measureTimeForAlgo(ITER_NUM, pathB, multipleStartLocalSearch, "B", "Multiple Start Local Search", -1);
        for(Map.Entry<String, TSPAlgorithm> entry : algorithmHashMap.entrySet()) {
            measureTimeForAlgo(ITER_NUM, pathA, entry.getValue(), "A", entry.getKey(), 254);
            measureTimeForAlgo(ITER_NUM, pathB, entry.getValue(), "B", entry.getKey(), 254);
        }
    }

    private static double measureTimeForAlgo(int iterNum, String path, TSPAlgorithm algorithm,
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
    private interface TSPAlgorithm {
        Pair<TSPSolution, Integer> apply(String path, double maxTime) throws FileNotFoundException;
    }
}
