package edu.put.imo.lab3;

import edu.put.imo.global.TSPSolution;

public class IntercycleMove implements Move{
    TSPSolution solution;
    long delta;
    int v1;
    int v2;
    int prev1;
    int prev2;
    int succ1;
    int succ2;

    public IntercycleMove(TSPSolution solution, int v1, int v2) {
        this.solution = solution;
        this.v1 = v1;
        this.v2 = v2;
        succ1 = solution.getNextCycleElement(true, v1);
        succ2 = solution.getNextCycleElement(false, v2);
        prev1 = solution.getPreviousCycleElement(true, v1);
        prev2 = solution.getPreviousCycleElement(false, v2);

        delta = 0;
    }
    @Override
    public void apply() {
        int i = solution.cycle1.indexOf(v1);
        int j = solution.cycle2.indexOf(v2);
        solution.cycle1.set(i, v2);
        solution.cycle2.set(j, v1);
    }

    @Override
    public void calculateDelta() {
        int i = v1;
        int j = v2;
        int nextI = solution.getNextCycleElement(true, i);
        int prevI = solution.getPreviousCycleElement(true, i);
        int nextJ = solution.getNextCycleElement(false, j);
        int prevJ = solution.getPreviousCycleElement(false, j);

        // Remove old edges
        delta -= solution.distanceMatrix[prevI][i];
        delta -= solution.distanceMatrix[i][nextI];
        delta -= solution.distanceMatrix[prevJ][j];
        delta -= solution.distanceMatrix[j][nextJ];

        // Add new edges
        delta += solution.distanceMatrix[prevI][j];
        delta += solution.distanceMatrix[j][nextI];
        delta += solution.distanceMatrix[prevJ][i];
        delta += solution.distanceMatrix[i][nextJ];
    }

    public boolean compare(IntercycleMove move) {
        return (v1 == move.v1 && v2 == move.v2);
    }
    @Override
    public boolean isDeletable() {
        return false;
    }

    @Override
    public boolean isApplicable() {
        return  (solution.cycle1.contains(v1) && solution.cycle2.contains(v2)) &&
                (prev1 == solution.getPreviousCycleElement(true, v1) &&
                prev2 == solution.getPreviousCycleElement(false, v2) &&
                succ1 == solution.getNextCycleElement(true, v1) &&
                succ2 == solution.getNextCycleElement(false, v2));
    }

    @Override
    public long getDelta() {
        return delta;
    }

    @Override
    public void printMove() {
        System.out.println("IntercycleMove: v1: "+v1+", v2: "+v2);
    }

    public int getV1() {
        return v1;
    }

    public int getV2() {
        return v2;
    }
}
