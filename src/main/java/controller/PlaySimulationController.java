package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class PlaySimulationController {

    @FXML
    private Button playBUTTON;

    @FXML
    private Button exitBUTTON;

    @FXML
    private void handlePlayGame() throws IOException {
        // Execute the SimulationUI
        URL fxmlURL = getClass().getResource("/javafx/selectScenario.fxml");
        System.out.println("FXML URL: " + fxmlURL);

        FXMLLoader loader = new FXMLLoader(fxmlURL);
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("SELECT SCENARIO");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

        Stage currentStage = (Stage) playBUTTON.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void handleExit() {
        // Close the current window
        Stage stage = (Stage) exitBUTTON.getScene().getWindow();
        stage.close();

        System.exit(0);
    }
}
