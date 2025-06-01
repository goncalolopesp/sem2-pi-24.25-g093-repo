package controller;

import domain.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import repository.*;
import ui.console.menu.MenuItem;

import domain.User;
import domain.City;
import domain.CurrentUser;
import domain.Map;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SimulationController {

    private static final int PEOPLE_VALUE = 5;
    private static final int MAIL_VALUE = 3;
    private static final double PEOPLE_RATIO = 0.7;

    private final User currentUser = CurrentUser.getInstance().getUser();
    private final SimulationRepository simulationRepo = Repositories.getInstance().getSimulationRepository();
    private final MapRepository mapRepo = Repositories.getInstance().getMapRepository();
    private final IndustryRepository industryRepo = Repositories.getInstance().getIndustryRepository();
    private final CityRepository cityRepo = Repositories.getInstance().getCityRepository();
    private final ScenarioRepository scenarioRepo = Repositories.getInstance().getScenarioRepository();
    private final StationRepository stationrepo = Repositories.getInstance().getStationRepository();
    private final BudgetRepository budgetRepo = Repositories.getInstance().getBudgetRepository();
    private final RouteRepository routeRepo = Repositories.getInstance().getRouteRepository();

    // ... [keeping all existing methods unchanged until here] ...

    /**
     * Executes all active routes for the current user
     */
    public Runnable executeRoutes(Map selectedMap, Scenario selectedScenario, Budget budget, AtomicBoolean running) {
        return () -> {
            if (running.get() && currentUser != null) {
                List<Route> userRoutes = routeRepo.getRoutes().stream()
                        .filter(r -> r.getPlayerEmail().equals(currentUser.getEmail())
                                && r.getMapName().equals(selectedMap.getMapName())
                                && r.getScenarioName().equals(selectedScenario.getScenarioName()))
                        .collect(Collectors.toList());

                for (Route route : userRoutes) {
                    executeRoute(route, selectedScenario, budget);
                }
            }
        };
    }

    private void executeRoute(Route route, Scenario scenario, Budget budget) {
        Train train = route.getTrain();
        if (train == null) return;

        List<Carriage> carriages = train.getCarriages();
        carriages.forEach(c -> c.setCapacityOwned(0));

        int totalPeopleCollected = 0, totalMailCollected = 0;

        for (PointOfRoute point : route.getPoints()) {
            if (isTrainFull(carriages)) break;

            Station station = point.getStation();
            if (station.getCapacityOwned() <= 0) continue;

            int availablePeople = (int) Math.round(station.getCapacityOwned() * PEOPLE_RATIO);
            int availableMail = station.getCapacityOwned() - availablePeople;

            if (point.getCargoTypes().contains(Carriage.Type.PEOPLE)) {
                int collected = collectCargo(carriages, Carriage.Type.PEOPLE, availablePeople);
                totalPeopleCollected += collected;
                availablePeople -= collected;
            }

            if (point.getCargoTypes().contains(Carriage.Type.MAIL)) {
                int collected = collectCargo(carriages, Carriage.Type.MAIL, availableMail);
                totalMailCollected += collected;
                availableMail -= collected;
            }

            station.setCapacityOwned(availablePeople + availableMail);
        }

        boolean isVaccinationCampaign = scenario.getHistoricalRestrictions()
                .stream().anyMatch(r -> r.equalsIgnoreCase("vaccination campaign"));

        int moneyWon = isVaccinationCampaign ?
                ((totalPeopleCollected * PEOPLE_VALUE) * 2) + (totalMailCollected * MAIL_VALUE) :
                (totalPeopleCollected * PEOPLE_VALUE) + (totalMailCollected * MAIL_VALUE);

        if (totalPeopleCollected + totalMailCollected > 0) {
            System.out.printf("\n[Route Complete] Cargo: %d, People: %d, Mail: %d, Earned: %d. Type your option: ",
                    totalPeopleCollected + totalMailCollected, totalPeopleCollected, totalMailCollected, moneyWon);
            budget.addBudget(moneyWon);
        }
    }

    private boolean isTrainFull(List<Carriage> carriages) {
        return carriages.stream().allMatch(c -> c.getCapacityOwned() >= c.getCapacity());
    }

    private int collectCargo(List<Carriage> carriages, Carriage.Type cargoType, int availableCargo) {
        int totalCollected = 0;
        int remainingCargo = availableCargo;

        for (Carriage carriage : carriages) {
            if (carriage.getType() == cargoType && remainingCargo > 0) {
                int spaceLeft = carriage.getCapacity() - carriage.getCapacityOwned();
                if (spaceLeft > 0) {
                    int toCollect = Math.min(remainingCargo, spaceLeft);
                    carriage.setCapacityOwned(carriage.getCapacityOwned() + toCollect);
                    totalCollected += toCollect;
                    remainingCargo -= toCollect;
                }
            }
        }
        return totalCollected;
    }

    // ... [rest of existing methods remain unchanged] ...

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

        addIndustriesToMap(grid, map);
        addCitiesToMap(grid, map);
        addStationsToMap(grid, map,scenario, width, height);

        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }

    private void addIndustriesToMap(String[][] grid, Map map) {
        List<Industry> industries = industryRepo.getIndustries().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());
        for (Industry industry : industries) {
            grid[industry.getLocation().getY()][industry.getLocation().getX()] = "🏭";
        }
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
                int nextYear = timePeriod.get().addAndGet(0, 1);
                sim.setCurrentYear(nextYear);

                if (timePeriod.get().get(0) < timePeriod.get().get(1)) {
                    System.out.println("\nYou have reached year " + nextYear + ". ");
                    Repositories.getInstance().getLocomotiveRepository().getLocomotives().forEach(l -> {
                        if (l.getStartYearOfOperation() == nextYear) {
                            System.out.println("Locomotive " + l.getModelName() + " has become available.");
                        }
                    });
                    removeOutdatedLocomotives(nextYear);
                    payLocomotiveMaintenance(budget);
                    System.out.print("Type your option: ");
                }
            }

            if (timePeriod.get().get(0) >= timePeriod.get().get(1)) {
                scheduler.shutdownNow();
                running.set(false);
                int resetYear = selectedScenario.getTimeRestrictions()[0];
                sim.setCurrentYear(resetYear);
                timePeriod.set(new AtomicIntegerArray(selectedScenario.getTimeRestrictions()));
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

    public void addCargoToStation(Map selectedMap, Scenario selectedScenario) {
        String mapName = selectedMap.getMapName();
        List<Station> stations = stationrepo.getStations();

        // Track stations that have reached max capacity to print warning only once
        Set<String> maxCapacityStations = new HashSet<>();

        addCityCargo(stations, mapName, selectedScenario, maxCapacityStations);
        addIndustryCargo(stations, mapName, selectedScenario, maxCapacityStations);
    }

    public void addCityCargo(List<Station> stations, String mapName, Scenario selectedScenario, Set<String> maxCapacityStations) {
        List<City> cities = cityRepo.getCities();

        for (City city : cities) {
            int totalGeneratedCargo = city.getTotalGeneratedCargo();

            stations.stream()
                    .filter(s -> s.getMapName().equals(mapName))
                    .filter(s -> s.getScenarioName().equals(selectedScenario.getScenarioName()))
                    .filter(s -> s.getOwnerPlayer().equals(currentUser.getEmail()))
                    .filter(s -> s.getName().contains(city.getName()))
                    .forEach(station -> {
                        int currentCapacity = station.getCapacityOwned();
                        int maxCapacity = station.getCapacity();

                        if (currentCapacity < maxCapacity) {
                            boolean isSchoolProgram = selectedScenario.getHistoricalRestrictions()
                                    .stream()
                                    .anyMatch(r -> r.equalsIgnoreCase("school program"));

                            int cargoToAdd = isSchoolProgram
                                    ? totalGeneratedCargo * 2
                                    : totalGeneratedCargo;

                            int actualAddedCargo = addCargoToStation(station, cargoToAdd, maxCapacityStations);
                            station.setPeopleStored((int) (station.getPeopleStored() + (actualAddedCargo * 0.7)));
                            station.setMailStored((int) (station.getMailStored() + (actualAddedCargo * 0.3)));
                        }
                    });
        }
    }

    public void addIndustryCargo(List<Station> stations, String mapName, Scenario selectedScenario, Set<String> maxCapacityStations) {
        List<Industry> industries = industryRepo.getIndustries();

        for (Industry ind : industries) {
            int w = ind.getArea();
            int h = ind.getArea();
            int originX = ind.getLocation().getX() - (w - 1) / 2;
            int originY = ind.getLocation().getY() - (h - 1) / 2;

            // Pre-filter stations to avoid repeated filtering in inner loops
            List<Station> relevantStations = stations.stream()
                    .filter(s -> s.getMapName().equals(mapName))
                    .filter(s -> s.getOwnerPlayer().equals(currentUser.getEmail()))
                    .filter(s -> s.getScenarioName().equals(selectedScenario.getScenarioName()))
                    .collect(Collectors.toList());

            // Track which stations have already received cargo from this industry
            Set<String> stationsProcessedForThisIndustry = new HashSet<>();

            for (int dx = 0; dx < w; dx++) {
                for (int dy = 0; dy < h; dy++) {
                    int cx = originX + dx;
                    int cy = originY + dy;

                    for (Station s : relevantStations) {
                        // Only add cargo once per station per industry
                        if (hasOverlap(cx, cy, s) && !stationsProcessedForThisIndustry.contains(s.getName())) {
                            int cargoAmount = getCargoPerIndustry(ind.getType());

                            if (cargoAmount > 0) {
                                int actualAddedCargo = addCargoToStation(s, cargoAmount, maxCapacityStations);
                                switch (ind.getType()) {
                                    case IRON_MINE:
                                        s.setIronStored(s.getIronStored() + actualAddedCargo);
                                        break;
                                    case COAL_MINE:
                                        s.setCoalStored(s.getCoalStored() + actualAddedCargo);
                                        break;
                                }
                                stationsProcessedForThisIndustry.add(s.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean hasOverlap(int cx, int cy, Station s) {
        int sw, sh, sox, soy;

        if (s.getType() == Station.Type.STATION) {
            sw = sh = 4;
            int sbX = s.getX();
            int sbY = s.getY();
            int sdx = (s.getOrientation() == Station.Orientation.NE || s.getOrientation() == Station.Orientation.SE) ? 1 : 0;
            int sdy = (s.getOrientation() == Station.Orientation.SW || s.getOrientation() == Station.Orientation.SE) ? 1 : 0;
            sox = sbX + sdx - 1;
            soy = sbY + sdy - 1;
        } else {
            sw = (s.getType() == Station.Type.DEPOT ? 3 : 5);
            sh = sw;
            sox = s.getX() - sw / 2;
            soy = s.getY() - sh / 2;
        }

        return cx >= sox && cx < sox + sw && cy >= soy && cy < soy + sh;
    }

    private int getCargoPerIndustry(IndustryType type) {
        switch (type) {
            case IRON_MINE:
                return 6;
            case COAL_MINE:
                return 4;
            default:
                return 0;
        }
    }

    private int addCargoToStation(Station station, int cargoToAdd, Set<String> maxCapacityStations) {
        int currentCapacity = station.getCapacityOwned();
        int maxCapacity = station.getCapacity();
        int actualCargoAdded = 0;

        if (currentCapacity < maxCapacity) {
            // Ensure we don't exceed capacity
            actualCargoAdded = Math.min(cargoToAdd, maxCapacity - currentCapacity);
            station.setCapacityOwned(currentCapacity + actualCargoAdded);

            System.out.printf(
                    "\nStored %d cargo for station %s (Total: %d/%d). Type your option: ",
                    actualCargoAdded,
                    station.getName(),
                    station.getCapacityOwned(),
                    maxCapacity
            );

            // Check if capacity is now at maximum and print warning only once
            if (station.getCapacityOwned() >= maxCapacity && !maxCapacityStations.contains(station.getName())) {
                System.out.printf(
                        "\nStation %s has reached maximum capacity (%d/%d)!",
                        station.getName(),
                        station.getCapacityOwned(),
                        maxCapacity
                );
                maxCapacityStations.add(station.getName());
            }
        }
        return actualCargoAdded;
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

    public List<Simulation> getSimulations() {
        return simulationRepo.getSimulations();
    }

    public Simulation createSimulation(Map selectedMap, Scenario selectedScenario, int currentYear, Budget userBudget) {
        return simulationRepo.add(new Simulation(selectedMap, selectedScenario, currentUser, currentYear, userBudget));
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

    public void updateSimulation(Map map, Scenario scenario, int newYear, Budget newBudget) {
        simulationRepo.updateSimulation(map, scenario, currentUser, newYear, newBudget);
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

    public Optional<Simulation> findBy(Map map, Scenario scenario) {
        return simulationRepo.findBy(map, scenario, currentUser);
    }
}