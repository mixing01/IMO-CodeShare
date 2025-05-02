package edu.put.imo.lab3;

import edu.put.imo.global.TSPSolution;
import edu.put.imo.global.Util;
import edu.put.imo.lab2.NeighbourType;

import java.util.*;


public class TSPCandidateSearch {
    public static void steepCandidates(TSPSolution solution) {
        // Priority queue sorted by delta
        PriorityQueue<Move> moveQueue = new PriorityQueue<>(Comparator.comparingLong(Move::getDelta));

        do {
            // Clear the queue from previous moves
            moveQueue.clear();

            // Generate new moves
            generateMoves(solution, moveQueue);

            // Get the best move
            Move bestMove = moveQueue.poll();

            // If there is none or if there is no improvement then end
            if (bestMove == null || bestMove.getDelta() >= 0)
                break;

            // Apply the best move
            bestMove.apply();

            // Calculate the cost
            solution.cost += bestMove.getDelta();
        } while (!moveQueue.isEmpty());
    }

    private static void generateMoves(TSPSolution solution, PriorityQueue<Move> moveQueue) {
        // Generate all intercycle moves
        for (int i = 0; i < solution.cycle1.size(); i++) {
            int v1 = solution.cycle1.get(i);
            ArrayList<Integer> neighbours = solution.getNNearestIntercycleNeighbours(10,v1);
            for (int neighbour : neighbours) {
                IntercycleMove move = new IntercycleMove(solution, solution.getPreviousCycleElement(true, v1), neighbour);
                move.calculateDelta();
                moveQueue.add(move);

                move = new IntercycleMove(solution, solution.getNextCycleElement(true, v1), neighbour);
                move.calculateDelta();
                moveQueue.add(move);
            }
        }

        for (int i = 0; i < solution.cycle2.size(); i++) {
            int v2 = solution.cycle2.get(i);
            ArrayList<Integer> neighbours = solution.getNNearestIntercycleNeighbours(10,v2);
            for (int neighbour : neighbours) {
                IntercycleMove move = new IntercycleMove(solution, neighbour, solution.getPreviousCycleElement(false, v2));
                move.calculateDelta();
                moveQueue.add(move);

                move = new IntercycleMove(solution, neighbour, solution.getNextCycleElement(false, v2));
                move.calculateDelta();
                moveQueue.add(move);
            }
        }

        // Generate all edge moves
        generateEdgeMovesForCycle(solution, moveQueue, true);  // cycle1
        generateEdgeMovesForCycle(solution, moveQueue, false); // cycle2
    }

    private static void generateEdgeMovesForCycle(TSPSolution solution, PriorityQueue<Move> moveQueue, boolean isCycle1) {
        List<Integer> cycle = isCycle1 ? solution.cycle1 : solution.cycle2;
        int size = cycle.size();
        // For all vertices in cycle
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> neighbours = solution.getNNearestNeighbours(10,cycle.get(i));
            for (int neighbour : neighbours) {
                // Skip adjacent or same vertices
                if (Math.abs(i - cycle.indexOf(neighbour)) <= 1) continue;
                EdgeMove move = new EdgeMove(solution, isCycle1, solution.getPreviousCycleElement(isCycle1, cycle.get(i)), neighbour);
                // Calculate delta and add move to queue
                move.calculateDelta();
                moveQueue.add(move);

                move = new EdgeMove(solution, isCycle1, cycle.get(i), solution.getNextCycleElement(isCycle1, neighbour));
                // Calculate delta and add move to queue
                move.calculateDelta();
                moveQueue.add(move);
            }
        }
    }

}
