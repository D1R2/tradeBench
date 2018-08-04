package main.java;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class interfaces the GUI with the application logic. Anything that can
 * happen to the GUI happens in this class, except for the original definition
 * of the GUI elements.
 *
 * @author daniel
 * @version 1.0
 * Created  2018-08-03
 * Modified 2018-08-03
 */
public class Controller {

    @FXML TableView tradesTable;
    @FXML TableColumn<Trade, Integer> numberCol;
    @FXML TableColumn<Trade, String> typeCol;
    @FXML TableColumn<Trade, String> entryTimeCol;
    @FXML TableColumn<Trade, Double> entryPriceCol;
    @FXML TableColumn<Trade, String> exitTimeCol;
    @FXML TableColumn<Trade, Double> exitPriceCol;

    @FXML
    public void initialize() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Trade t1 = new Trade(1, "short", LocalDateTime.parse("2018-01-01 09:30:15", formatter),
                LocalDateTime.parse("2018-01-01 09:30:48", formatter), 2950.50, 2950.00);
        Trade t2 = new Trade(1, "long", LocalDateTime.parse("2018-01-01 09:38:19", formatter),
                LocalDateTime.parse("2018-01-01 09:38:25", formatter), 2952.50, 2952.50);
        Trade t3 = new Trade(1, "long", LocalDateTime.parse("2018-01-01 10:04:55", formatter),
                LocalDateTime.parse("2018-01-01 10:05:48", formatter), 2957.75, 2958.50);
        Trade t4 = new Trade(1, "long", LocalDateTime.parse("2018-01-01 10:10:21", formatter),
                LocalDateTime.parse("2018-01-01 10:10:59", formatter), 2957.50, 2956.25);
        tradesTable.getItems().addAll(t1, t2, t3, t4);

        numberCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNumber()).asObject());
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        entryTimeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEntryTime().format(formatter)));
        exitTimeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExitTime().format(formatter)));
        entryPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getEntryPrice()).asObject());
        exitPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getExitPrice()).asObject());

    }

}
