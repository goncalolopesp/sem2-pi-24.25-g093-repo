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

    /**
     * Initializes the BuildStationController by setting up UI components and preparing validations.
     *
     * @param location The location used to resolve relative paths for the root object, or null if not known.
     * @param resources The ResourceBundle for localizing the root object, or null if not available.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        setupValidation();
        clearMessage();
    }

    /**
     * Configures the combo boxes for selecting station type and orientation.
     *
     * The method sets the available items for the stationTypeComboBox and orientationComboBox
     * UI elements based on all possible values of the StationType and Orientation enums, respectively.
     */
    private void setupComboBoxes() {
        stationTypeComboBox.setItems(FXCollections.observableArrayList(StationType.values()));
        orientationComboBox.setItems(FXCollections.observableArrayList(Orientation.values()));
    }

    /**
     * Configures validation listeners for the xCoordinateField and yCoordinateField text fields.
     *
     * The listeners ensure that any input in these fields consists only of numeric characters.
     * If a non-numeric character is entered, it is automatically removed from the input.
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
     * Initializes the necessary data and updates the budget label for the BuildStationController.
     *
     * @param map      The Map object representing the current game map.
     * @param scenario The Scenario object representing the game scenario.
     * @param budget   The Budget object representing the current budget details.
     * @param stage    The Stage object representing the current user interface stage.
     */
    public void initData(Map map, Scenario scenario, Budget budget, Stage stage) {
        this.map = map;
        this.scenario = scenario;
        this.budget = budget;
        this.stage = stage;

        updateBudgetLabel();
    }

    /**
     * Updates the text of the budget label to display the current budget amount.
     *
     * If the budget object is not null, this method fetches the budget value
     * from the `Budget` instance and updates the `budgetLabel` control with
     * a formatted string displaying the current budget.
     */
    private void updateBudgetLabel() {
        if (budget != null) {
            budgetLabel.setText("Current Budget: $" + budget.getBudget());
        }
    }

    /**
     * Handles changes to the selected station type in the station type combo box.
     *
     * This method updates the UI components and validates the budget based on the selected
     * station type. It adjusts labels for cost and capacity, determines the visibility
     * of the orientation box, and enables or disables the build button accordingly.
     * If the budget is insufficient for the selected station type, an error message is displayed.
     *
     * @param event The event triggered by changing the selection in the station type combo box.
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
     * Handles the action of building a station based on the user's input in the form.
     *
     * The method checks the user input for validity, fetches the current user, and processes
     * the station-building operation. It validates the selected station type, coordinates,
     * and remaining budget. Depending on the result of the operation, feedback is displayed
     * to the user through messages or updated UI elements.
     *
     * @param event The ActionEvent triggered when the user attempts to build a station.
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
     * Handles the cancel action triggered by the user.
     *
     * This method closes the current user interface stage if it is available.
     *
     * @param event The ActionEvent triggered when the cancel button is clicked.
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Validates the user input for building a station.
     *
     * This method checks that all required fields have been filled out properly:
     * - A station type is selected.
     * - Both X and Y coordinate fields have non-empty, valid numeric values.
     * - The coordinates are positive integers.
     *
     * If any validation fails, an appropriate error message is displayed to the user.
     *
     * @return true if the input is valid and meets all the criteria, false otherwise
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
     * Clears the input fields and resets the form to its initial state.
     *
     * This method performs the following actions:
     * - Resets the `stationTypeComboBox` and `orientationComboBox` to null.
     * - Clears the values in the `xCoordinateField` and `yCoordinateField` text fields.
     * - Hides and unmanages the `orientationBox`.
     * - Updates the labels for cost and capacity to their default values.
     * - Enables the `buildButton`.
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
     * Enum representing the types of messages that can be displayed to the user.
     *
     * The message types are used to specify the context or category of a message,
     * such as success notifications, error alerts, or informational messages.
     * This enum is primarily utilized in message display functionality to
     * distinguish between different types of feedback provided to the user.
     *
     * The available message types are:
     * - SUCCESS: Indicates a successful operation or outcome.
     * - ERROR: Represents an error or problem that occurred.
     */
    private enum MessageType {
        SUCCESS, ERROR
    }

    /**
     * Displays a message to the user with a specific style based on the message type.
     *
     * The method updates the text and style of the `messageLabel` control. The style
     * is determined by the `MessageType` parameter, allowing for a visual distinction
     * between success and error messages.
     *
     * @param message The message to be displayed to the user.
     * @param type    The type of the message, which determines the style (e.g., SUCCESS or ERROR).
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
        }
    }

    /**
     * Clears the message label
     */
    private void clearMessage() {
        messageLabel.setText("");
    }
}
