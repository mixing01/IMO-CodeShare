package edu.put.imo.global;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.IntStream;

import static edu.put.imo.global.Util.isNumeric;

public class TSPSolution {
    public List<Integer> cycle1;
    public List<Integer> cycle2;

    public List<Integer> removed;
    public int cost;
    public Integer[][] distanceMatrix;

    public List<int[]> points;

    public NeighbourType neighbourType;

    public TSPSolution(List<Integer> cycle1, List<Integer> cycle2, int cost) {
        this.cycle1 = cycle1;
        this.cycle2 = cycle2;
        this.cost = cost;
    }
    public TSPSolution(List<Integer> cycle1, List<Integer> cycle2, int cost, Integer[][] distanceMatrix, List<int[]> points) {
        this.cycle1 = cycle1;
        this.cycle2 = cycle2;
        this.cost = cost;
        this.distanceMatrix = distanceMatrix;
        this.points = points;
        this.removed = new ArrayList<>();
    }
    public TSPSolution(String filepath) throws FileNotFoundException {
        readPointsFromFile(filepath);

        cycle1 = new ArrayList<>();
        cycle2 = new ArrayList<>();
        removed = new ArrayList<>();
        ArrayList<Integer> remaining = new ArrayList<>(IntStream.range(0, points.size()).boxed().toList());
        Collections.shuffle(remaining);
        for (int i = 0; i < remaining.size(); i++) {
            if(i % 2 == 0) {
                cycle1.add(remaining.get(i));
            }
            else {
                cycle2.add(remaining.get(i));
            }
        }
        calculateGoalFunction();
    }

    public TSPSolution(String filepath, DataType dataType, int iter) throws FileNotFoundException {
        readPointsFromFile(filepath);

        File file = null;
        if(dataType == DataType.A) {
            file = new File("src/main/resources/data/hr_data/kroA200HR"+iter+".tsp");
        } else if (dataType == DataType.B) {
            file = new File("src/main/resources/data/hr_data/kroB200HR"+iter+".tsp");
        }
        cycle1 = new ArrayList<>();
        cycle2 = new ArrayList<>();
        assert file != null;
        Scanner sc = new Scanner(file);
        while(sc.hasNext()) {
            String line = sc.nextLine();
            if(isNumeric(line.substring(0,1))) {
                String[] split = line.split(" ");
                cycle1.add(Integer.parseInt(split[1]));
                cycle2.add(Integer.parseInt(split[2]));
            }
        }
        sc.close();
        calculateGoalFunction();
    }
    public TSPSolution(List<Integer> c1, List<Integer> c2, int c, Integer[][] distanceMatrix, List<int[]> points, NeighbourType neighbourType) {
        this.cycle1 = new ArrayList<>(c1);
        this.cycle2 = new ArrayList<>(c2);
        this.cost = c;
        this.distanceMatrix = distanceMatrix;
        this.points = points;
        this.neighbourType = neighbourType;
    }

    public TSPSolution(List<Integer> c1, List<Integer> c2, List<Integer> removed, int c, Integer[][] distanceMatrix, List<int[]> points, NeighbourType neighbourType) {
        this.cycle1 = new ArrayList<>(c1);
        this.cycle2 = new ArrayList<>(c2);
        this.removed = new ArrayList<>(removed);
        this.cost = c;
        this.distanceMatrix = distanceMatrix;
        this.points = points;
        this.neighbourType = neighbourType;
    }

    public int getNextCycleElement(boolean isCycle1, int i) {
        ArrayList<Integer> cycle = (ArrayList<Integer>) (isCycle1 ? cycle1 : cycle2);
        int index = cycle.indexOf(i);
        if (index != cycle.size()-1) {
            return cycle.get(index+1);
        }
        else {
            return cycle.get(0);
        }
    }

    public int getPreviousCycleElement(boolean isCycle1, int i) {
        ArrayList<Integer> cycle = (ArrayList<Integer>) (isCycle1 ? cycle1 : cycle2);
        int index = cycle.indexOf(i);
        if (index != 0) {
            return cycle.get(index-1);
        }
        else {
            return cycle.get(cycle.size()-1);
        }
    }

    public boolean isInCycle(boolean isCycle1, int i) {
        ArrayList<Integer> cycle = (ArrayList<Integer>) (isCycle1 ? cycle1 : cycle2);
        return cycle.contains(i);
    }

    public TSPSolution cloneSolution() {
        return new TSPSolution(new ArrayList<>(cycle1), new ArrayList<>(cycle2), new ArrayList<>(removed), cost, distanceMatrix, points, neighbourType);
    }

    private void calculateGoalFunction(){
        for(int i = 0; i < cycle1.size()-1; i++) {
            cost += distanceMatrix[cycle1.get(i)][cycle1.get(i+1)];
        }
        cost += distanceMatrix[cycle1.get((cycle1.size()-1))][cycle1.get(0)];

        for(int i = 0; i < cycle2.size()-1; i++) {
            cost += distanceMatrix[cycle2.get(i)][cycle2.get(i+1)];
        }
        cost += distanceMatrix[cycle2.get((cycle2.size()-1))][cycle2.get(0)];
    }

    private void readPointsFromFile(String filepath) throws FileNotFoundException {
        File f = new File(filepath);
        Scanner sc = new Scanner(f);
        points = new ArrayList<>();
        while(sc.hasNext()) {
            String line = sc.nextLine();
            if(isNumeric(line.substring(0,1))) {
                String[] split = line.split(" ");
                int[] point = {Integer.parseInt(split[1]), Integer.parseInt(split[2])};
                points.add(point);
            }
        }
        sc.close();

        int size = points.size();
        distanceMatrix = new Integer[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = i; j < size; j++) {
                if(i==j) {
                    distanceMatrix[i][j] = 0;
                }
                else {
                    distanceMatrix[i][j] = Util.getDistance(points.get(i),points.get(j));
                    distanceMatrix[j][i] = Util.getDistance(points.get(j),points.get(i));
                }
            }
        }
    }

    public ArrayList<Integer> getNNearestIntercycleNeighbours(int n, int v) {
        ArrayList<Integer> topN = new ArrayList<>();
        List<Integer> otherCycle;

        if (cycle1.contains(v)) {
            otherCycle = cycle2;
        } else if (cycle2.contains(v)) {
            otherCycle = cycle1;
        } else {
            return topN; // v nie należy do żadnego cyklu
        }

        // Lista par: [wierzchołek z drugiego cyklu, odległość do v]
        List<int[]> distances = new ArrayList<>();
        for (int u : otherCycle) {
            if (u != v) {
                distances.add(new int[]{u, distanceMatrix[v][u]});
            }
        }

        // Posortuj wg odległości
        distances.sort(Comparator.comparingInt(pair -> pair[1]));

        // Dodaj do wyniku pierwsze n (lub mniej)
        for (int i = 0; i < Math.min(n, distances.size()); i++) {
            topN.add(distances.get(i)[0]);
        }

        return topN;
    }

    public ArrayList<Integer> getNNearestNeighbours(int n, int v) {
        ArrayList<Integer> topN = new ArrayList<>();
        List<Integer> otherCycle;

        if (cycle1.contains(v)) {
            otherCycle = cycle1;
        } else if (cycle2.contains(v)) {
            otherCycle = cycle2;
        } else {
            return topN; // v nie należy do żadnego cyklu
        }

        // Lista par: [wierzchołek z drugiego cyklu, odległość do v]
        List<int[]> distances = new ArrayList<>();
        for (int u : otherCycle) {
            if (u != v) {
                distances.add(new int[]{u, distanceMatrix[v][u]});
            }
        }

        // Posortuj wg odległości
        distances.sort(Comparator.comparingInt(pair -> pair[1]));

        // Dodaj do wyniku pierwsze n (lub mniej)
        for (int i = 0; i < Math.min(n, distances.size()); i++) {
            topN.add(distances.get(i)[0]);
        }

        return topN;
    }

    public int findClosestElementFromList(int fromElement, List<Integer> checkedElements) {
        if(checkedElements.isEmpty() || fromElement < 0)
            return -1;
        int checkedElem = checkedElements.get(0);
        int minDistance = distanceMatrix[fromElement][checkedElem];
        int closestElement = checkedElem;
        for (int i = 1; i < checkedElements.size(); i++) {
            checkedElem = checkedElements.get(i);
            // Skip comparison with itself
            if (checkedElem == fromElement) continue;

            // Since we know distances are non-negative, we can skip that check
            if (distanceMatrix[fromElement][checkedElem] < minDistance) {
                minDistance = distanceMatrix[fromElement][checkedElem];
                closestElement = checkedElem;
            }
        }

        return closestElement;
    }

    public void updateCost() {
        cost = 0;
        calculateGoalFunction();
    }

}
