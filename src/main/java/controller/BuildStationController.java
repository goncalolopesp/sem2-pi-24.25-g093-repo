package controller;

import controller.CreateStationController;
import controller.CreateStationController.Error;
import domain.*;
import domain.Station.Orientation;
import dto.StationDTO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX Controller for building stations.
 * Replaces the console-based BuildStationUI with a graphical interface.
 */
public class BuildStationController implements Initializable {

    @FXML private ComboBox<StationType> stationTypeComboBox;
    @FXML private TextField xCoordinateField;
    @FXML private TextField yCoordinateField;
    @FXML private VBox orientationBox;
    @FXML private ComboBox<Orientation> orientationComboBox;
    @FXML private Button buildButton;
    @FXML private Button cancelButton;
    @FXML private Label budgetLabel;
    @FXML private Label costLabel;
    @FXML private Label capacityLabel;
    @FXML private Label messageLabel;

    private final CreateStationController ctrl = new CreateStationController();

    private Map map;
    private Scenario scenario;
    private Budget budget;
    private Stage stage;

    /**
     * Enum for station types with their properties
     */
    public enum StationType {
        DEPOT("DEPOT", 50, 100),
        STATION("STATION", 100, 200),
        TERMINAL("TERMINAL", 200, 400);

        private final String name;
        private final int cost;
        private final int capacity;

        StationType(String name, int cost, int capacity) {
            this.name = name;
            this.cost = cost;
            this.capacity = capacity;
        }

        public String getName() { return name; }
        public int getCost() { return cost; }
        public int getCapacity() { return capacity; }

        @Override
        public String toString() {
            return name + " (cost " + cost + ", cap " + capacity + ")";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        setupValidation();
        clearMessage();
    }

    /**
     * Sets up the combo boxes with their respective values
     */
    private void setupComboBoxes() {
        stationTypeComboBox.setItems(FXCollections.observableArrayList(StationType.values()));
        orientationComboBox.setItems(FXCollections.observableArrayList(Orientation.values()));
    }

    /**
     * Sets up validation for text fields
     */
    private void setupValidation() {
        xCoordinateField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                xCoordinateField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        yCoordinateField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                yCoordinateField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * Initializes the controller with required dependencies
     */
    public void initData(Map map, Scenario scenario, Budget budget, Stage stage) {
        this.map = map;
        this.scenario = scenario;
        this.budget = budget;
        this.stage = stage;

        updateBudgetLabel();
    }

    /**
     * Updates the budget label
     */
    private void updateBudgetLabel() {
        if (budget != null) {
            budgetLabel.setText("Current Budget: $" + budget.getBudget());
        }
    }

    /**
     * Handles station type selection changes
     */
    @FXML
    private void onStationTypeChanged(ActionEvent event) {
        StationType selectedType = stationTypeComboBox.getValue();

        if (selectedType != null) {
            costLabel.setText("Cost: $" + selectedType.getCost());
            capacityLabel.setText("Capacity: " + selectedType.getCapacity());
            boolean isStation = selectedType == StationType.STATION;
            orientationBox.setVisible(isStation);
            orientationBox.setManaged(isStation);
            if (budget != null && budget.getBudget() < selectedType.getCost()) {
                showMessage("Insufficient funds! You need $" + selectedType.getCost() +
                        " but only have $" + budget.getBudget(), MessageType.ERROR);
                buildButton.setDisable(true);
            } else {
                buildButton.setDisable(false);
                clearMessage();
            }
        }
    }

    /**
     * Handles the build station button click
     */
    @FXML
    private void handleBuildStation(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        User user = CurrentUser.getInstance().getUser();
        if (user == null) {
            showMessage("No user logged in.", MessageType.ERROR);
            return;
        }

        StationType selectedType = stationTypeComboBox.getValue();
        int x = Integer.parseInt(xCoordinateField.getText());
        int y = Integer.parseInt(yCoordinateField.getText());
        Orientation orientation = null;

        if (selectedType == StationType.STATION) {
            orientation = orientationComboBox.getValue();
            if (orientation == null) {
                showMessage("Please select an orientation for the station.", MessageType.ERROR);
                return;
            }
        }

        Error result = ctrl.buildStation(selectedType.getName(), map, scenario, x, y, orientation, budget);

        switch (result) {
            case OK:
                StationDTO dto = ctrl.getLastCreatedStationDTO();
                showMessage("Station '" + dto.getName() + "' built successfully at (" +
                        dto.getX() + "," + dto.getY() + ")", MessageType.SUCCESS);
                updateBudgetLabel();
                clearForm();
                break;

            case INVALID_COORDINATES:
                showMessage("Error: Coordinates are out of map bounds. Please try different coordinates.",
                        MessageType.ERROR);
                break;

            case OVERBUILD:
                showMessage("Error: Cannot build on occupied cell. Please choose different coordinates.",
                        MessageType.ERROR);
                break;

            case INSUFFICIENT_FUNDS:
                showMessage("Error: Insufficient funds to build this station.", MessageType.ERROR);
                updateBudgetLabel();
                break;

            case INVALID_TYPE:
                showMessage("Error: Invalid station type selected.", MessageType.ERROR);
                break;

            default:
                showMessage("Error: " + result + ". Failed to build station.", MessageType.ERROR);
                break;
        }
    }

    /**
     * Handles the cancel button click
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Validates the input fields
     */
    private boolean validateInput() {
        if (stationTypeComboBox.getValue() == null) {
            showMessage("Please select a station type.", MessageType.ERROR);
            return false;
        }

        if (xCoordinateField.getText().trim().isEmpty()) {
            showMessage("Please enter an X coordinate.", MessageType.ERROR);
            return false;
        }

        if (yCoordinateField.getText().trim().isEmpty()) {
            showMessage("Please enter a Y coordinate.", MessageType.ERROR);
            return false;
        }

        try {
            int x = Integer.parseInt(xCoordinateField.getText());
            int y = Integer.parseInt(yCoordinateField.getText());

            if (x < 1 || y < 1) {
                showMessage("Coordinates must be positive numbers.", MessageType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage("Please enter valid numeric coordinates.", MessageType.ERROR);
            return false;
        }

        return true;
    }

    /**
     * Clears the form fields
     */
    private void clearForm() {
        stationTypeComboBox.setValue(null);
        xCoordinateField.clear();
        yCoordinateField.clear();
        orientationComboBox.setValue(null);
        orientationBox.setVisible(false);
        orientationBox.setManaged(false);
        costLabel.setText("Cost: $0");
        capacityLabel.setText("Capacity: 0");
        buildButton.setDisable(false);
    }

    /**
     * Message types for styling
     */
    private enum MessageType {
        SUCCESS, ERROR, INFO
    }

    /**
     * Shows a message to the user
     */
    private void showMessage(String message, MessageType type) {
        messageLabel.setText(message);

        switch (type) {
            case SUCCESS:
                messageLabel.setStyle("-fx-text-fill: #90EE90;");
                break;
            case ERROR:
                messageLabel.setStyle("-fx-text-fill: #FF6B6B;");
                break;
            case INFO:
                messageLabel.setStyle("-fx-text-fill: #87CEEB;");
                break;
        }
    }

    /**
     * Clears the message label
     */
    private void clearMessage() {
        messageLabel.setText("");
    }
}
