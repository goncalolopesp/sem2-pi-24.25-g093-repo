package controller;

import domain.*;
import javafx.application.Platform;
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
import ui.gui.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerMenuController implements Initializable {

    @FXML private Button buildStationBtn;
    @FXML private Button upgradeStationTypeBtn;
    @FXML private Button upgradeStationBuildingBtn;
    @FXML private Button buyLocomotiveBtn;
    @FXML private Button listLocomotivesBtn;
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
    private boolean wasRunningBeforeNavigation = true;

    // Add scheduler for periodic UI updates
    private ScheduledExecutorService uiUpdateScheduler;
    private volatile boolean shouldUpdateUI = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resumeBtn.setVisible(false);
        updateButtonStates();

        // Start UI update scheduler
        startUIUpdateScheduler();

        // Force initial UI update
        Platform.runLater(() -> {
            updateUI();
            // Set flag to ensure continuous updates
            shouldUpdateUI = true;
        });
    }

    /**
     * Start a scheduler that periodically checks if UI needs updating
     */
    private void startUIUpdateScheduler() {
        // Shutdown existing scheduler if it exists
        if (uiUpdateScheduler != null && !uiUpdateScheduler.isShutdown()) {
            uiUpdateScheduler.shutdownNow();
        }

        uiUpdateScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "UIUpdateScheduler");
            t.setDaemon(true); // Make it a daemon thread
            return t;
        });

        uiUpdateScheduler.scheduleAtFixedRate(() -> {
            if (shouldUpdateUI && simulation != null && simulationUI != null) {
                Platform.runLater(() -> {
                    updateUI();
                    // Keep updating as long as we have valid references
                    shouldUpdateUI = true;
                });
            }
        }, 100, 500, TimeUnit.MILLISECONDS); // Start after 100ms, then every 500ms

        System.out.println("UI Update Scheduler started");
    }

    /**
     * Request UI update - can be called from any thread
     */
    public void requestUIUpdate() {
        shouldUpdateUI = true;
    }

    /**
     * Force immediate UI update - must be called from JavaFX Application Thread
     */
    public void forceUIUpdate() {
        if (Platform.isFxApplicationThread()) {
            updateUI();
            shouldUpdateUI = true; // Ensure continuous updates
        } else {
            Platform.runLater(() -> {
                updateUI();
                shouldUpdateUI = true;
            });
        }
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        System.out.println("Simulation set in PlayerMenuController");

        // Always try to update UI when simulation is set
        Platform.runLater(() -> {
            updateUI();
            shouldUpdateUI = true;
        });
    }

    public void setSimulationUI(SimulationUI simulationUI) {
        this.simulationUI = simulationUI;
        System.out.println("SimulationUI set in PlayerMenuController");

        // Always update UI when simulationUI is set
        Platform.runLater(() -> {
            updateUI();
            shouldUpdateUI = true;
        });

        if (wasRunningBeforeNavigation && simulationUI != null) {
            simulationUI.resumeGame();
            gameRunning = true;
            updateButtonStates();
            System.out.println("[Game resumed - returned to player menu]");
        }
    }

    public void updateUI() {
        if (simulation != null) {
            yearText.setText("Year: " + simulation.getCurrentYear());
            budgetText.setText("Budget: $" + simulation.getUserBudget().getBudget());
            displayMapInGrid();
        }
    }

    private void displayMapInGrid() {
        if (simulation == null || mapGridPane == null || simulationUI == null) {
            System.out.println("Cannot display map - missing references: " +
                    "simulation=" + (simulation != null) +
                    ", mapGridPane=" + (mapGridPane != null) +
                    ", simulationUI=" + (simulationUI != null));
            return;
        }

        mapGridPane.getChildren().clear();

        Map selectedMap = simulationUI.getSelectedMap();
        Scenario selectedScenario = simulationUI.getSelectedScenario();

        if (selectedMap == null || selectedScenario == null) {
            System.out.println("Cannot display map - missing map or scenario");
            return;
        }

        String[][] grid = createMapGrid(selectedMap);
        populateMapGrid(grid, selectedMap, selectedScenario);
        displayGridInUI(grid, selectedMap.getHeight(), selectedMap.getWidth());
    }

    private String[][] createMapGrid(Map map) {
        String[][] grid = new String[map.getHeight() + 1][map.getWidth() + 1];
        for (int y = 1; y <= map.getHeight(); y++) {
            for (int x = 1; x <= map.getWidth(); x++) {
                grid[y][x] = "⬛";
            }
        }
        return grid;
    }

    private void populateMapGrid(String[][] grid, Map map, Scenario scenario) {
        addIndustriesToGrid(grid, map);
        addCitiesToGrid(grid, map);
        addStationsToGrid(grid, map, scenario);
    }

    private void addIndustriesToGrid(String[][] grid, Map map) {
        simulationUI.getController().getIndustriesForMap(map).forEach(industry ->
                grid[industry.getLocation().getY()][industry.getLocation().getX()] = "🏭");
    }

    private void addCitiesToGrid(String[][] grid, Map map) {
        simulationUI.getController().getCitiesForMap(map).forEach(city -> {
            grid[city.getCenterY()][city.getCenterX()] = "🌆";
            city.getBlocks().forEach(hb -> grid[hb.getY()][hb.getX()] = "🏠");
        });
    }

    private void addStationsToGrid(String[][] grid, Map map, Scenario scenario) {
        User currentUser = CurrentUser.getInstance().getUser();
        if (currentUser == null) return;

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
        double maxWidth = 500.0;
        double maxHeight = 400.0; // Increased from 300.0 to 400.0

        double cellWidth = Math.min(maxWidth / width, 15.0);
        double cellHeight = Math.min(maxHeight / height, 15.0);

        // Clear existing content
        mapGridPane.getChildren().clear();

        // Set the GridPane alignment and padding
        mapGridPane.setAlignment(javafx.geometry.Pos.CENTER);

        // Set minimum size for the GridPane to ensure it fills the ScrollPane
        double totalWidth = cellWidth * width;
        double totalHeight = cellHeight * height;

        // Make sure the GridPane is at least as large as the ScrollPane content area
        mapGridPane.setMinWidth(Math.max(totalWidth, 680)); // ScrollPane is 700px wide minus some padding
        mapGridPane.setMinHeight(Math.max(totalHeight, 480)); // ScrollPane is now 500px high minus some padding

        // Center the GridPane content
        mapGridPane.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                Rectangle cell = new Rectangle(cellWidth, cellHeight);

                Color cellColor = getCellColor(grid[y][x]);
                cell.setFill(cellColor);
                cell.setStroke(Color.GRAY);
                cell.setStrokeWidth(0.5);

                // Add the cell to the grid at its relative position
                mapGridPane.add(cell, x - 1, y - 1);
            }
        }

        // Alternative approach: Set column and row constraints to center content
        mapGridPane.getColumnConstraints().clear();
        mapGridPane.getRowConstraints().clear();

        // Create column constraints
        for (int i = 0; i < width; i++) {
            javafx.scene.layout.ColumnConstraints colConstraints = new javafx.scene.layout.ColumnConstraints();
            colConstraints.setHalignment(javafx.geometry.HPos.CENTER);
            colConstraints.setPrefWidth(cellWidth);
            mapGridPane.getColumnConstraints().add(colConstraints);
        }

        // Create row constraints
        for (int i = 0; i < height; i++) {
            javafx.scene.layout.RowConstraints rowConstraints = new javafx.scene.layout.RowConstraints();
            rowConstraints.setValignment(javafx.geometry.VPos.CENTER);
            rowConstraints.setPrefHeight(cellHeight);
            mapGridPane.getRowConstraints().add(rowConstraints);
        }
    }

    private Color getCellColor(String cellContent) {
        switch (cellContent) {
            case "⬛": return Color.DARKGRAY;
            case "🏭": return Color.ORANGE;
            case "🌆": return Color.LIGHTBLUE;
            case "🏠": return Color.LIGHTGREEN;
            case "🚉": return Color.YELLOW;
            case "🅿️": return Color.LIGHTYELLOW;
            default: return Color.DARKGRAY;
        }
    }

    @FXML
    private void handleBuildStation() {
        if (simulationUI == null) {
            System.err.println("SimulationUI is not set");
            return;
        }

        navigateAwayFromMenu(() -> {
            Stage currentStage = (Stage) buildStationBtn.getScene().getWindow();
            Map map = simulationUI.getSelectedMap();
            Scenario scenario = simulationUI.getSelectedScenario();
            Budget budget = simulation.getUserBudget();

            BuildStationGUI.launch(currentStage, map, scenario, budget);
        });
    }

    @FXML
    private void handleUpgradeStationType() {
        if (simulationUI == null) {
            System.err.println("SimulationUI is not set");
            return;
        }

        navigateAwayFromMenu(() -> {
            Stage currentStage = (Stage) upgradeStationTypeBtn.getScene().getWindow();
            try {
                UpgradeStationTypeGUI gui = new UpgradeStationTypeGUI();
                gui.show(currentStage, simulationUI.getSelectedMap(), simulationUI.getSelectedScenario(), simulation.getUserBudget());
                requestUIUpdate();
            } catch (IOException e) {
                System.err.println("Failed to open Upgrade Station Type GUI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleUpgradeStationBuilding() {
        if (simulationUI == null) {
            System.err.println("SimulationUI is not set");
            return;
        }

        navigateAwayFromMenu(() -> {
            Stage currentStage = (Stage) upgradeStationBuildingBtn.getScene().getWindow();
            UpgradeStationGUI launcher = new UpgradeStationGUI(
                    simulationUI.getSelectedMap(),
                    simulationUI.getSelectedScenario(),
                    simulationUI.getTimePeriod(),
                    simulationUI.getSimulation().getUserBudget()
            );

            launcher.show(currentStage);
            requestUIUpdate();
        });
    }

    @FXML
    private void BuyLocomotive() {
        if (simulationUI == null) {
            System.err.println("SimulationUI is not set");
            return;
        }

        Stage currentStage = (Stage) buyLocomotiveBtn.getScene().getWindow();
        Map map = simulationUI.getSelectedMap();
        Scenario scenario = simulationUI.getSelectedScenario();
        Budget budget = simulation.getUserBudget();

        BuyLocomotiveGUI.launch(currentStage, map, scenario, budget);
    }

    @FXML
    private void ListLocomotives() {
        Stage currentStage = (Stage) listLocomotivesBtn.getScene().getWindow();
        Map map = simulationUI.getSelectedMap();
        Scenario scenario = simulationUI.getSelectedScenario();
        Budget budget = simulation.getUserBudget();

        ListLocomotivesGUI.launch(currentStage, map, scenario, budget);
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

        navigateAwayFromMenu(() -> {
            Stage currentStage = (Stage) assignTrainBtn.getScene().getWindow();
            Map map = simulationUI.getSelectedMap();
            Scenario scenario = simulationUI.getSelectedScenario();
            Budget budget = simulation.getUserBudget();

            AssignTrainGUI.launch(currentStage, map, scenario, budget);
        });
    }

    @FXML
    private void handlePause() {
        if (simulationUI != null) {
            simulationUI.pauseGame();
            gameRunning = false;
            wasRunningBeforeNavigation = false;
            updateButtonStates();
        }
    }

    @FXML
    private void handleResume() {
        if (simulationUI != null) {
            simulationUI.resumeGame();
            gameRunning = true;
            wasRunningBeforeNavigation = true;
            updateButtonStates();
        }
    }

    @FXML
    private void doLogout() throws Exception {
        if (simulationUI != null) {
            simulationUI.stopSimulation();
        }

        // Shutdown UI update scheduler
        if (uiUpdateScheduler != null) {
            uiUpdateScheduler.shutdownNow();
        }

        Stage stage = (Stage) backBtn.getScene().getWindow();
        new PlayMenuGUI().start(stage);
    }

    private void updateButtonStates() {
        pauseBtn.setVisible(gameRunning);
        resumeBtn.setVisible(!gameRunning);

        boolean buttonsEnabled = gameRunning;
        buildStationBtn.setDisable(!buttonsEnabled);
        upgradeStationTypeBtn.setDisable(!buttonsEnabled);
        upgradeStationBuildingBtn.setDisable(!buttonsEnabled);
        buyLocomotiveBtn.setDisable(!buttonsEnabled);
        assignTrainBtn.setDisable(!buttonsEnabled);
    }

    public void returnToPlayerMenu(Scene scene) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/PlayerMenu.fxml"));
            Parent root = loader.load();
            PlayerMenuController newController = loader.getController();

            // Transfer all state to the new controller
            newController.setSimulation(this.simulation);
            newController.setSimulationUI(this.simulationUI);
            newController.gameRunning = this.gameRunning;
            newController.wasRunningBeforeNavigation = this.wasRunningBeforeNavigation;
            newController.updateButtonStates();

            // Force immediate UI update when returning
            newController.forceUIUpdate();

            scene.setRoot(root);

            // Shutdown the old controller's scheduler
            if (uiUpdateScheduler != null) {
                uiUpdateScheduler.shutdownNow();
                System.out.println("Old UI Update Scheduler shutdown");
            }

            System.out.println("Successfully returned to PlayerMenu with state transfer");
        } catch (IOException e) {
            System.err.println("Failed to return to PlayerMenu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateAwayFromMenu(Runnable action) {
        wasRunningBeforeNavigation = gameRunning;

        if (gameRunning && (simulationUI != null)) {
            simulationUI.pauseGame();
            gameRunning = false;
            updateButtonStates();
            System.out.println("[Game paused - navigating away from menu]");
        }

        try {
            action.run();
        } catch (Exception e) {
            System.err.println("Error executing navigation action: " + e.getMessage());
            e.printStackTrace();

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

    private void pauseForAction(Runnable action) {
        boolean wasRunning = gameRunning;
        if (wasRunning && simulationUI != null) {
            simulationUI.pauseGame();
            gameRunning = false;
            updateButtonStates();
            System.out.println("[Game paused for quick action]");
        }

        try {
            action.run();
        } catch (Exception e) {
            System.err.println("Error executing action: " + e.getMessage());
            e.printStackTrace();
        }

        if (wasRunning && simulationUI != null) {
            simulationUI.resumeGame();
            gameRunning = true;
            updateButtonStates();
            System.out.println("[Game resumed after quick action]");
        }
    }
}