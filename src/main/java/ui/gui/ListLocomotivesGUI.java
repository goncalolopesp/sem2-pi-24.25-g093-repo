package ui.gui;

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

public class ListLocomotivesGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }

    public static void launch(Stage parentStage, Map map, Scenario scenario, Budget budget) {
        try {
            FXMLLoader loader = new FXMLLoader(ListLocomotivesGUI.class.getResource( "/javafx/listLocomotives.fxml"));
            Parent root = loader.load();
            BuyLocomotiveController controller = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("List Locomotives");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            if (parentStage != null) {
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(parentStage);
            }
            controller.initData(scenario, budget, stage, 2);
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error loading Load Station interface: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Falling back to console interface...");
            ui.console.ListLocomotiveUI consoleUI = new ui.console.ListLocomotiveUI();
            consoleUI.run();
        }
    }
}
