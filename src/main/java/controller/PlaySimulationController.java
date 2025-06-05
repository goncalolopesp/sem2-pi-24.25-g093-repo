package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ui.console.SimulationUI;

public class PlaySimulationController {

    @FXML
    private Button playBUTTON;

    @FXML
    private Button exitBUTTON;

    @FXML
    private void handlePlayGame() {
        // Execute the SimulationUI
        SimulationUI simulationUI = new SimulationUI();
        simulationUI.run();
    }

    @FXML
    private void handleExit() {
        // Close the current window
        Stage stage = (Stage) exitBUTTON.getScene().getWindow();
        stage.close();

        System.exit(0);
    }
}
