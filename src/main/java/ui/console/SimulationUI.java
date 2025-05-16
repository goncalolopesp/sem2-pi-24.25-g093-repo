package ui.console;

import controller.SimulationController;
import domain.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import ui.console.menu.MenuItem;

import java.util.ArrayList;
import java.util.List;
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

    private final User currentUser = CurrentUser.getInstance().getUser();
    private final SimulationController ctrl = new SimulationController();

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

        AtomicReference<AtomicIntegerArray> timePeriod = new AtomicReference<>(new AtomicIntegerArray(selectedScenario.getTimeRestrictions()));

        AtomicBoolean running = new AtomicBoolean(true);
        AtomicBoolean gameOver = new AtomicBoolean(false);
        AtomicInteger option = new AtomicInteger();

        System.out.println("Game started on year " + timePeriod.get().get(0) + " and will finish on year " + timePeriod.get().get(1));

        List<MenuItem> options = new ArrayList<>();

        ScheduledExecutorService timeScheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService generationScheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable timeTask = ctrl.passTime(selectedMap,selectedScenario, timePeriod, running, options, timeScheduler, gameOver);
        Runnable generationTask = ctrl.generateCargo(selectedMap, timePeriod, running, generationScheduler);

        timeScheduler.scheduleAtFixedRate(timeTask, 5, 5, TimeUnit.SECONDS);
        generationScheduler.scheduleAtFixedRate(generationTask, 2, 2, TimeUnit.SECONDS);

        do {
            if (running.get()) {
                // Game is running: show game actions
                ctrl.displayMapData(selectedMap);
                if (currentUser != null) {
                    System.out.println("Your budget is: " + currentUser.getBudgetValue());
                }

                if (options.isEmpty()) {
                    options.add(new MenuItem("Build A Station", new BuildStationUI(selectedMap)));
                    options.add(new MenuItem("Upgrade A Station (Type)", new UpgradeStationTypeUI(selectedMap)));
                    options.add(new MenuItem("Upgrade A Station (Building)", new UpgradeStationUI(selectedMap, timePeriod)));
                    options.add(new MenuItem("Buy a locomotive", new BuyLocomotiveUI(selectedScenario)));
                    options.add(new MenuItem("List my locomotives", new ListLocomotiveUI()));
                    options.add(new MenuItem("Associate a locomotive to a route", new AssignTrainUI(selectedMap)));
                    options.add(new MenuItem("Pause Game", () -> {
                        running.set(false);
                        options.clear();
                        System.out.println("Game paused.");
                    }));
                }

            } else {
                // Game is paused: only show resume
                options.add(new MenuItem("Resume Game", () -> {
                    running.set(true);
                    options.clear();
                    System.out.println("Game resumed.");
                }));
            }

            option.set(Utils.showAndSelectIndex(options, "\n\n--- GAME MENU --------------------------"));

            if (option.get() >= 0 && option.get() < options.size()) {
                // Only pause/resume if the selected option isn't Pause/Resume
                String selectedLabel = options.get(option.get()).toString();

                if (!selectedLabel.equals("Pause Game") && !selectedLabel.equals("Resume Game")) {
                    boolean wasRunning = running.get();  // Store previous state
                    if (wasRunning) running.set(false);  // Pause timer

                    options.get(option.get()).run();     // Run the menu item

                    if (wasRunning) running.set(true);   // Resume timer if it was running before
                } else {
                    options.get(option.get()).run();     // Handle Pause/Resume directly
                }
            } else if (option.get() == -1) {
                running.set(false); timeScheduler.shutdownNow(); System.out.println("Game stopped.");
                if (gameOver.get()) {
                    ctrl.resetGame(selectedMap);
                }
            }
        } while (option.get() != -1);
    }
}
