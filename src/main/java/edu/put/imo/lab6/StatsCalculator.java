package edu.put.imo.lab6;

import edu.put.imo.global.TSPSolution;
import edu.put.imo.global.Util;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class StatsCalculator {
    public static double vertexSimilarity(TSPSolution solution, TSPSolution benchmark) {
        int vertexCount = solution.cycle1.size() + solution.cycle2.size();
        double similarity1 = 0;
        for(Integer vertex : solution.cycle1) {
            if(benchmark.cycle1.contains(vertex))
                similarity1++;
        }
        for(Integer vertex : solution.cycle2) {
            if(benchmark.cycle2.contains(vertex))
                similarity1++;
        }
        similarity1 = similarity1/vertexCount;
        double similarity2 = 0;
        for(Integer vertex : solution.cycle1) {
            if(benchmark.cycle2.contains(vertex))
                similarity2++;
        }
        for(Integer vertex : solution.cycle2) {
            if(benchmark.cycle1.contains(vertex))
                similarity2++;
        }
        similarity2 = similarity2/vertexCount;
        return Math.max(similarity1, similarity2);
    }
    public static double edgeSimilarity(TSPSolution solution, TSPSolution benchmark) {
        int edgeCount = solution.cycle1.size() + solution.cycle2.size();
        double similarity = 0;
        for(Integer vertex : solution.cycle1) {
            int succ = solution.getNextCycleElement(true, vertex);
            if(benchmark.cycle1.contains(vertex) && (benchmark.getNextCycleElement(true, vertex) == succ || benchmark.getPreviousCycleElement(true, vertex) == succ))
                similarity++;
            else if (benchmark.cycle2.contains(vertex) && (benchmark.getNextCycleElement(false, vertex) == succ || benchmark.getPreviousCycleElement(false, vertex) == succ)) {
                similarity++;
            }
        }
        for(Integer vertex : solution.cycle2) {
            int succ = solution.getNextCycleElement(false, vertex);
            if(benchmark.cycle1.contains(vertex) && (benchmark.getNextCycleElement(true, vertex) == succ || benchmark.getPreviousCycleElement(true, vertex) == succ))
                similarity++;
            else if (benchmark.cycle2.contains(vertex) && (benchmark.getNextCycleElement(false, vertex) == succ || benchmark.getPreviousCycleElement(false, vertex) == succ)) {
                similarity++;
            }
        }
        similarity = similarity/edgeCount;
        return similarity;
    }

    public static void plotVertexSimilarity(ArrayList<TSPSolution> solutionList, TSPSolution benchmark, String title) throws IOException {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries series = new XYSeries("Similarity fraction",false);
        for(TSPSolution sol : solutionList) {
            series.add(sol.cost, vertexSimilarity(sol, benchmark));
        }
        xySeriesCollection.addSeries(series);
        JFreeChart chart = ChartFactory.createScatterPlot(title,"Cost", "Similarity", xySeriesCollection);
        ChartUtils.saveChartAsJPEG(new File("src/main/resources/graphs/"+ Util.getTimeString()+".jpg"),chart,1200,800);
    }

    public static void plotEdgeSimilarity(ArrayList<TSPSolution> solutionList, TSPSolution benchmark, String title) throws IOException {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries series = new XYSeries("Similarity fraction",false);
        for(TSPSolution sol : solutionList) {
            series.add(sol.cost, edgeSimilarity(sol, benchmark));
        }
        xySeriesCollection.addSeries(series);
        JFreeChart chart = ChartFactory.createScatterPlot(title,"Cost", "Similarity", xySeriesCollection);
        ChartUtils.saveChartAsJPEG(new File("src/main/resources/graphs/"+ Util.getTimeString()+".jpg"),chart,1200,800);
    }

    public static void plotSimilarity(ArrayList<TSPSolution> solutionList, double[] similarity, String title) throws IOException {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries series = new XYSeries("Similarity fraction",false);
        for(int i = 0; i < solutionList.size(); i++) {
            series.add(solutionList.get(i).cost, similarity[i]);
        }
        xySeriesCollection.addSeries(series);
        JFreeChart chart = ChartFactory.createScatterPlot(title,"Cost", "Similarity", xySeriesCollection);
        ChartUtils.saveChartAsJPEG(new File("src/main/resources/graphs/"+ Util.getTimeString()+".jpg"),chart,1200,800);
    }

    static double correlationCoefficient(double[] x, double[] y, int n) {
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double squareSumX = 0;
        double squareSumY = 0;

        for (int i = 0; i < n; i++) {
            sumX = sumX + x[i];
            sumY = sumY + y[i];
            sumXY = sumXY + x[i] * y[i];
            squareSumX = squareSumX + x[i] * x[i];
            squareSumY = squareSumY + y[i] * y[i];
        }
        return (n * sumXY - sumX * sumY)/(Math.sqrt((n * squareSumX - sumX * sumX) * (n * squareSumY - sumY * sumY)));
    }
}
