package edu.put.imo.lab3;

import edu.put.imo.global.TSPSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

public class UpdatedSteep {
    public static void steep(TSPSolution solution) {
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
        // Generate all intercycle moves that aren't deletable
        for (int i = 0; i < solution.cycle1.size(); i++) {
            for (int j = 0; j < solution.cycle2.size(); j++) {
                IntercycleMove move = new IntercycleMove(solution, solution.cycle1.get(i), solution.cycle2.get(j));
                move.calculateDelta();
                moveQueue.add(move);
            }
        }

        // Generate all edge moves that aren't deletable
        generateEdgeMovesForCycle(solution, moveQueue, true);  // cycle1
        generateEdgeMovesForCycle(solution, moveQueue, false); // cycle2
    }

    private static void generateEdgeMovesForCycle(TSPSolution solution, PriorityQueue<Move> moveQueue, boolean isCycle1) {
        List<Integer> cycle = isCycle1 ? solution.cycle1 : solution.cycle2;
        int size = cycle.size();
        // For all vertices in cycle
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Skip adjacent or same vertices
                if (Math.abs(i - j) <= 1) continue;
                EdgeMove move = new EdgeMove(solution, isCycle1, cycle.get(i), cycle.get(j));

                // Calculate delta and add move to queue
                move.calculateDelta();
                moveQueue.add(move);
            }
        }
    }

}
