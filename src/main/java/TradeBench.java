package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class is the launcher for the TradeBench application.
 * For this application, the front-end is done by Daniel Gelber,
 * the back-end is done by David Randolph, and the interface is
 * done by both as needed.
 *
 * @author Daniel Gelber
 * @version 1.0
 * Created  2018-08-03
 * Modified 2018-08-08
 */
public class TradeBench extends Application {

    /**
     * Start method is called when the program starts and it initializes
     * the window and loads the view.
     *
     * @param primaryStage the main window.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/main/resources/fxml/ui.fxml"));
        primaryStage.setTitle("Trade Bench");
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add("/main/resources/styles/MainStyle.css");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * Starts the application.
     *
     * @param args unused.
     */
    public static void main(String[] args) {

        launch(args);

    }

}
