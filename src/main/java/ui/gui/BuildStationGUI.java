package ui.gui;

import controller.BuildStationController;
import domain.Budget;
import domain.Map;
import domain.Scenario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility class to launch the Build Station JavaFX interface.
 * This replaces the console-based BuildStationUI with a modern GUI.
 */
public class BuildStationGUI {

    /**
     * Launches the Build Station interface as a modal dialog
     *
     * @param parentStage The parent stage (can be null)
     * @param map The map where the station will be built
     * @param scenario The scenario context
     * @param budget The player's budget
     */
    public static void launch(Stage parentStage, Map map, Scenario scenario, Budget budget) {
        try {
            FXMLLoader loader = new FXMLLoader(BuildStationGUI.class.getResource( "/javafx/buildStation.fxml"));
            Parent root = loader.load();
            BuildStationController controller = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Build Station");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            if (parentStage != null) {
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(parentStage);
            }
            controller.initData(map, scenario, budget, stage);
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error loading Build Station interface: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Falling back to console interface...");
            ui.console.BuildStationUI consoleUI = new ui.console.BuildStationUI(map, scenario, budget);
            consoleUI.run();
        }
    }
}