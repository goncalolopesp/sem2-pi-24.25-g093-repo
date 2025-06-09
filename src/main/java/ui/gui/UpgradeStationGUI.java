package ui.gui;

import controller.UpgradeStationFXController;
import domain.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Launcher class for the Upgrade Station JavaFX window.
 * This replaces the console-based UpgradeStationUI.
 */
public class UpgradeStationGUI {

    private final Map selectedMap;
    private final Scenario selectedScenario;
    private final AtomicReference<AtomicIntegerArray> timePeriod;
    private final Budget budget;

    public UpgradeStationGUI(Map selectedMap, Scenario selectedScenario,
                                  AtomicReference<AtomicIntegerArray> timePeriod, Budget budget) {
        this.selectedMap = selectedMap;
        this.selectedScenario = selectedScenario;
        this.timePeriod = timePeriod;
        this.budget = budget;
    }

    /**
     * Show the upgrade station window as a modal dialog
     */
    public void show() {
        show(null);
    }

    /**
     * Show the upgrade station window as a modal dialog with specified parent
     */
    public void show(Stage parentStage) {
        try {
            // Create the controller with required dependencies
            UpgradeStationFXController controller = new UpgradeStationFXController(
                    selectedMap, selectedScenario, timePeriod, budget
            );

            // Load FXML with custom controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/upgradeStation.fxml"));
            loader.setController(controller);

            Parent root = loader.load();

            // Create the stage
            Stage stage = new Stage();
            stage.setTitle("Upgrade Station");
            stage.setScene(new Scene(root, 550, 650));
            stage.setResizable(false);

            // Make it modal if parent is provided
            if (parentStage != null) {
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(parentStage);
            }

            // Center on screen or parent
            if (parentStage != null) {
                stage.setX(parentStage.getX() + (parentStage.getWidth() - 550) / 2);
                stage.setY(parentStage.getY() + (parentStage.getHeight() - 650) / 2);
            } else {
                stage.centerOnScreen();
            }

            // Show the window
            stage.showAndWait();

        } catch (Exception e) {
            System.err.println("Error loading Upgrade Station window: " + e.getMessage());
            e.printStackTrace();

            // Fallback to console version if JavaFX fails
            System.out.println("Falling back to console version...");
            ui.console.UpgradeStationUI consoleUI = new ui.console.UpgradeStationUI(
                    selectedMap, selectedScenario, timePeriod, budget
            );
            consoleUI.run();
        }
    }
}