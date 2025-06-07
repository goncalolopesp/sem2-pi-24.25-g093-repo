package controller;

import domain.Scenario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ui.gui.PlayMenuGUI;

import java.io.IOException;

public class PlayerMenuController {

    @FXML
    public AnchorPane PlayerMenu;

    private Scenario currentScenario;

    // Method to receive the scenario from PlayerMenu
    public void setScenario(Scenario scenario) {
        this.currentScenario = scenario;
    }

    @FXML
    public void BuyLocomotive(ActionEvent event) {
        // You can use currentScenario here
    }

    @FXML
    public void ListLocomotives(ActionEvent event) throws IOException {
        // You can use currentScenario here
    }

    @FXML
    public void ListStations(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/ListStationsGUI.fxml"));
        Parent root = loader.load();
        ListStationController controller = loader.getController();
        controller.setScenario(currentScenario);
        Scene currentScene = PlayerMenu.getScene();
        currentScene.setRoot(root);
    }

    @FXML
    public void doLogout(ActionEvent event) throws Exception {
        Stage stage = (Stage) PlayerMenu.getScene().getWindow();
        new PlayMenuGUI().start(stage);
    }
}