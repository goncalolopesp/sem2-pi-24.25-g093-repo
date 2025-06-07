package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ui.gui.SelectScenarioGUI;

import java.io.IOException;

public class PlaySimulationController {

    @FXML
    private Button playBUTTON;

    @FXML
    private Button exitBUTTON;

    @FXML
    private void handlePlayGame() throws IOException {
        Stage currentStage = (Stage) playBUTTON.getScene().getWindow();

        SelectScenarioGUI selectScenarioGUI = new SelectScenarioGUI();
        selectScenarioGUI.start(currentStage);
    }

    @FXML
    private void handleExit() {
        // Close the current window
        Stage stage = (Stage) exitBUTTON.getScene().getWindow();
        stage.close();

        System.exit(0);
    }
}
