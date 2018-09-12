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
 * @author Daniel Gelber and David Randolph
 * @version 1.0
 * Created  2018-08-03
 * Modified 2018-09-11
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

    }


    //FILE MENU METHODS

    /**
     * Allows user to create a new table in the mySQLite database.
     * @return boolean: true if successful, false if unsuccessful.
     */
    @FXML
    public boolean createNewTable(){
        //Array list of table type choices.
        ArrayList<String> choices = new ArrayList<String>();
        choices.add("Market Data");
        choices.add("Trade Data");
        choices.add("Select Table Type");

        //Get user selection via dropdown dialog box.
        String choice = optionDialog("New Table Type","Select new table type: ", "Table type: ",
                choices,"Select Table Type");

        if(!choice.equals("Select Table Type")) {
            //If choice is not "Select Table Type", retrieve input for new table name.
            newTableName = textInputDialog("Create New Table", "Please enter name of new table.",
                    "Table Name: ");
            //Verify that name is not already taken.
            if(TradeBenchModel.checkExists(newTableName)){
                warningAlert("This table already exists", "Please try a different table name.");
                return false;
            }
            //Create the new table if that name is not taken.
            else if(choice.equals("Market Data")) {
                TradeBenchModel.createMarketTable(newTableName);
            }
            else if(choice.equals("Trade Data")) {
                TradeBenchModel.createTradeTable(newTableName);
            }
            //Return false if something goes wrong.
            else {
                warningAlert("Something went wrong.", "Please try again.");
                return false;
            }
        }

        //Return false if the user did not select a table type.
        else{
            return false;
        }

        //If table created succesfully, report it via dialog and return true.
        String content = "'" + newTableName + "' was created succesfully.";
        informationDialog("Success!", content);

        return true;

    }


    /**
     * Enables user to import market data from a text file into the app's database.
     * FIXME Replace this method with a file-selector.
     */
    @FXML
    public void importMarketData() {

        //Dialog box to set file-path.
        String url = textInputDialog("Set Filepath","Enter the file's filepath", "Filepath: ");

        //Dialog box to set name of table to write to.
        String tableName = tableSelect();

        //Terminate process if no table selected, otherwise process data from file to database.
        if(tableName.equals("None")) {
            warningAlert("No table selected.", "Please try again.");
            return;
        }
        else{
            TradeBenchModel.processMarketData(url, tableName);
        }

        //Information dialog box reports if succesful.
        String content = "Data succesfully imported to '" + tableName + "'.";
        informationDialog("Success!", content);
    }


    /**
     * Enables user to import trade data from a .csv file into the app's database.
     * FIXME Replace this method with a file-selector.
     */
    @FXML
    public void importTradeData() {

        //Dialog box to set file-path.
        String url = textInputDialog("Set Filepath","Enter the file's filepath", "Filepath: ");

        //Dialog box to set name of table to write to.
        String tableName = tableSelect();

        //Terminate process if no table selected, otherwise process data from file to database.
        if(tableName.equals("None")) {
            warningAlert("No table selected.", "Please try again.");
            return;
        }
        else{
            TradeBenchModel.processTradeData(url, tableName);
        }

        //Information dialog box reports if succesful.
        String content = "Data succesfully imported to '" + tableName + "'.";
        informationDialog("Success!", content);
    }


    /**
     * Select and laod trades into the trade tableView display.
     */
    @FXML
    public void loadTrades() {
        //Calls tableSelect method to take user selected tableName and store it as a String.
        String tradeTableName = tableSelect();

        //Verifies the chosen table exists.
        if(TradeBenchModel.checkExists(tradeTableName)) {
            //Dialog box to get desired start date.
            startDate = textInputDialog("Start Date", "Please enter Start Date in format yyyy-MM-dd",
                    "Start Date: ");
            //Dialog box to get desired end date.
            endDate = textInputDialog("End Date", "Please enter End Date in format yyyy-MM-dd",
                    "End Date: ");
            //Gets a array list of trades from selected table within the given start and end dates.
            ArrayList<Trade> trades = TradeBenchModel.getTradeList(tradeTableName, startDate, endDate);

            //Clears the tableView of previously loaded trades.
            tradesTable.getItems().clear();

            //Loads trades into the tradesTable.
            for (Trade t : trades)
                tradesTable.getItems().add(t);

        }

        //Displays an alert if the selected table wasn't found.
        else {
            warningAlert("Table Not Found", "Please try again.");
        }
    }


    /**
     * Loads data into the chart display.
     * FIXME change to get input by clicking a trade from the chart.
     */
    public void loadChart() {

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


    /**
     * Deletes a selected table from the database.
     */
    @FXML
    public void deleteTable() {
        //ArrayList of existing table names + "String Table" option.
        ArrayList<String> tableChoices = TradeBenchModel.getTableNames();
        tableChoices.add("Select Table");

        //ArrayList for choices yes and no.
        ArrayList<String> yesNo = new ArrayList<String>();
        yesNo.add("No");
        yesNo.add("Yes");

        //Retrieves user-selected table name using an option Dialog box.
        String tableSelection = optionDialog("Select Table", "Select which table you'd like to delete.",
                "Choose Table", tableChoices, "Select Table");

        //Initiate "confirm" variable with default choice as no.
        String confirm = "No";

        //Displays a warning/verification box to verify intent to delete if an existing table was selected.
        if(!tableSelection.equals("Select Table")) {
            confirm = optionDialog("WARNING", "This table will be PERMANENTLY DELETED.", "Are you " +
                    "sure you wish to proceed?", yesNo, "No");
        }
        //Informs that a table wasn't selected and terminates process if no table was selected.
        else{
            informationDialog("No table selected.", "Please try again.");
            return;
        }

        //Initiates "content" string for later use.
        String content = null;

        //Deletes the table if the user selected "Yes".
        if(confirm.equals("Yes")) {
            TradeBenchModel.dropTable(tableSelection);
            //Checks that the table no longer exists and displays appropriate message.
            if(!TradeBenchModel.checkExists(tableSelection)) {
                content = "The '" + tableSelection + "' table was succesfully deleted.";
                informationDialog("Success.", content);
            }
            else {
                content = "Something went wrong and the '" + tableSelection + "' table was not deleted.";
                warningAlert("An error occured.", content);
            }
        }
        //Informs that table was not deleted if the user selected yes.
        else{
            content = "The '" + tableSelection + "' was not deleted.";
            informationDialog("Operation aborted.", content);
        }
    }


    //EMBEDDED FUNCTIONS

    /**
     * Dialog box that allows user to select(or create) which table to use. Returns tableName as a string.
     * @return String - Selected Table's name.
     */
    public String tableSelect() {

        //Set string array tableChoices of the names of existing tables + "Create New" and "Select Table.
        ArrayList<String> tableChoices = TradeBenchModel.getTableNames();
        tableChoices.add("Create New");
        tableChoices.add("Select Table");

        //Lets user select which table to use from a dropdown selection box w/ tableChoices as options.
        String tableSelection = optionDialog("Select Table", "Select which table you'd like to import to.",
                "Choose Table", tableChoices, "Select Table");

        //Runs the createNewTable function if the user selected 'Create New'. Returns result of createNewTable.
        if (tableSelection.equals("Create New")) {
            if(createNewTable()){
                return newTableName;
            }
            else {
                return "None";
            }
        }

        //Returns "None" if no table was selected, otherwise returns name of selected table.
        else if (tableSelection.equals("Select Table")) {
            return "None";
        }
        else {
            return tableSelection;
        }
    }


    //GENERIC INTERFACE TOOLS

    /**
     * Generic information dialog box.
     * @param title     The title of the box.
     * @param content   The message to display in the content of the box.
     */
    public void informationDialog(String title, String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }


    /**
     * A dialog box to retrieve a String from the user via a dropdown box of options.
     * @param title     The title of the box.
     * @param header    The header of the box. Set null to not have one.
     * @param content   The message next to the dropdown list.
     * @param options   The string array containing the options that will appear in the dropdown list.
     * @param defaultChoice The choice the dropdown list starts on.
     * @return
     */
    public String optionDialog(String title, String header, String content, ArrayList<String> options,
                               String defaultChoice) {
        //Instantiate return variable.
        String choice = defaultChoice;

        //Instantiate option dialog box.
        ChoiceDialog<String> dialog = new ChoiceDialog<>(defaultChoice, options);

        //Set dialog content.
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        //Wait for and retrieve response it selected.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            choice = result.get();
        }

        return choice;
    }


    /**
     * A dialog box to get a String of text from a user.
     * @param title     Title of the window the box appears in.
     * @param header    Header of the window. Set null to not have one.
     * @param content   The prompt for input.
     * @return          The user's input.
     */
    public String textInputDialog(String title, String header, String content) {
        //Reset this.output return variable.
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


    /**
     * Displays a warning dialog box with the desired message.
     * @param header    Header of the window. Set null to not have one.
     * @param content   The warning message to display.
     */
    public void warningAlert(String header, String content) {
        //Instantiate alert box.
        Alert alert = new Alert(Alert.AlertType.WARNING);

        //Set alert box fields.
        alert.setTitle("Warning");
        alert.setHeaderText(header);
        alert.setContentText(content);

        //Wait for response.
        alert.showAndWait();
    }






}

