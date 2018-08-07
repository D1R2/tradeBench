package main.java;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import main.java.Controls.*;
import main.java.Models.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This class interfaces the GUI with the application logic. Anything that can
 * happen to the GUI happens in this class, except for the original definition
 * of the GUI elements.
 *
 * @author Daniel Gelber
 * @version 1.0
 * Created  2018-08-03
 * Modified 2018-08-08
 */
public class Controller {

    // INSTANCE VARIABLES

    // A VBox that holds the chart is necessary because the CandleStickChart cannot be instantiated in the view.
    // The way that the author made the class requires the bars to be given on instantiation.
    @FXML VBox chartHolder;
    @FXML TableView tradesTable;
    @FXML TableColumn<Trade, Integer> numberCol;
    @FXML TableColumn<Trade, String> typeCol;
    @FXML TableColumn<Trade, String> entryTimeCol;
    @FXML TableColumn<Trade, Double> entryPriceCol;
    @FXML TableColumn<Trade, String> exitTimeCol;
    @FXML TableColumn<Trade, Double> exitPriceCol;
    @FXML MenuItem filterTrades;


    // METHODS

    /**
     * Called when the view is created. This loads in the starting table and chart.
     */
    @FXML
    public void initialize() {

        // These set which property from trade goes into which column in the table.
        // The formatter controls how the date is displayed.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        numberCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNumber()).asObject());
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        entryTimeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEntryTime().format(formatter)));
        exitTimeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExitTime().format(formatter)));
        entryPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getEntryPrice()).asObject());
        exitPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getExitPrice()).asObject());

        // Call a method in TradeBenchModel to get a list of Trades to go in the table.

        // Loop through that list, adding each Trade to tradesTable.

        // If there are no trades, load an empty chart as the child of

        // Otherwise, use loadChart(Trade trade) to default to the first item in the table.

    }

    /**
     * Reloads the chart with new data, particularly when a user
     * clicks on a new trade in the table.
     * @param trade the trade to load the chart with.
     */
    public void loadChart(Trade trade) {

        // Clear the current chart
        chartHolder.getChildren().clear();

        // Get list of bars based on trade object
        List<BarData> bars = TradeBenchModel.getBars(trade);

        // Put the bars into the chart
        CandleStickChart candleStickChart = new CandleStickChart(null, bars);
        candleStickChart.setLegendVisible(false);
        chartHolder.getChildren().add(candleStickChart);

    }

    @FXML
    public void setFilter() {
    //You can do a dialog box inside here to get input. Just google how to do it specifically with javaFX.
    }

}
