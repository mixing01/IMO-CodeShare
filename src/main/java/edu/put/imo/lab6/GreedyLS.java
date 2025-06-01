package edu.put.imo.lab6;

import edu.put.imo.global.TSPSolution;
import edu.put.imo.global.NeighbourType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static edu.put.imo.global.Util.reverseSegment;

public class GreedyLS {
    private GreedyLS(){}
    private static final Random rand = new Random();

    public static TSPSolution greedySearch(TSPSolution solution, NeighbourType neighbourType) {
        solution.neighbourType = neighbourType;
        boolean improved;
        do {
            improved = false;
            List<TSPSolution> neighbors = getNeighbors(solution);
            Collections.shuffle(neighbors, rand);
            for (TSPSolution neighbor : neighbors) {
                neighbor.updateCost();
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
        } else if (solution.neighbourType == NeighbourType.EDGE) {
            neighbours.addAll(getEdgeNeighbours(solution));
        }
        return neighbours;
    }

    private static ArrayList<TSPSolution> getIntercycleNeighbours(TSPSolution solution){
        ArrayList<TSPSolution> neighbours = new ArrayList<>();
        for (int i = 0; i < solution.cycle1.size(); i++) {
            for (int j = 0; j < solution.cycle2.size(); j++) {
                TSPSolution neighbor = solution.cloneSolution();
                int temp = neighbor.cycle1.get(i);
                neighbor.cycle1.set(i, neighbor.cycle2.get(j));
                neighbor.cycle2.set(j, temp);
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
                Collections.swap(neighbor.cycle1, i, j);
                neighbours.add(neighbor);
            }
        }

        for (int i = 0; i < solution.cycle2.size() - 1; i++) {
            for (int j = i + 1; j < solution.cycle2.size(); j++) {
                TSPSolution neighbor = solution.cloneSolution();
                Collections.swap(neighbor.cycle2, i, j);
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
                reverseSegment(neighbor.cycle1, i, j);
                neighbours.add(neighbor);
            }
        }

        for (int i = 0; i < solution.cycle2.size() - 1; i++) {
            for (int j = i + 2; j < solution.cycle2.size(); j++) {
                TSPSolution neighbor = solution.cloneSolution();
                reverseSegment(neighbor.cycle2, i, j);
                neighbours.add(neighbor);
            }
        }
        return neighbours;
    }
    private static int computeDelta(TSPSolution current, TSPSolution neighbor) {
        return (neighbor.cost - current.cost);
    }
}
