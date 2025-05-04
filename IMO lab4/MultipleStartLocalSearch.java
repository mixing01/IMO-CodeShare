package edu.put.imo.lab4;

import edu.put.imo.global.TSPSolution;
import edu.put.imo.lab3.TSPTurboLocalSearch;

import java.io.FileNotFoundException;

public class MultipleStartLocalSearch {
    public static Pair<TSPSolution, Integer> multipleStartLocalSearch(String path, double maxTime) throws FileNotFoundException {
        TSPSolution bestSol = new TSPSolution(path);
        for (int i = 0; i < 200; i++) {
            TSPSolution tempSolution = new TSPSolution(path);
            TSPTurboLocalSearch.turboSteep(tempSolution);
            if(tempSolution.cost < bestSol.cost) {
                bestSol = tempSolution.cloneSolution();
            }
        }
        return new Pair<>(bestSol, 200);
    }
}
