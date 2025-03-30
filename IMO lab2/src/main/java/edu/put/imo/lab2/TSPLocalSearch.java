package edu.put.imo.lab2;

import edu.put.imo.global.TSPSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TSPLocalSearch {
    private static Random rand = new Random();

    private TSPLocalSearch(){}

    public static TSPSolution steepestDescent(TSPSolution solution, NeighbourType neighbourType) {
        solution.neighbourType = neighbourType;
        boolean improved;
        do {
            improved = false;
            TSPSolution bestNeighbor = solution.cloneSolution();
            double bestDelta = 0;
            List<TSPSolution> neighbors = getNeighbors(solution);
            Collections.shuffle(neighbors, rand);
            for (TSPSolution neighbor : getNeighbors(solution)) {
                double delta = computeDelta(solution, neighbor);
                if (delta < bestDelta) {
                    bestDelta = delta;
                    bestNeighbor = neighbor;
                    improved = true;
                }
            }
            solution = bestNeighbor;
        } while (improved);
        return solution;
    }

    public static TSPSolution greedySearch(TSPSolution solution, NeighbourType neighbourType) {
        solution.neighbourType = neighbourType;
        boolean improved;

        do {
            improved = false;
            List<TSPSolution> neighbors = getNeighbors(solution);
            Collections.shuffle(neighbors, rand);
            for (TSPSolution neighbor : neighbors) {
                int delta = computeDelta(solution, neighbor);
                if (delta < 0) {
                    solution = neighbor;
                    improved = true;
                    break;
                }
            }
        } while (improved);
        return solution;
    }

    private static List<TSPSolution> getNeighbors(TSPSolution solution) {

        List<TSPSolution> neighbours = new ArrayList<>(getIntercycleNeighbours(solution));

        if(solution.neighbourType == NeighbourType.ALL || solution.neighbourType == NeighbourType.VERTEX) {
            neighbours.addAll(getVertexNeighbours(solution));
        } else if (solution.neighbourType == NeighbourType.ALL || solution.neighbourType == NeighbourType.EDGE) {
            neighbours.addAll(getEdgeNeighbours(solution));
        }
        return neighbours;
    }

    private static int computeDelta(TSPSolution current, TSPSolution neighbor) {
        return (neighbor.cost - current.cost);
    }

    private static int computeDeltaForSwap(TSPSolution solution, int i, int j, boolean isCycle1) {

        int delta = 0;

        List<Integer> cycle = isCycle1 ? solution.cycle1 : solution.cycle2;
        int prevI = (i == 0) ? cycle.size() - 1 : i - 1;
        int nextI = (i == cycle.size() - 1) ? 0 : i + 1;
        int prevJ = (j == 0) ? cycle.size() - 1 : j - 1;
        int nextJ = (j == cycle.size() - 1) ? 0 : j + 1;

        // Remove old edges
        if(nextJ != i) delta -= solution.distanceMatrix[cycle.get(prevI)][cycle.get(i)];
        if(nextI != j) delta -= solution.distanceMatrix[cycle.get(i)][cycle.get(nextI)];
        if(nextI != j) delta -= solution.distanceMatrix[cycle.get(prevJ)][cycle.get(j)];
        if(nextJ != i) delta -= solution.distanceMatrix[cycle.get(j)][cycle.get(nextJ)];

        // Add new edges
        delta += solution.distanceMatrix[cycle.get(prevI)][cycle.get(j)];
        delta += solution.distanceMatrix[cycle.get(j)][cycle.get(nextI)];
        delta += solution.distanceMatrix[cycle.get(prevJ)][cycle.get(i)];
        delta += solution.distanceMatrix[cycle.get(i)][cycle.get(nextJ)];
        return delta;
    }

    private static int computeDeltaForIntercycleSwap(TSPSolution solution, int i, int j) {
        int delta = 0;

        int prevI = (i == 0) ? solution.cycle1.size() - 1 : i - 1;
        int nextI = (i == solution.cycle1.size() - 1) ? 0 : i + 1;
        int prevJ = (j == 0) ? solution.cycle2.size() - 1 : j - 1;
        int nextJ = (j == solution.cycle2.size() - 1) ? 0 : j + 1;

        // Remove old edges
        delta -= solution.distanceMatrix[solution.cycle1.get(prevI)][solution.cycle1.get(i)];
        delta -= solution.distanceMatrix[solution.cycle1.get(i)][solution.cycle1.get(nextI)];
        delta -= solution.distanceMatrix[solution.cycle2.get(prevJ)][solution.cycle2.get(j)];
        delta -= solution.distanceMatrix[solution.cycle2.get(j)][solution.cycle2.get(nextJ)];

        // Add new edges
        delta += solution.distanceMatrix[solution.cycle1.get(prevI)][solution.cycle2.get(j)];
        delta += solution.distanceMatrix[solution.cycle2.get(j)][solution.cycle1.get(nextI)];
        delta += solution.distanceMatrix[solution.cycle2.get(prevJ)][solution.cycle1.get(i)];
        delta += solution.distanceMatrix[solution.cycle1.get(i)][solution.cycle2.get(nextJ)];

        return delta;
    }

    private static int computeDeltaForEdgeSwap(TSPSolution solution, int i, int j, boolean isCycle1) {
        int delta = 0;

        List<Integer> cycle = isCycle1 ? solution.cycle1 : solution.cycle2;

        // Indeksy w poprzednim i następnym wierzchołku
        int nextI = (i == cycle.size() - 1) ? 0 : i + 1;
        int prevJ = (j == 0) ? cycle.size() - 1 : j - 1;

        if(i == prevJ || j == nextI) {
            return 0;
        }

        // Przed zamianą - odejmujemy istniejące koszty
        delta -= solution.distanceMatrix[cycle.get(i)][cycle.get(nextI)]; // i -> i+1
        delta -= solution.distanceMatrix[cycle.get(prevJ)][cycle.get(j)]; // j-1 -> j

        // Po zamianie - dodajemy nowe koszty
        delta += solution.distanceMatrix[cycle.get(i)][cycle.get(prevJ)]; // i-1 -> j
        delta += solution.distanceMatrix[cycle.get(nextI)][cycle.get(j)]; // j -> i+1

        return delta;
    }

    public static TSPSolution randomWalk(TSPSolution solution, NeighbourType neighbourType, long timeLimit) {
        solution.neighbourType = neighbourType;
        TSPSolution bestSolution = solution.cloneSolution();
        long startTime = System.nanoTime();
        while (System.nanoTime() - startTime < timeLimit) {
            TSPSolution randomNeighbor = getRandomNeighbor(solution);
            if (randomNeighbor.cost < bestSolution.cost) {
                bestSolution = randomNeighbor;
            }
        }
        return bestSolution;
    }

    private static TSPSolution getRandomNeighbor(TSPSolution solution) {
        List<TSPSolution> neighbors = getNeighbors(solution);
        return neighbors.isEmpty() ? solution : neighbors.get(rand.nextInt(neighbors.size()));
    }

    private static void reverseSegment(List<Integer> cycle, int i, int j) {
        i++;
        j--;
        while (i < j) {
            Collections.swap(cycle, i, j);
            i++;
            j--;
        }
    }

    private static ArrayList<TSPSolution> getIntercycleNeighbours(TSPSolution solution){
        ArrayList<TSPSolution> neighbours = new ArrayList<>();
        for (int i = 0; i < solution.cycle1.size(); i++) {
            for (int j = 0; j < solution.cycle2.size(); j++) {
                TSPSolution neighbor = solution.cloneSolution();
                double delta = computeDeltaForIntercycleSwap(solution, i, j);
                int temp = neighbor.cycle1.get(i);
                neighbor.cycle1.set(i, neighbor.cycle2.get(j));
                neighbor.cycle2.set(j, temp);
                neighbor.cost += delta;
                neighbours.add(neighbor);
            }
        }
        return neighbours;
    }

    private static ArrayList<TSPSolution> getVertexNeighbours(TSPSolution solution){
        ArrayList<TSPSolution> neighbours = new ArrayList<>();
        for (int i = 0; i < solution.cycle1.size() - 1; i++) {
            for (int j = i + 1; j < solution.cycle1.size(); j++) {
                TSPSolution neighbor = solution.cloneSolution();
                int delta = computeDeltaForSwap(solution, i, j, true);
                Collections.swap(neighbor.cycle1, i, j);
                neighbor.cost += delta;
                neighbours.add(neighbor);
            }
        }

        for (int i = 0; i < solution.cycle2.size() - 1; i++) {
            for (int j = i + 1; j < solution.cycle2.size(); j++) {
                TSPSolution neighbor = solution.cloneSolution();
                double delta = computeDeltaForSwap(solution, i, j, false);
                Collections.swap(neighbor.cycle2, i, j);
                neighbor.cost += delta;
                neighbours.add(neighbor);
            }
        }
        return neighbours;
    }

    private static ArrayList<TSPSolution> getEdgeNeighbours(TSPSolution solution){
        ArrayList<TSPSolution> neighbours = new ArrayList<>();
        for (int i = 0; i < solution.cycle1.size() - 1; i++) {
            for (int j = i + 2; j < solution.cycle1.size(); j++) {
                TSPSolution neighbor = solution.cloneSolution();
                int delta = computeDeltaForEdgeSwap(solution, i, j, true);
                reverseSegment(neighbor.cycle1, i, j);
                neighbor.cost += delta; // Dodajemy delta tylko, nie obliczamy od nowa
                neighbours.add(neighbor);
            }
        }

        for (int i = 0; i < solution.cycle2.size() - 1; i++) {
            for (int j = i + 2; j < solution.cycle2.size(); j++) {
                TSPSolution neighbor = solution.cloneSolution();
                reverseSegment(neighbor.cycle2, i, j);
                int delta = computeDeltaForEdgeSwap(solution, i, j, false);
                neighbor.cost += delta; // Dodajemy delta tylko, nie obliczamy od nowa
                neighbours.add(neighbor);
            }
        }
        return neighbours;
    }
}
