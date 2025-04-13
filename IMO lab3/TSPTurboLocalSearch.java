package edu.put.imo.lab3;

import edu.put.imo.global.TSPSolution;

import java.util.ArrayList;
import java.util.List;

public class TSPTurboLocalSearch {

    public static int turboSteep(TSPSolution solution) {
        long start = System.nanoTime();
        ArrayList<IntercycleMove> intercycleMoves = (ArrayList<IntercycleMove>) generateIntercycleMoves(solution);
        ArrayList<EdgeMove> edgeMoves = (ArrayList<EdgeMove>) generateEdgeMoves(solution);
        do {
            if((double) (System.nanoTime() - start) / 1_000_000_000 > 3)
                return -1;
            edgeMoves.removeIf(EdgeMove::isDeletable);
            //intercycleMoves.removeIf(IntercycleMove::isDeletable);
            Move bestMove = findBestMove(edgeMoves, intercycleMoves);
            //System.out.println(bestMove.getDelta());
            if (bestMove.getDelta() >= 0)
                break;
            //System.out.println("\nNEW MOVE:");
            //System.out.println(solution.cycle1);
            //System.out.println(solution.cycle2);
            bestMove.apply();
            solution.cost+=bestMove.getDelta();
            //bestMove.printMove();
            //System.out.println(solution.cycle1);
            //System.out.println(solution.cycle2);
            if(bestMove instanceof IntercycleMove) {
                edgeMoves = (ArrayList<EdgeMove>) generateNewEdgeMovesAfterI(solution, edgeMoves, ((IntercycleMove) bestMove).v2, ((IntercycleMove) bestMove).v1);
                intercycleMoves = (ArrayList<IntercycleMove>) generateNewIntercycleMovesAfterI(solution, intercycleMoves, ((IntercycleMove) bestMove).v2, ((IntercycleMove) bestMove).v1);
            }
            else {
                edgeMoves = (ArrayList<EdgeMove>) generateNewEdgeMovesAfterE(solution, edgeMoves, ((EdgeMove) bestMove).v1, ((EdgeMove) bestMove).v2, ((EdgeMove) bestMove).isCycle1);
                intercycleMoves = (ArrayList<IntercycleMove>) generateNewIntercycleMovesAfterE(solution, intercycleMoves, ((EdgeMove) bestMove).v1, ((EdgeMove) bestMove).v2, ((EdgeMove) bestMove).isCycle1);
            }
        } while (true);
        return 0;
    }
    private static List<IntercycleMove> generateIntercycleMoves(TSPSolution solution) {
        ArrayList<IntercycleMove> moves = new ArrayList<>();
        for (int i = 0; i < solution.cycle1.size(); i++) {
            for (int j = 0; j < solution.cycle2.size(); j++) {
                IntercycleMove newM = new IntercycleMove(solution, solution.cycle1.get(i), solution.cycle2.get(j));
                newM.calculateDelta();
                moves.add(newM);
            }
        }
        return moves;
    }
    private static List<IntercycleMove> generateNewIntercycleMovesAfterI(TSPSolution solution, List<IntercycleMove> m, int new1, int new2) {
        ArrayList<IntercycleMove> moves = new ArrayList<>(m);
        for (int i = 0; i < solution.cycle1.size(); i++) {
                IntercycleMove newM = new IntercycleMove(solution, solution.cycle1.get(i), new2);
                if(!newM.isDeletable()) {
                    newM.calculateDelta();
                    moves.add(newM);
                }
        }
        for (int i = 0; i < solution.cycle2.size(); i++) {
            IntercycleMove newM = new IntercycleMove(solution, new1, solution.cycle2.get(i));
            if(!newM.isDeletable()) {
                newM.calculateDelta();
                moves.add(newM);
            }
        }
        return moves;
    }
    private static List<IntercycleMove> generateNewIntercycleMovesAfterE(TSPSolution solution, List<IntercycleMove> m, int new1, int new2, boolean isCycle1) {
        ArrayList<IntercycleMove> moves = new ArrayList<>(m);
        if(isCycle1) {
            for (int i = 0; i < solution.cycle2.size(); i++) {
                IntercycleMove newM1 = new IntercycleMove(solution, new1, solution.cycle2.get(i));
                IntercycleMove newM2 = new IntercycleMove(solution, solution.getNextCycleElement(true, new1), solution.cycle2.get(i));
                IntercycleMove newM3 = new IntercycleMove(solution, new2, solution.cycle2.get(i));
                IntercycleMove newM4 = new IntercycleMove(solution, solution.getPreviousCycleElement(true, new1), solution.cycle2.get(i));
                IntercycleMove[] newMoves = {newM1, newM2, newM3, newM4};
                for (IntercycleMove newM: newMoves) {
                    if(!newM.isDeletable()) {
                        newM.calculateDelta();
                        moves.add(newM);
                    }
                }
            }
            return moves;
        }
        else {
            for (int i = 0; i < solution.cycle1.size(); i++) {
                IntercycleMove newM1 = new IntercycleMove(solution, solution.cycle1.get(i), new1);
                IntercycleMove newM2 = new IntercycleMove(solution, solution.cycle1.get(i), solution.getNextCycleElement(false, new1));
                IntercycleMove newM3 = new IntercycleMove(solution, solution.cycle1.get(i), new2);
                IntercycleMove newM4 = new IntercycleMove(solution, solution.cycle1.get(i), solution.getPreviousCycleElement(false, new1));
                IntercycleMove[] newMoves = {newM1, newM2, newM3, newM4};
                for (IntercycleMove newM: newMoves) {
                    if(!newM.isDeletable()) {
                        newM.calculateDelta();
                        moves.add(newM);
                    }
                }
            }
            return moves;
        }

    }
    private static List<EdgeMove> generateEdgeMoves(TSPSolution solution) {
        ArrayList<EdgeMove> moves = new ArrayList<>();
        for (int i = 0; i < solution.cycle1.size(); i++) {
            for (int j = 0; j < solution.cycle1.size(); j++) {
                if (Math.abs(i-j) > 2) {
                    EdgeMove newM = new EdgeMove(solution, true, solution.cycle1.get(i), solution.cycle1.get(j));
                    newM.calculateDelta();
                    moves.add(newM);
                }

            }
        }

        for (int i = 0; i < solution.cycle2.size(); i++) {
            for (int j = 0; j < solution.cycle2.size(); j++) {
                if (Math.abs(i-j) > 2) {
                    EdgeMove newM = new EdgeMove(solution, false, solution.cycle2.get(i), solution.cycle2.get(j));
                    newM.calculateDelta();
                    moves.add(newM);
                }

            }
        }

        return moves;
    }

    private static List<EdgeMove> generateNewEdgeMovesAfterI(TSPSolution solution, List<EdgeMove> m, int new1, int new2) {
        ArrayList<EdgeMove> moves = new ArrayList<>(m);
        for (int i = 0; i < solution.cycle1.size(); i++) {
            if (Math.abs(i-solution.cycle1.indexOf(new1)) > 2) {
                EdgeMove edgeMove = new EdgeMove(solution, true, solution.cycle1.get(i), new1);
                if (!edgeMove.isDeletable()) {
                    edgeMove.calculateDelta();
                    moves.add(edgeMove);
                }
            }
        }

        for (int i = 0; i < solution.cycle2.size(); i++) {
            if (Math.abs(i-solution.cycle2.indexOf(new2)) > 2) {
                EdgeMove edgeMove = new EdgeMove(solution, false, solution.cycle2.get(i), new2);
                if (!edgeMove.isDeletable()) {
                    edgeMove.calculateDelta();
                    moves.add(edgeMove);
                }
            }
        }

        return moves;
    }

    private static List<EdgeMove> generateNewEdgeMovesAfterE(TSPSolution solution, List<EdgeMove> m, int new1, int new2, boolean isCycle1) {
        ArrayList<EdgeMove> moves = new ArrayList<>(m);
        if(isCycle1) {
            for (int i = 0; i < solution.cycle1.size(); i++) {
                if (Math.abs(i- solution.cycle1.indexOf(new1)) > 2) {
                    EdgeMove edgeMove = new EdgeMove(solution, true, solution.cycle1.get(i), new1);
                    if (!edgeMove.isDeletable()) {
                        edgeMove.calculateDelta();
                        moves.add(edgeMove);
                    }
                }
            }
        }
        else {
            for (int i = 0; i < solution.cycle2.size(); i++) {
                if (Math.abs(i-solution.cycle2.indexOf(new2)) > 2) {
                    EdgeMove edgeMove = new EdgeMove(solution, false, new2, solution.cycle2.get(i));
                    if (!edgeMove.isDeletable()) {
                        edgeMove.calculateDelta();
                        moves.add(edgeMove);
                    }
                }
            }
        }

        return moves;
    }

    private static Move findBestMove(List<EdgeMove> movesE, List<IntercycleMove> movesI) {
        Move bestMove = movesE.get(0);
        for(EdgeMove move: movesE) {
            if(move.isApplicable() && move.getDelta() < bestMove.getDelta())
                bestMove = move;
        }
        for(IntercycleMove move: movesI) {
            if(move.isApplicable() && move.getDelta() < bestMove.getDelta())
                bestMove = move;
        }
        return bestMove;
    }
}
