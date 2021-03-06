/*
 Copyright 2014 Zoi Capital, LLC
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package main.java.Controls;

import main.java.Helpers.DecimalAxisFormatter;
import main.java.Models.BarData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.util.Duration;

/**
 * A candlestick chart is a style of bar-chart used primarily to describe price
 * movements of a security, derivative, or currency over time.
 * <p>
 * The Data Y value is used for the opening price and then the close, high and
 * low values are stored in the Data's extra value property using a
 * CandleStickExtraValues object.
 *
 * @author RobTerpilowski
 * Modified by Daniel Gelber
 * Modified 2018-09-20
 */
public class CandleStickChart extends XYChart<String, Number> {

    // PROPERTIES

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    protected static final Logger logger = Logger.getLogger(CandleStickChart.class.getName());
    protected int maxBarsToDisplay;
    protected ObservableList<XYChart.Series<String, Number>> dataSeries;
    protected BarData lastBar;
    protected NumberAxis yAxis;
    protected CategoryAxis xAxis;


    // CONSTRUCTORS

    /**
     * Instantiates a new Candle stick chart.
     *
     * @param title the chart title
     * @param bars  the bars data to display in the chart
     */
    public CandleStickChart(String title, List<BarData> bars) {

        this(title, bars, Integer.MAX_VALUE);

    }

    /**
     * Instantiates a new Candle stick chart.
     *
     * @param title            the chart title
     * @param bars             the bars to display in the chart
     * @param maxBarsToDisplay the maximum number of bars to display in the chart
     */
    public CandleStickChart(String title, List<BarData> bars, int maxBarsToDisplay) {

        this(title, new CategoryAxis(), new NumberAxis(), bars, maxBarsToDisplay);

    }

    /**
     * Construct a new CandleStickChart with the given axis.
     *
     * @param title            the chart title
     * @param xAxis            the x axis to use
     * @param yAxis            the y axis to use
     * @param bars             the bars to display on the chart
     * @param maxBarsToDisplay the maximum number of bars to display on the chart
     */
    public CandleStickChart(String title, CategoryAxis xAxis, NumberAxis yAxis, List<BarData> bars, int maxBarsToDisplay) {

        super(xAxis, yAxis);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.maxBarsToDisplay = maxBarsToDisplay;

        yAxis.autoRangingProperty().set(true);
        yAxis.forceZeroInRangeProperty().setValue(Boolean.FALSE);
        setTitle(title);
        setAnimated(true);
        getStylesheets().add(getClass().getResource("/main/resources/styles/CandleStickChartStyles.css").toExternalForm());
        xAxis.setAnimated(true);
        yAxis.setAnimated(true);
        verticalGridLinesVisibleProperty().set(false);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        List<BarData> sublist = getSubList(bars, maxBarsToDisplay);
        for (BarData bar : sublist) {
            String label = "";
            label = sdf.format(bar.getDateTime().getTime());
            series.getData().add(new XYChart.Data<>(label, bar.getOpen(), bar));
        }

        dataSeries = FXCollections.observableArrayList(series);

        setData(dataSeries);
        lastBar = sublist.get(sublist.size() - 1);

    }


    // LOGIC

    /**
     * Defines a formatter to use when formatting the y-axis values.
     *
     * @param formatter The formatter to use when formatting the y-axis values
     */
    public void setYAxisFormatter(DecimalAxisFormatter formatter) {

        yAxis.setTickLabelFormatter(formatter);

    }

    /**
     * Appends a new bar on to the end of the chart.
     *
     * @param bar The bar to append to the chart
     */
    public void addBar(BarData bar) {

        if (dataSeries.get(0).getData().size() >= maxBarsToDisplay) {
            dataSeries.get(0).getData().remove(0);
        }

        int dataLength = dataSeries.get(0).getData().size();
        dataSeries.get(0).getData().get(dataLength - 1).setYValue(bar.getOpen());
        dataSeries.get(0).getData().get(dataLength - 1).setExtraValue(bar);
        String label = sdf.format(bar.getDateTime().getTime());

        lastBar = new BarData(bar.getDateTime(), bar.getClose(), bar.getClose(), bar.getClose(), bar.getClose(), 0);
        Data<String, Number> data = new XYChart.Data<>(label, lastBar.getOpen(), lastBar);
        dataSeries.get(0).getData().add(data);

    }

    /**
     * Update the "Last" price of the most recent bar.
     *
     * @param price The Last price of the most recent bar
     */
    public void updateLast(double price) {

        if (lastBar != null) {
            lastBar.update(price);

            int datalength = dataSeries.get(0).getData().size();
            dataSeries.get(0).getData().get(datalength - 1).setYValue(lastBar.getOpen());

            dataSeries.get(0).getData().get(datalength - 1).setExtraValue(lastBar);
        }

    }


    /**
     * Gets sub list.
     *
     * @param bars    the bars
     * @param maxBars the max bars
     * @return the sub list
     */
    protected List<BarData> getSubList(List<BarData> bars, int maxBars) {

        List<BarData> sublist;
        if (bars.size() > maxBars) {
            return bars.subList(bars.size() - 1 - maxBars, bars.size() - 1);
        } else {
            return bars;
        }

    }

    /**
     * Called to update and layout the content for the plot.
     */
    @Override
    protected void layoutPlotChildren() {

        // We have nothing to layout if no data is present
        if (getData() == null) {
            return;
        }

        // Update candle positions
        for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {

            Series<String, Number> series = getData().get(seriesIndex);
            Iterator<Data<String, Number>> iter = getDisplayedDataIterator(series);
            Path seriesPath;

            if (series.getNode() instanceof Path) {
                seriesPath = (Path) series.getNode();
                seriesPath.getElements().clear();
            }

            while (iter.hasNext()) {

                Data<String, Number> item = iter.next();
                double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(item));
                double y = getYAxis().getDisplayPosition(getCurrentDisplayedYValue(item));
                Node itemNode = item.getNode();
                BarData bar = (BarData) item.getExtraValue();

                if (itemNode instanceof Candle && item.getYValue() != null) {

                    Candle candle = (Candle) itemNode;

                    double close = getYAxis().getDisplayPosition(bar.getClose());
                    double high = getYAxis().getDisplayPosition(bar.getHigh());
                    double low = getYAxis().getDisplayPosition(bar.getLow());
                    double candleWidth = 10;

                    // Update candle
                    candle.update(close - y, high - y, low - y, candleWidth);

                    // Update tooltip content
                    candle.updateTooltip(bar.getOpen(), bar.getClose(), bar.getHigh(), bar.getLow());

                    // Position the candle
                    candle.setLayoutX(x);
                    candle.setLayoutY(y);

                }

            }

        }

    }

    /**
     * Unused.
     */
    @Override
    protected void dataItemChanged(Data<String, Number> item) {
    }

    /**
     * Adds a candle when a new data point is added.
     * @param series    the series
     * @param itemIndex the item index
     * @param item      the item
     */
    @Override
    protected void dataItemAdded(Series<String, Number> series, int itemIndex, Data<String, Number> item) {

        Node candle = createCandle(getData().indexOf(series), item, itemIndex);

        if (shouldAnimate()) {
            candle.setOpacity(0);
            getPlotChildren().add(candle);
            // Fade in new candle
            FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
            ft.setToValue(1);
            ft.play();
        } else {
            getPlotChildren().add(candle);
        }

        // Always draw average line on top
        if (series.getNode() != null) {
            series.getNode().toFront();
        }

    }

    /**
     * Removes a candle when the data point is removed.
     * @param item   the item
     * @param series the series
     */
    @Override
    protected void dataItemRemoved(Data<String, Number> item, Series<String, Number> series) {

        final Node candle = item.getNode();

        if (shouldAnimate()) {
            // Fade out old candle
            FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
            ft.setToValue(0);
            ft.setOnFinished((ActionEvent actionEvent) -> {
                getPlotChildren().remove(candle);
            });
            ft.play();
        } else {
            getPlotChildren().remove(candle);
        }

    }

    /**
     * Adds new candles when a series is added.
     * @param series      the series
     * @param seriesIndex the series index
     */
    @Override
    protected void seriesAdded(Series<String, Number> series, int seriesIndex) {

        // Handle any data already in series
        for (int j = 0; j < series.getData().size(); j++) {
            Data item = series.getData().get(j);
            Node candle = createCandle(seriesIndex, item, j);
            if (shouldAnimate()) {
                candle.setOpacity(0);
                getPlotChildren().add(candle);
                // Fade in new candle
                FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
                ft.setToValue(1);
                ft.play();
            } else {
                getPlotChildren().add(candle);
            }
        }

        // Create series path
        Path seriesPath = new Path();
        seriesPath.getStyleClass().setAll("candlestick-average-line", "series" + seriesIndex);
        series.setNode(seriesPath);
        getPlotChildren().add(seriesPath);

    }

    /**
     * Removes candles when a series is removed.
     * @param series the series
     */
    @Override
    protected void seriesRemoved(Series<String, Number> series) {

        // Remove all candle nodes
        for (XYChart.Data<String, Number> d : series.getData()) {

            final Node candle = d.getNode();

            if (shouldAnimate()) {
                // Fade out old candle
                FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
                ft.setToValue(0);
                ft.setOnFinished((ActionEvent actionEvent) -> {
                    getPlotChildren().remove(candle);
                });
                ft.play();
            } else {
                getPlotChildren().remove(candle);
            }

        }

    }

    /**
     * Create a new Candle node to represent a single data item.
     *
     * @param seriesIndex the index of the series the data item is in
     * @param item        the data item to create node for
     * @param itemIndex   the index of the data item in the series
     * @return New candle node to represent the give data item
     */
    private Node createCandle(int seriesIndex, final Data item, int itemIndex) {

        Node candle = item.getNode();

        // Check if candle has already been created
        if (candle instanceof Candle) {
            ((Candle) candle).setSeriesAndDataStyleClasses("series" + seriesIndex, "data" + itemIndex);
        } else {
            candle = new Candle("series" + seriesIndex, "data" + itemIndex);
            item.setNode(candle);
        }

        return candle;

    }

    /**
     * This is called when the range has been invalidated and we need to update
     * it. If the axis are auto ranging then we compile a list of all data that
     * the given axis has to plot and call invalidateRange() on the axis passing
     * it that data.
     */
    @Override
    protected void updateAxisRange() {

        // For candle stick chart we need to override this method as we need to let the axis know that they need to be able
        // to cover the whole area occupied by the high to low range not just its center data value
        final Axis<String> xa = getXAxis();
        final Axis<Number> ya = getYAxis();
        List<String> xData = null;
        List<Number> yData = null;

        if (xa.isAutoRanging()) {
            xData = new ArrayList<>();
        }
        if (ya.isAutoRanging()) {
            yData = new ArrayList<>();
        }

        if (xData != null || yData != null) {
            for (Series<String, Number> series : getData()) {
                for (Data<String, Number> data : series.getData()) {
                    if (xData != null) {
                        xData.add(data.getXValue());
                    }
                    if (yData != null) {
                        BarData extras = (BarData) data.getExtraValue();
                        if (extras != null) {
                            yData.add(extras.getHigh());
                            yData.add(extras.getLow());
                        } else {
                            yData.add(data.getYValue());
                        }
                    }
                }
            }
            if (xData != null) {
                xa.invalidateRange(xData);
            }
            if (yData != null) {
                ya.invalidateRange(yData);
            }
        }

    }

    /**
     * Candle node used for drawing a candle.
     */
    private class Candle extends Group {

        private final Line highLowLine = new Line();
        private final Region bar = new Region();
        private String seriesStyleClass;
        private String dataStyleClass;
        private boolean openAboveClose = true;
        private final Tooltip tooltip = new Tooltip();

        /**
         * Initializes a new candle.
         *
         * @param seriesStyleClass the series style class
         * @param dataStyleClass   the data style class
         */
        private Candle(String seriesStyleClass, String dataStyleClass) {

            setAutoSizeChildren(false);
            getChildren().addAll(highLowLine, bar);
            this.seriesStyleClass = seriesStyleClass;
            this.dataStyleClass = dataStyleClass;
            updateStyleClasses();
            tooltip.setGraphic(new TooltipContent());
            Tooltip.install(bar, tooltip);

        }

        /**
         * Sets series and data style classes.
         *
         * @param seriesStyleClass the series style class
         * @param dataStyleClass   the data style class
         */
        public void setSeriesAndDataStyleClasses(String seriesStyleClass, String dataStyleClass) {

            this.seriesStyleClass = seriesStyleClass;
            this.dataStyleClass = dataStyleClass;
            updateStyleClasses();

        }

        /**
         * Updates the candle.
         *
         * @param closeOffset the close offset
         * @param highOffset  the high offset
         * @param lowOffset   the low offset
         * @param candleWidth the candle width
         */
        public void update(double closeOffset, double highOffset, double lowOffset, double candleWidth) {

            openAboveClose = closeOffset > 0;
            updateStyleClasses();
            highLowLine.setStartY(highOffset);
            highLowLine.setEndY(lowOffset);
            if (candleWidth == -1) {
                candleWidth = bar.prefWidth(-1);
            }
            if (openAboveClose) {
                bar.resizeRelocate(-candleWidth / 2, 0, candleWidth, closeOffset);
            } else {
                bar.resizeRelocate(-candleWidth / 2, closeOffset, candleWidth, closeOffset * -1);
            }

        }

        /**
         * Update tooltip.
         *
         * @param open  the open
         * @param close the close
         * @param high  the high
         * @param low   the low
         */
        public void updateTooltip(double open, double close, double high, double low) {

            TooltipContent tooltipContent = (TooltipContent) tooltip.getGraphic();
            tooltipContent.update(open, close, high, low);

        }

        /**
         * Update style class.
         */
        private void updateStyleClasses() {

            getStyleClass().setAll("candlestick-candle", seriesStyleClass, dataStyleClass);
            highLowLine.getStyleClass().setAll("candlestick-line", seriesStyleClass, dataStyleClass,
                    openAboveClose ? "open-above-close" : "close-above-open");
            bar.getStyleClass().setAll("candlestick-bar", seriesStyleClass, dataStyleClass,
                    openAboveClose ? "open-above-close" : "close-above-open");

        }

    }

    /**
     * Tooltip content that pops up when a candle is hovered over.
     */
    private class TooltipContent extends GridPane {

        private final Label openValue = new Label();
        private final Label closeValue = new Label();
        private final Label highValue = new Label();
        private final Label lowValue = new Label();

        /**
         * Initializes a new TooltipContent.
         */
        private TooltipContent() {

            Label open = new Label("OPEN:");
            Label close = new Label("CLOSE:");
            Label high = new Label("HIGH:");
            Label low = new Label("LOW:");

            open.getStyleClass().add("candlestick-tooltip-label");
            close.getStyleClass().add("candlestick-tooltip-label");
            high.getStyleClass().add("candlestick-tooltip-label");
            low.getStyleClass().add("candlestick-tooltip-label");

            setConstraints(open, 0, 0);
            setConstraints(openValue, 1, 0);
            setConstraints(close, 0, 1);
            setConstraints(closeValue, 1, 1);
            setConstraints(high, 0, 2);
            setConstraints(highValue, 1, 2);
            setConstraints(low, 0, 3);
            setConstraints(lowValue, 1, 3);

            getChildren().addAll(open, openValue, close, closeValue, high, highValue, low, lowValue);

        }

        /**
         * Updates the Tooltip Content.
         *
         * @param open  the open
         * @param close the close
         * @param high  the high
         * @param low   the low
         */
        public void update(double open, double close, double high, double low) {

            openValue.setText(Double.toString(open));
            closeValue.setText(Double.toString(close));
            highValue.setText(Double.toString(high));
            lowValue.setText(Double.toString(low));

        }

    }

    /**
     * The constant chart.
     */
    protected static CandleStickChart chart;

}