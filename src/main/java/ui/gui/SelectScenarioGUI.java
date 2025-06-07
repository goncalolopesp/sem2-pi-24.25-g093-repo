package ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SelectScenarioGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlURL = getClass().getResource("/javafx/selectScenario.fxml");
        System.out.println("FXML URL: " + fxmlURL);

        FXMLLoader loader = new FXMLLoader(fxmlURL);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setTitle("SELECT SCENARIO");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
