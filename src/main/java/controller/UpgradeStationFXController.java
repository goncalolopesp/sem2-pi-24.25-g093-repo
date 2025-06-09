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
 * The UpgradeStationFXController class is responsible for managing the user interface and
 * functionalities related to upgrading user-owned stations in the application. This includes
 * displaying stations, managing available upgrades, handling budget updates, and processing
 * user interactions to apply upgrades.
 *
 * The controller works in conjunction with JavaFX UI components, synchronizing data and
 * operations to ensure a smooth user experience. It also interacts with data models such as
 * the map, scenario, budget, and time period to provide accurate and contextual upgrade options.
 *
 * Responsibilities:
 * - Displaying a list of user-owned stations and their details.
 * - Calculating and displaying available building upgrades.
 * - Checking budget availability and updating the budget post-upgrade.
 * - Managing user interactions with the upgrade process.
 * - Handling errors and providing feedback messages to the user.
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

    /**
     * Constructs an instance of UpgradeStationFXController to manage and display
     * upgrade-related functionalities for user-owned stations in the application.
     *
     * @param selectedMap the map where the stations are located
     * @param selectedScenario the scenario currently in use
     * @param timePeriod an atomic reference tracking time period data
     * @param budget the budget available for upgrades
     */
    public UpgradeStationFXController(Map selectedMap, Scenario selectedScenario,
                                      AtomicReference<AtomicIntegerArray> timePeriod, Budget budget) {
        this.selectedMap = selectedMap;
        this.selectedScenario = selectedScenario;
        this.timePeriod = timePeriod;
        this.budget = budget;

        initializeBuildingData();
    }

    /**
     * Initializes the controller by setting up the UI components, loading user-owned stations,
     * and updating the budget display.
     *
     * @param location The location used to resolve relative paths for the root object, or null if it is not specified.
     * @param resources The resources used to localize the root object, or null if no resources are specified.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadStations();
        updateBudgetDisplay();
    }

    /**
     * Initializes building-related data, including costs, capacities, and display-to-internal
     * mappings for various building types utilized in the application. This setup is used to
     * manage and display information about upgrades and available buildings.
     *
     * The method performs the following key functions:
     * 1. Populates the buildingCosts map with the cost of each building type.
     * 2. Assigns capacities to each building type and populates the buildingCapacities map.
     * 3. Maps user-friendly display names to internal identifiers for building types
     *    and populates the displayToInternal map.
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
     * Configures the user interface components for the UpgradeStationFXController.
     *
     * This method performs the following actions:
     * 1. Configures the station list view to allow single selection only.
     * 2. Initially hides the UI elements related to station upgrade options,
     *    including the selected station box and upgrade options box.
     * 3. Disables the upgrade button.
     * 4. Clears any initial messages displayed in the message label.
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
     * Loads and filters the stations owned by the current user for the selected map and scenario,
     * and updates the station list view with user-owned stations.
     *
     * The method performs the following steps:
     * 1. Retrieves the station repository from the singleton instance of Repositories.
     * 2. Filters the stations based on the following criteria:
     *    - Owned by the current user (matching the user's email address).
     *    - Belonging to the currently selected scenario.
     *    - Located on the currently selected map.
     * 3. Collects the filtered stations into the `ownedStations` list.
     * 4. If no stations are found, sets an appropriate message in the message label
     *    and exits the method.
     * 5. Creates a formatted display name for each station in the filtered list
     *    and populates the station list view with these names.
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
     * Updates the budget display in the user interface with the current budget value.
     *
     * This method ensures that the budget label is updated on the JavaFX Application Thread
     * by using the Platform.runLater method. The updated text is formatted as
     * "Current Budget: $\<budget_amount\>" with a comma as the thousand separator.
     *
     * Responsibilities:
     * - Accesses the budget object to retrieve the current budget value.
     * - Uses the budget value to format the text for the budgetLabel UI component.
     * - Ensures that updates to the UI are performed on the JavaFX Application Thread
     *   to avoid thread conflicts.
     */
    private void updateBudgetDisplay() {
        Platform.runLater(() -> {
            budgetLabel.setText(String.format("Current Budget: $%,d", budget.getBudget()));
        });
    }

    /**
     * Handles the event triggered when a station is selected from the station list view.
     * Updates the selected station information, displays details of the selected station,
     * and loads available upgrade options for it.
     *
     * @param event the MouseEvent triggered by the user's interaction with the station list view.
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
     * Displays detailed information about the currently selected station
     * in the user interface. If a station is selected, its name, location,
     * and type are formatted into a string and shown in the UI component
     * designated for station details. Additionally, the visibility of the
     * selected station box is updated to make the details visible.
     *
     * Functionality includes:
     * - Retrieving the name, coordinates, and type of the selected station.
     * - Formatting the station details into a readable string.
     * - Updating the corresponding label to display the formatted details.
     * - Ensuring the selected station details box is visible.
     *
     * If no station is selected, the method performs no action.
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
     * Loads and configures the available upgrade options for the currently selected station.
     *
     * This method filters the possible upgrade options based on several conditions and
     * populates the UI components for upgrade*/
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
     * Handles the event triggered when the selected upgrade option changes in the UI.
     *
     * This method performs the following actions:
     * 1. Retrieves the user-selected upgrade option from the `upgradeOptionComboBox`.
     * 2. Maps the selected display-friendly upgrade option to its internal identifier
     *    using the `displayToInternal` map and sets the `selectedBuildingType`.
     * 3. Updates the cost and capacity labels in the UI by calling `updateCostAndCapacity()`.
     * 4. Enables the upgrade button if an upgrade option is selected; otherwise, disables it.
     *
     * If no valid upgrade option is selected, ensures that the upgrade button is disabled.
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
     * Updates the UI components with the cost and capacity of the selected building type
     * and modifies the state of the upgrade button based on the user's budget.
     *
     * Functionality:
     * - Retrieves the cost and capacity of the `selectedBuildingType` from the `buildingCosts`
     *   and `buildingCapacities` maps, respectively.
     * - Updates the `costLabel` and `capacityLabel` in the UI to display the cost
     *   and capacity values.
     * - Compares the user's current budget with the cost of the selected building type
     *   to determine whether the upgrade is affordable:
     *   - If the budget is insufficient, changes the style of `costLabel` to indicate
     *     an error and displays a message in the `messageLabel`.
     *   - If the budget is sufficient, reverts `costLabel` to a valid state, removes
     *     any error messages, and enables the `upgradeButton`.
     *
     * Behavior is conditional on the presence of a valid `selectedBuildingType`.
     * If no type is selected, the method takes no action.
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
     * Handles the process of upgrading a station by validating user selections,
     * checking the budget, and applying the upgrade if all conditions are met.
     *
     * This method performs the following steps:
     * 1. Validates the selected station and building type to ensure they are not null.
     * 2. Checks whether the current budget is sufficient for the upgrade cost.
     * 3. Retrieves the list of existing buildings for the selected station.
     * 4. Invokes the upgrade operation on the selected station and building type.
     * 5. Updates the budget, applies mutual exclusivity rules for buildings, and refreshes
     *    the user interface components if the upgrade succeeds.
     * 6. Displays appropriate messages to the user based on the success or failure of the upgrade operation.
     *
     * Preconditions:
     * - The `selectedStation` and `selectedBuildingType` must have valid values.
     * - The `budget` and `buildingCosts` must be properly initialized and synchronized with the UI.
     *
     * Postconditions:
     * - Budget is decremented by the upgrade cost if the upgrade is successful.
     * - The display of the budget, upgrade options, and related UI components is refreshed.
     * - Selection for upgrade options is reset after the operation completes.
     *
     * Error Handling:
     * - If no station or building type is selected, an error message is displayed.
     * - If insufficient funds are available for the upgrade, an error message is displayed.
     * - If the upgrade operation fails, an appropriate error message is shown, and no state changes are applied.
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
     * Handles the action for the cancel button.
     * This method is triggered when the cancel button is clicked.
     * It retrieves the current stage from the button's scene and closes it.
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Displays a message on the message label with a specific style based on the message type.
     *
     * @param message   the text to display in the message label
     * @param isError   specifies whether the message is an error message (true) or not (false)
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
     * Checks for mutual exclusivity between a new building type and a list of existing buildings.
     * Ensures no duplicate building types exist and handles conflicts for specific building categories.
     *
     * @param existingBuildings a list of existing buildings to check against
     * @param newType           the type of the new building to verify
     */
    private void checkMutualExclusivity(List<Building> existingBuildings, String newType) {
        String newLower = newType.toLowerCase().trim();

        for (Building building : existingBuildings) {
            String existing = building.getBuildingType().toLowerCase().trim();

            if (existing.equals(newLower)) {
                return;
            }

            if ((existing.contains("hotel") && newLower.contains("hotel")) ||
                    (existing.contains("cafe") && newLower.contains("cafe"))) {

                ctrl.getBuildingRepository().remove(building.getStation(), building.getBuildingType());
                return;
            }
        }
    }

    /**
     * Retrieves the UpgradeStationController instance.
     *
     * @return the instance of UpgradeStationController
     */
    public UpgradeStationController getUpgradeStationController() {
        return ctrl;
    }
}