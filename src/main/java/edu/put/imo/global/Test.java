package edu.put.imo.global;

import edu.put.imo.lab3.TSPTurboLocalSearch;
import edu.put.imo.lab4.LargeNeighbourhoodSearch;
import edu.put.imo.lab4.Pair;
import edu.put.imo.lab5.Genetic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Test {
    public static void main(String[] args) throws IOException {
        String pathA = "src/main/resources/data/att48.tsp";
        TSPSolution p1 = new TSPSolution(pathA);
        TSPSolution p2 = new TSPSolution(pathA);
        TSPSolution[] ch = Genetic.recombinePermutation(p1,p2);
        System.out.println("Parents cycle1");
        System.out.println(p1.cycle1);
        System.out.println(p2.cycle1);
        System.out.println("Children cycle1");
        System.out.println(ch[0].cycle1);
        System.out.println(ch[1].cycle1);

    }
}
