package main.java;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import main.java.Controls.*;
import main.java.Models.*;
import main.java.Models.TradeBenchModel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @FXML TableColumn<Trade, String> instrumentCol;
    @FXML TableColumn<Trade, String> marketPositionCol;
    @FXML TableColumn<Trade, String> entryDateCol;
    @FXML TableColumn<Trade, String> entryTimeCol;
    @FXML TableColumn<Trade, Double> entryPriceCol;
    @FXML TableColumn<Trade, String> exitDateCol;
    @FXML TableColumn<Trade, String> exitTimeCol;
    @FXML TableColumn<Trade, Double> exitPriceCol;
    @FXML MenuItem loadTrades;
    @FXML MenuItem loadChart;
    @FXML MenuItem deleteTable;

    private CandleStickChart chart;
    private String output;
    private String startDate;
    private String endDate;
    private String newTableName;


    // METHODS

    /**
     * Called when the view is created. This loads in the starting table and chart.
     */
    @FXML
    public void initialize() {

        // These set which property from trade goes into which column in the table.
        numberCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getTradeNumber()).asObject());
        instrumentCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInstrument()));
        marketPositionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMarketPosition()));
        entryDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEntryDate()));
        entryTimeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEntryTime()));
        exitDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExitDate()));
        exitTimeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExitTime()));
        entryPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getEntryPrice()).asObject());
        exitPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getExitPrice()).asObject());

        TradeBenchModel.initializer();


        // Call a method in TradeBenchModel to get a list of Trades to go in the table.

        // Loop through that list, adding each Trade to tradesTable.

        // If there are no trades, load an empty chart as the child of

        // Otherwise, use loadChart(Trade trade) to default to the first item in the table.

    }

    /**
     * Reloads the chart with new data, particularly when a user
     * clicks on a new trade in the table.
     * //@param trade the trade to load the chart with. FIXME remove this line?
     */
    public void loadChart() {
        //FIXME change to get input by clicking a trade from the chart.

        //Get names for dropdown choices.
        ArrayList<String> tableChoices = TradeBenchModel.getTableNames();
        tableChoices.add("Select Table");

        //Select trade table to load from using dropdown dialog.
        String tradeTableName = optionDialog("Select Trade Table", "Select which trade data table you'd like" +
                        " to use.", "Choose Table", tableChoices, "Select Table");

        //Terminate process if table not selected.
        if(tradeTableName.equals("Select Table")) {
            warningAlert("Process Terminated", "Table not selected, please try again.");
            return;
        }

        //Select trade number to load.
        String tradeNumber = textInputDialog("Trade Number", "Which trade number would you like to display?",
                "Trade #");

        //Select market data table to load from.
        String marketTableName = optionDialog("Select Market Table", "Select which market data table you'd " +
                "like to load from.", "Choose Table", tableChoices, "Select Table");

        //Terminate Process if table not selected.
        if(marketTableName.equals("Select Table")) {
            warningAlert("Process Terminated", "Table not selected. Please try again.");
            return;
        }

        Trade trade = TradeBenchModel.getTrade(tradeTableName, tradeNumber);

        // Clear the current chart
        chartHolder.getChildren().clear();

        // Get list of bars based on trade object
        //FIXME delete if no red.
        List<BarData> bars = TradeBenchModel.getBars(trade, marketTableName);

        // Put the bars into the chart
        CandleStickChart candleStickChart = new CandleStickChart(null, bars);
        candleStickChart.setLegendVisible(false);
        chartHolder.getChildren().add(candleStickChart);


    }

    @FXML public void importMarketData() {
        //FIXME change to file selector.
        String url = textInputDialog("Set Filepath","Enter the file's filepath", "Filepath: ");
        String tableName = tableSelect();

        if(tableName.equals("None")) {
            warningAlert("No table selected.", "Please try again.");
            return;
        }
        else{
            TradeBenchModel.processMarketData(url, tableName);
        }

        String content = "Data succesfully imported to '" + tableName + "'.";
        informationDialog("Success!", content);
    }

    @FXML public void importTradeData() {
        //FIXME Change to file selector.
        String url = textInputDialog("Set Filepath","Enter the file's filepath", "Filepath: ");
        String tableName = tableSelect();

        if(tableName.equals("None")) {
            warningAlert("No table selected.", "Please try again.");
            return;
        }
        else{
            TradeBenchModel.processTradeExports(url, tableName);
        }

        String content = "Data succesfully imported to '" + tableName + "'.";
        informationDialog("Success!", content);
    }

    public String tableSelect() {

        ArrayList<String> tableChoices = TradeBenchModel.getTableNames();
        tableChoices.add("Create New");
        tableChoices.add("Select Table");

        String tableSelection = optionDialog("Select Table", "Select which table you'd like to import to.",
                "Choose Table", tableChoices, "Select Table");

        if (tableSelection.equals("Create New")) {
            if(createNewTable()){
                return newTableName;
            }
            else {
                return "None";
            }
        }
        else if (tableSelection.equals("Select Table")) {
            return "None";
        }
        else {
            return tableSelection;
        }
    }

    @FXML
    public boolean createNewTable(){
        ArrayList<String> choices = new ArrayList<String>();
        choices.add("Market Data");
        choices.add("Trade Data");
        choices.add("Select Table Type");

        String choice = optionDialog("New Table Type","Select new table type: ", "Table type: ",
                choices,"Select Table Type");

        if(!choice.equals("Select Table Type")) {
            newTableName = textInputDialog("Create New Table", "Please enter name of new table.",
                    "Table Name: ");
            if(TradeBenchModel.checkExists(newTableName)){
                warningAlert("This table already exists", "Please try a different table name.");
                return false;
            }
            else if(choice.equals("Market Data")) {
                TradeBenchModel.createMarketTable(newTableName);
            }
            else if(choice.equals("Trade Data")) {
                TradeBenchModel.createTradeTable(newTableName);
            }
            else {
                warningAlert("Something went wrong.", "Please try again.");
                return false;
            }

        }
        else{
            return false;
        }

        String content = "'" + newTableName + "' was created succesfully.";
        informationDialog("Success!", content);

        return true;

    }


    @FXML
    public void loadTrades() {
        String tradeTableName = tableSelect();

        if(TradeBenchModel.checkExists(tradeTableName)) {
            startDate = textInputDialog("Start Date", "Please enter Start Date in format yyyy-MM-dd",
                    "Start Date: ");
            endDate = textInputDialog("End Date", "Please enter End Date in format yyyy-MM-dd",
                    "End Date: ");
            ArrayList<Trade> trades = TradeBenchModel.getTradeList(tradeTableName, startDate, endDate);

            //FIXME Add trades to TableView tradesTable.
            tradesTable.getItems().clear();

            for (Trade t : trades){
                tradesTable.getItems().add(t);
            }

        }
        else {
            warningAlert("Table Not Found", "Please try again.");
        }

    }

    @FXML
    public void deleteTable() {
        ArrayList<String> tableChoices = TradeBenchModel.getTableNames();
        tableChoices.add("Select Table");

        ArrayList<String> yesNo = new ArrayList<String>();
        yesNo.add("No");
        yesNo.add("Yes");


        String tableSelection = optionDialog("Select Table", "Select which table you'd like to delete.",
                "Choose Table", tableChoices, "Select Table");


        String confirm = "No";

        if(!tableSelection.equals("Select Table")) {
            confirm = optionDialog("WARNING", "This table will be PERMANENTLY DELETED.", "Are you sure you wish to proceed?",
                    yesNo, "No");
        }
        else{
            informationDialog("No table selected.", "Please try again.");
            return;
        }

        String content = null;

        if(confirm.equals("Yes")) {
            TradeBenchModel.dropTable(tableSelection);
            if(!TradeBenchModel.checkExists(tableSelection)) {
                content = "The '" + tableSelection + "' table was succesfully deleted.";
                informationDialog("Success.", content);
            }
            else {
                content = "Something went wrong and the '" + tableSelection + "' table was not deleted.";
                warningAlert("An error occured.", content);
            }
        }
        else{
            content = "The '" + tableSelection + "' was not deleted.";
            informationDialog("Operation aborted.", content);
        }
    }

    public String textInputDialog(String title, String header, String content) {
        this.output = "No Output";
        TextInputDialog dialog = new TextInputDialog("");

        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            this.output = name;
        });
        return this.output;
    }

    public void warningAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public String optionDialog(String title, String header, String content, ArrayList<String> options,
                               String defaultChoice) {

        String choice = defaultChoice;

        ChoiceDialog<String> dialog = new ChoiceDialog<>(defaultChoice, options);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            choice = result.get();
        }

        return choice;
    }

    public void informationDialog(String title, String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }
}

