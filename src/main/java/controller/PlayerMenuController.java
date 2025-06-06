package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import repository.AuthRepository;
import repository.Repositories;

import java.io.IOException;

public class PlayerMenuController {

    @FXML
    public AnchorPane PlayerMenu;

    @FXML
    public void BuyLocomotive(ActionEvent event) {

    }

    @FXML
    public void ListLocomotives(ActionEvent event) throws IOException {

    }

    @FXML
    public void ListStations(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/ListStationsGUI.fxml"));
        Parent root = loader.load();
        Scene currentScene = PlayerMenu.getScene();
        currentScene.setRoot(root);
    }

    @FXML
    public void doLogout(ActionEvent event) {
        Stage stage = (Stage) PlayerMenu.getScene().getWindow();
        stage.close();
        AuthRepository ar = Repositories.getInstance().getAuthRepository();
        ar.doLogout();
    }

}
