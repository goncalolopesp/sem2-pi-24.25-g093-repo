package ui.gui;

import controller.UpgradeStationTypeUIController;
import domain.Budget;
import domain.Map;
import domain.Scenario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * GUI class responsible for loading and displaying the Upgrade Station Type interface.
 * Creates a modal dialog window for upgrading station types.
 */
public class UpgradeStationTypeGUI {

    private Stage stage;
    private UpgradeStationTypeUIController controller;

    /**
     * Creates and displays the Upgrade Station Type GUI.
     *
     * @param parentStage the parent stage (for modal behavior)
     * @param selectedMap the map where stations are located
     * @param selectedScenario the scenario context
     * @param budget the player's budget
     * @throws IOException if the FXML file cannot be loaded
     */
    public void show(Stage parentStage, Map selectedMap, Scenario selectedScenario, Budget budget) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/upgradeStationType.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.initialize(selectedMap, selectedScenario, budget);
        stage = new Stage();
        stage.setTitle("Upgrade Station Type");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        if (parentStage != null) {
            stage.setX(parentStage.getX() + (parentStage.getWidth() - 550) / 2);
            stage.setY(parentStage.getY() + (parentStage.getHeight() - 650) / 2);
        }
        stage.showAndWait();
    }

    /**
     * Creates and displays the Upgrade Station Type GUI without a parent stage.
     *
     * @param selectedMap the map where stations are located
     * @param selectedScenario the scenario context
     * @param budget the player's budget
     * @throws IOException if the FXML file cannot be loaded
     */
    public void show(Map selectedMap, Scenario selectedScenario, Budget budget) throws IOException {
        show(null, selectedMap, selectedScenario, budget);
    }

    /**
     * Closes the Upgrade Station Type window if it's currently open.
     */
    public void close() {
        if (stage != null && stage.isShowing()) {
            stage.close();
        }
    }

    /**
     * Gets the controller associated with this GUI.
     *
     * @return the UpgradeStationTypeUIController instance, or null if not initialized
     */
    public UpgradeStationTypeUIController getController() {
        return controller;
    }

    /**
     * Gets the stage associated with this GUI.
     *
     * @return the Stage instance, or null if not initialized
     */
    public Stage getStage() {
        return stage;
    }
}