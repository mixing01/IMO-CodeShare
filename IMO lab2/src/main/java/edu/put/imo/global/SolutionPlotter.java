package edu.put.imo.global;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SolutionPlotter {
    public static void plotTSPSolution(TSPSolution tspSolution, String title) throws IOException {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        if(!tspSolution.cycle1.isEmpty()) {
            XYSeries series1 = new XYSeries("Cycle 1",false);
            XYSeries series2 = new XYSeries("Cycle 2", false);
            for(int i : tspSolution.cycle1) {
                series1.add(tspSolution.points.get(i)[0], tspSolution.points.get(i)[1]);
            }
            series1.add(tspSolution.points.get(tspSolution.cycle1.get(0))[0],
                    tspSolution.points.get(tspSolution.cycle1.get(0))[1]);

            for(int i : tspSolution.cycle2) {
                series2.add(tspSolution.points.get(i)[0], tspSolution.points.get(i)[1]);
            }
            series2.add(tspSolution.points.get(tspSolution.cycle2.get(0))[0],
                    tspSolution.points.get(tspSolution.cycle2.get(0))[1]);

            xySeriesCollection.addSeries(series1);
            xySeriesCollection.addSeries(series2);
            JFreeChart chart = ChartFactory.createScatterPlot(title+" ("+tspSolution.cost+")","X", "Y", xySeriesCollection);
            XYPlot plot = (XYPlot) chart.getPlot();
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setDefaultStroke(new BasicStroke(20.0f));
            renderer.setSeriesLinesVisible(0, true);
            plot.setRenderer(renderer);
            ChartUtils.saveChartAsJPEG(new File("src/main/resources/graphs/"+Util.getTimeString()+".jpg"),chart,1200,800);

        }
        else {
            XYSeries series = new XYSeries("Dataset");
            for(int[] point : tspSolution.points) {
                series.add(point[0], point[1]);
            }
            xySeriesCollection.addSeries(series);
            JFreeChart chart = ChartFactory.createScatterPlot(title,"X", "Y", xySeriesCollection);
            ChartUtils.saveChartAsJPEG(new File("src/main/resources/graphs/"+Util.getTimeString()+".jpg"),chart,1200,800);
        }
    }
}
