package controller;

import domain.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ui.console.SimulationUI;
import ui.console.*;
import ui.gui.PlayMenuGUI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerMenuController implements Initializable {

    @FXML private Button buildStationBtn;
    @FXML private Button upgradeStationTypeBtn;
    @FXML private Button upgradeStationBuildingBtn;
    @FXML private Button buyLocomotiveBtn;
    @FXML private Button listLocomotivesBtn;
    @FXML private Button listStationsBtn;
    @FXML private Button assignTrainBtn;
    @FXML private Button pauseBtn;
    @FXML private Button resumeBtn;
    @FXML private Button backBtn;
    @FXML private Text budgetText;
    @FXML private Text yearText;
    @FXML private GridPane mapGridPane;

    private SimulationUI simulationUI;
    private Simulation simulation;
    private boolean gameRunning = true;
    private boolean wasRunningBeforeNavigation = true; // Track state before navigation

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize UI state
        resumeBtn.setVisible(false);
        updateButtonStates();
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        System.out.println("Simulation set in PlayerMenuController");
        // Only update UI if we also have simulationUI
        if (simulationUI != null) {
            updateUI();
        }
    }

    public void setSimulationUI(SimulationUI simulationUI) {
        this.simulationUI = simulationUI;
        System.out.println("SimulationUI set in PlayerMenuController");
        // Update UI now that we have both simulation and simulationUI
        updateUI();

        // Resume game when returning to player menu (if it was running before)
        if (wasRunningBeforeNavigation && simulationUI != null) {
            simulationUI.resumeGame();
            gameRunning = true;
            updateButtonStates();
            System.out.println("[Game resumed - returned to player menu]");
        }
    }

    public void updateUI() {
        if (simulation != null) {
            // Update year
            yearText.setText("Year: " + simulation.getCurrentYear());

            // Update budget
            budgetText.setText("Budget: $" + simulation.getUserBudget().getBudget());

            // Update map display
            displayMapInGrid();
        }
    }

    private void displayMapInGrid() {
        if (simulation == null || mapGridPane == null) return;

        // Clear existing map
        mapGridPane.getChildren().clear();

        Map selectedMap = simulationUI.getSelectedMap();
        Scenario selectedScenario = simulationUI.getSelectedScenario();

        if (selectedMap == null || selectedScenario == null) return;

        // Create map grid using the existing logic from SimulationController
        String[][] grid = createMapGrid(selectedMap);
        populateMapGrid(grid, selectedMap, selectedScenario);

        // Display the grid in JavaFX
        displayGridInUI(grid, selectedMap.getHeight(), selectedMap.getWidth());
    }

    private String[][] createMapGrid(Map map) {
        String[][] grid = new String[map.getHeight() + 1][map.getWidth() + 1];
        for (int y = 1; y <= map.getHeight(); y++) {
            for (int x = 1; x <= map.getWidth(); x++) {
                grid[y][x] = "⬛"; // Default empty space
            }
        }
        return grid;
    }

    private void populateMapGrid(String[][] grid, Map map, Scenario scenario) {
        // Use the existing logic from SimulationController
        SimulationController controller = simulationUI.getController();

        // Add industries
        addIndustriesToGrid(grid, map);

        // Add cities
        addCitiesToGrid(grid, map);

        // Add stations
        addStationsToGrid(grid, map, scenario);
    }

    private void addIndustriesToGrid(String[][] grid, Map map) {
        // Get industries for this map and add them to grid
        simulationUI.getController().getIndustriesForMap(map).forEach(industry ->
                grid[industry.getLocation().getY()][industry.getLocation().getX()] = "🏭");
    }

    private void addCitiesToGrid(String[][] grid, Map map) {
        // Get cities for this map and add them to grid
        simulationUI.getController().getCitiesForMap(map).forEach(city -> {
            grid[city.getCenterY()][city.getCenterX()] = "🌆";
            city.getBlocks().forEach(hb -> grid[hb.getY()][hb.getX()] = "🏠");
        });
    }

    private void addStationsToGrid(String[][] grid, Map map, Scenario scenario) {
        User currentUser = CurrentUser.getInstance().getUser();
        if (currentUser == null) return;

        // Get user stations and add them to grid
        simulationUI.getController().getUserStations(map, scenario).forEach(station ->
                addStationToGrid(grid, station, map.getWidth(), map.getHeight()));
    }

    private void addStationToGrid(String[][] grid, Station station, int width, int height) {
        switch (station.getType()) {
            case DEPOT:
                paintStationArea(grid, station, 3, 3, width, height);
                break;
            case TERMINAL:
                paintStationArea(grid, station, 5, 5, width, height);
                break;
            case STATION:
                paintOrientedStation(grid, station, width, height);
                break;
            default:
                if (isValidPosition(station.getX(), station.getY(), width, height)) {
                    grid[station.getY()][station.getX()] = "🚉";
                }
        }
    }

    private void paintStationArea(String[][] grid, Station station, int w, int h, int maxW, int maxH) {
        int ox = station.getX() - (w / 2);
        int oy = station.getY() - (h / 2);
        paintArea(grid, maxW, maxH, station.getX(), station.getY(), ox, oy, w, h);
    }

    private void paintOrientedStation(String[][] grid, Station station, int width, int height) {
        int w = 4, h = 4;
        int ox = station.getX() - 1;
        int oy = station.getY() - 1;

        int sdx = 1, sdy = 1;
        switch (station.getOrientation()) {
            case NE: sdx = 2; break;
            case SW: sdy = 2; break;
            case SE: sdx = 2; sdy = 2; break;
        }

        for (int dy = 0; dy < h; dy++) {
            for (int dx = 0; dx < w; dx++) {
                int gx = ox + dx;
                int gy = oy + dy;
                if (isValidPosition(gx, gy, width, height)) {
                    grid[gy][gx] = (dx == sdx && dy == sdy) ? "🚉" : "🅿️";
                }
            }
        }
    }

    private boolean isValidPosition(int x, int y, int maxW, int maxH) {
        return x >= 1 && x <= maxW && y >= 1 && y <= maxH;
    }

    private void paintArea(String[][] grid, int maxW, int maxH, int cx, int cy, int ox, int oy, int w, int h) {
        for (int dy = 0; dy < h; dy++) {
            for (int dx = 0; dx < w; dx++) {
                int x = ox + dx;
                int y = oy + dy;
                if (isValidPosition(x, y, maxW, maxH)) {
                    grid[y][x] = (x == cx && y == cy) ? "🚉" : "🅿️";
                }
            }
        }
    }

    private void displayGridInUI(String[][] grid, int height, int width) {
        // Calculate cell size based on available space
        double maxWidth = 500.0; // Available width for map
        double maxHeight = 300.0; // Available height for map

        double cellWidth = Math.min(maxWidth / width, 15.0);
        double cellHeight = Math.min(maxHeight / height, 15.0);

        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                Rectangle cell = new Rectangle(cellWidth, cellHeight);

                // Set color based on cell content
                Color cellColor = getCellColor(grid[y][x]);
                cell.setFill(cellColor);
                cell.setStroke(Color.GRAY);
                cell.setStrokeWidth(0.5);

                // Add tooltip with cell information
                String cellInfo = getCellInfo(grid[y][x], x, y);
                if (!cellInfo.isEmpty()) {
                    // You can add Tooltip here if needed
                    // Tooltip.install(cell, new Tooltip(cellInfo));
                }

                mapGridPane.add(cell, x - 1, y - 1);
            }
        }
    }

    private Color getCellColor(String cellContent) {
        switch (cellContent) {
            case "⬛": return Color.DARKGRAY; // Empty space
            case "🏭": return Color.ORANGE; // Industry
            case "🌆": return Color.LIGHTBLUE; // City center
            case "🏠": return Color.LIGHTGREEN; // City blocks
            case "🚉": return Color.YELLOW; // Station
            case "🅿️": return Color.LIGHTYELLOW; // Station area
            default: return Color.DARKGRAY;
        }
    }

    private String getCellInfo(String cellContent, int x, int y) {
        switch (cellContent) {
            case "🏭": return "Industry at (" + x + "," + y + ")";
            case "🌆": return "City Center at (" + x + "," + y + ")";
            case "🏠": return "City Block at (" + x + "," + y + ")";
            case "🚉": return "Station at (" + x + "," + y + ")";
            case "🅿️": return "Station Area at (" + x + "," + y + ")";
            default: return "";
        }
    }


    @FXML
    private void handleBuildStation() {
        if (simulationUI == null) {
            System.err.println("SimulationUI is not set");
            return;
        }

        navigateAwayFromMenu(() -> {
            new BuildStationUI(
                    simulationUI.getSelectedMap(),
                    simulationUI.getSelectedScenario(),
                    simulation.getUserBudget()
            ).run();
        });
    }

    @FXML
    private void handleUpgradeStationType() {
        if (simulationUI == null) {
            System.err.println("SimulationUI is not set");
            return;
        }

        navigateAwayFromMenu(() -> {
            new UpgradeStationTypeUI(
                    simulationUI.getSelectedMap(),
                    simulationUI.getSelectedScenario(),
                    simulation.getUserBudget()
            ).run();
        });
    }

    @FXML
    private void handleUpgradeStationBuilding() {
        if (simulationUI == null) {
            System.err.println("SimulationUI is not set");
            return;
        }

        navigateAwayFromMenu(() -> {
            new UpgradeStationUI(
                    simulationUI.getSelectedMap(),
                    simulationUI.getSelectedScenario(),
                    simulationUI.getTimePeriod(),
                    simulation.getUserBudget()
            ).run();
        });
    }

    @FXML
    private void BuyLocomotive() {
        if (simulationUI == null) {
            System.err.println("SimulationUI is not set");
            return;
        }

        navigateAwayFromMenu(() -> {
            new BuyLocomotiveUI(
                    simulationUI.getSelectedScenario(),
                    simulationUI.getSelectedMap(),
                    simulation.getUserBudget()
            ).run();
        });
    }

    @FXML
    private void ListLocomotives() {
        // ListLocomotiveUI is likely a console operation that returns immediately
        // so we use pauseForAction instead of navigateAwayFromMenu
        pauseForAction(() -> {
            new ListLocomotiveUI().run();
        });
    }

    @FXML
    private void ListStations() {
        if (simulationUI == null) {
            System.err.println("SimulationUI is not set");
            return;
        }

        navigateAwayFromMenu(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/ListStationsGUI.fxml"));
            Parent root;
            try {
                root = loader.load();
            } catch (IOException e) {
                System.err.println("Failed to load ListStationsGUI.fxml: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            ListStationController controller = loader.getController();
            // Set the scenario and also pass a reference to this PlayerMenuController
            controller.setScenario(simulationUI.getSelectedScenario());
            controller.setPlayerMenuController(this);

            Scene currentScene = listLocomotivesBtn.getScene();
            currentScene.setRoot(root);
        });
    }

    @FXML
    private void handleAssignTrain() {
        if (simulationUI == null) {
            System.err.println("SimulationUI is not set");
            return;
        }

        navigateAwayFromMenu(() -> new AssignTrainUI(
                simulationUI.getSelectedMap(),
                simulationUI.getSelectedScenario(),
                simulation.getUserBudget()
        ).run());
    }

    @FXML
    private void handlePause() {
        if (simulationUI != null) {
            simulationUI.pauseGame();
            gameRunning = false;
            wasRunningBeforeNavigation = false; // User manually paused
            updateButtonStates();
        }
    }

    @FXML
    private void handleResume() {
        if (simulationUI != null) {
            simulationUI.resumeGame();
            gameRunning = true;
            wasRunningBeforeNavigation = true; // User manually resumed
            updateButtonStates();
        }
    }

    @FXML
    private void doLogout() throws Exception {
        if (simulationUI != null) {
            simulationUI.stopSimulation();
        }

        // Close current window
        Stage stage = (Stage) backBtn.getScene().getWindow();
        new PlayMenuGUI().start(stage);
    }

    private void updateButtonStates() {
        pauseBtn.setVisible(gameRunning);
        resumeBtn.setVisible(!gameRunning);

        // Disable other buttons when game is paused (optional)
        boolean buttonsEnabled = gameRunning;
        buildStationBtn.setDisable(!buttonsEnabled);
        upgradeStationTypeBtn.setDisable(!buttonsEnabled);
        upgradeStationBuildingBtn.setDisable(!buttonsEnabled);
        buyLocomotiveBtn.setDisable(!buttonsEnabled);
        assignTrainBtn.setDisable(!buttonsEnabled);
    }

    /**
     * Method to return to this PlayerMenu from other screens
     * This preserves the simulation state and UI references
     */
    public void returnToPlayerMenu(Scene scene) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/PlayerMenu.fxml"));
            Parent root = loader.load();
            PlayerMenuController newController = loader.getController();

            // Transfer all the state to the new controller
            newController.setSimulationUI(this.simulationUI);
            newController.setSimulation(this.simulation);
            newController.gameRunning = this.gameRunning;
            newController.wasRunningBeforeNavigation = this.wasRunningBeforeNavigation;
            newController.updateButtonStates();

            scene.setRoot(root);
        } catch (IOException e) {
            System.err.println("Failed to return to PlayerMenu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Enhanced method that pauses the game when navigating away from the menu
     * and remembers the previous state for when the player returns
     */
    private void navigateAwayFromMenu(Runnable action) {
        // Remember if the game was running before navigation
        wasRunningBeforeNavigation = gameRunning;

        if (gameRunning && (simulationUI != null)) {
            simulationUI.pauseGame();
            gameRunning = false;
            updateButtonStates();
            System.out.println("[Game paused - navigating away from menu]");
        }

        // Execute the navigation action
        try {
            action.run();
        } catch (Exception e) {
            System.err.println("Error executing navigation action: " + e.getMessage());
            e.printStackTrace();

            // If navigation failed, restore the previous game state
            if (wasRunningBeforeNavigation && (simulationUI != null)) {
                simulationUI.resumeGame();
                gameRunning = true;
                updateButtonStates();
                System.out.println("[Game resumed - navigation failed]");
            }
        }

        gameRunning = true;
        updateButtonStates();
    }

    /**
     * Utility method to pause the game, execute an action, then resume if it was running
     * Use this for quick console-based actions that don't involve UI navigation
     */
    private void pauseForAction(Runnable action) {
        boolean wasRunning = gameRunning;
        if (wasRunning && simulationUI != null) {
            simulationUI.pauseGame();
            gameRunning = false;
            updateButtonStates();
            System.out.println("[Game paused for quick action]");
        }

        // Execute the action
        try {
            action.run();
        } catch (Exception e) {
            System.err.println("Error executing action: " + e.getMessage());
            e.printStackTrace();
        }

        // Resume if it was running before
        if (wasRunning && simulationUI != null) {
            simulationUI.resumeGame();
            gameRunning = true;
            updateButtonStates();
            System.out.println("[Game resumed after quick action]");
        }
    }
}