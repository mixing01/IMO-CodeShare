package edu.put.imo.lab5;

import edu.put.imo.global.TSPSolution;
import edu.put.imo.lab3.TSPTurboLocalSearch;
import edu.put.imo.lab3.UpdatedSteep;
import edu.put.imo.lab4.LargeNeighbourhoodSearch;
import edu.put.imo.lab4.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Genetic {
    private static ArrayList<TSPSolution> initializePopulation(int popSize, String filepath) throws FileNotFoundException {
        ArrayList<TSPSolution> population = new ArrayList<>();
        for(int i = 0; i < popSize; i++) {
            TSPSolution solution = new TSPSolution(filepath);
            UpdatedSteep.steep(solution);
            population.add(solution);
        }
        population.sort((s1, s2) -> {
            if (s1.cost == s2.cost)
                return 0;
            return s1.cost < s2.cost ? 1 : -1;
        });
        return population;
    }
    public static Pair<TSPSolution, Integer> geneticBase(int popSize, String filepath, double maxTime) throws IOException {
        ArrayList<TSPSolution> population = initializePopulation(popSize, filepath);
        int iterNum = 0;
        double elapsedTime = 0;
        while(elapsedTime < maxTime) {
            iterNum++;
            long start = System.nanoTime();
            TSPSolution[] parents = getParents(population);
            TSPSolution child = recombineBase(parents[0], parents[1]);
            insertSolution(population, child);
            removeWorstFromPopulation(population,popSize);
            long end = System.nanoTime();
            elapsedTime += (double) (end - start)/1_000_000_000;
        }

        return new Pair<>(population.get(population.size()-1),iterNum);
    }
    public static Pair<TSPSolution, Integer> geneticBaseLS(int popSize, String filepath, double maxTime) throws IOException {
        ArrayList<TSPSolution> population = initializePopulation(popSize, filepath);
        int iterNum = 0;
        double elapsedTime = 0;
        while(elapsedTime < maxTime) {
            iterNum++;
            long start = System.nanoTime();
            TSPSolution[] parents = getParents(population);
            TSPSolution child = recombineBase(parents[0], parents[1]);
            UpdatedSteep.steep(child);
            insertSolution(population, child);
            removeWorstFromPopulation(population,popSize);
            long end = System.nanoTime();
            elapsedTime += (double) (end - start)/1_000_000_000;
        }

        return new Pair<>(population.get(population.size()-1),iterNum);
    }

    public static Pair<TSPSolution, Integer> geneticPermutation(int popSize, String filepath, double maxTime) throws IOException {
        ArrayList<TSPSolution> population = initializePopulation(popSize, filepath);
        int iterNum = 0;
        double elapsedTime = 0;
        while(elapsedTime < maxTime) {
            iterNum++;
            long start = System.nanoTime();
            ArrayList<TSPSolution> parents = getParentsTournament(population);
            addOffsprings(population,parents);
            removeWorstFromPopulation(population,popSize);
            long end = System.nanoTime();
            elapsedTime += (double) (end - start)/1_000_000_000;
        }

        return new Pair<>(population.get(population.size()-1),iterNum);
    }

    public static Pair<TSPSolution, Integer> geneticPermutationLS(int popSize, String filepath, double maxTime) throws IOException {
        ArrayList<TSPSolution> population = initializePopulation(popSize, filepath);
        int iterNum = 0;
        double elapsedTime = 0;
        while(elapsedTime < maxTime) {
            iterNum++;
            long start = System.nanoTime();
            ArrayList<TSPSolution> parents = getParentsTournament(population);
            addOffspringsLS(population,parents);
            removeWorstFromPopulation(population,popSize);
            long end = System.nanoTime();
            elapsedTime += (double) (end - start)/1_000_000_000;
        }

        return new Pair<>(population.get(population.size()-1),iterNum);
    }


    private static void removeWorstFromPopulation(List<TSPSolution> population, int popSize) {
        while(population.size() > popSize)
            population.remove(0);
    }

    private static void insertSolution(List<TSPSolution> population, TSPSolution sol) {
        for (int i = 0; i < population.size(); i++) {
            if (population.get(i).cost > sol.cost) continue;
            if (population.get(i).cycle1.equals(sol.cycle1) && population.get(i).cycle2.equals(sol.cycle2)) return;
            if (population.get(i).cycle2.equals(sol.cycle1) && population.get(i).cycle1.equals(sol.cycle2)) return;
            population.add(i, sol);
            return;
        }
        population.add(sol);
    }

    private static void printPopulation(List<TSPSolution> population) {
        for(TSPSolution sol : population) {
            System.out.println(sol.cost);
        }
    }

    private static TSPSolution[] getParents(ArrayList<TSPSolution> population) {
        List<TSPSolution> copy = new ArrayList<>(population);
        Collections.shuffle(copy);
        return new TSPSolution[] {copy.get(0), copy.get(1)};
    }

    private static ArrayList<TSPSolution> getParentsTournament(ArrayList<TSPSolution> population) {
        List<TSPSolution> copy = new ArrayList<>(population);
        ArrayList<TSPSolution> parents = new ArrayList<>();
        Collections.shuffle(copy);
        if(copy.size() % 2 == 1)
            copy.remove(0);
        for(int i = 0; i < copy.size(); i+=2) {
            TSPSolution parent = copy.get(i).cost < copy.get(i+1).cost ? copy.get(i) : copy.get(i+1);
            parents.add(parent);
        }
        return parents;
    }

    private static TSPSolution recombineBase(TSPSolution p1, TSPSolution p2) {
        ArrayList<Integer> p2cycle1 = (ArrayList<Integer>) p2.cycle1;
        ArrayList<Integer> p2cycle2 = (ArrayList<Integer>) p2.cycle2;

        TSPSolution solution = p1.cloneSolution();
        
        for(int vertex : solution.cycle1) {
            if(vertex == -1) continue;
            if(!p2cycle1.contains(vertex) || p1.getNextCycleElement(true, vertex) != p2.getNextCycleElement(true, vertex)) {
                solution.removed.add(vertex);
                int succ = solution.getNextCycleElement(true, vertex);
                int prev = solution.getPreviousCycleElement(true, vertex);
                if(succ != -1)
                    solution.cost -= solution.distanceMatrix[vertex][succ];
                if(prev != -1)
                    solution.cost -= solution.distanceMatrix[vertex][prev];
                solution.cycle1.set(solution.cycle1.indexOf(vertex), -1);
            }
        }
        for(int i = 0; i < solution.cycle1.size(); i++) {
            int currentVertex = solution.cycle1.get(i);
            if(currentVertex != -1 &&
                    solution.cycle1.get(Math.floorMod(i-1, solution.cycle1.size())) == -1 && 
                    solution.cycle1.get(Math.floorMod(i+1, solution.cycle1.size())) == -1) {
                solution.removed.add(currentVertex);
                int succ = solution.getNextCycleElement(true, currentVertex);
                int prev = solution.getPreviousCycleElement(true, currentVertex);
                if(succ != -1)
                    solution.cost -= solution.distanceMatrix[currentVertex][succ];
                if(prev != -1)
                    solution.cost -= solution.distanceMatrix[currentVertex][prev];
                solution.cycle1.set(i,-1);
            }
        }

        for(int vertex : solution.cycle2) {
            if(vertex == -1) continue;
            if(!p2cycle2.contains(vertex) || p1.getNextCycleElement(false, vertex) != p2.getNextCycleElement(false, vertex)) {
                solution.removed.add(vertex);
                int succ = solution.getNextCycleElement(false, vertex);
                int prev = solution.getPreviousCycleElement(false, vertex);
                if(succ != -1)
                    solution.cost -= solution.distanceMatrix[vertex][succ];
                if(prev != -1)
                    solution.cost -= solution.distanceMatrix[vertex][prev];
                solution.cycle2.set(solution.cycle2.indexOf(vertex), -1);
            }
        }
        for(int i = 0; i < solution.cycle2.size(); i++) {
            int currentVertex = solution.cycle2.get(i);
            if(currentVertex != -1 &&
                    solution.cycle2.get(Math.floorMod(i-1, solution.cycle2.size())) == -1 &&
                    solution.cycle2.get(Math.floorMod(i+1, solution.cycle2.size())) == -1) {
                solution.removed.add(currentVertex);
                int succ = solution.getNextCycleElement(false, currentVertex);
                int prev = solution.getPreviousCycleElement(false, currentVertex);
                if(succ != -1)
                    solution.cost -= solution.distanceMatrix[currentVertex][succ];
                if(prev != -1)
                    solution.cost -= solution.distanceMatrix[currentVertex][prev];
                solution.cycle2.set(i,-1);
            }
        }
        repair(solution);
        return solution;
    }
    private static void repair(TSPSolution solution) {
        int size1 = solution.cycle1.size();
        if(Collections.frequency(solution.cycle1,-1) == size1) {
            solution.cycle1.set(0,solution.removed.get(0));
            solution.removed.remove(0);
        }
        int checkedIndex1 = solution.cycle1.indexOf(-1);
        while(checkedIndex1 != -1) {
            int checkedVertex1 = solution.cycle1.get(checkedIndex1);
            int prev1 = solution.cycle1.get(Math.floorMod(checkedIndex1-1, size1));
            while(prev1 == -1) {
                checkedIndex1 = Math.floorMod(checkedIndex1-1, size1);
                checkedVertex1 = solution.cycle1.get(checkedIndex1);
                prev1 = solution.cycle1.get(Math.floorMod(checkedIndex1-1, size1));
            }
            while(checkedVertex1 == -1) {
                int closest1 = solution.findClosestElementFromList(prev1, solution.removed);
                int cost1 = solution.distanceMatrix[prev1][closest1];
                solution.cycle1.set(checkedIndex1, closest1);
                solution.cost += cost1;
                checkedVertex1 = closest1;
                prev1 = checkedVertex1;
                checkedIndex1 = (checkedIndex1 + 1) % size1;
                checkedVertex1 = solution.cycle1.get(checkedIndex1);
                solution.removed.remove((Integer) closest1);
            }
            solution.cost += solution.distanceMatrix[checkedVertex1][prev1];
            checkedIndex1 = solution.cycle1.indexOf(-1);

        }


        int size2 = solution.cycle2.size();
        if(Collections.frequency(solution.cycle2,-1) == size2) {
            solution.cycle2.set(0,solution.removed.get(0));
            solution.removed.remove(0);
        }
        int checkedIndex2 = solution.cycle2.indexOf(-1);
        while(checkedIndex2 != -1) {
            int checkedVertex2 = solution.cycle2.get(checkedIndex2);
            int prev2 = solution.cycle2.get(Math.floorMod(checkedIndex2-1, size2));
            while(prev2 == -1) {
                checkedIndex2 = Math.floorMod(checkedIndex2-1, size2);
                checkedVertex2 = solution.cycle2.get(checkedIndex2);
                prev2 = solution.cycle2.get(Math.floorMod(checkedIndex2-1, size2));
            }
            while(checkedVertex2 == -1) {
                int closest2 = solution.findClosestElementFromList(prev2, solution.removed);
                int cost2 = solution.distanceMatrix[prev2][closest2];
                solution.cycle2.set(checkedIndex2, closest2);
                solution.cost += cost2;
                checkedVertex2 = closest2;
                prev2 = checkedVertex2;
                checkedIndex2 = (checkedIndex2 + 1) % size2;
                checkedVertex2 = solution.cycle2.get(checkedIndex2);
                solution.removed.remove((Integer) closest2);
            }
            solution.cost += solution.distanceMatrix[checkedVertex2][prev2];
            checkedIndex2= solution.cycle2.indexOf(-1);
        }
    }

    private static void addOffsprings(ArrayList<TSPSolution> population, ArrayList<TSPSolution> parents) {
        List<TSPSolution> copy = new ArrayList<>(parents);
        Collections.shuffle(copy);
        List<TSPSolution> winners = getParentsTournament(population);
        Collections.shuffle(winners);
        for(int i = 0; i+1<winners.size(); i+=2) {
            TSPSolution[] children = recombinePermutation(winners.get(i), winners.get(i+1));
            insertSolution(population, children[0]);
            insertSolution(population, children[1]);

        }
    }

    private static void addOffspringsLS(ArrayList<TSPSolution> population, ArrayList<TSPSolution> parents) {
        List<TSPSolution> copy = new ArrayList<>(parents);
        Collections.shuffle(copy);
        List<TSPSolution> winners = getParentsTournament(population);
        Collections.shuffle(winners);
        for(int i = 0; i+1<winners.size(); i+=2) {
            TSPSolution[] children = recombinePermutation(winners.get(i), winners.get(i+1));
            TSPTurboLocalSearch.turboSteep(children[0]);
            TSPTurboLocalSearch.turboSteep(children[1]);
            insertSolution(population, children[0]);
            insertSolution(population, children[1]);
        }
    }

    public static TSPSolution[] recombinePermutation(TSPSolution p1, TSPSolution p2) {
        ArrayList<Integer> indices = new ArrayList<>();
        for(int i = 0; i < p1.cycle1.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        ArrayList<Integer> splitPoints = new ArrayList<>();
        splitPoints.add(indices.get(0));
        splitPoints.add(indices.get(1));
        Collections.sort(splitPoints);
        ArrayList<Integer> cycleCon1 = new ArrayList<>(p1.cycle1);
        cycleCon1.addAll(p1.cycle2);

        ArrayList<Integer> cycleCon2 = new ArrayList<>(p2.cycle1);
        cycleCon2.addAll(p2.cycle2);

        ArrayList<ArrayList<Integer>> splitCycle1 = new ArrayList<>();
        splitCycle1.add(new ArrayList<>(cycleCon1.subList(splitPoints.get(0),splitPoints.get(1))));

        ArrayList<ArrayList<Integer>> splitCycle2 = new ArrayList<>();
        splitCycle2.add(new ArrayList<>(cycleCon2.subList(splitPoints.get(0),splitPoints.get(1))));

        ArrayList<Integer> ch1Con = new ArrayList<>();
        ArrayList<Integer> ch2Con = new ArrayList<>();
        int i1 = 0;
        int added1 = 0;
        while(added1 < splitPoints.get(0)) {
            if(!splitCycle1.get(0).contains(cycleCon2.get(i1))) {
                ch1Con.add(cycleCon2.get(i1));
                added1++;
            }
            i1++;
        }
        ch1Con.addAll(splitCycle1.get(0));
        added1 = 0;
        while(added1 < cycleCon1.size() - splitPoints.get(1)) {
            if(!splitCycle1.get(0).contains(cycleCon2.get(i1))) {
                ch1Con.add(cycleCon2.get(i1));
                added1++;
            }
            i1++;
        }

        int i2 = 0;
        int added2 = 0;
        while(added2 < splitPoints.get(0)) {
            if(!splitCycle2.get(0).contains(cycleCon1.get(i2))) {
                ch2Con.add(cycleCon1.get(i2));
                added2++;
            }
            i2++;
        }
        ch2Con.addAll(splitCycle2.get(0));
        added2 = 0;
        while(added2 < cycleCon2.size() - splitPoints.get(1)) {
            if(!splitCycle2.get(0).contains(cycleCon1.get(i2))) {
                ch2Con.add(cycleCon1.get(i2));
                added2++;
            }
            i2++;
        }

        TSPSolution ch1 = p1.cloneSolution();
        ch1.cycle1 = new ArrayList<>(ch1Con.subList(0,p1.cycle1.size()));
        ch1.cycle2 = new ArrayList<>(ch1Con.subList(p1.cycle1.size(), ch1Con.size()));
        ch1.updateCost();
        TSPSolution ch2 = p2.cloneSolution();
        ch2.cycle1 = new ArrayList<>(ch2Con.subList(0,p2.cycle1.size()));
        ch2.cycle2 = new ArrayList<>(ch2Con.subList(p2.cycle1.size(), ch2Con.size()));
        ch2.updateCost();

        return new TSPSolution[] {ch1, ch2};
    }

}
