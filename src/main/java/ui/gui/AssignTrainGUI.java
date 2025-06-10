package ui.gui;

import domain.Budget;
import domain.Map;
import domain.Scenario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import controller.AssignTrainGUIController;

import java.io.IOException;

public class AssignTrainGUI {

    public static void launch(Stage parentStage, Map selectedMap, Scenario selectedScenario, Budget budget) {
        try {
            FXMLLoader loader = new FXMLLoader(AssignTrainGUI.class.getResource("/javafx/assignTrain.fxml"));
            Parent root = loader.load();

            AssignTrainGUIController controller = loader.getController();
            controller.setData(selectedMap, selectedScenario, budget);

            Stage stage = new Stage();
            stage.setTitle("Assign Train to Route");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(parentStage);
            stage.setOnShown(e -> {
                stage.setX(parentStage.getX() + (parentStage.getWidth() - stage.getWidth()) / 2);
                stage.setY(parentStage.getY() + (parentStage.getHeight() - stage.getHeight()) / 2);
            });

            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Failed to load AssignTrain GUI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}