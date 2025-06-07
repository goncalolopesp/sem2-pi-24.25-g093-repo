package controller;

import domain.Building;
import domain.CurrentUser;
import domain.Scenario;
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
import java.util.stream.Collectors;

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

    /** Current scenario passed from PlayerMenuController */
    private Scenario currentScenario;

    /** Reference to the PlayerMenuController for proper navigation */
    private PlayerMenuController playerMenuController;

    /**
     * Method to receive the scenario from PlayerMenuController
     */
    public void setScenario(Scenario scenario) {
        this.currentScenario = scenario;
        System.out.println("Scenario set in ListStationController: " +
                (scenario != null ? scenario.getScenarioName() : "null"));

        // Initialize the station list after receiving the scenario
        loadStations();
    }

    /**
     * Set reference to the PlayerMenuController for proper navigation
     */
    public void setPlayerMenuController(PlayerMenuController playerMenuController) {
        this.playerMenuController = playerMenuController;
        System.out.println("PlayerMenuController reference set in ListStationController");
    }

    /**
     * Load stations into the ComboBox - called after scenario is set
     */
    private void loadStations() {
        if (currentScenario == null) {
            System.err.println("Cannot load stations: currentScenario is null");
            return;
        }

        // Clear existing items first
        StationList.getItems().clear();

        List<Station> userStations = getStations();
        System.out.println("Loading " + userStations.size() + " stations for user");

        for (Station station : userStations){
            if (station.getOwnerPlayer().equalsIgnoreCase(CurrentUser.getInstance().getUser().getEmail())){
                StationList.getItems().add(station);
            }
        }
    }

    /**
     * Retrieves all stations in the system
     * @return List of all stations
     */
    public List<Station> getStations(){
        if (currentScenario == null) {
            System.err.println("Cannot get stations: currentScenario is null");
            return new ArrayList<>();
        }

        return sr.getStations().stream()
                .filter(s -> s.getScenarioName().equals(currentScenario.getScenarioName()))
                .collect(Collectors.toList());
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

        if (selectedStation == null) {
            StationDescription.setText("Please select a station from the list.");
            return;
        }

        String text = "Station: " + selectedStation.getName() + "\n" +
                "Center:" + selectedStation.getX() + "," + selectedStation.getY() + "\n" +
                "Type:" + selectedStation.getType() + "\n" +
                "Cargo: " + selectedStation.getCapacityOwned() + "/" + selectedStation.getCapacity();

        List<Building> buildings = getBuildingByStation(selectedStation);
        if (!buildings.isEmpty()){
            text += "\n========= BUILDINGS =========";
            for (Building building : buildings) {
                text += "\n" + "Buildings: " + building.getBuildingType();
            }
        }

        StationDescription.setText(text);
    }

    @FXML
    public void initialize(){
        // Don't load stations here since currentScenario will be null
        // The loadStations() method will be called from setScenario()
        System.out.println("ListStationController initialize() called - waiting for scenario...");
    }

    @FXML
    public void GoBackToPlayerMenu() throws IOException {
        if (playerMenuController != null) {
            // Use the reference to properly navigate back
            Scene currentScene = ListStations.getScene();
            playerMenuController.returnToPlayerMenu(currentScene);
        } else {
            System.err.println("PlayerMenuController reference is null - cannot navigate back properly");

            // Fallback to the old method (but this will lose simulation state)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/PlayerMenu.fxml"));
            Parent root = loader.load();
            Scene currentScene = ListStations.getScene();
            currentScene.setRoot(root);
        }
    }
}