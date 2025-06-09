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

    public void initialize(Map map, Scenario scenario, Budget budget) {
        this.selectedMap = map;
        this.selectedScenario = scenario;
        this.budget = budget;
        this.upgradeController = new UpgradeStationTypeController(map, scenario, budget);

        updateBudgetLabel();
        loadOwnedStations();
        setupOrientationComboBox();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageLabel.setText("");
    }

    private void updateBudgetLabel() {
        budgetLabel.setText("Current Budget: " + budget.getBudget() + "$");
    }

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

    private void setupOrientationComboBox() {
        orientationComboBox.getItems().addAll("NW", "NE", "SW", "SE");
    }

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

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

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