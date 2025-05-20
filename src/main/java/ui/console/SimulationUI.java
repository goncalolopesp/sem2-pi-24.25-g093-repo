package ui.console;

import controller.SimulationController;
import domain.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;
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
import repository.BudgetRepository;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Launches the user interface responsible for making the game playable.
 */
public class SimulationUI implements Runnable {

    private final User currentUser = CurrentUser.getInstance().getUser();
    private final SimulationController ctrl = new SimulationController();
    private final BudgetRepository budgetRepository = Repositories.getInstance().getBudgetRepository();
    Budget userBudget;

    @Override
    public void run() {
        Map selectedMap = ctrl.getMap();
        if (selectedMap == null) {
            System.out.println("No map selected");
            return;
        }

        Scenario selectedScenario = ctrl.getScenario(selectedMap);

        if (selectedScenario == null) {
            System.out.println("No scenario selected");
            return;
        }

        userBudget = checkOrCreateBudget(selectedMap, selectedScenario);

        // With something like:
        int startYear = userBudget.getCurrentYear();
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

        ScheduledExecutorService timeScheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService generationScheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable timeTask = ctrl.passTime(selectedScenario, timePeriod, running, options, timeScheduler, gameOver,userBudget);
        Runnable generationTask = ctrl.generateCargo(selectedMap,selectedScenario, timePeriod, running, generationScheduler);

        timeScheduler.scheduleAtFixedRate(timeTask, 60, 60, TimeUnit.SECONDS);
        generationScheduler.scheduleAtFixedRate(generationTask, 10, 10, TimeUnit.SECONDS);

        do {
            if (running.get()) {
                ctrl.displayMapData(selectedMap, selectedScenario);
                if (currentUser != null) {
                    System.out.println("Your budget is: " + userBudget.getBudget());
                }
                if (options.isEmpty()) {
                    options.add(new MenuItem("Build A Station", new BuildStationUI(selectedMap,selectedScenario,userBudget)));
                    options.add(new MenuItem("Upgrade A Station (Type)", new UpgradeStationTypeUI(selectedMap,selectedScenario, userBudget)));
                    options.add(new MenuItem("Upgrade A Station (Building)", new UpgradeStationUI(selectedMap, selectedScenario,timePeriod, userBudget)));
                    options.add(new MenuItem("Buy a locomotive", new BuyLocomotiveUI(selectedScenario,selectedMap)));
                    options.add(new MenuItem("List my locomotives", new ListLocomotiveUI()));
                    options.add(new MenuItem("List my stations", new ListStationsUI()));
                    options.add(new MenuItem("Associate a locomotive to a route", new AssignTrainUI(selectedMap,selectedScenario, userBudget)));
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

            // Show whatever menu we built and get the user’s choice
            option.set( Utils.showAndSelectIndex(options, "\n\n--- GAME MENU --------------------------") );

            // Execute it (or handle “Cancel” = -1)
            if (option.get() >= 0 && option.get() < options.size()) {
                String label = options.get(option.get()).toString();

                // for any “real” option (not Pause/Resume) we pause the game…
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
                // This branch will also catch the “0 – Cancel” keypress
                running.set(false);
                timeScheduler.shutdownNow();
                generationScheduler.shutdownNow();
                System.out.println("Game stopped.");
                if (gameOver.get()) {
                    ctrl.resetGame(selectedMap, selectedScenario, userBudget);
                }
            }
        } while (option.get() != -1);
    }

    /**
     * Checks if a budget exists for the current user, map and scenario.
     * If not, creates a new one with 1000 as the initial value.
     *
     * @param selectedMap The selected map
     * @param selectedScenario The selected scenario
     * @return The budget for the user
     */
    private Budget checkOrCreateBudget(Map selectedMap, Scenario selectedScenario) {
        Budget budget = null;

        if (currentUser != null) {
            Optional<Budget> existingBudget = budgetRepository.getUserBudgetFromScenarioInMap(
                    selectedScenario, selectedMap, currentUser);

            if (existingBudget.isPresent()) {
                budget = existingBudget.get();
            } else {
                budget = new Budget(1000, selectedMap, selectedScenario, currentUser);
                budgetRepository.createBudget(budget);
            }
        }
        return budget;
    }
}
