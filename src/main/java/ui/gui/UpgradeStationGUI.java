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
 * This class provides a graphical user interface for the upgrade station functionality,
 * replacing the console-based implementation.
 */

public class UpgradeStationGUI {

    private final Map selectedMap;
    private final Scenario selectedScenario;
    private final AtomicReference<AtomicIntegerArray> timePeriod;
    private final Budget budget;

    /**
     * Constructs a new UpgradeStationGUI instance.
     *
     * @param selectedMap The map currently selected in the application
     * @param selectedScenario The scenario currently selected in the application
     * @param timePeriod The atomic reference to the time period array
     * @param budget The budget object containing financial constraints
     */

    public UpgradeStationGUI(Map selectedMap, Scenario selectedScenario,
                                  AtomicReference<AtomicIntegerArray> timePeriod, Budget budget) {
        this.selectedMap = selectedMap;
        this.selectedScenario = selectedScenario;
        this.timePeriod = timePeriod;
        this.budget = budget;
    }

    /**
     * Shows the upgrade station window as a non-modal dialog.
     * This method displays the window centered on the screen.
     */

    public void show() {
        show(null);
    }

    /**
     * Shows the upgrade station window as a modal dialog.
     * If a parent stage is provided, the window will be displayed centered relative to the parent window.
     * If the JavaFX interface fails to load, it falls back to the console-based interface.
     *
     * @param parentStage The parent stage to which this window should be modal.
     *                    If null, the window will be non-modal and centered on screen.
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