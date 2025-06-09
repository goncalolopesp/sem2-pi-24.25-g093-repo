package ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class PlayMenuGUI extends Application {

    /**
     * Launches the JavaFX application for the PlayMenuGUI.
     * This method initializes and displays the application's primary stage
     * by invoking the {@code launch()} method provided by the {@code Application} class.
     *
     * This is used to start the graphical user interface for the Play Simulation module.
     */
    public void launchApp() {
        launch();
    }

    /**
     * Starts the JavaFX application and initializes the primary stage.
     * This method sets up the main window for the Play Simulation interface,
     * loading the layout defined in the associated FXML file and applying
     * necessary configurations to the stage.
     *
     * @param stage The primary stage for the JavaFX application.
     * @throws Exception If an error occurs during FXML loading or scene setup.
     */
    @Override
    public void start(Stage stage) throws Exception {
        URL fxmlURL = getClass().getResource("/javafx/playSimulation.fxml");

        FXMLLoader loader = new FXMLLoader(fxmlURL);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("PLAY SIMULATION");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }
}
