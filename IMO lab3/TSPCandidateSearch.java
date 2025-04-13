package edu.put.imo.lab3;

import edu.put.imo.global.TSPSolution;
import edu.put.imo.global.Util;
import edu.put.imo.lab2.NeighbourType;

import java.util.*;


public class TSPCandidateSearch {
    private static Random rand = new Random();

    public static TSPSolution steepCandidates(TSPSolution solution, int candidates) {
        boolean improved;
        do {
            improved = false;
            TSPSolution bestNeighbor = solution.cloneSolution();
            double bestDelta = 0;
            List<TSPSolution> neighbors = getNeighbors(solution, candidates);
            for (TSPSolution neighbor : neighbors) {
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


    private static List<TSPSolution> getNeighbors(TSPSolution solution, int candidates) {
        List<TSPSolution> neighbours = new ArrayList<>(getIntercycleNeighbours(solution, candidates));
        neighbours.addAll(getEdgeNeighbours(solution, candidates));
        return neighbours;
    }

    private static int computeDelta(TSPSolution current, TSPSolution neighbor) {
        return (neighbor.cost - current.cost);
    }

    private static int computeDeltaForIntercycleSwap(TSPSolution solution, int i1, int i2) {
        int delta = 0;

        int v1 = solution.cycle1.get(i1);
        int v2 = solution.cycle2.get(i2);

        int size1 = solution.cycle1.size();
        int size2 = solution.cycle2.size();

        int prev1 = solution.cycle1.get((i1 - 1 + size1) % size1);
        int next1 = solution.cycle1.get((i1 + 1) % size1);

        int prev2 = solution.cycle2.get((i2 - 1 + size2) % size2);
        int next2 = solution.cycle2.get((i2 + 1) % size2);

        // Usuwamy stare krawędzie
        delta -= solution.distanceMatrix[prev1][v1];
        delta -= solution.distanceMatrix[v1][next1];
        delta -= solution.distanceMatrix[prev2][v2];
        delta -= solution.distanceMatrix[v2][next2];

        // Dodajemy nowe (po zamianie v1 <-> v2)
        delta += solution.distanceMatrix[prev1][v2];
        delta += solution.distanceMatrix[v2][next1];
        delta += solution.distanceMatrix[prev2][v1];
        delta += solution.distanceMatrix[v1][next2];

        return delta;
    }

    private static int calculateDeltaForEdgeSwap(TSPSolution solution, int v1, int v2, boolean isCycle1) {
        List<Integer> cycle = isCycle1 ? solution.cycle1 : solution.cycle2;
        int delta = 0;
        // Indeksy w poprzednim i następnym wierzchołku
        int nextI = solution.getNextCycleElement(isCycle1, v1);
        int prevJ = solution.getPreviousCycleElement(isCycle1, v2);
        if(v1 != prevJ && v2 != nextI && nextI != prevJ) {
            // Przed zamianą - odejmujemy istniejące koszty
            delta -= solution.distanceMatrix[v1][nextI]; // i -> i+1
            delta -= solution.distanceMatrix[prevJ][v2]; // j-1 -> j

            // Po zamianie - dodajemy nowe koszty
            delta += solution.distanceMatrix[v1][prevJ]; // i-1 -> j
            delta += solution.distanceMatrix[nextI][v2]; // j -> i+1
        }
        else {
            delta = 0;
        }
        return delta;
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

    private static ArrayList<TSPSolution> getIntercycleNeighbours(TSPSolution solution, int n){
        ArrayList<TSPSolution> neighbours = new ArrayList<>();
        for (int v1 : solution.cycle1) {
            ArrayList<Integer> nearest = solution.getNNearestIntercycleNeighbours(n,v1);
            for(int v2 : nearest) {
                TSPSolution neighbor = solution.cloneSolution();
                int i1 = neighbor.cycle1.indexOf(v1);
                int i2 = neighbor.cycle2.indexOf(v2);

                double delta = computeDeltaForIntercycleSwap(solution, i1, i2);
                neighbor.cycle1.set(i1, v2);
                neighbor.cycle2.set(i2, v1);
                neighbor.cost += delta;
                neighbours.add(neighbor);
            }
        }

        for (int v2 : solution.cycle2) {
            ArrayList<Integer> nearest = solution.getNNearestIntercycleNeighbours(n,v2);
            for(int v1 : nearest) {
                TSPSolution neighbor = solution.cloneSolution();
                int i1 = neighbor.cycle1.indexOf(v1);
                int i2 = neighbor.cycle2.indexOf(v2);

                double delta = computeDeltaForIntercycleSwap(solution, i1, i2);
                neighbor.cycle1.set(i1, v2);
                neighbor.cycle2.set(i2, v1);
                neighbor.cost += delta;
                neighbours.add(neighbor);
            }
        }
        return neighbours;
    }


    private static ArrayList<TSPSolution> getEdgeNeighbours(TSPSolution solution, int n){
        ArrayList<TSPSolution> neighbours = new ArrayList<>();
        for (int v1 : solution.cycle1) {
            ArrayList<Integer> nearest = solution.getNNearestNeighbours(n,v1);
            for(int v2 : nearest) {
                TSPSolution neighbor = solution.cloneSolution();
                int i1 = neighbor.cycle1.indexOf(v1);
                int i2 = neighbor.cycle1.indexOf(v2);
                if(i1>i2) {
                    double delta = calculateDeltaForEdgeSwap(solution, v2, v1, true);
                    Util.reverseSegment(neighbor.cycle1, i2, i1);
                    neighbor.cost += delta; // Dodajemy delta tylko, nie obliczamy od nowa
                    neighbours.add(neighbor);
                }
                else if (i1 < i2) {
                    double delta = calculateDeltaForEdgeSwap(solution, v1, v2, true);
                    Util.reverseSegment(neighbor.cycle1, i1, i2);
                    neighbor.cost += delta; // Dodajemy delta tylko, nie obliczamy od nowa
                    neighbours.add(neighbor);
                }

            }
        }

        for (int v2 : solution.cycle2) {
            ArrayList<Integer> nearest = solution.getNNearestNeighbours(n,v2);
            for(int v1 : nearest) {
                TSPSolution neighbor = solution.cloneSolution();
                int i1 = neighbor.cycle2.indexOf(v1);
                int i2 = neighbor.cycle2.indexOf(v2);
                if(i1>i2) {
                    double delta = calculateDeltaForEdgeSwap(solution, v2, v1, false);
                    Util.reverseSegment(neighbor.cycle2, i2, i1);
                    neighbor.cost += delta; // Dodajemy delta tylko, nie obliczamy od nowa
                    neighbours.add(neighbor);
                }
                else if (i1 < i2) {
                    double delta = calculateDeltaForEdgeSwap(solution, v1, v2, false);
                    Util.reverseSegment(neighbor.cycle2, i1, i2);
                    neighbor.cost += delta; // Dodajemy delta tylko, nie obliczamy od nowa
                    neighbours.add(neighbor);
                }
            }
        }
        return neighbours;
    }
}
