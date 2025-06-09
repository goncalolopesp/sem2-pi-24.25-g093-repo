package controller;

import domain.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import repository.Repositories;
import repository.StationRepository;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * JavaFX Controller for the Upgrade Station UI.
 * Replaces the console-based UpgradeStationUI with a graphical interface.
 */
public class UpgradeStationFXController implements Initializable {

    // FXML Components
    @FXML private Label budgetLabel;
    @FXML private ListView<String> stationListView;
    @FXML private VBox selectedStationBox;
    @FXML private Label selectedStationLabel;
    @FXML private VBox upgradeOptionsBox;
    @FXML private ComboBox<String> upgradeOptionComboBox;
    @FXML private Label costLabel;
    @FXML private Label capacityLabel;
    @FXML private Button upgradeButton;
    @FXML private Button cancelButton;
    @FXML private Label messageLabel;

    // Business Logic Components
    private final UpgradeStationController ctrl = new UpgradeStationController();
    private Station selectedStation;
    private String selectedBuildingType;
    private final Budget budget;
    private final Map selectedMap;
    private final Scenario selectedScenario;
    private final AtomicReference<AtomicIntegerArray> timePeriod;

    // Building costs (same as console version)
    private static final int COST_TELEGRAPH = 250, COST_TELEPHONE = 270;
    private static final int COST_SCAFE = 340, COST_LCAFE = 430;
    private static final int COST_CUSTOMS = 570, COST_POFFICE = 730;
    private static final int COST_SHOTEL = 940, COST_LHOTEL = 1170;
    private static final int COST_SILO = 1450, COST_LSTORAGE = 1750;

    // Building data maps
    private final HashMap<String, Integer> buildingCosts = new HashMap<>();
    private final HashMap<String, Integer> buildingCapacities = new HashMap<>();
    private final HashMap<String, String> displayToInternal = new HashMap<>();

    // Data storage
    private List<Station> ownedStations;

    public UpgradeStationFXController(Map selectedMap, Scenario selectedScenario,
                                      AtomicReference<AtomicIntegerArray> timePeriod, Budget budget) {
        this.selectedMap = selectedMap;
        this.selectedScenario = selectedScenario;
        this.timePeriod = timePeriod;
        this.budget = budget;

        initializeBuildingData();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadStations();
        updateBudgetDisplay();
    }

    /**
     * Initialize building cost and capacity data
     */
    private void initializeBuildingData() {
        // Building costs
        buildingCosts.put("telegraph", COST_TELEGRAPH);
        buildingCosts.put("telephone", COST_TELEPHONE);
        buildingCosts.put("small cafe", COST_SCAFE);
        buildingCosts.put("large cafe", COST_LCAFE);
        buildingCosts.put("customs", COST_CUSTOMS);
        buildingCosts.put("post office", COST_POFFICE);
        buildingCosts.put("small hotel", COST_SHOTEL);
        buildingCosts.put("large hotel", COST_LHOTEL);
        buildingCosts.put("silo", COST_SILO);
        buildingCosts.put("liquid storage", COST_LSTORAGE);

        // Building capacities (you may need to adjust these based on your domain logic)
        buildingCapacities.put("telegraph", 50);
        buildingCapacities.put("telephone", 75);
        buildingCapacities.put("small cafe", 100);
        buildingCapacities.put("large cafe", 200);
        buildingCapacities.put("customs", 150);
        buildingCapacities.put("post office", 120);
        buildingCapacities.put("small hotel", 300);
        buildingCapacities.put("large hotel", 500);
        buildingCapacities.put("silo", 1000);
        buildingCapacities.put("liquid storage", 1200);

        // Display name mappings
        displayToInternal.put("Telegraph ($250) (1855-1875)", "telegraph");
        displayToInternal.put("Telephone ($270) (1876-Present)", "telephone");
        displayToInternal.put("Small Cafe ($340)", "small cafe");
        displayToInternal.put("Large Cafe ($430)", "large cafe");
        displayToInternal.put("Customs ($570)", "customs");
        displayToInternal.put("Post Office ($730)", "post office");
        displayToInternal.put("Small Hotel ($940)", "small hotel");
        displayToInternal.put("Large Hotel ($1170)", "large hotel");
        displayToInternal.put("Silo ($1450)", "silo");
        displayToInternal.put("Liquid Storage ($1750)", "liquid storage");
    }

    /**
     * Setup UI components
     */
    private void setupUI() {
        // Configure ListView
        stationListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Initially hide upgrade options
        selectedStationBox.setVisible(false);
        upgradeOptionsBox.setVisible(false);
        upgradeButton.setDisable(true);

        // Clear initial message
        messageLabel.setText("");
    }

    /**
     * Load user's stations from repository
     */
    private void loadStations() {
        StationRepository repo = Repositories.getInstance().getStationRepository();
        ownedStations = repo.getStations().stream()
                .filter(s -> s.getOwnerPlayer().equals(CurrentUser.getInstance().getUser().getEmail()))
                .filter(s -> s.getScenarioName().equals(selectedScenario.getScenarioName()))
                .filter(s -> s.getMapName().equals(selectedMap.getMapName()))
                .collect(Collectors.toList());

        if (ownedStations.isEmpty()) {
            messageLabel.setText("You have no stations on this map.");
            return;
        }

        ObservableList<String> stationNames = FXCollections.observableArrayList();
        for (Station station : ownedStations) {
            String displayName = String.format("%s (center: %d,%d) - type: %s",
                    station.getName(), station.getX(), station.getY(), station.getType());
            stationNames.add(displayName);
        }

        stationListView.setItems(stationNames);
    }

    /**
     * Update budget display
     */
    private void updateBudgetDisplay() {
        Platform.runLater(() -> {
            budgetLabel.setText(String.format("Current Budget: $%,d", budget.getBudget()));
        });
    }

    /**
     * Handle station selection from ListView
     */
    @FXML
    private void onStationSelected(MouseEvent event) {
        int selectedIndex = stationListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < ownedStations.size()) {
            selectedStation = ownedStations.get(selectedIndex);
            showSelectedStation();
            loadUpgradeOptions();
        }
    }

    /**
     * Show selected station information
     */
    private void showSelectedStation() {
        if (selectedStation != null) {
            String stationInfo = String.format(
                    "Name: %s\nLocation: (%d, %d)\nType: %s",
                    selectedStation.getName(),
                    selectedStation.getX(),
                    selectedStation.getY(),
                    selectedStation.getType()
            );
            selectedStationLabel.setText(stationInfo);
            selectedStationBox.setVisible(true);
        }
    }

    /**
     * Load available upgrade options for selected station
     */
    private void loadUpgradeOptions() {
        if (selectedStation == null) return;

        ObservableList<String> options = FXCollections.observableArrayList();
        int currentYear = timePeriod.get().get(0);

        // Check each building type
        for (String displayName : displayToInternal.keySet()) {
            String buildingType = displayToInternal.get(displayName);

            // Check if building already exists
            if (ctrl.verifyIfBuildingExists(selectedStation, buildingType)) {
                continue;
            }

            // Check year restrictions
            if (buildingType.equals("telegraph") && (currentYear < 1855 || currentYear > 1875)) {
                continue;
            }
            if (buildingType.equals("telephone") && currentYear < 1876) {
                continue;
            }

            options.add(displayName);
        }

        upgradeOptionComboBox.setItems(options);
        upgradeOptionsBox.setVisible(true);

        if (options.isEmpty()) {
            messageLabel.setText("No upgrade options available for this station.");
        } else {
            messageLabel.setText("");
        }
    }

    /**
     * Handle upgrade option selection
     */
    @FXML
    private void onUpgradeOptionChanged() {
        String selectedDisplay = upgradeOptionComboBox.getValue();
        if (selectedDisplay != null) {
            selectedBuildingType = displayToInternal.get(selectedDisplay);
            updateCostAndCapacity();
            upgradeButton.setDisable(false);
        } else {
            upgradeButton.setDisable(true);
        }
    }

    /**
     * Update cost and capacity labels
     */
    private void updateCostAndCapacity() {
        if (selectedBuildingType != null) {
            int cost = buildingCosts.getOrDefault(selectedBuildingType, 0);
            int capacity = buildingCapacities.getOrDefault(selectedBuildingType, 0);

            costLabel.setText(String.format("$%,d", cost));
            capacityLabel.setText(String.valueOf(capacity));

            // Check if user can afford it
            if (budget.getBudget() < cost) {
                costLabel.setStyle("-fx-text-fill: #FF6B6B;");
                messageLabel.setText("Insufficient funds for this upgrade.");
                upgradeButton.setDisable(true);
            } else {
                costLabel.setStyle("-fx-text-fill: #FFD700;");
                messageLabel.setText("");
                upgradeButton.setDisable(false);
            }
        }
    }

    /**
     * Handle upgrade station button click
     */
    @FXML
    private void handleUpgradeStation() {
        if (selectedStation == null || selectedBuildingType == null) {
            showMessage("Please select a station and upgrade option.", true);
            return;
        }

        int cost = buildingCosts.getOrDefault(selectedBuildingType, 0);
        if (budget.getBudget() < cost) {
            showMessage("Insufficient funds for this upgrade.", true);
            return;
        }

        List<Building> buildingList = Repositories.getInstance().getBuildingRepository().getBuildings().stream().filter(b -> b.getStation().equals(selectedStation)).collect(Collectors.toList());

        // Perform the upgrade
        Optional<Building> building = Optional.ofNullable(
                ctrl.upgradeStation(selectedStation, selectedBuildingType)
        );

        if (building.isPresent()) {
            // Deduct cost from budget
            budget.subtractBudget(cost);
            updateBudgetDisplay();

            checkMutualExclusivity(buildingList, selectedBuildingType);

            showMessage("Building successfully created!", false);

            // Refresh the upgrade options
            loadUpgradeOptions();

            // Reset selection
            upgradeOptionComboBox.setValue(null);
            upgradeButton.setDisable(true);
            costLabel.setText("$0");
            capacityLabel.setText("0");
        } else {
            showMessage("Building creation failed. Please try again.", true);
        }
    }

    /**
     * Handle cancel button click
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Show message to user
     */
    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        if (isError) {
            messageLabel.setStyle("-fx-text-fill: #FF6B6B;");
        } else {
            messageLabel.setStyle("-fx-text-fill: #90EE90;");
        }
    }

    /**
     * Check mutual exclusivity for display purposes
     */
    private boolean checkMutualExclusivityForDisplay(String newType) {
        List<Building> existingBuildings = ctrl.getBuildings();
        String newLower = newType.toLowerCase().trim();

        for (Building building : existingBuildings) {
            if (!building.getStation().equals(selectedStation)) {
                continue;
            }

            String existing = building.getBuildingType().toLowerCase().trim();

            if (existing.equals(newLower)) {
                return false;
            }

            if ((existing.contains("hotel") && newLower.contains("hotel")) ||
                    (existing.contains("cafe") && newLower.contains("cafe"))) {
                return false; // Would conflict
            }
        }
        return true;
    }

    /**
     * Check mutual exclusivity and handle removal (same as console version)
     */
    private boolean checkMutualExclusivity(List<Building> existingBuildings, String newType) {
        String newLower = newType.toLowerCase().trim();

        for (Building building : existingBuildings) {
            String existing = building.getBuildingType().toLowerCase().trim();

            if (existing.equals(newLower)) {
                return false;
            }

            if ((existing.contains("hotel") && newLower.contains("hotel")) ||
                    (existing.contains("cafe") && newLower.contains("cafe"))) {

                ctrl.getBuildingRepository().remove(building.getStation(), building.getBuildingType());
                return true;
            }
        }
        return false;
    }

    /**
     * Get the controller (for external access)
     */
    public UpgradeStationController getUpgradeStationController() {
        return ctrl;
    }
}