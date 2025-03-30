package edu.put.imo.lab2;

import edu.put.imo.global.DataType;
import edu.put.imo.global.SolutionPlotter;
import edu.put.imo.global.TSPSolution;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        Dictionary<String, TSPSolution> bestSolutionsA = new Hashtable<>();
        Dictionary<String, TSPSolution> bestSolutionsB = new Hashtable<>();

        Dictionary<String, Long> worstSolutionsA = new Hashtable<>();
        Dictionary<String, Long> worstSolutionsB = new Hashtable<>();

        Dictionary<String, Long> meanSolutionsA = new Hashtable<>();
        Dictionary<String, Long> meanSolutionsB = new Hashtable<>();


        Dictionary<String, Long> bestTimesA = new Hashtable<>();
        Dictionary<String, Long> bestTimesB = new Hashtable<>();

        Dictionary<String, Long> worstTimesA = new Hashtable<>();
        Dictionary<String, Long> worstTimesB = new Hashtable<>();

        Dictionary<String, Long> meanTimesA = new Hashtable<>();
        Dictionary<String, Long> meanTimesB = new Hashtable<>();


        String filepathA = "src/main/resources/data/kroA200.tsp";
        String filepathB = "src/main/resources/data/kroB200.tsp";
        Object[][] configurations = {
                {"Greedy_Vertex_Random", FunctionType.GREEDY, NeighbourType.VERTEX, false},
                {"Greedy_Vertex_Heuristics", FunctionType.GREEDY, NeighbourType.VERTEX, true},
                {"Greedy_Edge_Random", FunctionType.GREEDY, NeighbourType.EDGE, false},
                {"Greedy_Edge_Heuristics", FunctionType.GREEDY, NeighbourType.EDGE, true},
                {"Steepest_Vertex_Random", FunctionType.STEEP, NeighbourType.VERTEX, false},
                {"Steepest_Vertex_Heuristics", FunctionType.STEEP, NeighbourType.VERTEX, true},
                {"Steepest_Edge_Random", FunctionType.STEEP, NeighbourType.EDGE, false},
                {"Steepest_Edge_Heuristics", FunctionType.STEEP, NeighbourType.EDGE, true}
        };
        int iterations = 2;
        for (Object[] obj : configurations) {
            runExperiment(iterations, filepathA, (String) obj[0], (FunctionType) obj[1], (NeighbourType) obj[2],
                    (Boolean) obj[3], DataType.A, bestSolutionsA, worstSolutionsA, meanSolutionsA,
                    bestTimesA, worstTimesA, meanTimesA);
        }

        for (Object[] obj : configurations) {
            runExperiment(iterations, filepathB, (String) obj[0], (FunctionType) obj[1], (NeighbourType) obj[2],
                    (Boolean) obj[3], DataType.B, bestSolutionsB, worstSolutionsB, meanSolutionsB,
                    bestTimesB, worstTimesB, meanTimesB);
        }

        long highestMeanTimeA = meanTimesA.get(configurations[0][0]);
        long highestMeanTimeB = meanTimesB.get(configurations[0][0]);
        for (Object[] obj : configurations) {
            if(meanTimesA.get(obj[0]) > highestMeanTimeA) {
                highestMeanTimeA = meanTimesA.get(obj[0]);
            }

            if(meanTimesB.get(obj[0]) > highestMeanTimeB) {
                highestMeanTimeB = meanTimesA.get(obj[0]);
            }
        }
        runRandomWalk(iterations, highestMeanTimeA, filepathA, bestSolutionsA, worstSolutionsA, meanSolutionsA,
                    bestTimesA, worstTimesA, meanTimesA);

        runRandomWalk(iterations, highestMeanTimeB, filepathB, bestSolutionsB, worstSolutionsB, meanSolutionsB,
                    bestTimesB, worstTimesB, meanTimesB);
        String bestStringA = "{";
        String bestStringB = "{";
        for (Object[] obj : configurations) {
            bestStringA = bestStringA.concat(((String) obj[0])+"="+bestSolutionsA.get(obj[0]).cost+", ");
            bestStringB = bestStringB.concat(((String) obj[0])+"="+bestSolutionsB.get(obj[0]).cost+", ");
        }
        bestStringA = bestStringA.concat("}");
        bestStringB = bestStringB.concat("}");
        System.out.println("Funkcje celu (najlepsza, najgorsza i średnia):");
        System.out.println(bestStringA);
        System.out.println(worstSolutionsA);
        System.out.println(meanSolutionsA);
        System.out.println();
        System.out.println(bestStringB);
        System.out.println(worstSolutionsB);
        System.out.println(meanSolutionsB);
        System.out.println("Czasy (najlepszy, najgorszy i średni):");
        System.out.println(bestTimesA);
        System.out.println(worstTimesA);
        System.out.println(meanTimesA);
        System.out.println();
        System.out.println(bestTimesB);
        System.out.println(worstTimesB);
        System.out.println(meanTimesB);

        for (Object[] obj : configurations) {
            SolutionPlotter.plotTSPSolution(bestSolutionsA.get(obj[0]),"(A) "+obj[0]);
            SolutionPlotter.plotTSPSolution(bestSolutionsB.get(obj[0]),"(B) "+obj[0]);
        }

        SolutionPlotter.plotTSPSolution(bestSolutionsA.get("Random_walk"),"(A) Random_walk");
        SolutionPlotter.plotTSPSolution(bestSolutionsB.get("Random_walk"),"(B) Random_walk");


    }

    private static void runExperiment(int iterations, String filepath, String key, FunctionType functionType,
                                      NeighbourType neighbourType, boolean useHeuristics, DataType dataType,
                                      Dictionary<String, TSPSolution> bestSolutions, Dictionary<String, Long> worstSolutions,
                                      Dictionary<String, Long> meanSolutions, Dictionary<String, Long> bestTimes,
                                      Dictionary<String, Long> worstTimes, Dictionary<String, Long> meanTimes
    ) throws FileNotFoundException {
        TSPSolution bestSolution = null;
        long totalElapsedTime = 0;

        // Inicjalizacja pierwszego rozwiązania
        TSPSolution tspSolution = useHeuristics ? new TSPSolution(filepath, dataType, 1) : new TSPSolution(filepath);
        long startTime = System.nanoTime();
        if(functionType == FunctionType.GREEDY)
            bestSolution = TSPLocalSearch.greedySearch(tspSolution, neighbourType);
        else if (functionType == FunctionType.STEEP) {
            bestSolution = TSPLocalSearch.steepestDescent(tspSolution, neighbourType);
        }
        long elapsedTime = System.nanoTime() - startTime;

        bestSolutions.put(key, bestSolution);
        worstSolutions.put(key, (long) bestSolution.cost);
        long totalCost = bestSolution.cost;
        bestTimes.put(key, elapsedTime);
        worstTimes.put(key, elapsedTime);
        totalElapsedTime += elapsedTime;

        // Pętla powtarzająca proces 100 razy
        for (int i = 2; i <= iterations; i++) {
            System.out.println("Iteration " + i + " for " + key);
            tspSolution = useHeuristics ? new TSPSolution(filepath, dataType, i) : new TSPSolution(filepath);
            startTime = System.nanoTime();
            TSPSolution newSolution = TSPLocalSearch.greedySearch(tspSolution, neighbourType);
            elapsedTime = System.nanoTime() - startTime;

            if (newSolution.cost < bestSolutions.get(key).cost) {
                bestSolutions.put(key, newSolution);
            } else if (newSolution.cost > worstSolutions.get(key)) {
                worstSolutions.put(key, (long) newSolution.cost);
            }
            totalCost += newSolution.cost;

            if (elapsedTime < bestTimes.get(key)) {
                bestTimes.put(key, elapsedTime);
            } else if (elapsedTime > worstTimes.get(key)) {
                worstTimes.put(key, elapsedTime);
            }
            totalElapsedTime += elapsedTime;
        }
        meanSolutions.put(key, totalCost/iterations);
        meanTimes.put(key, totalElapsedTime/iterations);

    }

    private static void runRandomWalk(int iterations, long lowestMeanTime, String filepath,
                                      Dictionary<String, TSPSolution> bestSolutions, Dictionary<String, Long> worstSolutions,
                                      Dictionary<String, Long> meanSolutions, Dictionary<String, Long> bestTimes,
                                      Dictionary<String, Long> worstTimes, Dictionary<String, Long> meanTimes)
            throws FileNotFoundException {
        String key = "Random_walk";
        TSPSolution bestSolution = null;
        long totalElapsedTime = 0;

        // Inicjalizacja pierwszego rozwiązania
        TSPSolution tspSolution = new TSPSolution(filepath);
        long startTime = System.nanoTime();
        bestSolution = TSPLocalSearch.randomWalk(tspSolution, NeighbourType.ALL, lowestMeanTime);
        long elapsedTime = System.nanoTime() - startTime;

        bestSolutions.put(key, bestSolution);
        worstSolutions.put(key, (long) bestSolution.cost);
        long totalCost = bestSolution.cost;
        bestTimes.put(key, elapsedTime);
        worstTimes.put(key, elapsedTime);
        totalElapsedTime += elapsedTime;

        // Pętla powtarzająca proces 100 razy
        for (int i = 2; i <= iterations; i++) {
            System.out.println("Iteration " + i + " for " + key);
            tspSolution = new TSPSolution(filepath);
            startTime = System.nanoTime();
            TSPSolution newSolution = TSPLocalSearch.randomWalk(tspSolution, NeighbourType.ALL, lowestMeanTime);
            elapsedTime = System.nanoTime() - startTime;

            if (newSolution.cost < bestSolutions.get(key).cost) {
                bestSolutions.put(key, newSolution);
            } else if (newSolution.cost > worstSolutions.get(key)) {
                worstSolutions.put(key, (long) newSolution.cost);
            }
            totalCost += newSolution.cost;

            if (elapsedTime < bestTimes.get(key)) {
                bestTimes.put(key, elapsedTime);
            } else if (elapsedTime > worstTimes.get(key)) {
                worstTimes.put(key, elapsedTime);
            }
            totalElapsedTime += elapsedTime;
        }
        meanSolutions.put(key, totalCost/iterations);
        meanTimes.put(key, totalElapsedTime/iterations);
    }



}
