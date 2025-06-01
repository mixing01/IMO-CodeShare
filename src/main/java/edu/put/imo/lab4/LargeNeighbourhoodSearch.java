package edu.put.imo.lab4;

import edu.put.imo.global.TSPSolution;
import edu.put.imo.lab3.TSPTurboLocalSearch;

import java.io.FileNotFoundException;
import java.util.Random;

public class LargeNeighbourhoodSearch {
    static Random random = new Random();
    static int[] upOrDown = {-1, 1};

    public static Pair<TSPSolution, Integer> largeNeighbourhoodSearchNoLS(String path, double maxTime) throws FileNotFoundException {
        int iterNum = 0;
        TSPSolution solution = new TSPSolution(path);
        TSPTurboLocalSearch.turboSteep(solution);
        double elapsedTime = 0;
        while (elapsedTime < maxTime) {
            iterNum++;
            long start = System.nanoTime();
            TSPSolution tempSolution = solution.cloneSolution();
            destroy(tempSolution, 0.3, 3);
            repair(tempSolution);
            if(solution.cost > tempSolution.cost) {
                solution = tempSolution.cloneSolution();
            }
            long end = System.nanoTime();
            elapsedTime += (double) (end - start)/1_000_000_000;
        }
        return new Pair<>(solution, iterNum);
    }
    public static Pair<TSPSolution, Integer> largeNeighbourhoodSearchWithLS(String path, double maxTime) throws FileNotFoundException {
        int iterNum = 0;
        TSPSolution solution = new TSPSolution(path);
        TSPTurboLocalSearch.turboSteep(solution);
        double elapsedTime = 0;
        while (elapsedTime < maxTime) {
            iterNum++;
            long start = System.nanoTime();
            TSPSolution tempSolution = solution.cloneSolution();
            destroy(tempSolution, 0.3, 3);
            repair(tempSolution);
            TSPTurboLocalSearch.turboSteep(tempSolution);
            if(solution.cost > tempSolution.cost) {
                solution = tempSolution.cloneSolution();
            }
            long end = System.nanoTime();
            elapsedTime += (double) (end - start)/1_000_000_000;
        }
        return new Pair<>(solution, iterNum);
    }

    public static void destroy(TSPSolution solution, double destroyFrac, int spots) {
        double tempFrac = destroyFrac/2;
        int verticesDestroyed1 = ((int) Math.ceil(solution.cycle1.size()*tempFrac))/spots;
        int verticesDestroyed2 = ((int) Math.ceil(solution.cycle1.size()*tempFrac))/spots;
        for(int i = 0; i < spots; i++) {
            // Cycle 1
            int destroyed1 = 0;
            int destroyingIndex1 = random.nextInt(solution.cycle1.size());
            int destroyedVertex1 = solution.cycle1.get(destroyingIndex1);
            while (destroyed1 < verticesDestroyed1) {
                if(destroyedVertex1 != -1) {
                    int succ = solution.getNextCycleElement(true, destroyedVertex1);
                    int prev = solution.getPreviousCycleElement(true, destroyedVertex1);
                    solution.removed.add(destroyedVertex1);
                    if(succ != -1)
                        solution.cost -= solution.distanceMatrix[destroyedVertex1][succ];
                    if(prev != -1)
                        solution.cost -= solution.distanceMatrix[destroyedVertex1][prev];
                    solution.cycle1.set(destroyingIndex1, -1);
                    destroyed1++;
                }
                destroyingIndex1 = (destroyingIndex1 + 1)%solution.cycle1.size();
                destroyedVertex1 = solution.cycle1.get(destroyingIndex1);
            }

            // Cycle 2
            int destroyed2 = 0;
            int destroyingIndex2 = random.nextInt(solution.cycle2.size());
            int destroyedVertex2 = solution.cycle2.get(destroyingIndex2);
            while (destroyed2 < verticesDestroyed2) {
                if(destroyedVertex2 != -1) {
                    int succ = solution.getNextCycleElement(false, destroyedVertex2);
                    int prev = solution.getPreviousCycleElement(false, destroyedVertex2);
                    solution.removed.add(destroyedVertex2);
                    if(succ != -1)
                        solution.cost -= solution.distanceMatrix[destroyedVertex2][succ];
                    if(prev != -1)
                        solution.cost -= solution.distanceMatrix[destroyedVertex2][prev];
                    solution.cycle2.set(destroyingIndex2, -1);
                    destroyed2++;
                }
                destroyingIndex2 = (destroyingIndex2 + 1)%solution.cycle2.size();
                destroyedVertex2 = solution.cycle2.get(destroyingIndex2);
            }
        }
    }

    public static void repair(TSPSolution solution) {
        System.out.println("Repairing...");
        int size1 = solution.cycle1.size();
        int checkedIndex1 = solution.cycle1.indexOf(-1);
        while(checkedIndex1 != -1) {
            System.out.println("Loop1...");
            int checkedVertex1 = solution.cycle1.get(checkedIndex1);
            int prev1 = solution.cycle1.get(Math.floorMod(checkedIndex1-1, size1));
            while(prev1 == -1) {
                System.out.println("Loop2...");
                checkedIndex1 = Math.floorMod(checkedIndex1-1, size1);
                checkedVertex1 = solution.cycle1.get(checkedIndex1);
                prev1 = solution.cycle1.get(Math.floorMod(checkedIndex1-1, size1));
            }
            while(checkedVertex1 == -1) {
                System.out.println("Loop3...");
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
        int checkedIndex2 = solution.cycle2.indexOf(-1);
        while(checkedIndex2 != -1) {
            System.out.println("Loop4...");
            int checkedVertex2 = solution.cycle2.get(checkedIndex2);
            int prev2 = solution.cycle2.get(Math.floorMod(checkedIndex2-1, size2));
            while(prev2 == -1) {
                System.out.println("Loop5...");
                checkedIndex2 = Math.floorMod(checkedIndex2-1, size2);
                checkedVertex2 = solution.cycle2.get(checkedIndex2);
                prev2 = solution.cycle2.get(Math.floorMod(checkedIndex2-1, size2));
            }
            while(checkedVertex2 == -1) {
                System.out.println("Loop6...");
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
}
