package controller;

import domain.Scenario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import repository.Repositories;
import repository.ScenarioRepository;
import ui.console.SimulationUI;

import java.util.List;

public class SelectScenarioController {

    @FXML
    private ComboBox<Scenario> scenarioCOMBOBOX;

    @FXML
    private Label mapLABEL;

    @FXML
    private Label portLABEL;

    @FXML
    private Label timeLABEL;

    @FXML
    private Label techLABEL;

    @FXML
    private Label histLABEL;

    @FXML
    private Label prodLABEL;

    @FXML
    private Label profLABEL;

    @FXML
    private Button playBUTTON;

    /**
     * Initializes the controller with required data and sets up the UI element actions.
     *
     * This method retrieves a list of available scenarios from the {@link ScenarioRepository}.
     * If no scenarios are found, an error alert is displayed to notify the user.
     * If scenarios are available, they are added to the ComboBox `scenarioCOMBOBOX`.
     * An action listener is also set on the ComboBox to display scenario information and
     * make the play button visible upon selection.
     *
     * The method ensures that the application is correctly initialized and ready for user interaction.
     */
    @FXML
    public void initialize() {
        ScenarioRepository scenarioRepo = Repositories.getInstance().getScenarioRepository();
        List<Scenario> scenarios = scenarioRepo.getScenarios();

        if (scenarios.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No Available Scenarios Found!");
            alert.showAndWait();
            return;
        }

        scenarioCOMBOBOX.getItems().addAll(scenarios);
        scenarioCOMBOBOX.setOnAction(event -> { displayScenarioInfo(); playBUTTON.setVisible(true); });
    }

    /**
     * Displays detailed information about the selected scenario in the associated UI labels.
     *
     * This method retrieves the currently selected scenario from the 'scenarioCOMBOBOX'. If no scenario
     * is selected, the method returns without updating the UI.
     *
     * For the selected scenario, the following details are extracted and displayed on corresponding
     * UI labels:
     * - Map name
     * - Port behavior
     * - Time restrictions (start and end times)
     * - Technological restrictions (only the first entry)
     * - Historical restrictions (only the first entry)
     * - Production rate
     * - Profit multiplier
     *
     * The extracted values are formatted and set to the respective labels: 'mapLABEL', 'portLABEL',
     * 'timeLABEL', 'techLABEL', 'histLABEL', 'prodLABEL', and 'profLABEL'.
     *
     * This populates the relevant UI elements with scenario-specific data to provide context for user interaction.
     */
    @FXML
    private void displayScenarioInfo() {
        Scenario currentScenario = scenarioCOMBOBOX.getValue();
        if (currentScenario == null) return;

        String currentMap = currentScenario.getSelectedMap();
        int[] timeRestrictions = currentScenario.getTimeRestrictions();
        int portBehavior = currentScenario.getPortBehavior();
        List<String> technologicalRestrictions = currentScenario.getTechnologicalRestrictions();
        List<String> historicalRestrictions = currentScenario.getHistoricalRestrictions();
        double productionRate = currentScenario.getProductionRate();
        double profitMultiplier = currentScenario.getProfitMultiplier();

        mapLABEL.setText("MAP: " + currentMap);
        portLABEL.setText("PORT: " + portBehavior);
        timeLABEL.setText("TIME : " + timeRestrictions[0] + " TO " + timeRestrictions[1]);
        techLABEL.setText("TECHNOLOGICAL RESTRICTIONS: " + technologicalRestrictions.get(0));
        histLABEL.setText("HISTORICAL RESTRICTIONS: " + historicalRestrictions.get(0));
        prodLABEL.setText("PRODUCTION RATE: " + productionRate);
        profLABEL.setText("PROFIT MULTIPLIER: " + profitMultiplier);
    }

    /**
     * Handles the action for initiating the simulation of a selected scenario.
     *
     * This method is triggered when the "Play Game" button is clicked. It performs the following actions:
     * - Retrieves the scenario selected in the ComboBox `scenarioCOMBOBOX`.
     * - Displays a warning alert if no scenario is selected and exits the method.
     * - Initializes the simulation UI with the selected scenario.
     * - Switches the current stage to the simulation interface.
     *
     * @throws Exception if an error occurs during the initialization or transition to the simulation UI.
     */
    @FXML
    private void handlePlayGame() throws Exception {
        Scenario selectedScenario = scenarioCOMBOBOX.getValue();

        if (selectedScenario == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Please select a scenario first!");
            alert.showAndWait();
            return;
        }

        Stage currentStage = (Stage) playBUTTON.getScene().getWindow();

        SimulationUI simulationUI = new SimulationUI(selectedScenario);
        simulationUI.start(currentStage);
    }
}