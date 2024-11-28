package ncnl.balayanexpensewise.utils;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class BarChartUtil {

    public static void configureBarChart(BarChart<String, Number> barChart) {
        // Create series for SSC, CICS, and CET
        XYChart.Series<String, Number> seriesSSC = new XYChart.Series<>();
        seriesSSC.setName("SSC");
        seriesSSC.getData().add(new XYChart.Data<>("Service", 44));
        seriesSSC.getData().add(new XYChart.Data<>("Expense", 55));

        XYChart.Series<String, Number> seriesCICS = new XYChart.Series<>();
        seriesCICS.setName("CICS");
        seriesCICS.getData().add(new XYChart.Data<>("Jan", 23));
        seriesCICS.getData().add(new XYChart.Data<>("Feb", 34));
        seriesCICS.getData().add(new XYChart.Data<>("Mar", 12));

        XYChart.Series<String, Number> seriesCET = new XYChart.Series<>();
        seriesCET.setName("CET");
        seriesCET.getData().add(new XYChart.Data<>("Jan", 50));
        seriesCET.getData().add(new XYChart.Data<>("Feb", 60));
        seriesCET.getData().add(new XYChart.Data<>("Mar", 30));

        // Add series to the chart (ensure they are added in the specified order)
        barChart.getData().addAll(seriesSSC, seriesCICS, seriesCET);

        // Styling each series
        for (XYChart.Series<String, Number> series : barChart.getData()) {
            if (series.getNode() != null) {
                series.getNode().setStyle("-fx-bar-fill: steelblue;");
            }
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-bar-fill: orange;");
                }
            }
        }
    }
}
