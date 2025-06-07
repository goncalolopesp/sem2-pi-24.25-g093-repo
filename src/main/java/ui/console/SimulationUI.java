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
    private final Map selectedMap;

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

        this.selectedMap = mapRepo.getMaps().stream()
                .filter(map -> map.getMapName().equals(selectedScenario.getSelectedMap()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Map not found for scenario: " + selectedScenario.getSelectedMap()));
    }

    public void start() throws Exception {
        start(new Stage());
    }

    public void start(Stage stage) throws Exception {
        if (!initializeSimulation()) {
            System.err.println("Failed to initialize simulation");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/PlayerMenu.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        controller.setSimulationUI(this);
        controller.setSimulation(sim);

        Scene scene = new Scene(root, 1065, 777);
        stage.setTitle("Railway Simulation");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(false);

        stage.setOnCloseRequest(event -> {
            event.consume();
            stopSimulation();
            stage.close();
        });

        stage.show();
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
        // Clean up existing schedulers first
        if (timeScheduler != null && !timeScheduler.isShutdown()) {
            timeScheduler.shutdownNow();
        }
        if (generationScheduler != null && !generationScheduler.isShutdown()) {
            generationScheduler.shutdownNow();
        }
        if (routeScheduler != null && !routeScheduler.isShutdown()) {
            routeScheduler.shutdownNow();
        }

        // Create new schedulers
        timeScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "TimeScheduler");
            t.setDaemon(true);
            return t;
        });
        generationScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "GenerationScheduler");
            t.setDaemon(true);
            return t;
        });
        routeScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "RouteScheduler");
            t.setDaemon(true);
            return t;
        });

        System.out.println("Starting simulation threads for map: " + selectedMap.getMapName() +
                " and scenario: " + selectedScenario.getScenarioName());

        // Time Task - Fixed to handle return value properly
        Runnable timeTask = () -> {
            if (running.get() && !gameOver.get()) {
                try {

                    // Call passTime and execute the returned runnable
                    Runnable timeRunner = ctrl.passTime(
                            selectedScenario,
                            timePeriod,
                            running,
                            null, // options parameter - you might need to pass actual options
                            timeScheduler,
                            gameOver,
                            sim.getUserBudget(),
                            sim
                    );

                    if (timeRunner != null) {
                        timeRunner.run();
                    }

                    requestUIUpdate();

                    // Always update UI for time-based changes
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

        // Generation Task - Fixed to call the method correctly
        Runnable generationTask = () -> {
            if (running.get() && !gameOver.get()) {
                try {
                    ctrl.generateCargo(selectedMap, selectedScenario, timePeriod, running, generationScheduler);
                    // Request UI update as cargo generation might affect the display
                    requestUIUpdate();
                } catch (Exception e) {
                    System.err.println("Error in generation task: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        Runnable routeTask = () -> {
            if (running.get() && !gameOver.get()) {
                try {
                    long previousBudget = sim.getUserBudget().getBudget();

                    // Call executeRoutes and execute the returned runnable
                    Runnable routeRunner = ctrl.executeRoutes(selectedMap, selectedScenario, sim.getUserBudget(), running);

                    if (routeRunner != null) {
                        routeRunner.run();
                    }

                    // Check if budget changed due to route execution and trigger UI update
                    long currentBudget = sim.getUserBudget().getBudget();
                    if (previousBudget != currentBudget) {
                        requestUIUpdate();
                    }
                } catch (Exception e) {
                    System.err.println("Error in route task: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        // Schedule tasks with proper error handling
        try {
            timeScheduler.scheduleAtFixedRate(timeTask, 5, 20, TimeUnit.SECONDS); // Start after 5 seconds

            generationScheduler.scheduleAtFixedRate(generationTask, 10, 10, TimeUnit.SECONDS);

            routeScheduler.scheduleAtFixedRate(routeTask, 15, 5, TimeUnit.SECONDS); // Start after 15 seconds
        } catch (Exception e) {
            System.err.println("Error starting simulation threads: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Request a UI update - safe to call from any thread
     */
    public void requestUIUpdate() {
        if (controller != null) {
            controller.requestUIUpdate();
        }
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
        System.out.println("Stopping simulation...");
        running.set(false);

        // Shutdown schedulers with proper timeout
        shutdownScheduler(timeScheduler, "Time");
        shutdownScheduler(generationScheduler, "Generation");
        shutdownScheduler(routeScheduler, "Route");

        if (sim != null) {
            try {
                ctrl.updateSimulation(
                        selectedMap,
                        selectedScenario,
                        sim.getCurrentYear(),
                        sim.getUserBudget()
                );
                System.out.println("Simulation state saved.");

                if (gameOver.get()) {
                    ctrl.resetGame(selectedMap, selectedScenario, sim.getUserBudget());
                    System.out.println("Game reset completed.");
                }

                if (new SimulationFile().save()) {
                    System.out.println("Game saved to file.");
                }
            } catch (Exception e) {
                System.err.println("Error saving simulation: " + e.getMessage());
                e.printStackTrace();
            }
        }

        sim = null;
        System.out.println("Simulation stopped.");
    }

    private void shutdownScheduler(ScheduledExecutorService scheduler, String name) {
        if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("Shutting down " + name + " scheduler...");
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("Force shutting down " + name + " scheduler...");
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.err.println("Interrupted while shutting down " + name + " scheduler");
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // Getters remain the same
    public Simulation getSimulation() {
        return sim;
    }

    public SimulationController getController() {
        return ctrl;
    }

    public Scenario getSelectedScenario() {
        return selectedScenario;
    }

    public Map getSelectedMap() {
        return selectedMap;
    }

    public AtomicReference<AtomicIntegerArray> getTimePeriod() {
        return timePeriod;
    }
}