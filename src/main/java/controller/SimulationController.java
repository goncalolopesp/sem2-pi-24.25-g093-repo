package controller;

import domain.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import repository.*;
import ui.console.menu.MenuItem;

import domain.User;
import domain.City;
import domain.CurrentUser;
import domain.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SimulationController {

    private final User currentUser = CurrentUser.getInstance().getUser();
    private final SimulationRepository simulationRepo = Repositories.getInstance().getSimulationRepository();
    private final MapRepository mapRepo = Repositories.getInstance().getMapRepository();
    private final ScenarioRepository scenarioRepo = Repositories.getInstance().getScenarioRepository();
    private final StationRepository stationrepo = Repositories.getInstance().getStationRepository();
    private final CityRepository cityRepo = Repositories.getInstance().getCityRepository();
    private final BudgetRepository budgetRepo = Repositories.getInstance().getBudgetRepository();
    private final List<City> cities = cityRepo.getCities();

    public void displayMapData(Map map, Scenario scenario) {
        int width = map.getWidth();
        int height = map.getHeight();
        String[][] grid = new String[height + 1][width + 1];
        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                grid[y][x] = "⬛";
            }
        }

        addCitiesToMap(grid, map);
        addStationsToMap(grid, map,scenario, width, height);

        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }

    public Runnable passTime(
            Scenario selectedScenario,
            AtomicReference<AtomicIntegerArray> timePeriod,
            AtomicBoolean running,
            List<MenuItem> options,
            ScheduledExecutorService scheduler,
            AtomicBoolean gameOver,
            Budget budget,
            Simulation sim
    ) {
        return () -> {
            if (running.get()) {
                // advance the year
                int nextYear = timePeriod.get().addAndGet(0, 1);

                // write it back into the same Budget
                sim.setCurrentYear(nextYear);

                if (timePeriod.get().get(0) < timePeriod.get().get(1)) {
                    System.out.print("\nYou have reached year " + nextYear + ". ");
                    removeOutdatedLocomotives(nextYear);
                    payLocomotiveMaintenance(budget);
                    System.out.print("Type your option: ");
                }
            }

            // if we've gone past the scenario's end…
            if (timePeriod.get().get(0) >= timePeriod.get().get(1)) {
                scheduler.shutdownNow();
                running.set(false);              // ← tell the UI we're no longer "running"

                // reset the saved year back to the scenario start
                int resetYear = selectedScenario.getTimeRestrictions()[0];
                sim.setCurrentYear(resetYear);

                timePeriod.set(new AtomicIntegerArray(
                        selectedScenario.getTimeRestrictions()
                ));
                options.clear();
                gameOver.set(true);

                System.out.print("\nGame Over. Final Year was reached. Please insert 0 to return to menu: ");
            }
        };
    }

    public Runnable generateCargo(Map selectedMap,Scenario selectedScenario, AtomicReference<AtomicIntegerArray> timePeriod, AtomicBoolean running, ScheduledExecutorService generationScheduler) {
        return () -> {
            if (running.get()) {
                if (timePeriod.get().get(0) <= timePeriod.get().get(1)) {
                    addCargoToStation(selectedMap,selectedScenario);
                }
            }
            if (timePeriod.get().get(0) > timePeriod.get().get(1)) {
                generationScheduler.shutdownNow();
            }
        };
    }

    public void addCargoToStation(
            Map selectedMap,
            Scenario selectedScenario
    ) {
        String mapName = selectedMap.getMapName();

        for (City city : cities) {
            int totalGeneratedCargo = city.getTotalGeneratedCargo();

            stationrepo.getStations().stream()
                    .filter(s -> s.getMapName().equals(mapName))
                    .filter(s -> s.getScenarioName().equals(selectedScenario.getScenarioName()))
                    .filter(s -> s.getOwnerPlayer().equals(currentUser.getEmail()))
                    .filter(s -> s.getName().contains(city.getName()))
                    .forEach(station -> {
                        // 1) If there's still room…
                        if (station.getCapacityOwned() < station.getCapacity()) {

                            // a) refill the flag so at some future tick we could re-notify again
                            station.setMaxCapacityNotified(false);

                            // b) add cargo (double if School Program)
                            boolean isSchoolProgram = selectedScenario.getHistoricalRestrictions()
                                    .stream()
                                    .anyMatch(r -> r.equalsIgnoreCase("school program"));

                            int toAdd = isSchoolProgram
                                    ? totalGeneratedCargo * 2
                                    : totalGeneratedCargo;
                            station.setCapacityOwned(
                                    station.getCapacityOwned() + toAdd
                            );

                            System.out.printf(
                                    "\nStored %d cargo for station %s. Type your option: ",
                                    toAdd,
                                    station.getName()
                            );

                        }
                        // 2) Otherwise—we're at (or above) capacity…
                        else {
                            // only notify *once* per station
                            if (!station.isMaxCapacityNotified()) {
                                station.setCapacityOwned(station.getCapacity());  // cap it
                                System.out.printf(
                                        "\nMax capacity reached for station %s. Type your option: ",
                                        station.getName()
                                );
                                station.setMaxCapacityNotified(true);
                            }
                        }
                    });
        }
    }

    public void resetGame(Map selectedMap, Scenario selectedScenario, Budget budget) {
        List<Locomotive> l = new ArrayList<>();
        currentUser.setLocomotives(l);
        budget.setBudget(1000);
        stationrepo.removeStationsByMapScenarioAndOwner(
                selectedMap.getMapName(),
                selectedScenario.getScenarioName(),
                currentUser.getEmail()
        );
    }

    private void addCitiesToMap(String[][] grid, Map map) {
        List<City> cities = cityRepo.getCities().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());
        for (City city : cities) {
            grid[city.getCenterY()][city.getCenterX()] = "🌆";
            for (HouseBlock hb : city.getBlocks()) {
                grid[hb.getY()][hb.getX()] = "🏠";
            }
        }
    }

    private void addStationsToMap(String[][] grid, Map map, Scenario scenario, int width, int height) {
        if (currentUser != null) {
            String owner = currentUser.getEmail();
            for (Station st : stationrepo.getStations()) {
                if (!st.getMapName().equals(map.getMapName()) ||
                        !st.getOwnerPlayer().equals(owner) ||
                        !st.getScenarioName().equals(scenario.getScenarioName())) {
                    continue;
                }

                int w, h, ox, oy;
                switch (st.getType()) {
                    case DEPOT:
                        w = h = 3;
                        ox = st.getX() - 1;
                        oy = st.getY() - 1;
                        paintArea(grid, width, height, st.getX(), st.getY(), ox, oy, w, h);
                        break;
                    case TERMINAL:
                        w = h = 5;
                        ox = st.getX() - 2;
                        oy = st.getY() - 2;
                        // paint 5x5
                        paintArea(grid, width, height, st.getX(), st.getY(), ox, oy, w, h);
                        break;
                    case STATION:
                        w = h = 4;
                        ox = st.getX() - 1;
                        oy = st.getY() - 1;
                        int sdx = 1, sdy = 1;
                        switch (st.getOrientation()) {
                            case NW:
                                break;
                            case NE:
                                sdx = 2;
                                break;
                            case SW:
                                sdy = 2; break;
                            case SE:
                                sdx = 2; sdy = 2; break;
                        }
                        for (int dy = 0; dy < h; dy++) {
                            for (int dx = 0; dx < w; dx++) {
                                int gx = ox + dx;
                                int gy = oy + dy;
                                if (gx >= 1 && gx <= width && gy >= 1 && gy <= height) {
                                    grid[gy][gx] = (dx == sdx && dy == sdy) ? "🚉" : "🅿️";
                                }
                            }
                        }
                        break;
                    default:
                        grid[st.getY()][st.getX()] = "🚉";
                }
            }
        }
    }

    private void paintArea(String[][] grid, int maxW, int maxH,
                           int cx, int cy, int ox, int oy, int w, int h) {
        for (int dy = 0; dy < h; dy++) {
            for (int dx = 0; dx < w; dx++) {
                int x = ox + dx;
                int y = oy + dy;
                if (x >= 1 && x <= maxW && y >= 1 && y <= maxH) {
                    grid[y][x] = (x == cx && y == cy) ? "🚉" : "🅿️";
                }
            }
        }
    }

    public Map getMap() {
        List<Map> maps = mapRepo.getMaps();
        if (maps.isEmpty()) {
            System.out.println("No maps available.");
            return null;
        }
        int idx = Utils.showAndSelectIndex(maps, "Select a map:");

        if (idx == -1) {
            return null;
        }

        return maps.get(idx);
    }

    public Scenario getScenario(Map selectedMap) {
        List<Scenario> scenarios = scenarioRepo.getScenarios().stream()
                .filter(s -> s.getSelectedMap().equals(selectedMap.getMapName()))
                .collect(Collectors.toList());
        if (scenarios.isEmpty()) {
            System.out.println("No scenarios available for map '" + selectedMap.getMapName() + "'.");
            return null;
        }
        int idx = Utils.showAndSelectIndex(scenarios, "Select a scenario:");

        if (idx == -1) {
            return null;
        }

        return scenarios.get(idx);
    }

    public void payLocomotiveMaintenance(Budget budget){
        boolean canPay;
        int yearlyBudget;
        int yearlyManagementCost;
        List<Locomotive> canUse = new ArrayList<>();
        for (Locomotive loc : currentUser.getLocomotives()){
            yearlyBudget = loc.getFuelCost();
            yearlyManagementCost = loc.getMaintenanceCost();
            canPay = yearlyManagementCost <= budget.getBudget();

            if (canPay){
                budget.subtractBudget(yearlyManagementCost);
                loc.setAvailability(yearlyBudget <= budget.getBudget());
                canUse.add(loc);
                if (loc.getAvailability())
                    budget.subtractBudget(yearlyBudget);

            }

        }
        currentUser.setLocomotives(canUse);
    }

    public void removeOutdatedLocomotives(int year) {
        List<Locomotive> locomotives = new ArrayList<>();
        for (Locomotive locomotive : currentUser.getLocomotives()) {
            if (!(locomotive.getEndYearOfOperation() <= year)) {
                locomotives.add(locomotive);
            }else{
                System.out.println("\n" + locomotive.getModelName() + " removed from user's locomotives since the end of its operation is " + year);
            }
        }
        currentUser.setLocomotives(locomotives);
    }

    /**
     * Checks if a budget exists for the current user, map and scenario.
     * If not, creates a new one with 1000 as the initial value.
     *
     * @param selectedMap The selected map
     * @param selectedScenario The selected scenario
     * @return The budget for the user
     */
    public Budget checkOrCreateBudget(Map selectedMap, Scenario selectedScenario) {
        Budget budget = null;

        if (currentUser != null) {
            Optional<Budget> existingBudget = budgetRepo.getUserBudgetFromScenarioInMap(
                    selectedScenario, selectedMap, currentUser);

            if (existingBudget.isPresent()) {
                budget = existingBudget.get();
            } else {
                budget = new Budget(1000, selectedMap, selectedScenario, currentUser);
                budgetRepo.createBudget(budget);
            }
        }
        return budget;
    }

    public List<Simulation> getSimulations() {
        return simulationRepo.getSimulations();
    }

    public Simulation createSimulation(Map selectedMap, Scenario selectedScenario, int currentYear, Budget userBudget) {
        return simulationRepo.add(new Simulation(selectedMap, selectedScenario, currentUser, currentYear, userBudget));
    }

    public void updateSimulation(Map map,
                                 Scenario scenario,
                                 int newYear,
                                 Budget newBudget) {
        simulationRepo.updateSimulation(map, scenario, currentUser, newYear, newBudget);
    }

    public Optional<Simulation> findBy(Map map, Scenario scenario) {
        return simulationRepo.findBy(map, scenario, currentUser);
    }
}
