package controller;

import domain.Building;
import domain.CurrentUser;
import domain.Scenario;
import domain.Station;
import dto.StationDTO;
import dto.StationDetailDTO;
import dto.BuildingDTO;
import dto.DTOMapperStation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
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

    Repositories repositories = Repositories.getInstance();

    StationRepository sr = repositories.getStationRepository();

    BuildingRepository br = repositories.getBuildingRepository();

    private Scenario currentScenario;

    /**
     * Sets the current scenario to the provided {@code scenario} and loads the associated stations.
     *
     * This method updates the {@code currentScenario} field to the specified scenario and
     * proceeds to call the {@code loadStations} method to populate the station list
     * UI component with data relevant to the scenario.
     *
     * @param scenario the scenario to set as the current active scenario
     */
    public void setScenario(Scenario scenario) {
        this.currentScenario = scenario;
        loadStations();
    }

    /**
     * Loads the list of stations owned by the current user into the station list UI component.
     *
     * This method retrieves all stations associated with the current scenario and filters them
     * to include only those stations owned by the currently logged-in user. The filtered list
     * of stations is then displayed in the station list component. If the current scenario
     * is null, an error message is printed to the standard error output and no action is taken.
     *
     * Precondition:
     * - `currentScenario` must be properly initialized before this method is called.
     *
     * Behavior:
     * - Clears the existing items in the station list.
     * - Retrieves all stations associated with the current scenario through the `getStations` method.
     * - Filters the stations to include only those owned by the current user, which is determined
     *   by comparing the station owner's email with the logged-in user's email.
     * - Populates the station list UI component with the filtered list of stations.
     *
     * Error Handling:
     * - If `currentScenario` is null, no stations are loaded, and an error message is logged.
     */
    private void loadStations() {
        if (currentScenario == null) {
            System.err.println("Cannot load stations: currentScenario is null");
            return;
        }

        // Clear existing items first
        StationList.getItems().clear();

        List<Station> userStations = getStationEntities();

        for (Station station : userStations){
            if (station.getOwnerPlayer().equalsIgnoreCase(CurrentUser.getInstance().getUser().getEmail())){
                StationList.getItems().add(station);
            }
        }
    }

    /**
     * Retrieves a list of station DTOs associated with the current scenario.
     *
     * This method filters the stations available in the system to include only those
     * that belong to the scenario currently set as active. If the current scenario
     * is not set (null), an error message is displayed, and an empty list is returned.
     *
     * @return a list of StationDTO objects associated with the active scenario, or an empty
     * list if the current scenario is null or no matching stations are found
     */
    public List<StationDTO> getStations(){
        if (currentScenario == null) {
            System.err.println("Cannot get stations: currentScenario is null");
            return new ArrayList<>();
        }

        List<Station> stations = sr.getStations().stream()
                .filter(s -> s.getScenarioName().equals(currentScenario.getScenarioName()))
                .collect(Collectors.toList());

        return DTOMapperStation.toDTOList(stations);
    }

    /**
     * Retrieves a list of station entities associated with the current scenario.
     * Used internally for operations that require full domain objects.
     *
     * @return a list of Station domain objects associated with the active scenario
     */
    private List<Station> getStationEntities(){
        if (currentScenario == null) {
            System.err.println("Cannot get stations: currentScenario is null");
            return new ArrayList<>();
        }

        return sr.getStations().stream()
                .filter(s -> s.getScenarioName().equals(currentScenario.getScenarioName()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves detailed information about a specific station including its buildings.
     *
     * @param stationDTO the StationDTO object representing the station
     * @return a StationDetailDTO containing complete station and building information
     */
    public StationDetailDTO getStationDetails(StationDTO stationDTO) {
        Station station = sr.getStations().stream()
                .filter(s -> s.getName().equals(stationDTO.getName()) &&
                        s.getX() == stationDTO.getX() &&
                        s.getY() == stationDTO.getY())
                .findFirst()
                .orElse(null);

        if (station == null) {
            return null;
        }

        List<Building> buildings = getBuildingByStation(station);
        return DTOMapperStation.toDetailDTO(station, buildings);
    }

    /**
     * Retrieves a list of buildings associated with a specific station.
     *
     * This method filters the available buildings to include only those that are
     * associated with the given station. The filtering is performed by comparing
     * the station of each building to the provided station.
     *
     * @param station the station whose associated buildings are to be retrieved
     * @return a list of buildings that are associated with the specified station
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

    /**
     * Retrieves building DTOs for a specific station.
     *
     * @param stationDTO the StationDTO representing the station
     * @return a list of BuildingDTO objects associated with the station
     */
    public List<BuildingDTO> getBuildingDTOs(StationDTO stationDTO) {
        Station station = sr.getStations().stream()
                .filter(s -> s.getName().equals(stationDTO.getName()) &&
                        s.getX() == stationDTO.getX() &&
                        s.getY() == stationDTO.getY())
                .findFirst()
                .orElse(null);

        if (station == null) {
            return new ArrayList<>();
        }

        List<Building> buildings = getBuildingByStation(station);
        return DTOMapperStation.buildingsToDTOList(buildings);
    }

    /**
     * Displays the description of the selected station in the UI.
     *
     * This method retrieves the currently selected station from the station list UI component and
     * displays its details, such as name, coordinates, type, and cargo capacity, in a textual format.
     * If no station is selected, a message prompting the user to select a station is displayed instead.
     * Additionally, if the selected station has associated buildings, a list of those buildings is
     * appended to the description.
     *
     * Behavior:
     * - If no station is selected, displays a message requesting the user to make a selection.
     * - If a station is selected, displays its name, coordinates, type, and cargo capacity.
     * - Retrieves and displays the building information associated with the selected station, if any.
     *
     * Preconditions:
     * - The `StationList` UI component must be populated with a list of stations.
     * - `StationDescription` must be initialized as the output text area or field for station details.
     * - `getBuildingByStation` must return a list of buildings corresponding to the given station.
     *
     * Error Handling:
     * - If no station is selected, a default message is displayed instead of station details.
     */
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

    /**
     * Initializes the controller after its root element has been completely loaded.
     *
     * This method is automatically called by the JavaFX framework following the
     * loading of the FXML file. It serves as the entry point for initializing
     * any user interface components, bindings, event handlers, or other setup
     * tasks required for the controller's operation.
     *
     * Intended Functionality:
     * - Set up UI elements and event handlers.
     * - Initialize internal state or data relevant to the controller.
     * - May include calls to methods for additional setup processes or data loading.
     *
     * Note: This method is invoked once, ensuring that the controller's
     * environment is fully initialized and ready for interaction.
     */
    @FXML
    public void initialize(){

    }

    /**
     * Returns the user to the main menu by closing the current stage.
     *
     * This method handles navigation from the current UI to the main menu.
     * It retrieves the stage associated with the `ListStations` UI component
     * and closes it, effectively ending the current view.
     *
     * Behavior:
     * - Retrieves the current stage using `ListStations`.
     * - Closes the stage if it exists.
     *
     * Preconditions:
     * - The `ListStations` UI component must have an active scene attached to a stage.
     *
     * Error Handling:
     * - If the current stage is null, no action is performed.
     */
    @FXML
    public void returnToMenu() {
        Stage currentStage = (Stage) ListStations.getScene().getWindow();
        if (currentStage != null) {
            currentStage.close();
        }
    }
}
