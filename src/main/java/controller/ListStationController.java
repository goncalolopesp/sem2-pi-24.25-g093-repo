package controller;

import domain.Building;
import domain.CurrentUser;
import domain.Station;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import repository.BuildingRepository;
import repository.Repositories;
import repository.StationRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for retrieving station and building information
 * Provides methods to list stations and find buildings associated with specific stations
 */
public class ListStationController {

    @FXML
    public TextArea StationDescription;

    @FXML
    public ComboBox<Station> StationList;

    @FXML
    public AnchorPane ListStations;

    /** Singleton instance of repositories */
    Repositories repositories = Repositories.getInstance();

    /** Repository for station data */
    StationRepository sr = repositories.getStationRepository();

    /** Repository for building data */
    BuildingRepository br = repositories.getBuildingRepository();

    /**
     * Retrieves all stations in the system
     * @return List of all stations
     */
    public List<Station> getStations(){
        return sr.getStations();
    }

    /**
     * Retrieves all buildings associated with a specific station
     * @param station The station to find buildings for
     * @return List of buildings located at the specified station
     */
    public List<Building> getBuildingByStation(Station station){
        List<Building> lstBuilding = new ArrayList<>();
        List<Building> fetched = br.getBuildings();

        for (Building building : fetched) {
            if (building.getStation().equals(station)){
                lstBuilding.add(building);
            }
        }
        return lstBuilding;
    }

    @FXML
    public void ShowStationDescription(){

        Station selectedStation = StationList.getSelectionModel().getSelectedItem();
        String text = "Station: " + selectedStation.getName() + "\n" + "Center:" + selectedStation.getX() + "," + selectedStation.getY() + "\n" + "Type:" + selectedStation.getType();

        List<Building> buildings = getBuildingByStation(selectedStation);
        if (buildings.size() > 0){
            text += "========= BUILDINGS =========";
            for (Building building : buildings) {
                text += "\n" + "Buildings: " + building.getBuildingType();
            }
        }
        StationDescription.setText(text);
    }

    @FXML
    public void initialize(){
        for (Station station : getStations()){
            if (station.getOwnerPlayer().equalsIgnoreCase(CurrentUser.getInstance().getUser().getEmail())){
                StationList.getItems().add(station);
            }
        }
    }

    @FXML
    public void GoBackToPlayerMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/PlayerMenu.fxml"));
        Parent root = loader.load();
        Scene currentScene = ListStations.getScene();
        currentScene.setRoot(root);
    }
}