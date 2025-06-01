package edu.put.imo.lab3;

import edu.put.imo.global.TSPSolution;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TSPTurboLocalSearch {

    private static final long TIME_LIMIT_MS = 100000; // 3 seconds

    public static int turboSteep(TSPSolution solution) {
        long startTime = System.nanoTime();

        // Priority queue sorted by move delta (best moves first)
        PriorityQueue<Move> moveQueue = new PriorityQueue<>(Comparator.comparingLong(Move::getDelta));

        // Initial move generation - add all non-deletable moves
        generateInitialMoves(solution, moveQueue);

        int improvements = 0;
        ArrayList<Move> nonApplicableMoves = new ArrayList<>();
        while (!moveQueue.isEmpty() && !timeExceeded(startTime)) {
            //System.out.printf("Delta: %d | Queue length: %d\n", solution.cost, moveQueue.size());

            // Get the best move (lowest delta)
            Move bestMove = moveQueue.poll();
            if (bestMove == null)
                break;
            // Skip if move is deletable (permanently invalid)
            if (bestMove.isDeletable()) {
                continue;
            }

            // Check if move is currently applicable
            if (!bestMove.isApplicable()) {
                // Re-add to queue if not applicable now but might become applicable later
                nonApplicableMoves.add(bestMove);
                continue;
            }
            //System.out.println(bestMove.getDelta());

            // Stop if no improving moves left
            if (bestMove.getDelta() >= 0) {
                break;
            }
            //System.out.println("Applying...");
            // Apply the move
            bestMove.apply();
            //System.out.println("Applied!");
            solution.cost += bestMove.getDelta();
            improvements++;
            //System.out.println("Updating...");
            // Update the move queue with new potential moves
            updateMoveQueueAfterMove(moveQueue, solution, bestMove, nonApplicableMoves);
            //System.out.println("Updated!");
            nonApplicableMoves.clear();
        }

        return improvements > 0 ? 0 : -1;
    }

    private static boolean timeExceeded(long startTime) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) > TIME_LIMIT_MS;
    }

    private static void generateInitialMoves(TSPSolution solution, PriorityQueue<Move> moveQueue) {
        // Generate all intercycle moves that aren't deletable
        for (int i = 0; i < solution.cycle1.size(); i++) {
            for (int j = 0; j < solution.cycle2.size(); j++) {
                IntercycleMove move = new IntercycleMove(solution, solution.cycle1.get(i), solution.cycle2.get(j));
                move.calculateDelta();
                if (!move.isDeletable()) {
                    moveQueue.add(move);
                }
            }
        }

        // Generate all edge moves that aren't deletable
        generateEdgeMovesForCycle(solution, moveQueue, true);  // cycle1
        generateEdgeMovesForCycle(solution, moveQueue, false); // cycle2
    }

    private static void generateEdgeMovesForCycle(TSPSolution solution, PriorityQueue<Move> moveQueue, boolean isCycle1) {
        List<Integer> cycle = isCycle1 ? solution.cycle1 : solution.cycle2;
        int size = cycle.size();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Only skip adjacent moves if they can't be reversed
                if (Math.abs(i - j) <= 1) continue;

                EdgeMove move = new EdgeMove(solution, isCycle1, cycle.get(i), cycle.get(j));
                move.calculateDelta();
                if (!move.isDeletable()) {
                    moveQueue.add(move);
                }
            }
        }
    }

    private static void updateMoveQueueAfterMove(PriorityQueue<Move> moveQueue, TSPSolution solution, Move appliedMove, ArrayList<Move> nonApplicableMoves) {
        moveQueue.addAll(nonApplicableMoves);
        // Generate new moves based on the applied move

        if (appliedMove instanceof IntercycleMove) {
            //System.out.println("Updating Intercycle...");
            IntercycleMove im = (IntercycleMove) appliedMove;
            addNewIntercycleMovesAfterIntercycle(moveQueue, solution, im.v2, im.v1);
            addNewEdgeMovesAfterIntercycle(moveQueue, solution, im.v1, im.v2);
            //System.out.println("Updated Intercycle!");
        } else if (appliedMove instanceof EdgeMove) {
            //System.out.println("Updating Edge...");
            EdgeMove em = (EdgeMove) appliedMove;
            addNewEdgeMovesAfterEdge(moveQueue, solution, em.v1, em.v2);
            addNewIntercycleMovesAfterEdge(moveQueue, solution, em.v1, em.v2);
            //System.out.println("Updated Edge!");

        }
    }
    private static void addNewIntercycleMovesAfterEdge(PriorityQueue<Move> moveQueue, TSPSolution solution, int v1, int v2) {
        boolean isCycle1 = solution.cycle1.contains(v1);
        List<Integer> cycle = isCycle1 ? solution.cycle1 : solution.cycle2;
        List<Integer> otherCycle = isCycle1 ? solution.cycle2 : solution.cycle1;

        int size = cycle.size();
        if(isCycle1) {
            for (int i = Math.floorMod(cycle.indexOf(v1)-1,cycle.size()); i != Math.floorMod(cycle.indexOf(v2)+1,cycle.size()); i = (i+1)%cycle.size()) {
                for (int j = 0; j < otherCycle.size(); j++) {
                    if (Math.abs(i - j) > 1) {
                        IntercycleMove move = new IntercycleMove(solution, cycle.get(i), otherCycle.get(j));
                        move.calculateDelta();
                        moveQueue.add(move);
                    }
                }
            }
        }
        else {
            for (int i = (cycle.indexOf(v1)) % cycle.size(); i != Math.floorMod(cycle.indexOf(v2),cycle.size()); i = (i+1)%cycle.size()) {
                for (int j = 0; j < otherCycle.size(); j++) {
                    if (Math.abs(i - j) > 1) {
                        IntercycleMove move = new IntercycleMove(solution, otherCycle.get(j), cycle.get(i));
                        move.calculateDelta();
                        moveQueue.add(move);
                    }
                }
            }
        }

    }
    private static void addNewIntercycleMovesAfterIntercycle(PriorityQueue<Move> moveQueue, TSPSolution solution, int newV1, int newV2) {
        // Add all possible intercycle moves involving the swapped vertices
        if (solution.cycle2.contains(newV2)) {
            for (int v : solution.cycle1) {
                IntercycleMove move = new IntercycleMove(solution, v, newV2);
                move.calculateDelta();
                moveQueue.add(move);
            }
        }
        else {
            for (int v : solution.cycle2) {
                IntercycleMove move = new IntercycleMove(solution, newV2, v);
                move.calculateDelta();
                moveQueue.add(move);
            }
        }
        if (solution.cycle2.contains(newV1)) {
            for (int v : solution.cycle1) {
                IntercycleMove move = new IntercycleMove(solution, v, newV1);
                move.calculateDelta();
                moveQueue.add(move);
            }
        }
        else {
            for (int v : solution.cycle2) {
                IntercycleMove move = new IntercycleMove(solution, newV1, v);
                move.calculateDelta();
                moveQueue.add(move);
            }
        }
    }
    private static  void addNewEdgeMovesAfterEdge(PriorityQueue<Move> moveQueue, TSPSolution solution, int v1, int v2) {
        boolean cycleV1Is1 = solution.cycle1.contains(v1);
        List<Integer> cycle = cycleV1Is1 ? solution.cycle1 : solution.cycle2;
        for (int i = Math.floorMod(cycle.indexOf(v1-1),cycle.size()); i != Math.floorMod(cycle.indexOf(v2+1),cycle.size()); i = (i+1)%cycle.size()) {
            //System.out.println(Math.floorMod(cycleV2.indexOf(v2) - 1,cycleV2.size()));
            for (int j = 0; j < cycle.size(); j++) {
                if (Math.abs(i - j) > 1) {
                    EdgeMove move = new EdgeMove(solution, cycleV1Is1, cycle.get(i), cycle.get(j));
                    move.calculateDelta();
                    moveQueue.add(move);

                    move = new EdgeMove(solution, cycleV1Is1, cycle.get(j), cycle.get(i));
                    move.calculateDelta();
                    moveQueue.add(move);
                }
            }
        }
    }
    private static void addNewEdgeMovesAfterIntercycle(PriorityQueue<Move> moveQueue, TSPSolution solution, int v1, int v2) {
        boolean cycleV1Is1 = solution.cycle1.contains(v1);
        List<Integer> cycleV1 = cycleV1Is1 ? solution.cycle1 : solution.cycle2;
        boolean cycleV2Is1 = solution.cycle1.contains(v2);
        List<Integer> cycleV2 = cycleV2Is1 ? solution.cycle1 : solution.cycle2;
        for (int i = 0; i < cycleV1.size(); i++) {
            int current1 = cycleV1.get(i);
            if (Math.abs(cycleV1.indexOf(v1) - i) > 1) {
                EdgeMove move = new EdgeMove(solution, cycleV1Is1, v1, current1);
                move.calculateDelta();
                moveQueue.add(move);

                move = new EdgeMove(solution, cycleV1Is1, current1, v1);
                move.calculateDelta();
                moveQueue.add(move);

            }
        }
        for(int i = 0; i < cycleV2.size(); i++) {
            int current2 = cycleV2.get(i);
            if (Math.abs(cycleV2.indexOf(v2) - i) > 1) {
                EdgeMove move = new EdgeMove(solution, cycleV2Is1, v2, current2);
                move.calculateDelta();
                moveQueue.add(move);

                move = new EdgeMove(solution, cycleV2Is1, current2, v2);
                move.calculateDelta();
                moveQueue.add(move);
            }
        }
        // Add all possible edge moves involving the affected vertices

    }
}