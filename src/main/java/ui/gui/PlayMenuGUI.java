package ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class PlayMenuGUI extends Application {

    public void launchApp() {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        URL fxmlURL = getClass().getResource("/javafx/playSimulation.fxml");

        FXMLLoader loader = new FXMLLoader(fxmlURL);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("PLAY SIMULATION");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }
}
