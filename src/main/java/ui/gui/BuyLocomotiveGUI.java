package ui.gui;

import controller.BuildStationController;
import controller.BuyLocomotiveController;
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

public class BuyLocomotiveGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }

    /**
     * Opens the Buy Locomotive GUI
     */
    public static void launch(Stage parentStage, Map map, Scenario scenario, Budget budget) {
        try {
            FXMLLoader loader = new FXMLLoader(BuyLocomotiveGUI.class.getResource( "/javafx/buyLocomotive.fxml"));
            Parent root = loader.load();
            BuyLocomotiveController controller = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Buy Locomotive");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            if (parentStage != null) {
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(parentStage);
            }
            controller.initData(scenario, budget, stage, 1);
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
