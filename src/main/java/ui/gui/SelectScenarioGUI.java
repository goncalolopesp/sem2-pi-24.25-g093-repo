package ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SelectScenarioGUI extends Application {

    /**
     * Initializes and displays the primary JavaFX stage for the SelectScenarioGUI.
     * This method sets up the main window by loading the FXML layout, creating a scene,
     * and configuring the primary stage for the application.
     *
     * @param primaryStage The primary stage for the JavaFX application.
     * @throws IOException If an error occurs during FXML file loading.
     */
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
