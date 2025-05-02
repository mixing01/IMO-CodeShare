package edu.put.imo.lab3;

import edu.put.imo.global.TSPSolution;
import edu.put.imo.global.Util;

import java.util.ArrayList;
import java.util.List;

public class EdgeMove extends Move{
    boolean isCycle1;

    public EdgeMove(TSPSolution solution, boolean isCycle1, int v1, int v2) {
        if (solution == null) throw new IllegalArgumentException("Solution cannot be null.");
        this.solution = solution;
        this.isCycle1 = isCycle1;
        this.v1 = v1;
        this.v2 = v2;
        if (!solution.isInCycle(isCycle1, v1) || !solution.isInCycle(isCycle1, v2)) {
            throw new IllegalArgumentException("Vertices must be in the specified cycle.");
        }
        this.succ1 = solution.getNextCycleElement(isCycle1, v1);
        this.prev2 = solution.getPreviousCycleElement(isCycle1, v2);
        this.delta = 0;
    }

    @Override
    public boolean isDeletable() {
        if (solution == null) return true; // No solution → deletable
        List<Integer> cycle = isCycle1 ? solution.cycle1 : solution.cycle2;
        int idxV1 = cycle.indexOf(v1);
        int idxV2 = cycle.indexOf(v2);
        if (idxV1 == idxV2) return true;
        if (!cycle.contains(v1) || !cycle.contains(v2)) return true;
        if (succ1 != solution.getNextCycleElement(isCycle1, v1) && succ1 != solution.getPreviousCycleElement(isCycle1, v1)) return true;
        if (prev2 != solution.getNextCycleElement(isCycle1, v2) && prev2 != solution.getPreviousCycleElement(isCycle1, v2)) return true;
        return false;
    }
    @Override
    public boolean isApplicable() {
        // 1. Check if vertices are still in the cycle
        if (!solution.isInCycle(isCycle1, v1) || !solution.isInCycle(isCycle1, v2)) {
            return false;
        }
        return (solution.getNextCycleElement(isCycle1, v1) == succ1) &&
                    (solution.getPreviousCycleElement(isCycle1, v2) == prev2);
    }

    @Override
    public long getDelta() {
        if (delta > Integer.MAX_VALUE || delta < Integer.MIN_VALUE) {
            throw new ArithmeticException("Delta value out of bounds.");
        }
        return delta;
    }

    @Override
    public void printMove() {
        if(isCycle1)
            System.out.printf("(1) EdgeMove -> v1 (%d -> %d)[%d] pos1 (%d), v2 (%d <- %d)[%d] pos2 (%d) %n",v1,succ1,solution.getNextCycleElement(isCycle1,v1),solution.cycle1.indexOf(v1),v2,prev2,solution.getPreviousCycleElement(isCycle1,v2),solution.cycle1.indexOf(v2));
        else
            System.out.printf("(2) EdgeMove -> v1 (%d -> %d)[%d] pos1 (%d), v2 (%d <- %d)[%d] pos2 (%d) %n",v1,succ1,solution.getNextCycleElement(isCycle1,v1),solution.cycle2.indexOf(v1),v2,prev2,solution.getPreviousCycleElement(isCycle1,v2),solution.cycle2.indexOf(v2));


    }

    @Override
    public void apply() {
        List<Integer> cycle = isCycle1 ? solution.cycle1 : solution.cycle2;
        int idxV1 = cycle.indexOf(v1);
        int idxV2 = cycle.indexOf(v2);

        if (idxV1 == -1 || idxV2 == -1) {
            throw new IllegalStateException("Vertices not found in the cycle.");
        }

        Util.reverseSegment(cycle, idxV1, idxV2);
    }
    @Override
    public void calculateDelta() {
        List<Integer> cycle = isCycle1 ? solution.cycle1 : solution.cycle2;

        // Indeksy w poprzednim i następnym wierzchołku
        int nextI = solution.getNextCycleElement(isCycle1, v1);
        int prevJ = solution.getPreviousCycleElement(isCycle1, v2);
        if (v1 == v2 || nextI == v2 || prevJ == v1) {
            delta = 0;
            return;
        }
        // Przed zamianą - odejmujemy istniejące koszty
        delta -= solution.distanceMatrix[v1][nextI]; // i -> i+1
        delta -= solution.distanceMatrix[prevJ][v2]; // j-1 -> j

        // Po zamianie - dodajemy nowe koszty
        delta += solution.distanceMatrix[v1][prevJ]; // i-1 -> j
        delta += solution.distanceMatrix[nextI][v2]; // j -> i+1

    }
}
