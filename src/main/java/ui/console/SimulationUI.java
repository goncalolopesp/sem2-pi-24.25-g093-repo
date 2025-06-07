package ui.console;

import controller.PlayerMenuController;
import controller.SimulationController;
import domain.*;
import repository.MapRepository;
import repository.Repositories;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

public class SimulationUI {

    private final SimulationController ctrl = new SimulationController();
    private final MapRepository mapRepo = Repositories.getInstance().getMapRepository();

    private Simulation sim;
    private PlayerMenuController controller;
    private final Scenario selectedScenario;
    private final Map selectedMap; // Cache the selected map

    // Game state variables
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean gameOver = new AtomicBoolean(false);
    private AtomicReference<AtomicIntegerArray> timePeriod;

    // Schedulers
    private ScheduledExecutorService timeScheduler;
    private ScheduledExecutorService generationScheduler;
    private ScheduledExecutorService routeScheduler;

    public SimulationUI(Scenario selectedScenario) {
        if (selectedScenario == null) {
            throw new IllegalArgumentException("Selected scenario cannot be null");
        }
        this.selectedScenario = selectedScenario;

        // Initialize and cache the selected map based on the scenario
        this.selectedMap = mapRepo.getMaps().stream()
                .filter(map -> map.getMapName().equals(selectedScenario.getSelectedMap()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Map not found for scenario: " + selectedScenario.getSelectedMap()));
    }

    public void start() throws Exception {
        start(new Stage());
    }

    public void start(Stage stage) throws Exception {

        // Initialize the simulation
        if (!initializeSimulation()) {
            System.err.println("Failed to initialize simulation");
            return;
        }

        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/PlayerMenu.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        // Pass simulation data to controller - ORDER MATTERS!
        // Set SimulationUI first so it's available when setSimulation is called
        controller.setSimulationUI(this);
        controller.setSimulation(sim);

        // Create scene and show stage
        Scene scene = new Scene(root, 1065, 777);
        stage.setTitle("Railway Simulation");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(false);

        // Handle window close event
        stage.setOnCloseRequest(event -> {
            event.consume();
            stopSimulation();
            stage.close();
        });

        stage.show();

        // Start simulation threads
        startSimulationThreads();
    }

    private boolean initializeSimulation() {
        System.out.println("\nChecking for existing simulations...");
        System.out.println("Available simulations: " + ctrl.getSimulations().size());

        User currentUser = CurrentUser.getInstance().getUser();
        if (currentUser == null) {
            System.out.println("No user logged in");
            return false;
        }

        System.out.println("Using scenario: " + selectedScenario.getScenarioName());
        System.out.println("Using map: " + selectedMap.getMapName());

        if (sim == null) {
            // Try to find existing simulation using the constructor's selectedScenario
            Optional<Simulation> existingSimulation = ctrl.findBy(selectedMap, selectedScenario);

            if (existingSimulation.isPresent()) {
                sim = existingSimulation.get();
                System.out.println("FOUND existing simulation! Resuming from year " + sim.getCurrentYear());
            } else {
                System.out.println("NO existing simulation found. Creating new one.");
                sim = ctrl.createSimulation(
                        selectedMap,
                        selectedScenario,
                        selectedScenario.getTimeRestrictions()[0],
                        ctrl.checkOrCreateBudget(selectedMap, selectedScenario)
                );
                System.out.println("New simulation created for year " + sim.getCurrentYear());
            }
        }

        if (sim.getUserBudget().getUser() != null) {
            currentUser.setLocomotives(sim.getUserBudget().getUser().getLocomotives());
        }

        int startYear = sim.getCurrentYear();
        int endYear = selectedScenario.getTimeRestrictions()[1];
        timePeriod = new AtomicReference<>(new AtomicIntegerArray(new int[]{startYear, endYear}));

        if (startYear == selectedScenario.getTimeRestrictions()[0]) {
            System.out.println("Game started on year " + startYear + " and will finish on year " + endYear);
        } else {
            System.out.println("Resuming game on year " + startYear + ", will finish on year " + endYear);
        }

        return true;
    }

    private void startSimulationThreads() {
        // Create all schedulers
        timeScheduler = Executors.newSingleThreadScheduledExecutor();
        generationScheduler = Executors.newSingleThreadScheduledExecutor();
        routeScheduler = Executors.newSingleThreadScheduledExecutor();

        // Use the selectedMap cached from constructor
        System.out.println("Starting simulation threads for map: " + selectedMap.getMapName() +
                " and scenario: " + selectedScenario.getScenarioName());

        // Create JavaFX-compatible tasks that update UI on JavaFX Application Thread
        Runnable timeTask = () -> {
            if (running.get() && !gameOver.get()) {
                try {
                    // Call the passTime method using the constructor's selectedScenario
                    Runnable timeRunner = ctrl.passTime(
                            selectedScenario,
                            timePeriod,
                            running,
                            null,
                            timeScheduler,
                            gameOver,
                            sim.getUserBudget(),
                            sim
                    );

                    // Execute the returned Runnable
                    timeRunner.run();

                    // Update UI on JavaFX Application Thread
                    Platform.runLater(() -> {
                        if (controller != null) {
                            controller.updateUI();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error in time task: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        Runnable generationTask = () -> {
            if (running.get()) {
                try {
                    ctrl.generateCargo(selectedMap, selectedScenario, timePeriod, running, generationScheduler);
                } catch (Exception e) {
                    System.err.println("Error in generation task: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        Runnable routeTask = () -> {
            if (running.get()) {
                // Use the selectedMap and selectedScenario from constructor
                ctrl.executeRoutes(selectedMap, selectedScenario, sim.getUserBudget(), running);
            }
        };

        // Schedule all tasks
        timeScheduler.scheduleAtFixedRate(timeTask, 20, 20, TimeUnit.SECONDS);
        generationScheduler.scheduleAtFixedRate(generationTask, 10, 10, TimeUnit.SECONDS);
        routeScheduler.scheduleAtFixedRate(routeTask, 5, 5, TimeUnit.SECONDS);
    }

    public void pauseGame() {
        running.set(false);
        System.out.println("Game paused.");
    }

    public void resumeGame() {
        running.set(true);
        System.out.println("Game resumed.");
    }

    public void stopSimulation() {
        running.set(false);

        // Shutdown all schedulers
        if (timeScheduler != null) timeScheduler.shutdownNow();
        if (generationScheduler != null) generationScheduler.shutdownNow();
        if (routeScheduler != null) routeScheduler.shutdownNow();

        // Save simulation state using the constructor's selectedMap and selectedScenario
        if (sim != null) {
            ctrl.updateSimulation(
                    selectedMap,
                    selectedScenario,
                    sim.getCurrentYear(),
                    sim.getUserBudget()
            );
            System.out.println("Game stopped.");

            if (gameOver.get()) {
                ctrl.resetGame(selectedMap, selectedScenario, sim.getUserBudget());
            }

            if (new SimulationFile().save()) {
                System.out.println("Game saved.");
            }
        }
        sim = null;
    }

    public Simulation getSimulation() {
        return sim;
    }

    public SimulationController getController() {
        return ctrl;
    }

    /**
     * Get the selected scenario that was provided in the constructor
     */
    public Scenario getSelectedScenario() {
        return selectedScenario;
    }

    /**
     * Get the selected map that corresponds to the selected scenario
     */
    public Map getSelectedMap() {
        return selectedMap;
    }

    public AtomicReference<AtomicIntegerArray> getTimePeriod() {
        return timePeriod;
    }
}