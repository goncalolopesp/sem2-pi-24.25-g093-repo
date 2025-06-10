package controller;

import domain.*;
import domain.Station.Orientation;
import domain.Station.Type;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import repository.Repositories;
import repository.StationRepository;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * JavaFX Controller for the Upgrade Station Type UI.
 * Handles the user interface for upgrading station types.
 */
public class UpgradeStationTypeUIController implements Initializable {

    @FXML private Label budgetLabel;
    @FXML private ListView<String> stationListView;
    @FXML private VBox selectedStationBox;
    @FXML private Label selectedStationLabel;
    @FXML private VBox upgradeOptionsBox;
    @FXML private ComboBox<String> upgradeOptionComboBox;
    @FXML private VBox orientationBox;
    @FXML private ComboBox<String> orientationComboBox;
    @FXML private Label costLabel;
    @FXML private Label capacityLabel;
    @FXML private Button upgradeButton;
    @FXML private Button cancelButton;
    @FXML private Label messageLabel;

    private Map selectedMap;
    private Scenario selectedScenario;
    private Budget budget;
    private Station selectedStation;
    private List<Station> ownedStations;
    private UpgradeStationTypeController upgradeController;

    /**
     * Initializes the UI controller with the specified map, scenario, and budget.
     * This method sets up the necessary data and UI components for managing station upgrades.
     *
     * @param map      the map to be used for the current context
     * @param scenario the scenario associated with the current map
     * @param budget   the budget allocated for station upgrades
     */
    public void initialize(Map map, Scenario scenario, Budget budget) {
        this.selectedMap = map;
        this.selectedScenario = scenario;
        this.budget = budget;
        this.upgradeController = new UpgradeStationTypeController(map, scenario, budget);

        updateBudgetLabel();
        loadOwnedStations();
        setupOrientationComboBox();
    }

    /**
     * Initializes the controller class with the specified location and resources.
     * Clears the message label text to prepare the user interface for interaction.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if no localization is required.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageLabel.setText("");
    }

    /**
     * Updates the label displaying the current budget information.
     * Retrieves the current budget value from the associated budget object and sets it as the label's text.
     * The label displays the budget in the format "Current Budget: [amount]$".
     */
    private void updateBudgetLabel() {
        budgetLabel.setText("Current Budget: " + budget.getBudget() + "$");
    }

    /**
     * Loads the stations owned by the current user for the selected map and scenario.
     *
     * This method retrieves all available stations from the StationRepository, filters them
     * based on the owner (currently logged-in user), selected map, and scenario, and populates
     * the ownedStations list with the matching stations. It then updates the stationListView
     * UI component by displaying the filtered stations or a message indicating that no stations
     * are available for upgrade.
     *
     * If no stations are owned by the user for the current context, the stationListView is
     * disabled, and a message is displayed to inform the user.
     */
    private void loadOwnedStations() {
        StationRepository repo = Repositories.getInstance().getStationRepository();
        ownedStations = repo.getStations().stream()
                .filter(s -> s.getOwnerPlayer().equals(CurrentUser.getInstance().getUser().getEmail()))
                .filter(s -> s.getScenarioName().equals(selectedScenario.getScenarioName()))
                .filter(s -> s.getMapName().equals(selectedMap.getMapName()))
                .collect(Collectors.toList());

        if (ownedStations.isEmpty()) {
            stationListView.getItems().add("No stations available for upgrade");
            stationListView.setDisable(true);
            messageLabel.setText("You have no stations on this map to upgrade.");
            return;
        }

        for (Station station : ownedStations) {
            String displayText = station.getName() +
                    " (center: " + station.getX() + "," + station.getY() +
                    ") - type: " + station.getType();
            stationListView.getItems().add(displayText);
        }
    }

    /**
     * Configures the orientationComboBox by populating it with predefined orientation options.
     *
     * This method adds the following orientation values to the orientationComboBox:
     * "NW", "NE", "SW", "SE".
     * These values represent potential layout or directional configurations for a station.
     */
    private void setupOrientationComboBox() {
        orientationComboBox.getItems().addAll("NW", "NE", "SW", "SE");
    }

    /**
     * Handles the event when a station is selected from the station list in the UI.
     *
     * This method retrieves the selected station from the list view based on the user's selection.
     * It performs the following steps:
     * 1. Checks if the selected index is valid and within bounds of the ownedStations list.
     * 2. If valid, retrieves the corresponding station object and updates the selected station field.
     * 3. Updates the UI components to display details of the selected station, including its name,
     *    coordinates, and type.
     * 4. Makes the UI elements for the selected station visible.
     * 5. Calls {@link #setupUpgradeOptions()} to configure available upgrade options for the station.
     * 6. Clears any previous message displayed in the message label.
     */
    @FXML
    private void onStationSelected() {
        int selectedIndex = stationListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= ownedStations.size()) {
            return;
        }

        selectedStation = ownedStations.get(selectedIndex);
        selectedStationLabel.setText(selectedStation.getName() +
                " at (" + selectedStation.getX() + "," + selectedStation.getY() +
                ") - Current type: " + selectedStation.getType());

        selectedStationBox.setVisible(true);
        setupUpgradeOptions();
        messageLabel.setText("");
    }

    /**
     * Sets up the available upgrade options for the selected station based on its current type.
     *
     * This method configures the upgradeOptionComboBox with the appropriate upgrade options by checking
     * the type of the selected station. Depending on the station type, different upgrade options are
     * populated:
     * - DEPOT: Options to upgrade to STATION or TERMINAL are added.
     * - STATION: Option to upgrade to TERMINAL is added.
     * - TERMINAL: A message indicating no further upgrades is displayed, and the upgrade options
     *   box is hidden.
     * - For any unknown type, a message is displayed, and the upgrade options box is hidden.
     *
     * After configuring the options, the upgrade options box is made visible, and the upgrade button
     * is initially disabled.
     */
    private void setupUpgradeOptions() {
        upgradeOptionComboBox.getItems().clear();
        Type currentType = selectedStation.getType();

        switch (currentType) {
            case DEPOT:
                upgradeOptionComboBox.getItems().addAll(
                        "STATION - cost: $50",
                        "TERMINAL - cost: $150"
                );
                break;
            case STATION:
                upgradeOptionComboBox.getItems().addAll(
                        "TERMINAL - cost: $100"
                );
                break;
            case TERMINAL:
                messageLabel.setText("This station is already a TERMINAL. No further upgrades available.");
                upgradeOptionsBox.setVisible(false);
                return;
            default:
                messageLabel.setText("Unknown station type. No upgrades available.");
                upgradeOptionsBox.setVisible(false);
                return;
        }

        upgradeOptionsBox.setVisible(true);
        upgradeButton.setDisable(true);
    }

    /**
     * Handles changes in the selected upgrade option for a station in the UI.
     *
     * This method performs the following steps:
     * 1. Retrieves the selected upgrade option from the `upgradeOptionComboBox`.
     * 2. Parses the selected option to extract the target station type and associated cost.
     * 3. Updates the visibility and interaction states of UI components based on the target type:
     *    - If the target type is `STATION`, the orientation selection UI is made visible,
     *      the orientation combo box value is reset, and the upgrade button is disabled.
     *    - For all other target types, the orientation selection UI is hidden, and the upgrade button is enabled.
     * 4. Updates the cost label to display the required cost for the upgrade.
     * 5. Calculates and displays the new capacity of the station based on the target type:
     *    - DEPOT: Capacity of 100.
     *    - STATION: Capacity of 200.
     *    - TERMINAL: Capacity of 400.
     * 6. Checks if the current budget is sufficient for the upgrade:
     *    - If the budget is insufficient*/
    @FXML
    private void onUpgradeOptionChanged() {
        String selectedOption = upgradeOptionComboBox.getValue();
        if (selectedOption == null) return;

        String[] parts = selectedOption.split(" - cost: \\$");
        String targetTypeStr = parts[0];
        int cost = Integer.parseInt(parts[1]);

        Type targetType = Type.valueOf(targetTypeStr);
        if (targetType == Type.STATION) {
            orientationBox.setVisible(true);
            orientationComboBox.setValue(null);
            upgradeButton.setDisable(true);
        } else {
            orientationBox.setVisible(false);
            upgradeButton.setDisable(false);
        }
        costLabel.setText("$" + cost);

        int newCapacity;
        switch (targetType) {
            case DEPOT: newCapacity = 100; break;
            case STATION: newCapacity = 200; break;
            case TERMINAL: newCapacity = 400; break;
            default: newCapacity = 0;
        }
        capacityLabel.setText(String.valueOf(newCapacity));
        if (budget.getBudget() < cost) {
            messageLabel.setText("Insufficient budget. Required: $" + cost + ", Available: $" + budget.getBudget());
            upgradeButton.setDisable(true);
        } else {
            messageLabel.setText("");
        }
    }

    /**
     * Handles the event when the "Upgrade Station" button is clicked in the UI.
     *
     * This method performs the following operations:
     *
     * 1. Validates if a station is selected:
     *    - If no station is selected, displays a message asking the user to select a station.
     *
     * 2. Checks if a valid upgrade option is selected:
     *    - If no upgrade option is chosen, displays a message prompting the user to select an upgrade option.
     *
     * 3. Parses the selected upgrade option:
     *    - Extracts the target station type and the upgrade cost from the selected option.
     *    - Converts the target type string and cost into their respective formats.
     *
     * 4. Determines the required orientation if the target type is `STATION`:
     *    - Checks if an orientation is selected.
     *    - If no orientation is selected, displays a message prompting the user to select one.
     *    - For other types, retains the orientation of the selected station.
     *
     * 5. Checks if the current budget is sufficient for the upgrade:
     *    - If the budget is insufficient, displays an error message showing the required and*/
    @FXML
    private void handleUpgradeStation() {
        if (selectedStation == null) {
            messageLabel.setText("Please select a station first.");
            return;
        }

        String selectedOption = upgradeOptionComboBox.getValue();
        if (selectedOption == null) {
            messageLabel.setText("Please select an upgrade option.");
            return;
        }

        String[] parts = selectedOption.split(" - cost: \\$");
        String targetTypeStr = parts[0];
        int cost = Integer.parseInt(parts[1]);

        Type targetType = Type.valueOf(targetTypeStr);
        Orientation orientation;

        if (targetType == Type.STATION) {
            String orientationStr = orientationComboBox.getValue();
            if (orientationStr == null) {
                messageLabel.setText("Please select an orientation for the STATION upgrade.");
                return;
            }
            orientation = Orientation.valueOf(orientationStr);
        } else {
            orientation = selectedStation.getOrientation();
        }
        if (budget.getBudget() < cost) {
            messageLabel.setText("Insufficient budget. Required: $" + cost + ", Available: $" + budget.getBudget());
            return;
        }
        boolean success = upgradeController.upgradeType(selectedStation, targetType, orientation, budget, cost);

        if (success) {
            messageLabel.setText("Upgrade to " + targetType + " completed successfully!");
            messageLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            updateBudgetLabel();
            stationListView.getItems().clear();
            selectedStationBox.setVisible(false);
            upgradeOptionsBox.setVisible(false);
            orientationBox.setVisible(false);
            loadOwnedStations();
        } else {
            messageLabel.setText("Upgrade failed. Overbuild is not Allowed.");
            messageLabel.setTextFill(javafx.scene.paint.Color.web("#FF6B6B"));
        }
    }

    /**
     * Handles the event when the "Cancel" button is clicked in the UI.
     *
     * This method performs the following operations:
     * 1. Retrieves the current stage associated with the cancel button.
     * 2. Closes the stage, effectively closing the current window.
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the event triggered when the station orientation is changed in the UI.
     *
     * This method performs the following operations:
     * 1. Retrieves the selected orientation from the `orientationComboBox`.
     * 2. Retrieves the selected upgrade option from the `upgradeOptionComboBox`.
     * 3. Validates if both orientation and upgrade option are selected:
     *    - If both are selected:
     *        a. Parses the selected upgrade option to extract its associated cost.
     *        b. Checks if the current budget is sufficient for the upgrade:
     *           - If the budget is sufficient, enables the "Upgrade" button and clears the message label.
     *           - If the budget is insufficient, disables the "Upgrade" button and displays an error message showing
     *             the required cost and the available budget.
     *    - If either is not selected, disables the "Upgrade" button.
     */
    @FXML
    private void onOrientationChanged() {
        String selectedOrientation = orientationComboBox.getValue();
        String selectedOption = upgradeOptionComboBox.getValue();

        if (selectedOrientation != null && selectedOption != null) {
            String[] parts = selectedOption.split(" - cost: \\$");
            int cost = Integer.parseInt(parts[1]);

            if (budget.getBudget() >= cost) {
                upgradeButton.setDisable(false);
                messageLabel.setText("");
            } else {
                upgradeButton.setDisable(true);
                messageLabel.setText("Insufficient budget. Required: $" + cost + ", Available: $" + budget.getBudget());
            }
        } else {
            upgradeButton.setDisable(true);
        }
    }
}