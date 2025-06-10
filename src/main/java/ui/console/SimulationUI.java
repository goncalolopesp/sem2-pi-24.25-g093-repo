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

    /**
     * Constructs a new SimulationUI instance based on the specified scenario.
     * Initializes the selected scenario and retrieves the corresponding map
     * from the repository. If the selected map is not found, an exception is thrown.
     *
     * @param selectedScenario the scenario to be used for the simulation; must not be null
     * @throws IllegalArgumentException if the provided scenario is null or if the
     *                                  map for the scenario cannot be found
     */
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

    /**
     * Starts the simulation UI by creating a new instance of the JavaFX Stage.
     * This method is an entry point that delegates to the overloaded start method
     * responsible for initializing and displaying the application stage.
     *
     * @throws Exception if an error occurs during the initialization or loading of UI components
     */
    public void start() throws Exception {
        start(new Stage());
    }

    /**
     * Initializes and displays the main stage for the railway simulation application.
     * This method sets up the JavaFX user interface, including loading FXML resources,
     * creating controllers, and configuring the primary stage.
     *
     * The method initializes the simulation and assigns the necessary components to the
     * controller. It sets up the primary scene, configures the stage properties, and
     * handles the application close request to ensure proper cleanup of resources.
     * Additionally, this method starts simulation threads once the UI is fully configured.
     *
     * @param stage the primary stage for the JavaFX application, provided by the platform; must not be null
     * @throws Exception if an error occurs during initialization or while loading FXML resources
     */
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

    /**
     * Initializes the simulation by checking for existing simulations or creating a new one.
     * This method retrieves the current user, verifies selected scenario and map,
     * and initializes the simulation object accordingly. If an existing simulation
     * is found, it resumes from the last saved state. Otherwise, it creates a new
     * simulation and sets up the initial game parameters.
     *
     * @return true if the simulation was successfully initialized, false if no user is logged in
     */
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

    /**
     * Starts and manages the simulation threads responsible for time updates, cargo generation,
     * and route execution. This method initializes and schedules tasks to run periodically, ensuring
     * the simulation progresses and the UI is updated accordingly.
     *
     * Each thread performs the following roles:
     * - TimeScheduler: Advances the simulation time, updates scenarios, and manages end-of-game conditions.
     * - GenerationScheduler: Handles the generation of cargo items based on the selected map and scenario.
     * - RouteScheduler: Executes active routes, adjusts the user's budget, and requests budget-related UI updates.
     *
     * Pre-existing schedulers are shut down prior to creating new ones to avoid conflicts.
     * The newly created schedulers run as daemon threads.
     *
     * Time Task:
     * - Advances the simulation year by interacting with the controller.
     * - Handles any associated runnable functions and always invokes a UI update.
     *
     * Generation Task:
     * - Invokes the controller to generate cargo for the selected map and scenario.
     * - Triggers a UI update as cargo generation may affect the simulation display.
     *
     * Route Task:
     * - Executes active routes using the controller logic.
     * - Monitors changes in the user's budget, updating the UI if necessary.
     *
     * Schedulers operate with fixed intervals:
     * - Time Task: Executes every 20 seconds, starting after an initial delay of 5 seconds.
     * - Generation Task: Executes every 10 seconds, starting after an initial delay of 10 seconds.
     * - Route Task: Executes every 5 seconds, starting after an initial delay of 15 seconds.
     *
     * Any errors encountered during task execution or scheduling are logged to the standard error stream.
     */
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
                    Runnable routeRunner = ctrl.executeRoutes(selectedMap, selectedScenario, sim.getUserBudget(), running, sim.getCurrentYear());

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
     * Requests an update of the user interface by invoking the corresponding
     * method on the associated controller, if it exists. This method acts as a
     * delegate to ensure that any necessary UI changes are properly handled
     * through the controller.
     *
     * This method is typically used to propagate updates in response to changes
     * within the simulation or application state, ensuring the user interface
     * reflects the most up-to-date information. If the controller is not set,
     * calling this method will have no effect.
     *
     * Thread-Safety:
     * This method may be safely called from any thread, as the underlying
     * controller's implementation ensures proper handling of UI updates.
     */
    public void requestUIUpdate() {
        if (controller != null) {
            controller.requestUIUpdate();
        }
    }

    /**
     * Pauses the current simulation by setting the running state to false.
     * This method ensures that any ongoing simulation processes are halted,
     * and prints a notification to the console indicating the paused state.
     *
     * Thread-Safety:
     * This method is thread-safe as it uses an AtomicBoolean to manage the running state.
     *
     * Side Effects:
     * - Updates the `running` field to false.
     */
    public void pauseGame() {
        running.set(false);
    }

    /**
     * Resumes the current simulation by setting the running state to true.
     * This method ensures that any previously paused simulation processes
     * are restarted, and prints a notification to the console indicating
     * the resumed state.
     *
     * Thread-Safety:
     * This method is thread-safe as it utilizes an AtomicBoolean to manage
     * the running state.
     *
     * Side Effects:
     * - Updates the `running` field to true.
     */
    public void resumeGame() {
        running.set(true);
    }

    /**
     * Stops the current simulation, ensuring proper cleanup of resources, saving the simulation state,
     * and resetting game data if necessary. This method handles the following tasks:
     *
     * - Marks the simulation as no longer running by updating the `running` state to false.
     * - Shuts down all active schedulers (Time, Generation, and Route) with a controlled timeout to avoid
     *   resource leaks or unexpected behavior.
     * - Attempts to save the current simulation state using the associated controller, including the map,
     *   scenario, current year, and user budget.
     * - If the game-over condition is met, performs a game reset by clearing user-related data
     *   (such as locomotives and stations) and reinitializing the game parameters.
     * - Saves the game state to a file using a separate utility.
     * - Logs any errors encountered during the saving or resetting process for troubleshooting purposes.
     * - Sets the simulation object to null to indicate it is no longer active.
     *
     * Thread-Safety:
     * This method should be used in a controlled environment where no concurrent modifications
     * are performed on the simulation state or related objects while this method is executing.
     *
     * Side Effects:
     * - Updates the `running` state to false.
     * - Performs I/O operations, including saving the simulation state to persistent storage.
     * - Resets user game data in case of a game-over condition.
     * - Outputs information and error messages to the console for debugging.
     */
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

    /**
     * Shuts down the specified scheduler associated with a specific name, ensuring a graceful termination.
     * If the scheduler does not shut down within the given timeout, it forces termination.
     * Handles potential interruptions during the shutdown process.
     *
     * @param scheduler the ScheduledExecutorService instance to be shut down; may be null.
     * @param name the name associated with the scheduler, used for logging purposes; must not be null.
     */
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

    /**
     * Retrieves the current simulation instance associated with the SimulationUI.
     *
     * This method provides access to the `Simulation` object that represents
     * the core of the railway simulation. It can be used to interact with or
     * query the simulation state, such as retrieving details about the active
     * map, scenario, or simulation parameters.
     *
     * @return the current `Simulation` instance if initialized; otherwise, null.
     */
    public Simulation getSimulation() {
        return sim;
    }

    /**
     * Retrieves the current simulation controller associated with the SimulationUI.
     *
     * This method provides access to the `SimulationController` that is responsible
     * for managing the logic and interactions within the simulation. The controller
     * serves as a central point for controlling simulation behavior and facilitating
     * communication between the UI and simulation core.
     *
     * @return the `SimulationController` instance if initialized; otherwise, null.
     */
    public SimulationController getController() {
        return ctrl;
    }

    /**
     * Retrieves the currently selected simulation scenario.
     *
     * This method returns the `Scenario` object that represents the active
     * scenario being used in the simulation. The selected scenario is
     * typically initialized during the construction of the `SimulationUI`
     * instance and may determine the configurations, rules, and environment
     * the simulation operates under.
     *
     * @return the currently selected `Scenario` if one has been set; otherwise, null.
     */
    public Scenario getSelectedScenario() {
        return selectedScenario;
    }

    /**
     * Retrieves the currently selected map associated with the simulation UI.
     *
     * This method provides access to the `Map` object that has been chosen for
     * the simulation, allowing interaction with map-specific data or configurations.
     * The selected map is typically initialized based on the scenario selection during
     * the SimulationUI's setup phase.
     *
     * @return the currently selected `Map` object if it has been set; otherwise, null.
     */
    public Map getSelectedMap() {
        return selectedMap;
    }

    /**
     * Retrieves the current time period represented as an AtomicReference containing an AtomicIntegerArray.
     *
     * @return an AtomicReference containing an AtomicIntegerArray that represents the time period
     */
    public AtomicReference<AtomicIntegerArray> getTimePeriod() {
        return timePeriod;
    }
}