package ui.gui;

import controller.BuyLocomotiveController;
import controller.ListStationController;
import domain.Budget;
import domain.Map;
import domain.Scenario;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ListStationsGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

    }

    /**
     * Opens the List Stations GUI
     */
    public static void launch(Stage parentStage, Map map, Scenario scenario, Budget budget) {
        try {
            FXMLLoader loader = new FXMLLoader(ListStationsGUI.class.getResource( "/javafx/ListStationsGUI.fxml"));
            Parent root = loader.load();
            ListStationController controller = loader.getController();
            controller.setScenario(scenario);
            Stage stage = new Stage();
            stage.setTitle("List Stations");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            if (parentStage != null) {
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(parentStage);
            }
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error loading Buy Station interface: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Falling back to console interface...");
            ui.console.BuyLocomotiveUI consoleUI = new ui.console.BuyLocomotiveUI(scenario, map, budget);
            consoleUI.run();
        }
    }
}
