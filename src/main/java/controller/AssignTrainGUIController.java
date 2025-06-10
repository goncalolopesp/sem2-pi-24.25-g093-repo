package controller;

import domain.*;
import domain.Carriage.Type;
import dto.DTOMapperTrain;
import dto.TrainDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import repository.Repositories;
import repository.StationRepository;
import domain.Map;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class AssignTrainGUIController implements Initializable {

    @FXML private Label budgetLabel;
    @FXML private ListView<String> trainListView;
    @FXML private VBox routeSection;
    @FXML private ListView<String> routeListView;
    @FXML private ComboBox<String> stationComboBox;
    @FXML private VBox cargoSelectionBox;
    @FXML private VBox cargoCheckBoxContainer;
    @FXML private VBox cargoModeBox;
    @FXML private ComboBox<String> cargoModeComboBox;
    @FXML private Button addStationButton;
    @FXML private Button assignButton;
    @FXML private Button clearRouteButton;
    @FXML private Button cancelButton;
    @FXML private Label messageLabel;

    private Map selectedMap;
    private Scenario selectedScenario;
    private Budget budget;
    private AssignTrainController assignTrainController;

    private List<Train> userTrains;
    private List<Station> availableStations;
    private List<PointOfRoute> route;
    private Train selectedTrain;
    private Station selectedStationForRoute;
    private List<CheckBox> cargoCheckBoxes;

    public void setData(Map selectedMap, Scenario selectedScenario, Budget budget) {
        this.selectedMap = selectedMap;
        this.selectedScenario = selectedScenario;
        this.budget = budget;
        this.assignTrainController = new AssignTrainController();
        this.route = new ArrayList<>();
        this.cargoCheckBoxes = new ArrayList<>();

        initializeUI();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxStyling();
        setupEventHandlers();
    }

    private void initializeUI() {
        budgetLabel.setText("Current Budget: $" + budget.getBudget());

        var currentUser = CurrentUser.getInstance().getUser();
        String playerEmail = currentUser.getEmail();

        availableStations = getPlayerStationsOnMap(playerEmail);
        if (availableStations.size() < 2) {
            showMessage("Error: You need at least 2 stations on this map to assign a train.", true);
            assignButton.setVisible(false);
            return;
        }

        userTrains = getPlayerTrains(playerEmail);
        if (userTrains.isEmpty()) {
            showMessage("Error: You need at least 1 train to assign.", true);
            assignButton.setVisible(false);
            return;
        }

        populateTrainList();
        populateStationComboBox();
        setupCargoCheckBoxes();
    }

    private void setupComboBoxStyling() {
        // Style the station ComboBox
        setupStationComboBox();
        // Style the cargo mode ComboBox
        setupCargoModeComboBox();
    }

    private void setupStationComboBox() {
        // Set button cell (what shows when closed)
        stationComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
                setTextFill(javafx.scene.paint.Color.WHITE);
                setStyle("-fx-background-color: transparent;");
            }
        });

        // Set cell factory (dropdown items)
        stationComboBox.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #2A2A3E; -fx-text-fill: white;");
                }
                setTextFill(javafx.scene.paint.Color.WHITE);
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                if (selected) {
                    setStyle("-fx-background-color: #404040; -fx-text-fill: white;");
                } else {
                    if (getItem() != null) {
                        setStyle("-fx-background-color: #2A2A3E; -fx-text-fill: white;");
                    }
                }
            }
        });
    }

    private void setupCargoModeComboBox() {
        cargoModeComboBox.getItems().addAll(
                "FULL - Wait until all selected carriages are full",
                "HALF - Wait until half of selected carriages are full",
                "AVAILABLE - Take all available cargo and move on"
        );
        cargoModeComboBox.setValue("AVAILABLE - Take all available cargo and move on");

        // Set button cell (what shows when closed)
        cargoModeComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
                setTextFill(javafx.scene.paint.Color.WHITE);
                setStyle("-fx-background-color: transparent;");
            }
        });

        // Set cell factory (dropdown items)
        cargoModeComboBox.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #2A2A3E; -fx-text-fill: white;");
                }
                setTextFill(javafx.scene.paint.Color.WHITE);
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                if (selected) {
                    setStyle("-fx-background-color: #404040; -fx-text-fill: white;");
                } else {
                    if (getItem() != null) {
                        setStyle("-fx-background-color: #2A2A3E; -fx-text-fill: white;");
                    }
                }
            }
        });
    }

    private void setupEventHandlers() {
        trainListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                handleTrainSelection();
            }
        });

        stationComboBox.setOnAction(e -> handleStationSelection());
    }

    private void populateTrainList() {
        trainListView.getItems().clear();

        for (int i = 0; i < userTrains.size(); i++) {
            Train train = userTrains.get(i);
            TrainDTO dto = DTOMapperTrain.toDTO(train);

            StringBuilder trainInfo = new StringBuilder();
            trainInfo.append(String.format("[%d] Locomotive: %s, Total Carriages: %d",
                    i, dto.getLocomotiveModel(), dto.getTotalCarriages()));

            List<String> types = dto.getCarriageTypes();
            List<Integer> counts = dto.getCarriageCounts();

            for (int j = 0; j < types.size(); j++) {
                int count = counts.get(j);
                if (count > 0) {
                    trainInfo.append(String.format("\n     - %s: %d", types.get(j), count));
                }
            }

            trainListView.getItems().add(trainInfo.toString());
        }
    }

    private void populateStationComboBox() {
        stationComboBox.getItems().clear();

        for (int i = 0; i < availableStations.size(); i++) {
            Station station = availableStations.get(i);
            String stationInfo = String.format("[%d] %s at (x=%d, y=%d)",
                    i, station.getName(), station.getX(), station.getY());
            stationComboBox.getItems().add(stationInfo);
        }
    }

    private void setupCargoCheckBoxes() {
        cargoCheckBoxContainer.getChildren().clear();
        cargoCheckBoxes.clear();

        String[] cargoTypes = {"PEOPLE", "MAIL", "COAL", "IRON", "STEEL"};

        for (String cargoType : cargoTypes) {
            CheckBox checkBox = new CheckBox(cargoType);
            checkBox.setTextFill(javafx.scene.paint.Color.WHITE);
            checkBox.setStyle("-fx-text-fill: white;");
            cargoCheckBoxes.add(checkBox);
            cargoCheckBoxContainer.getChildren().add(checkBox);
        }
    }

    private void handleTrainSelection() {
        int selectedIndex = trainListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < userTrains.size()) {
            selectedTrain = userTrains.get(selectedIndex);
            routeSection.setVisible(true);
            showMessage("Train selected. Now create a route by adding stations.", false);
        }
    }

    private void handleStationSelection() {
        String selectedItem = stationComboBox.getValue();
        if (selectedItem != null && !selectedItem.trim().isEmpty()) {
            try {
                int startIndex = selectedItem.indexOf('[') + 1;
                int endIndex = selectedItem.indexOf(']');

                if (startIndex > 0 && endIndex > startIndex) {
                    int stationIndex = Integer.parseInt(selectedItem.substring(startIndex, endIndex));

                    if (stationIndex >= 0 && stationIndex < availableStations.size()) {
                        selectedStationForRoute = availableStations.get(stationIndex);
                        cargoSelectionBox.setVisible(true);
                        cargoModeBox.setVisible(true);
                        addStationButton.setVisible(true);
                    } else {
                        selectedStationForRoute = null;
                        showMessage("Invalid station selection.", true);
                    }
                }
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                selectedStationForRoute = null;
                showMessage("Error parsing station selection.", true);
            }
        } else {
            selectedStationForRoute = null;
            cargoSelectionBox.setVisible(false);
            cargoModeBox.setVisible(false);
            addStationButton.setVisible(false);
        }
    }

    @FXML
    private void handleAddStation() {
        if (selectedStationForRoute == null) {
            showMessage("Please select a station first.", true);
            return;
        }

        try {
            List<Type> selectedCargoTypes = new ArrayList<>();
            for (int i = 0; i < cargoCheckBoxes.size(); i++) {
                if (cargoCheckBoxes.get(i).isSelected()) {
                    switch (i) {
                        case 0: selectedCargoTypes.add(Type.PEOPLE); break;
                        case 1: selectedCargoTypes.add(Type.MAIL); break;
                        case 2: selectedCargoTypes.add(Type.COAL); break;
                        case 3: selectedCargoTypes.add(Type.IRON); break;
                        case 4: selectedCargoTypes.add(Type.STEEL); break;
                    }
                }
            }

            if (selectedCargoTypes.isEmpty()) {
                showMessage("Please select at least one cargo type.", true);
                return;
            }

            CargoMode cargoMode = getSelectedCargoMode();

            PointOfRoute point = new PointOfRoute(selectedStationForRoute, selectedCargoTypes, cargoMode);
            route.add(point);
            updateRouteDisplay();
            String stationName = selectedStationForRoute.getName();
            availableStations.remove(selectedStationForRoute);
            stationComboBox.getSelectionModel().clearSelection();
            stationComboBox.setValue(null);
            populateStationComboBox();
            resetStationSelection();

            if (route.size() >= 2) {
                assignButton.setVisible(true);
                clearRouteButton.setVisible(true);
            }

            showMessage(String.format("Station '%s' added to route. Route now has %d points.",
                    stationName, route.size()), false);

        } catch (Exception e) {
            System.err.println("Error in handleAddStation: " + e.getMessage());
            e.printStackTrace();
            showMessage("Error adding station: " + e.getMessage(), true);
        }
    }

    private void updateRouteDisplay() {
        routeListView.getItems().clear();

        for (int i = 0; i < route.size(); i++) {
            PointOfRoute point = route.get(i);
            StringBuilder pointInfo = new StringBuilder();
            pointInfo.append(String.format("%d. %s at (x=%d, y=%d)",
                    i + 1, point.getStation().getName(),
                    point.getStation().getX(), point.getStation().getY()));

            pointInfo.append("\n   Cargo: ");
            List<String> cargoNames = point.getCargoTypes().stream()
                    .map(Type::toString)
                    .collect(Collectors.toList());
            pointInfo.append(String.join(", ", cargoNames));

            pointInfo.append("\n   Mode: ").append(point.getCargoMode().toString());

            routeListView.getItems().add(pointInfo.toString());
        }
    }

    private CargoMode getSelectedCargoMode() {
        String selected = cargoModeComboBox.getValue();
        if (selected == null) return CargoMode.AVAILABLE;

        if (selected.startsWith("FULL")) return CargoMode.FULL;
        if (selected.startsWith("HALF")) return CargoMode.HALF;
        return CargoMode.AVAILABLE;
    }

    private void resetStationSelection() {
        selectedStationForRoute = null;
        stationComboBox.getSelectionModel().clearSelection();
        stationComboBox.setValue(null);

        cargoSelectionBox.setVisible(false);
        cargoModeBox.setVisible(false);
        addStationButton.setVisible(false);

        for (CheckBox checkBox : cargoCheckBoxes) {
            checkBox.setSelected(false);
        }

        cargoModeComboBox.setValue("AVAILABLE - Take all available cargo and move on");
    }

    @FXML
    private void handleAssignTrain() {
        if (selectedTrain == null) {
            showMessage("Please select a train first.", true);
            return;
        }

        if (route.size() < 2) {
            showMessage("Route must have at least 2 stations.", true);
            fillRouteWithDefaultStations();
        }

        try {
            assignTrainController.assignExistingTrainToRoute(
                    selectedTrain, route, selectedScenario, budget);

            showMessage("Train assigned to route successfully!", false);

            javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(2000);
                    return null;
                }
            };

            task.setOnSucceeded(e -> handleCancel());
            new Thread(task).start();

        } catch (Exception e) {
            showMessage("Error assigning train: " + e.getMessage(), true);
        }
    }

    private void fillRouteWithDefaultStations() {
        while (route.size() < 2 && !availableStations.isEmpty()) {
            Station station = availableStations.remove(0);
            List<Type> defaultCargo = Arrays.asList(Type.PEOPLE, Type.MAIL);
            PointOfRoute point = new PointOfRoute(station, defaultCargo, CargoMode.AVAILABLE);
            route.add(point);
        }
        updateRouteDisplay();
    }

    @FXML
    private void handleClearRoute() {
        route.clear();

        var currentUser = CurrentUser.getInstance().getUser();
        availableStations = getPlayerStationsOnMap(currentUser.getEmail());

        populateStationComboBox();
        updateRouteDisplay();
        resetStationSelection();

        assignButton.setVisible(false);
        clearRouteButton.setVisible(false);

        showMessage("Route cleared.", false);
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        if (isError) {
            messageLabel.setTextFill(javafx.scene.paint.Color.web("#FF6B6B"));
        } else {
            messageLabel.setTextFill(javafx.scene.paint.Color.web("#90EE90"));
        }
    }

    private List<Train> getPlayerTrains(String playerEmail) {
        return Repositories.getInstance().getTrainRepository().getTrains().stream()
                .filter(train -> train.getUserEmail().equals(playerEmail))
                .collect(Collectors.toList());
    }

    private List<Station> getPlayerStationsOnMap(String playerEmail) {
        StationRepository stationRepo = Repositories.getInstance().getStationRepository();
        return stationRepo.getStations().stream()
                .filter(s -> s.getMapName().equals(selectedMap.getMapName()))
                .filter(s -> s.getScenarioName().equals(selectedScenario.getScenarioName()))
                .filter(s -> s.getOwnerPlayer().equals(playerEmail))
                .collect(Collectors.toList());
    }
}