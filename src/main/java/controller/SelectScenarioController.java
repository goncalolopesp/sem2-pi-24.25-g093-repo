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

import java.io.IOException;
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