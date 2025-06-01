package edu.put.imo.lab6;

import edu.put.imo.global.NeighbourType;
import edu.put.imo.global.TSPSolution;
import edu.put.imo.lab4.Pair;
import edu.put.imo.lab5.Genetic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import static java.lang.Math.round;

public class Main {
    public static void main(String[] args) throws IOException {
        //generateLS();
        //generateBenchmark();
        ArrayList<TSPSolution> solA = ResultReader.readGreedyResults("src/main/resources/data/greedy_ls_results/greedy_ls_A.txt");
        ArrayList<TSPSolution> solB = ResultReader.readGreedyResults("src/main/resources/data/greedy_ls_results/greedy_ls_B.txt");
        ArrayList<TSPSolution> solASorted = new ArrayList<>(solA);
        ArrayList<TSPSolution> solBSorted = new ArrayList<>(solB);
        solASorted.sort((a, b) -> b.cost - a.cost);
        solBSorted.sort((a, b) -> b.cost - a.cost);
        TSPSolution benA = ResultReader.readBenchmarkResults("src/main/resources/data/benchmark_results/benchmark_A.txt");
        TSPSolution benB = ResultReader.readBenchmarkResults("src/main/resources/data/benchmark_results/benchmark_B.txt");

        double[] costsA1 = new double[solA.size()];
        double[] costsB1 = new double[solB.size()];
        double[] costsA2 = new double[solASorted.size()];
        double[] costsB2 = new double[solBSorted.size()];

        double[] simA1V = new double[solA.size()];
        double[] simB1V = new double[solB.size()];
        double[] simA2V = new double[solASorted.size()];
        double[] simB2V = new double[solBSorted.size()];

        double[] simA1E = new double[solA.size()];
        double[] simB1E = new double[solB.size()];
        double[] simA2E = new double[solASorted.size()];
        double[] simB2E = new double[solBSorted.size()];

        for(int i = 0; i < solA.size(); i++) {
            costsA1[i] = solA.get(i).cost;
            simA1V[i] = StatsCalculator.vertexSimilarity(solA.get(i), benA);
            simA1E[i] = StatsCalculator.edgeSimilarity(solA.get(i), benA);
        }
        for(int i = 0; i < solB.size(); i++) {
            costsB1[i] = solB.get(i).cost;
            simB1V[i] = StatsCalculator.vertexSimilarity(solB.get(i), benB);
            simB1E[i] = StatsCalculator.edgeSimilarity(solB.get(i), benB);
        }
        for(int i = 0; i < solASorted.size(); i++) {
            System.out.println(i);
            costsA2[i] = solASorted.get(i).cost;
            simA2V[i] = 0;
            simA2E[i] = 0;
            for(int j = 0; j < solASorted.size(); j++) {
                if(i != j) {
                    simA2V[i] += StatsCalculator.vertexSimilarity(solASorted.get(i), solASorted.get(j));
                    simA2E[i] += StatsCalculator.edgeSimilarity(solASorted.get(i), solASorted.get(j));
                }
            }
            simA2V[i] /= solASorted.size()-1;
            simA2E[i] /= solASorted.size()-1;
        }
        for(int i = 0; i < solBSorted.size(); i++) {
            costsB2[i] = solBSorted.get(i).cost;
            simB2V[i] = 0;
            simB2E[i] = 0;
            for(int j = 0; j < solBSorted.size(); j++) {
                if(i != j) {
                    simB2V[i] += StatsCalculator.vertexSimilarity(solBSorted.get(i), solBSorted.get(j));
                    simB2E[i] += StatsCalculator.edgeSimilarity(solBSorted.get(i), solBSorted.get(j));
                }
            }
            simB2V[i] /= solBSorted.size()-1;
            simB2E[i] /= solBSorted.size()-1;
        }

        StatsCalculator.plotVertexSimilarity(solA, benA, "(A) Vertex similarity to best (" + String.format("%.2f", StatsCalculator.correlationCoefficient(costsA1, simA1V, solA.size())) + ")");
        StatsCalculator.plotVertexSimilarity(solB, benB, "(B) Vertex similarity to best (" + String.format("%.2f", StatsCalculator.correlationCoefficient(costsB1, simB1V, solB.size())) + ")");
        StatsCalculator.plotEdgeSimilarity(solA, benA, "(A) Edge similarity to best (" + String.format("%.2f", StatsCalculator.correlationCoefficient(costsA1, simA1E, solA.size())) + ")");
        StatsCalculator.plotEdgeSimilarity(solB, benB, "(B) Edge similarity to best (" + String.format("%.2f", StatsCalculator.correlationCoefficient(costsB1, simB1E, solB.size())) + ")");

        StatsCalculator.plotSimilarity(solASorted, simA2V, "(A) Vertex similarity to average (" + String.format("%.2f", StatsCalculator.correlationCoefficient(costsA2, simA2V, solASorted.size())) + ")");
        StatsCalculator.plotSimilarity(solBSorted, simB2V, "(B) Vertex similarity to average (" + String.format("%.2f", StatsCalculator.correlationCoefficient(costsB2, simB2V, solBSorted.size())) + ")");
        StatsCalculator.plotSimilarity(solASorted, simA2E, "(A) Edge similarity to average (" + String.format("%.2f", StatsCalculator.correlationCoefficient(costsA2, simA2E, solASorted.size())) + ")");
        StatsCalculator.plotSimilarity(solBSorted, simB2E, "(B) Edge similarity to average (" + String.format("%.2f", StatsCalculator.correlationCoefficient(costsB2, simB2E, solBSorted.size())) + ")");





    }

    private static void generateLS() throws IOException {
        String fileA = "src/main/resources/data/kroA200.tsp";
        String fileB = "src/main/resources/data/kroB200.tsp";
        File resA = new File("src/main/resources/data/greedy_ls_results/greedy_ls_A.txt");
        File resB = new File("src/main/resources/data/greedy_ls_results/greedy_ls_B.txt");
        if(resA.createNewFile()) {
            System.out.println("Created file A.");
        }
        else {
            System.out.println("File A already exists.");
            if(resA.delete()) {
                System.out.println("Succesfully deleted file A.");
            }
            else {
                throw new IOException("Cannot delete file A.");
            }
            if(resA.createNewFile()){
                System.out.println("Created file A.");
            }
            else {
                throw new IOException("Cannot delete file A.");
            }
        }
        if(resB.createNewFile()) {
            System.out.println("Created file B.");
        }
        else {
            System.out.println("File B already exists.");
            if(resB.delete()) {
                System.out.println("Succesfully deleted file B.");
            }
            else {
                throw new IOException("Cannot delete file B.");
            }
            if(resB.createNewFile()){
                System.out.println("Created file B.");
            }
            else {
                throw new IOException("Cannot delete file B.");
            }
        }
        try (FileWriter fwA = new FileWriter(resA); FileWriter fwB = new FileWriter(resB)) {
            for(int i = 0; i < 1000; i++) {
                TSPSolution solA = new TSPSolution(fileA);
                solA = GreedyLS.greedySearch(solA, NeighbourType.EDGE);
                fwA.write(solA.cycle1.toString());
                fwA.write("\n");
                fwA.write(solA.cycle2.toString());
                fwA.write("\n");
                fwA.write(""+solA.cost);
                fwA.write("\n\n");


                TSPSolution solB = new TSPSolution(fileB);
                solB = GreedyLS.greedySearch(solB, NeighbourType.EDGE);
                fwB.write(solB.cycle1.toString());
                fwB.write("\n");
                fwB.write(solB.cycle2.toString());
                fwB.write("\n");
                fwB.write(""+solB.cost);
                fwB.write("\n\n");
            }
        }

    }

    private static void generateBenchmark() throws IOException {
        String fileA = "src/main/resources/data/kroA200.tsp";
        String fileB = "src/main/resources/data/kroB200.tsp";
        File resA = new File("src/main/resources/data/benchmark_results/benchmark_A.txt");
        File resB = new File("src/main/resources/data/benchmark_results/benchmark_B.txt");
        if(resA.createNewFile()) {
            System.out.println("Created file A.");
        }
        else {
            System.out.println("File A already exists.");
            if(resA.delete()) {
                System.out.println("Succesfully deleted file A.");
            }
            else {
                throw new IOException("Cannot delete file A.");
            }
            if(resA.createNewFile()){
                System.out.println("Created file A.");
            }
            else {
                throw new IOException("Cannot delete file A.");
            }
        }
        if(resB.createNewFile()) {
            System.out.println("Created file B.");
        }
        else {
            System.out.println("File B already exists.");
            if(resB.delete()) {
                System.out.println("Succesfully deleted file B.");
            }
            else {
                throw new IOException("Cannot delete file B.");
            }
            if(resB.createNewFile()){
                System.out.println("Created file B.");
            }
            else {
                throw new IOException("Cannot delete file B.");
            }
        }
        try (FileWriter fwA = new FileWriter(resA); FileWriter fwB = new FileWriter(resB)) {
            TSPSolution solA = new TSPSolution(fileA);
            Pair<TSPSolution, Integer> geneticResA = Genetic.geneticBaseLS(20, fileA, 255);
            solA = geneticResA.getFirst();
            fwA.write(solA.cycle1.toString());
            fwA.write("\n");
            fwA.write(solA.cycle2.toString());
            fwA.write("\n");
            fwA.write(""+solA.cost);
            fwA.write("\n\n");


            Pair<TSPSolution, Integer> geneticResB = Genetic.geneticBaseLS(20, fileB, 255);
            TSPSolution solB = geneticResB.getFirst();
            fwB.write(solB.cycle1.toString());
            fwB.write("\n");
            fwB.write(solB.cycle2.toString());
            fwB.write("\n");
            fwB.write(""+solB.cost);
            fwB.write("\n\n");
        }

    }
}
