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

    /**
     * Displays the map data by printing a visual representation of the map.
     * The map is represented as a grid with cities and stations marked on it,
     * and other areas filled with default symbols.
     *
     * @param map the map object containing the details of the map to be displayed
     * @param scenario the scenario object associated with the map, influencing how stations are displayed
     */
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

    /**
     * Advances the simulation's timeline by manipulating the current time period and performing related updates
     * such as maintenance costs, locomotive updates, and checking for the end of the scenario.
     * It also interacts with the user interface to provide updates on the game's state.
     *
     * @param selectedScenario the scenario associated with the current simulation
     * @param timePeriod an atomic reference to an array holding the current and final year of the scenario
     * @param running a flag indicating whether the simulation is actively running
     * @param options a list of menu items representing possible user actions in the game
     * @param scheduler the executor service responsible for scheduling game ticks
     * @param gameOver a flag indicating whether the game has reached its ending condition
     * @param budget the budget available to the player, utilized within the simulation
     * @param sim the simulation object containing state information such as the current year
     * @return a Runnable encapsulating the operations to be performed during each tick of the simulated time
     */
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

    /**
     * Generates a Runnable task for producing cargo to be added to stations based on the selected map and scenario.
     * The cargo generation process is limited by the provided time period and can be stopped using the running flag.
     * The task shuts down the given scheduler upon completion of the defined time period.
     *
     * @param selectedMap the map object representing the current simulation
     * @param selectedScenario the scenario object influencing the cargo generation process
     * @param timePeriod an atomic reference to an array containing the current and final time periods
     * @param running a flag indicating whether cargo generation is actively running
     * @param generationScheduler the scheduler responsible for managing cargo generation timing
     * @return a Runnable task responsible for executing the cargo generation logic
     */
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

    /**
     * Adds cargo to stations based on the selected map and scenario.
     * The method considers station capacity and certain scenario-specific
     * rules (e.g., "School Program") while distributing cargo among stations.
     * If a station reaches its maximum capacity, notifications are managed accordingly.
     *
     * @param selectedMap the map object representing the current simulation
     * @param selectedScenario the scenario object influencing cargo distribution logic
     */
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

    /**
     * Resets the current game state by performing actions such as clearing locomotives,
     * resetting the budget, and removing user-specific stations from the selected map and scenario.
     *
     * @param selectedMap the map selected for the current simulation
     * @param selectedScenario the scenario selected for the current simulation
     * @param budget the budget associated with the player's game session, which will be reset
     */
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

    /**
     * Populates the given grid with visual representations of cities and their associated house blocks
     * based on the map's data.
     * Cities corresponding to the given map are retrieved, and their locations are marked on the grid
     * with specific symbols. City centers are marked as "🌆", and house blocks are marked as "🏠".
     *
     * @param grid a 2D array representing the map's grid where visual city elements will be added
     * @param map the map object containing information about the cities and their locations
     */
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

    /**
     * Populates the given grid with visual representations of stations based on their type,
     * owner, map name, and scenario. Stations are displayed with specific symbols
     * depending on their type (depot, terminal, station) and orientation.
     *
     * @param grid a 2D array representing the map's grid where stations will be added
     * @param map the map object representing the selected map of the simulation
     * @param scenario the scenario object influencing station-related rules and display
     * @param width the width of the grid defining the map's horizontal boundary
     * @param height the height of the grid defining the map's vertical boundary
     */
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

    /**
     * Fills a specific area of the grid with visual symbols, representing either a station center
     * (marked as "🚉") or the station area (marked as "🅿️"). The filling process ensures that
     * the content stays within the defined grid boundaries.
     *
     * @param grid the 2D array that represents the map grid to be painted
     * @param maxW the maximum width limit of the grid
     * @param maxH the maximum height limit of the grid
     * @param cx the x-coordinate of the station to be symbolized as "🚉"
     * @param cy the y-coordinate of the station to be symbolized as "🚉"
     * @param ox the starting x-coordinate offset for painting the area
     * @param oy the starting y-coordinate offset for painting the area
     * @param w the width of the area to be painted
     * @param h the height of the area to be painted
     */
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

    /**
     * Retrieves a map from the repository. If no maps are available,
     * it returns null after displaying an appropriate message. If there
     * are maps available, the user is prompted to select one.
     *
     * @return the selected map from the repository, or null if no maps are available or no selection is made
     */
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

    /**
     * Retrieves a scenario associated with the selected map from the repository.
     * If no scenarios are available for the specified map, a message is displayed,
     * and null is returned. If scenarios are available, the user is prompted to
     * select one. The selected scenario is then returned.
     *
     * @param selectedMap the map object for which scenarios are to be retrieved
     * @return the selected scenario associated with the specified map,
     *         or null if no scenarios are available or no selection is made
     */
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

    /**
     * Pays for the maintenance costs of all locomotives owned by the current user
     * within the constraints of the provided budget. The method evaluates each locomotive's
     * maintenance and fuel costs, updating their availability and adjusting the budget accordingly.
     *
     * @param budget the budget associated with the current user, used to pay for locomotive maintenance and fuel costs
     */
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

    /**
     * Removes locomotives owned by the current user that are outdated based on the specified year.
     * Locomotives with an end year of operation less than or equal to the given year will be removed,
     * while the others are retained. A message is logged for each removed locomotive.
     *
     * @param year the cutoff year; locomotives with an end year of operation less than or equal to this year
     *             will be considered outdated and removed
     */
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

    /**
     * Retrieves a list of all simulations from the simulation repository.
     *
     * @return a list of Simulation objects available in the repository
     */
    public List<Simulation> getSimulations() {
        return simulationRepo.getSimulations();
    }

    /**
     * Creates a new simulation using the provided map, scenario, year, and user budget.
     * The simulation is added to the simulation repository and returned as the created object.
     *
     * @param selectedMap the map object selected for the simulation
     * @param selectedScenario the scenario object selected for the simulation
     * @param currentYear the year in which the simulation starts
     * @param userBudget the budget allocated to the user for the simulation
     * @return the newly created Simulation object
     */
    public Simulation createSimulation(Map selectedMap, Scenario selectedScenario, int currentYear, Budget userBudget) {
        return simulationRepo.add(new Simulation(selectedMap, selectedScenario, currentUser, currentYear, userBudget));
    }

    /**
     * Updates the simulation with the provided map, scenario, new year, and new budget.
     *
     * @param map       The map object associated with the simulation.
     * @param scenario  The scenario under which the simulation should operate.
     * @param newYear   The new year to update the simulation to.
     * @param newBudget The budget details to be applied to the simulation.
     */
    public void updateSimulation(Map map,
                                 Scenario scenario,
                                 int newYear,
                                 Budget newBudget) {
        simulationRepo.updateSimulation(map, scenario, currentUser, newYear, newBudget);
    }

    /**
     * Finds a simulation based on the provided map and scenario.
     *
     * @param map the map containing key-value pairs for searching the simulation
     * @param scenario the scenario used as part of the search criteria
     * @return an Optional containing the Simulation if found, or an empty Optional if not found
     */
    public Optional<Simulation> findBy(Map map, Scenario scenario) {
        return simulationRepo.findBy(map, scenario, currentUser);
    }
}
