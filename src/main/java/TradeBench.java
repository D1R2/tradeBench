package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class is the launcher for the TradeBench application.
 *
 * @author  daniel
 * @version 1.0
 * Created  2018-08-03
 * Modified 2018-08-03
 */
public class TradeBench extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/main/resources/fxml/ui.fxml"));
        primaryStage.setTitle("Trade Bench");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
