package ui.gui;

import controller.PlayerMenuController;
import domain.Scenario;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PlayerMenu extends Application {

    private Scenario selectedScenario;

    public PlayerMenu(Scenario selectedScenario) {
        this.selectedScenario = selectedScenario;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/PlayerMenu.fxml"));
        Parent root = loader.load();
        PlayerMenuController controller = loader.getController();
        controller.setScenario(selectedScenario);

        Scene scene = new Scene(root);
        primaryStage.setTitle("PLAYER MENU");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}