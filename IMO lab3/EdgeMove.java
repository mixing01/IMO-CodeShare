package edu.put.imo.lab3;

import edu.put.imo.global.TSPSolution;
import edu.put.imo.global.Util;

import java.util.ArrayList;
import java.util.List;

public class EdgeMove implements Move{
    TSPSolution solution;
    boolean isCycle1;
    long delta;
    int v1;
    int v2;
    int succ1;
    int prev2;

    public EdgeMove(TSPSolution solution, boolean isCycle1, int v1, int v2) {
        this.solution = solution;
        this.isCycle1 = isCycle1;
        this.v1 = v1;
        this.v2 = v2;
        succ1 = solution.getNextCycleElement(isCycle1, v1);
        prev2 = solution.getPreviousCycleElement(isCycle1, v2);
        delta = 0;
    }

    @Override
    public boolean isDeletable() {
        return  (!solution.isInCycle(isCycle1, v1) || !solution.isInCycle(isCycle1, v2) || !solution.isInCycle(isCycle1, succ1) || !solution.isInCycle(isCycle1, prev2)) ||
                ((succ1 != solution.getNextCycleElement(isCycle1, v1) && v1 != solution.getNextCycleElement(isCycle1, succ1)) ||
                (prev2 != solution.getPreviousCycleElement(isCycle1, v2) && v2 != solution.getPreviousCycleElement(isCycle1, prev2)));
    }
    @Override
    public boolean isApplicable() {
        return (v2 != solution.getNextCycleElement(isCycle1, v1)) && (succ1 == solution.getNextCycleElement(isCycle1,v1)) && (prev2 == solution.getPreviousCycleElement(isCycle1, v2));
    }

    @Override
    public long getDelta() {
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
        if(isCycle1)
            Util.reverseSegment(solution.cycle1, solution.cycle1.indexOf(v1), solution.cycle1.indexOf(v2));
        else
            Util.reverseSegment(solution.cycle2, solution.cycle2.indexOf(v1), solution.cycle2.indexOf(v2));

    }

    @Override
    public void calculateDelta() {
        List<Integer> cycle = isCycle1 ? solution.cycle1 : solution.cycle2;

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
    }

    public boolean compare(EdgeMove move) {
        return (isCycle1 == move.isCycle1 &&
                v1 == move.v1 &&
                v2 == move.v2 &&
                succ1 == move.succ1 &&
                prev2 == move.prev2);
    }
}
