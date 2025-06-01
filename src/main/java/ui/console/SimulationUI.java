package ui.console;

import controller.SimulationController;
import domain.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import repository.MapRepository;
import repository.Repositories;
import ui.console.menu.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Launches the user interface responsible for making the game playable.
 */
public class SimulationUI implements Runnable {

    private final SimulationController ctrl = new SimulationController();
    private final MapRepository mapRepo = Repositories.getInstance().getMapRepository();

    private Simulation sim;

    /**
     * Executes the main logic of the SimulationUI class to manage and run the simulation.
     * This method is responsible for loading existing simulations or creating new ones,
     * initializing game components, managing time progression, cargo generation logic,
     * and presenting a user interface for interactions with options.
     *
     * The method verifies the prerequisites required to run a simulation, including the
     * presence of a logged-in user, a selected map, and a scenario. It checks for existing
     * simulations in the repository and resumes or creates one as necessary.
     * After setting up the simulation, it handles periodic tasks such as time progression
     * and cargo generation using separate scheduled threads.
     *
     * A navigational game menu is presented to the user, allowing actions such as building
     * or upgrading stations, managing locomotives, assigning trains, and pausing or resuming
     * the game. The menu dynamically adjusts based on the simulation's running state.
     *
     * Upon exit or pause, the method ensures the simulation state is saved, stops periodic
     * tasks, and provides the option to reset the game if required.
     */
    @Override
    public void run() {

        System.out.println("\nChecking for existing simulations...");
        System.out.println("Available simulations: " + ctrl.getSimulations().size());
        System.out.println();

        User currentUser = CurrentUser.getInstance().getUser();
        if (currentUser == null) {
            System.out.println("No user logged in");
            return;
        }

        Scenario selectedScenario = ctrl.getScenario();
        if (selectedScenario == null) {
            System.out.println("No scenario selected.");
            return;
        }

        String mapName = selectedScenario.getSelectedMap();
        Map selectedMap = mapRepo.getMaps().stream()
                .filter(map -> map.getMapName().equals(mapName))
                .findFirst()
                .orElse(null);

        if (selectedMap == null) {
            System.out.println("Map not found for scenario: " + mapName);
            return;
        }

        if (selectedScenario == null) {
            System.out.println("No scenario selected");
            return;
        }

        if (sim == null) {
            // DEBUG: Let's see what's in the repository
            System.out.println("\n=== DEBUGGING SIMULATION LOADING ===");
            List<Simulation> allSims = ctrl.getSimulations();
            System.out.println("Total simulations in repository: " + allSims.size());
            System.out.println();

            for (int i = 0; i < allSims.size(); i++) {
                Simulation s = allSims.get(i);
                System.out.println("Simulation " + i + ":");
                System.out.println("  Map: '" + s.getSelectedMap().getMapName() + "'");
                System.out.println("  Scenario: '" + s.getSelectedScenario().getScenarioName() + "'");
                System.out.println("  User: '" + (s.getUser() != null ? s.getUser().getEmail() : "null") + "'");
                System.out.println("  Year: " + s.getCurrentYear());
                System.out.println();
            }

            System.out.println("=== SEARCHING FOR SIMULATION ===");
            System.out.println("Looking for map: '" + selectedMap.getMapName() + "'");
            System.out.println("Looking for scenario: '" + selectedScenario.getScenarioName() + "'");
            System.out.println("Looking for user: '" + (currentUser != null ? currentUser.getEmail() : "null") + "'");
            System.out.println();

            // Try to find existing simulation
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
            System.out.println("=== END DEBUG ===\n");
        }

        if (sim.getUserBudget().getUser() != null) {
            currentUser.setLocomotives(sim.getUserBudget().getUser().getLocomotives());
        }

        int startYear = sim.getCurrentYear();
        int endYear   = selectedScenario.getTimeRestrictions()[1];
        AtomicReference<AtomicIntegerArray> timePeriod =
                new AtomicReference<>(new AtomicIntegerArray(new int[]{ startYear, endYear }));

        if (startYear == selectedScenario.getTimeRestrictions()[0]) {
            System.out.println("Game started on year " + startYear + " and will finish on year " + endYear);
        } else {
            System.out.println("Resuming game on year " + startYear + ", will finish on year " + endYear);
        }

        AtomicBoolean running = new AtomicBoolean(true);
        AtomicBoolean gameOver = new AtomicBoolean(false);
        AtomicInteger option = new AtomicInteger();

        List<MenuItem> options = new ArrayList<>();

        // Create all schedulers
        ScheduledExecutorService timeScheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService generationScheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService routeScheduler = Executors.newSingleThreadScheduledExecutor();

        // Create all tasks
        Runnable timeTask = ctrl.passTime(selectedScenario, timePeriod, running, options, timeScheduler, gameOver, sim.getUserBudget(), sim);
        Runnable generationTask = ctrl.generateCargo(selectedMap, selectedScenario, timePeriod, running, generationScheduler);
        Runnable routeTask = ctrl.executeRoutes(selectedMap, selectedScenario, sim.getUserBudget(), running);

        // Schedule all tasks
        timeScheduler.scheduleAtFixedRate(timeTask, 20, 20, TimeUnit.SECONDS);
        generationScheduler.scheduleAtFixedRate(generationTask, 10, 10, TimeUnit.SECONDS);
        routeScheduler.scheduleAtFixedRate(routeTask, 5, 5, TimeUnit.SECONDS); // Execute routes every 5 seconds

        do {
            if (running.get()) {
                ctrl.displayMapData(selectedMap, selectedScenario);
                if (currentUser != null) {
                    System.out.println("Your budget is: " + sim.getUserBudget().getBudget());
                }
                if (options.isEmpty()) {
                    options.add(new MenuItem("Build A Station", new BuildStationUI(selectedMap,selectedScenario, sim.getUserBudget())));
                    options.add(new MenuItem("Upgrade A Station (Type)", new UpgradeStationTypeUI(selectedMap,selectedScenario, sim.getUserBudget())));
                    options.add(new MenuItem("Upgrade A Station (Building)", new UpgradeStationUI(selectedMap, selectedScenario,timePeriod, sim.getUserBudget())));
                    options.add(new MenuItem("Buy a locomotive", new BuyLocomotiveUI(selectedScenario, selectedMap, sim.getUserBudget())));
                    options.add(new MenuItem("List my locomotives", new ListLocomotiveUI()));
                    options.add(new MenuItem("List my stations", new ListStationsUI()));
                    options.add(new MenuItem("Associate a locomotive to a route", new AssignTrainUI(selectedMap,selectedScenario, sim.getUserBudget())));
                    options.add(new MenuItem("Pause Game", () -> {
                        running.set(false);
                        options.clear();
                        System.out.println("Game paused.");
                    }));
                }
            } else {
                options.clear();
                options.add(new MenuItem("Resume Game", () -> {
                    running.set(true);
                    options.clear();
                    System.out.println("Game resumed.");
                }));
            }

            // Show whatever menu we built and get the user's choice
            option.set( Utils.showAndSelectIndex(options, "\n\n--- GAME MENU --------------------------") );

            // Execute it (or handle "Cancel" = -1)
            if (option.get() >= 0 && option.get() < options.size()) {
                String label = options.get(option.get()).toString();

                // for any "real" option (not Pause/Resume) we pause the game…
                if (!label.equals("Pause Game") && !label.equals("Resume Game")) {
                    boolean wasRunning = running.get();
                    if (wasRunning) {
                        running.set(false);
                        System.out.println("[Game paused while you work]");
                    }

                    // run your submenu (e.g. BuildStationUI, etc)
                    options.get(option.get()).run();

                    // then resume if we were running
                    if (wasRunning) {
                        running.set(true);
                        System.out.println("[Game resumed]");
                    }
                }
                else {
                    // Pause Game or Resume Game just toggle running themselves
                    options.get(option.get()).run();
                }
            }
            else if (option.get() == -1) {
                // This branch will also catch the "0 – Cancel" keypress
                running.set(false);

                // Shutdown all schedulers
                timeScheduler.shutdownNow();
                generationScheduler.shutdownNow();
                routeScheduler.shutdownNow();

                ctrl.updateSimulation(
                        sim.getSelectedMap(),
                        sim.getSelectedScenario(),
                        sim.getCurrentYear(),
                        sim.getUserBudget()
                );
                sim = null;
                System.out.println("Game stopped.");
                if (gameOver.get()) {
                    ctrl.resetGame(selectedMap, selectedScenario, sim.getUserBudget());
                }
                if (new SimulationFile().save()) {
                    System.out.println("Game saved.");
                }
            }
        } while (option.get() != -1);
    }
}