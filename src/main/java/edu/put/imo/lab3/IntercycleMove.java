package edu.put.imo.lab3;

import edu.put.imo.global.TSPSolution;

public class IntercycleMove extends Move{
    public IntercycleMove(TSPSolution solution, int v1, int v2) {
        if (solution == null) throw new IllegalArgumentException("Solution cannot be null.");
        this.solution = solution;
        this.v1 = v1;
        this.v2 = v2;

        if (!solution.cycle1.contains(v1) || !solution.cycle2.contains(v2)) {
            throw new IllegalArgumentException("v1 must be in cycle1 and v2 in cycle2.");
        }

        this.succ1 = solution.getNextCycleElement(true, v1);
        this.succ2 = solution.getNextCycleElement(false, v2);
        this.prev1 = solution.getPreviousCycleElement(true, v1);
        this.prev2 = solution.getPreviousCycleElement(false, v2);
        this.delta = 0;
    }
    @Override
    public void apply() {
        int i = solution.cycle1.indexOf(v1);
        int j = solution.cycle2.indexOf(v2);
        if (i == -1 || j == -1) {
            throw new IllegalStateException("v1 or v2 not found in their cycles.");
        }
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
        if (move == null) return false;
        return (v1 == move.v1 && v2 == move.v2) || (v1 == move.v2 && v2 == move.v1);
    }
    @Override
    public boolean isDeletable() {
        return !solution.cycle1.contains(v1) || !solution.cycle2.contains(v2);
    }

    @Override
    public boolean isApplicable() {
        // Check if vertices are still in their cycles
        if (!solution.cycle1.contains(v1) || !solution.cycle2.contains(v2)) {
            return false;
        }
        if (v1 == v2) {
            return false;
        }
        // Check connectivity (account for cycle boundaries)
        boolean prev1Valid = (prev1 == solution.getPreviousCycleElement(true, v1));
        boolean prev2Valid = (prev2 == solution.getPreviousCycleElement(false, v2));
        boolean succ1Valid = (succ1 == solution.getNextCycleElement(true, v1));
        boolean succ2Valid = (succ2 == solution.getNextCycleElement(false, v2));

        return prev1Valid && prev2Valid && succ1Valid && succ2Valid;
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
