package edu.put.imo.lab3;

import edu.put.imo.global.TSPSolution;

public abstract class Move {
    TSPSolution solution;
    long delta;
    int v1;
    int v2;
    int prev1;
    int prev2;
    int succ1;
    int succ2;
    abstract void apply();
    abstract void calculateDelta();

    abstract boolean isDeletable();

    abstract boolean isApplicable();

    abstract long getDelta();

    abstract void printMove();

}
