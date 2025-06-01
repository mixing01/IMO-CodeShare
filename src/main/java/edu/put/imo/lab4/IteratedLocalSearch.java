package edu.put.imo.lab4;

import edu.put.imo.global.TSPSolution;
import edu.put.imo.lab3.EdgeMove;
import edu.put.imo.lab3.IntercycleMove;
import edu.put.imo.lab3.TSPTurboLocalSearch;

import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Random;

public class IteratedLocalSearch {
    static Random random = new Random();
    public static Pair<TSPSolution, Integer> iteratedLocalSearch(String path, double maxTime) throws FileNotFoundException {
        int iterNum = 0;
        TSPSolution solution = new TSPSolution(path);
        TSPTurboLocalSearch.turboSteep(solution);
        double elapsedTime = 0;
        while (elapsedTime < maxTime) {
            iterNum++;
            long start = System.nanoTime();
            TSPSolution tempSolution = solution.cloneSolution();
            perturbate(tempSolution, 3);
            TSPTurboLocalSearch.turboSteep(tempSolution);
            if(solution.cost > tempSolution.cost) {
                solution = tempSolution.cloneSolution();
            }
            long end = System.nanoTime();
            elapsedTime += (double) (end - start)/1_000_000_000;
        }

        return new Pair<>(solution, iterNum);
    }
    private static void perturbate(TSPSolution solution, int numberOfMoves) {
        int[] v;
        for(int i = 0; i < numberOfMoves; i++) {
            v = getRandomVertices(solution, 0);
            IntercycleMove im = new IntercycleMove(solution, v[0], v[1]);
            im.calculateDelta();
            solution.cost += im.getDelta();
            im.apply();

            v = getRandomVertices(solution, 1);
            EdgeMove em1 = new EdgeMove(solution, true, v[0], v[1]);
            em1.calculateDelta();
            solution.cost += em1.getDelta();
            em1.apply();

            v = getRandomVertices(solution, 2);
            EdgeMove em2 = new EdgeMove(solution, false, v[0], v[1]);
            em2.calculateDelta();
            solution.cost += em2.getDelta();
            em2.apply();
        }
    }
    private static int[] getRandomVertices(TSPSolution solution, int whichCycle) { // 0 - get from both, 1 - cycle 1, 2 - cycle 2
        if (whichCycle == 0) {
            int index1 = random.nextInt(solution.cycle1.size());
            int index2 = random.nextInt(solution.cycle2.size());
            int v1 = solution.cycle1.get(index1);
            int v2 = solution.cycle2.get(index2);
            return new int[]{v1, v2};
        } else if (whichCycle == 1) {
            ArrayList<Integer> cycle = new ArrayList<>(solution.cycle1.size());
            cycle.addAll(solution.cycle1);
            int index1 = random.nextInt(cycle.size());
            int v1 = cycle.get(index1);
            cycle.remove(index1);
            int index2 = random.nextInt(cycle.size());
            int v2 = cycle.get(index2);
            return new int[]{v1, v2};
        } else if (whichCycle == 2){
            ArrayList<Integer> cycle = new ArrayList<>(solution.cycle2.size());
            cycle.addAll(solution.cycle2);
            int index1 = random.nextInt(cycle.size());
            int v1 = cycle.get(index1);
            cycle.remove(index1);
            int index2 = random.nextInt(cycle.size());
            int v2 = cycle.get(index2);
            return new int[]{v1, v2};
        } else {
            throw new InvalidParameterException("Wrong cycle number");
        }
    }
}
