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

/**
 * Controller class for handling user interactions with the Assign Train GUI.
 * Implements the logic for assigning trains to routes, managing routes, stations, and cargo selections.
 */
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
    private static int PORT_BEHAVIOUR = 1;

    private List<Train> userTrains;
    private List<Station> availableStations;
    private List<PointOfRoute> route;
    private Train selectedTrain;
    private Station selectedStationForRoute;
    private List<CheckBox> cargoCheckBoxes;

    /**
     * Sets the data required for the AssignTrainGUIController.
     * Initializes the selected map, scenario, budget, and other related components,
     * and prepares the UI elements.
     *
     * @param selectedMap the map selected by the user
     * @param selectedScenario the scenario selected by the user
     * @param budget the budget associated with the session
     */
    public void setData(Map selectedMap, Scenario selectedScenario, Budget budget) {
        this.selectedMap = selectedMap;
        this.selectedScenario = selectedScenario;
        this.budget = budget;
        this.assignTrainController = new AssignTrainController();
        this.route = new ArrayList<>();
        this.cargoCheckBoxes = new ArrayList<>();

        initializeUI();
    }

    /**
     * Initializes the AssignTrainGUIController by setting up UI components and event handlers.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if none are specified.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxStyling();
        setupEventHandlers();
    }

    /**
     * Initializes the user interface for assigning trains, ensuring all necessary data and UI components
     * are properly set up and validated before use.
     *
     * The method performs the following steps:
     * 1. Updates the budget label to display the current budget.
     * 2. Retrieves the currently authenticated user's email and fetches a list of their stations on the selected map.
     * 3. Validates that the user has at least two stations on the map. If not, an error message is displayed,
     *    the "Assign" button is hidden, and the method terminates.
     * 4. Retrieves the user's available trains and checks if they have at least one train. If not, an error message
     *    is displayed, the "Assign" button is hidden, and the method terminates.
     * 5. Proceeds to populate various UI components:
     *    - Populates the train list with the available trains.
     *    - Populates the station combo box with the user's stations on the map.
     *    - Sets up checkboxes for cargo selection.
     */
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

    /**
     * Configures the styling and appearance of ComboBox UI elements used in the application.
     *
     * This method specifically applies visual styles and configurations to:
     * - The station ComboBox (styled using the {@code setupStationComboBox} method).
     * - The cargo mode ComboBox (styled using the {@code setupCargoModeComboBox} method).
     *
     * By invoking the respective setup methods, this ensures uniform and visually
     * appealing styling for both ComboBoxes while maintaining functionality.
     */
    private void setupComboBoxStyling() {
        setupStationComboBox();
        setupCargoModeComboBox();
    }

    /**
     * Configures and sets up the appearance and behavior of the stationComboBox UI element.
     *
     * This method customizes the ComboBox to ensure a consistent visual style and functionality
     * by applying specific cell factories and button cell configurations. It includes:
     *
     * 1. Configuring the button cell to display a selected item with white text,
     *    a transparent background, and no text when the selection is empty or null.
     * 2. Setting up the cell factory for dropdown items, applying individual cell styles
     *    such as text color and background color based on whether an item is selected
     *    or not, ensuring proper distinction and visual clarity between selected and
     *    non-selected states.
     * 3. Ensuring uniform styling across all dropdown items and the button cell.
     * 4. Adding white text color for all items and transparent or custom background styles.
     */
    private void setupStationComboBox() {
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

            /**
             * Updates the selected state of this list cell and applies the corresponding styles.
             *
             * When the cell is selected, this method applies a specific style to distinguish
             * the selected state visually. If the cell is deselected, it reverts to a default
             * style, provided the cell contains an item.
             *
             * @param selected A boolean indicating the selected state of the cell.
             *                 true if the cell is selected; false otherwise.
             */
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

    /**
     * Configures the Cargo Mode ComboBox with available options and custom styles.
     *
     * This method sets up the cargoModeComboBox UI control by doing the following:
     * 1. Adds predefined cargo modes as selectable options:
     *    - "FULL - Wait until all selected carriages are full"
     *    - "HALF - Wait until half of selected carriages are full"
     *    - "AVAILABLE - Take all available cargo and move on"
     * 2. Sets the default value of the ComboBox to "AVAILABLE - Take all available cargo and move on".
     * 3. Customizes the appearance of the button cell to display the selected item. Applies the following styles:
     *    - White text color.
     *    - Transparent background.
     *    - Null text if the cell is empty or the selection is null.
     * 4. Applies a custom cell factory to style the dropdown items with the following behavior:
     *    - Sets white text color and a dark background for dropdown items.
     *    - Differentiates between selected and non-selected items by changing background and text styles.
     * 5. Ensures uniform styling and visual clarity for both the ComboBox button cell and its dropdown items.
     */
    private void setupCargoModeComboBox() {
        cargoModeComboBox.getItems().addAll(
                "FULL - Wait until all selected carriages are full",
                "HALF - Wait until half of selected carriages are full",
                "AVAILABLE - Take all available cargo and move on"
        );
        cargoModeComboBox.setValue("AVAILABLE - Take all available cargo and move on");
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

            /**
             * Updates the selection state of this cell and applies corresponding styles.
             *
             * @param selected A boolean indicating whether the cell is selected.
             *                 If true, applies a dark gray background and white text.
             *                 If false, reverts to the default styles for unselected cells.
             */
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

    /**
     * Configures event handlers for specific user interface components to handle
     * relevant user interactions such as train list selection and station selection.
     *
     * The method performs the following configurations:
     * 1. Listens for item selection changes in the trainListView component and triggers
     *    the {@code handleTrainSelection()} method to handle the selection of a train.
     * 2. Sets up an action event listener for the stationComboBox component to invoke
     *    the {@code handleStationSelection()} method whenever a station is selected.
     *
     * These event handlers enable dynamic updates to the user interface based on the
     * user's interaction with the train list and station dropdown, ensuring proper state
     * transitions and relevant UI element visibility.
     */
    private void setupEventHandlers() {
        trainListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                handleTrainSelection();
            }
        });

        stationComboBox.setOnAction(e -> handleStationSelection());
    }

    /**
     * Populates the train list view with details of all trains owned by the user.
     *
     * This method performs the following steps:
     * 1. Clears the train list view to ensure it contains only the latest data.
     * 2. Iterates over each train in the user's train collection.
     * 3. For each train, creates a data transfer object (DTO) for easier manipulation and display.
     * 4. Constructs a detailed string representation of the train, including:
     *    - The index of the train in the collection.
     *    - The locomotive model associated with the train.
     *    - The total number of carriages.
     *    - Details for each carriage type along with counts, if any.
     * 5. Adds the constructed string representation to the train list view.
     */
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

    /**
     * Populates the stationComboBox with a list of available stations.
     *
     * This method clears any existing items in the stationComboBox, iterates through
     * the availableStations list, and formats each station's details into a string
     * that includes the station's index, name, and coordinates. These formatted strings
     * are then added to the ComboBox as selectable items.
     *
     * Functionality:
     * - Clears the current items in the stationComboBox to ensure the content is updated.
     * - Iterates over each station in the list of available stations.
     * - Extracts each station's name and coordinates (x and y).
     * - Constructs a formatted string representation of the station in the format:
     *   "[index] stationName at (x=xValue, y=yValue)".
     * - Adds the formatted string for each station to the stationComboBox.
     *
     * Use this method to refresh the stationComboBox whenever the list of available
     * stations is updated or reloaded to*/
    private void populateStationComboBox() {
        stationComboBox.getItems().clear();

        for (int i = 0; i < availableStations.size(); i++) {
            Station station = availableStations.get(i);
            String stationInfo = String.format("[%d] %s at (x=%d, y=%d)",
                    i, station.getName(), station.getX(), station.getY());
            stationComboBox.getItems().add(stationInfo);
        }
    }

    /**
     * Initializes and configures the cargo selection checkboxes used in the cargo selection UI.
     *
     * This method performs the following steps:
     * 1. Clears any existing cargo checkboxes from the `cargoCheckBoxContainer` and `cargoCheckBoxes` list
     *    to ensure the UI reflects the most up-to-date options.
     * 2. Defines a fixed array of cargo types: "PEOPLE", "MAIL", "COAL", "IRON", and "STEEL".
     * 3. Iterates through each cargo type and creates a new `CheckBox` instance for it.
     *    - Sets the text color of the checkbox to white for better visibility against the UI background.
     *    - Applies custom styling to visually ensure the checkboxes maintain a consistent look.
     * 4. Adds each created checkbox to the `cargoCheckBoxes` list.
     * 5. Appends the checkbox to the `cargoCheckBoxContainer` to display it within the UI.
     *
     * This method is specifically designed to prepare the cargo selection interface for user interaction
     * and ensure the cargo selection mechanism is both functional and visually aligned with the application's design.
     */
    private void setupCargoCheckBoxes() {
        cargoCheckBoxContainer.getChildren().clear();
        cargoCheckBoxes.clear();

        String [] cargoTypes;
        if (selectedScenario.getPortBehavior() != PORT_BEHAVIOUR) {
            cargoTypes = new String[]{"PEOPLE", "MAIL"};
        } else {
            cargoTypes = new String[]{"PEOPLE", "MAIL", "COAL", "IRON", "STEEL"};
        }

        for (String cargoType : cargoTypes) {
            CheckBox checkBox = new CheckBox(cargoType);
            checkBox.setTextFill(javafx.scene.paint.Color.WHITE);
            checkBox.setStyle("-fx-text-fill: white;");
            cargoCheckBoxes.add(checkBox);
            cargoCheckBoxContainer.getChildren().add(checkBox);
        }
    }

    /**
     * Handles the selection of a train from the train list view.
     *
     * This method retrieves the currently selected train from the user interface
     * and updates the application's state accordingly. If a valid train is selected,
     * the corresponding train object is retrieved from the userTrains list and stored.
     * Additionally, the route creation section is made visible, and a message is
     * displayed to inform the user about the next steps for route creation.
     *
     * Conditions checked:
     * - The selected index is within the bounds of the userTrains list.
     *
     * Postconditions:
     * - The selectedTrain variable is updated with the corresponding train object.
     * - The route creation section becomes visible.
     * - A message is displayed to the user.
     */
    private void handleTrainSelection() {
        int selectedIndex = trainListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < userTrains.size()) {
            selectedTrain = userTrains.get(selectedIndex);
            routeSection.setVisible(true);
            showMessage("Train selected. Now create a route by adding stations.", false);
        }
    }

    /**
     * Handles the selection of a station from a dropdown menu (stationComboBox).
     *
     * This method retrieves the user's selected station from the dropdown, validates
     * the selection, and updates the UI accordingly. If a valid selection is made,
     * it identifies the selected station and makes additional UI components visible
     * for further interaction. If the selection is invalid or an error occurs during*/
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

    /**
     * Handles the addition of a selected station to the route.
     *
     * This method checks if a station is selected and ensures the user has chosen
     * at least one cargo type. Upon successful validation, it adds the selected station
     * along with the chosen cargo types and cargo mode as a new point in the route.
     * The method also updates the route display, adjusts the available stations list,
     * and prepares the UI elements for further user interactions.
     *
     * Actions performed:
     * - Verifies that a station is selected*/
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

    /**
     * Updates the display of the route in the ListView component.
     *
     * This method clears the current items in the ListView and iterates through the existing route list.
     * For each point in the route, it constructs a detailed string containing the station name, station coordinates,
     * the list of cargo types, and the cargo mode. These details are then added as items to the ListView.
     * The method ensures the route display is synchronized with the current state of the route.
     */
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

    /**
     * Retrieves the selected cargo mode based on the value from the cargo mode combo box.
     * If no mode is selected, it defaults to CargoMode.AVAILABLE.
     * The method determines the cargo mode by analyzing the selected value's prefix.
     *
     * @return the selected CargoMode, which can be CargoMode.FULL, CargoMode.HALF, or CargoMode.AVAILABLE.
     */
    private CargoMode getSelectedCargoMode() {
        String selected = cargoModeComboBox.getValue();
        if (selected == null) return CargoMode.AVAILABLE;

        if (selected.startsWith("FULL")) return CargoMode.FULL;
        if (selected.startsWith("HALF")) return CargoMode.HALF;
        return CargoMode.AVAILABLE;
    }

    /**
     * Resets the station selection and updates the associated UI components to their default state.
     *
     * This method performs the following actions:
     * - Clears the selected station for the route and resets station selection dropdowns.
     * - Hides UI components related to cargo selection and station addition.
     * - Clears the state of all cargo-related checkboxes.
     * - Sets the cargo mode dropdown to its default option.
     */
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

    /**
     * Handles the process of assigning a train to a selected route.
     *
     * This method performs the following:
     * - Validates that a train is selected. If no train is selected, an error message is shown.
     * - Ensures that the route has at least two stations. If not, displays a message and fills the route with default stations.
     * - Attempts to assign the selected train to the specified route in the current scenario with the given budget.
     * - Upon a successful assignment, displays a success message and executes a background task with a small delay.
     * - If an error occurs during the assignment, an error message is displayed with the relevant error details.
     *
     * The method also includes background processing to delay the cancellation of the dialog or UI element associated with this task.
     *
     * Handles exceptions gracefully to ensure the user is notified of any issues.
     */
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

    /**
     * Ensures the route is populated with default stations if it contains less than two stations.
     * Adds stations from the available stations list until the route has at least two stations or
     * the available stations list is empty. Each added station is initialized with default cargo types
     * (people and mail) and set to the AVAILABLE cargo mode.
     * Finally, updates the route display to reflect changes.
     */
    private void fillRouteWithDefaultStations() {
        while (route.size() < 2 && !availableStations.isEmpty()) {
            Station station = availableStations.remove(0);
            List<Type> defaultCargo = Arrays.asList(Type.PEOPLE, Type.MAIL);
            PointOfRoute point = new PointOfRoute(station, defaultCargo, CargoMode.AVAILABLE);
            route.add(point);
        }
        updateRouteDisplay();
    }

    /**
     * Handles the action of clearing the current route. This method performs the following:
     *
     * - Clears the route data.
     * - Retrieves the list of stations available to the current user.
     * - Updates the station combo box with the available stations.
     * - Updates the route display to reflect the cleared state.
     * - Resets the station selection to its initial state.
     * - Hides the assign and clear route buttons.
     * - Displays a confirmation message to the user indicating that the route has been cleared.
     */
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

    /**
     * Handles the event triggered by the cancel action.
     * This method closes the current stage (window) associated with the cancel button.
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Displays a message on the message label with styling based on the error status.
     *
     * @param message the message to be displayed
     * @param isError whether the message indicates an error (true if error, false otherwise)
     */
    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        if (isError) {
            messageLabel.setTextFill(javafx.scene.paint.Color.web("#FF6B6B"));
        } else {
            messageLabel.setTextFill(javafx.scene.paint.Color.web("#90EE90"));
        }
    }

    /**
     * Retrieves a list of trains associated with a specific player's email.
     *
     * @param playerEmail the email of the player whose trains are to be retrieved
     * @return a list of Train objects belonging to the specified player
     */
    private List<Train> getPlayerTrains(String playerEmail) {
        return Repositories.getInstance().getTrainRepository().getTrains().stream()
                .filter(train -> train.getUserEmail().equals(playerEmail))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of stations owned by a specified player on the currently selected map and scenario.
     *
     * @param playerEmail the email address of the player whose stations are to be retrieved
     * @return a list of stations belonging to the specified player on the current map and scenario
     */
    private List<Station> getPlayerStationsOnMap(String playerEmail) {
        StationRepository stationRepo = Repositories.getInstance().getStationRepository();
        return stationRepo.getStations().stream()
                .filter(s -> s.getMapName().equals(selectedMap.getMapName()))
                .filter(s -> s.getScenarioName().equals(selectedScenario.getScenarioName()))
                .filter(s -> s.getOwnerPlayer().equals(playerEmail))
                .collect(Collectors.toList());
    }
}