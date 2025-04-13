package edu.put.imo.lab3;

public interface Move {
    void apply();
    void calculateDelta();

    boolean isDeletable();

    boolean isApplicable();

    long getDelta();

    void printMove();

}
