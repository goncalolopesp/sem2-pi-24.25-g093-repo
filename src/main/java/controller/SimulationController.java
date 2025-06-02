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
    private static final int COAL_VALUE = 4;
    private static final int IRON_VALUE = 6;

    private final User currentUser = CurrentUser.getInstance().getUser();
    private final SimulationRepository simulationRepo = Repositories.getInstance().getSimulationRepository();
    private final MapRepository mapRepo = Repositories.getInstance().getMapRepository();
    private final IndustryRepository industryRepo = Repositories.getInstance().getIndustryRepository();
    private final CityRepository cityRepo = Repositories.getInstance().getCityRepository();
    private final ScenarioRepository scenarioRepo = Repositories.getInstance().getScenarioRepository();
    private final StationRepository stationrepo = Repositories.getInstance().getStationRepository();
    private final BudgetRepository budgetRepo = Repositories.getInstance().getBudgetRepository();
    private final RouteRepository routeRepo = Repositories.getInstance().getRouteRepository();

    public Runnable executeRoutes(Map selectedMap, Scenario selectedScenario, Budget budget, AtomicBoolean running) {
        return () -> {
            if (running.get() && currentUser != null) {
                List<Route> userRoutes = getUserRoutes(selectedMap, selectedScenario);
                userRoutes.forEach(route -> executeRoute(route, selectedScenario, budget));
            }
        };
    }

    private List<Route> getUserRoutes(Map map, Scenario scenario) {
        return routeRepo.getRoutes().stream()
                .filter(r -> r.getPlayerEmail().equals(currentUser.getEmail())
                        && r.getMapName().equals(map.getMapName())
                        && r.getScenarioName().equals(scenario.getScenarioName()))
                .collect(Collectors.toList());
    }

    private void executeRoute(Route route, Scenario scenario, Budget budget) {
        Train train = route.getTrain();
        if (train == null) return;

        List<Carriage> carriages = train.getCarriages();
        resetCarriageCapacity(carriages);

        CargoCollectionResult result = new CargoCollectionResult();

        for (PointOfRoute point : route.getPoints()) {
            if (isTrainFull(carriages)) break;

            Station station = point.getStation();
            if (station.getCapacityOwned() <= 0) continue;

            collectCargoFromStation(station, point.getCargoTypes(), carriages, result);
        }

        if (result.hasIronAndCoal()) {
            deliverToSteelMills(route, carriages, result);
        }

        int totalEarnings = (int) (calculateEarnings(result, scenario) * scenario.getProfitMultiplier());
        if (result.getTotalCargo() > 0) {
            displayRouteComplete(result, totalEarnings);
            budget.addBudget(totalEarnings);
        }
    }

    private void resetCarriageCapacity(List<Carriage> carriages) {
        carriages.forEach(c -> c.setCapacityOwned(0));
    }

    private void deliverToSteelMills(Route route, List<Carriage> carriages, CargoCollectionResult result) {
        Map selectedMap = mapRepo.getMaps().stream()
                .filter(map -> map.getMapName().equals(route.getMapName()))
                .findFirst()
                .orElse(null);

        if (selectedMap == null) {
            return;
        }

        List<Industry> steelMillIndustries = industryRepo.getIndustries().stream()
                .filter(industry -> industry.getType().equals(IndustryType.STEEL_MILL))
                .filter(industry -> industry.getMapName().equals(selectedMap.getMapName()))
                .collect(Collectors.toList());

        for (Industry steelMill : steelMillIndustries) {
            if (!result.hasIronAndCoal()) {
                break;
            }

            deliverCargoToSteelMill(carriages, result, steelMill);
        }
    }

    private void deliverCargoToSteelMill(List<Carriage> carriages, CargoCollectionResult result, Industry steelMill) {

        int ironDelivered = removeCargoFromTrain(carriages, Carriage.Type.IRON);
        int coalDelivered = removeCargoFromTrain(carriages, Carriage.Type.COAL);

        result.iron -= ironDelivered;
        result.coal -= coalDelivered;

        steelMill.setStoredIron(steelMill.getStoredIron() + ironDelivered);
        steelMill.setStoredCoal(steelMill.getStoredCoal() + coalDelivered);

        if (ironDelivered > 0 || coalDelivered > 0) {
            System.out.printf("\nDelivered %d iron and %d coal to steel mill at location (%d, %d). Type your option: ",
                    ironDelivered, coalDelivered, steelMill.getLocation().getX(), steelMill.getLocation().getY());
        }


    }

    private int removeCargoFromTrain(List<Carriage> carriages, Carriage.Type cargoType) {
        int totalRemoved = 0;
        for (Carriage carriage : carriages) {
            if (carriage.getType() == cargoType) {
                totalRemoved += carriage.getCapacityOwned();
                carriage.setCapacityOwned(0);
            }
        }
        return totalRemoved;
    }

    private void collectCargoFromStation(Station station, List<Carriage.Type> requestedTypes,
                                         List<Carriage> carriages, CargoCollectionResult result) {
        for (Carriage.Type cargoType : requestedTypes) {
            int availableCargo = getAvailableCargoAtStation(station, cargoType);
            if (availableCargo > 0) {
                int collected = collectCargo(carriages, cargoType, availableCargo);
                updateStationCargo(station, cargoType, collected);
                updateCollectionResult(result, cargoType, collected);
            }
        }
        updateStationCapacity(station);
    }

    private int getAvailableCargoAtStation(Station station, Carriage.Type cargoType) {
        switch (cargoType) {
            case PEOPLE: return station.getPeopleStored();
            case MAIL: return station.getMailStored();
            case COAL: return station.getCoalStored();
            case IRON: return station.getIronStored();
            default: return 0;
        }
    }

    private void updateStationCargo(Station station, Carriage.Type cargoType, int collectedAmount) {
        switch (cargoType) {
            case PEOPLE:
                station.setPeopleStored(Math.max(0, station.getPeopleStored() - collectedAmount));
                break;
            case MAIL:
                station.setMailStored(Math.max(0, station.getMailStored() - collectedAmount));
                break;
            case COAL:
                station.setCoalStored(Math.max(0, station.getCoalStored() - collectedAmount));
                break;
            case IRON:
                station.setIronStored(Math.max(0, station.getIronStored() - collectedAmount));
                break;
        }
    }

    private void updateStationCapacity(Station station) {
        station.setCapacityOwned(station.getPeopleStored() + station.getMailStored() +
                station.getCoalStored() + station.getIronStored());
    }

    private void updateCollectionResult(CargoCollectionResult result, Carriage.Type cargoType, int amount) {
        switch (cargoType) {
            case PEOPLE: result.people += amount; break;
            case MAIL: result.mail += amount; break;
            case COAL: result.coal += amount; break;
            case IRON: result.iron += amount; break;
        }
    }

    private int calculateEarnings(CargoCollectionResult result, Scenario scenario) {
        boolean isVaccinationCampaign = hasHistoricalRestriction(scenario, "vaccination campaign");
        int peopleEarnings = isVaccinationCampaign ?
                (result.people * PEOPLE_VALUE * 2) : (result.people * PEOPLE_VALUE);

        return peopleEarnings + (result.mail * MAIL_VALUE) +
                (result.coal * COAL_VALUE) + (result.iron * IRON_VALUE);
    }

    private boolean hasHistoricalRestriction(Scenario scenario, String restriction) {
        return scenario.getHistoricalRestrictions().stream()
                .anyMatch(r -> r.equalsIgnoreCase(restriction));
    }

    private void displayRouteComplete(CargoCollectionResult result, int totalEarnings) {
        System.out.printf("\n[Route Complete] Total Cargo: %d (People: %d, Mail: %d) - Earned: %d. Type your option: ",
                result.getTotalCargo(), result.people, result.mail, totalEarnings);
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

    private static class CargoCollectionResult {
        int people = 0;
        int mail = 0;
        int coal = 0;
        int iron = 0;

        int getTotalCargo() {
            return people + mail + coal + iron;
        }

        boolean hasIronAndCoal() {
            return iron > 0 && coal > 0;
        }
    }

    // Map display methods (simplified)
    public void displayMapData(Map map, Scenario scenario) {
        String[][] grid = createMapGrid(map);
        populateMapGrid(grid, map, scenario);
        printMapGrid(grid, map.getHeight(), map.getWidth());
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
        addIndustriesToMap(grid, map);
        addCitiesToMap(grid, map);
        addStationsToMap(grid, map, scenario, map.getWidth(), map.getHeight());
    }

    private void printMapGrid(String[][] grid, int height, int width) {
        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }

    private void addIndustriesToMap(String[][] grid, Map map) {
        getIndustriesForMap(map).forEach(industry ->
                grid[industry.getLocation().getY()][industry.getLocation().getX()] = "🏭");
    }

    private List<Industry> getIndustriesForMap(Map map) {
        return industryRepo.getIndustries().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());
    }

    private void addCitiesToMap(String[][] grid, Map map) {
        getCitiesForMap(map).forEach(city -> {
            grid[city.getCenterY()][city.getCenterX()] = "🌆";
            city.getBlocks().forEach(hb -> grid[hb.getY()][hb.getX()] = "🏠");
        });
    }

    private List<City> getCitiesForMap(Map map) {
        return cityRepo.getCities().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());
    }

    private void addStationsToMap(String[][] grid, Map map, Scenario scenario, int width, int height) {
        if (currentUser == null) return;

        getUserStations(map, scenario).forEach(station ->
                addStationToGrid(grid, station, width, height));
    }

    private List<Station> getUserStations(Map map, Scenario scenario) {
        return stationrepo.getStations().stream()
                .filter(st -> st.getMapName().equals(map.getMapName()) &&
                        st.getOwnerPlayer().equals(currentUser.getEmail()) &&
                        st.getScenarioName().equals(scenario.getScenarioName()))
                .collect(Collectors.toList());
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

    // Time management methods
    public Runnable passTime(Scenario selectedScenario, AtomicReference<AtomicIntegerArray> timePeriod,
                             AtomicBoolean running, List<MenuItem> options, ScheduledExecutorService scheduler,
                             AtomicBoolean gameOver, Budget budget, Simulation sim) {
        return () -> {
            if (!running.get()) return;

            int nextYear = timePeriod.get().addAndGet(0, 1);
            sim.setCurrentYear(nextYear);

            if (nextYear < timePeriod.get().get(1)) {
                handleYearProgression(nextYear, budget);
            } else {
                handleGameEnd(selectedScenario, timePeriod, scheduler, running, options, gameOver, sim);
            }
        };
    }

    private void handleYearProgression(int year, Budget budget) {
        System.out.println("\nYou have reached year " + year + ". ");
        announceNewLocomotives(year);
        removeOutdatedLocomotives(year);
        payLocomotiveMaintenance(budget);
        System.out.print("Type your option: ");
    }

    private void announceNewLocomotives(int year) {
        Repositories.getInstance().getLocomotiveRepository().getLocomotives().stream()
                .filter(l -> l.getStartYearOfOperation() == year)
                .forEach(l -> System.out.println("Locomotive " + l.getModelName() + " has become available."));
    }

    private void handleGameEnd(Scenario scenario, AtomicReference<AtomicIntegerArray> timePeriod,
                               ScheduledExecutorService scheduler, AtomicBoolean running,
                               List<MenuItem> options, AtomicBoolean gameOver, Simulation sim) {
        scheduler.shutdownNow();
        running.set(false);

        int resetYear = scenario.getTimeRestrictions()[0];
        sim.setCurrentYear(resetYear);
        timePeriod.set(new AtomicIntegerArray(scenario.getTimeRestrictions()));

        options.clear();
        gameOver.set(true);
        System.out.print("\nGame Over. Final Year was reached. Please insert 0 to return to menu: ");
    }

    public Runnable generateCargo(Map selectedMap, Scenario selectedScenario,
                                  AtomicReference<AtomicIntegerArray> timePeriod, AtomicBoolean running,
                                  ScheduledExecutorService generationScheduler) {
        return () -> {
            if (running.get() && timePeriod.get().get(0) <= timePeriod.get().get(1)) {
                addCargoToStation(selectedMap, selectedScenario);
            }
            if (timePeriod.get().get(0) > timePeriod.get().get(1)) {
                generationScheduler.shutdownNow();
            }
        };
    }

    public void addCargoToStation(Map selectedMap, Scenario selectedScenario) {
        String mapName = selectedMap.getMapName();
        List<Station> stations = getUserStationsForMap(mapName, selectedScenario);
        Set<Station> maxCapacityStations = new HashSet<>();

        addCityCargo(stations, selectedScenario, maxCapacityStations);
        addIndustryCargo(stations, selectedScenario, maxCapacityStations);
    }

    private List<Station> getUserStationsForMap(String mapName, Scenario scenario) {
        return stationrepo.getStations().stream()
                .filter(s -> s.getMapName().equals(mapName) &&
                        s.getScenarioName().equals(scenario.getScenarioName()) &&
                        s.getOwnerPlayer().equals(currentUser.getEmail()))
                .collect(Collectors.toList());
    }

    public void addCityCargo(List<Station> stations, Scenario selectedScenario, Set<Station> maxCapacityStations) {
        cityRepo.getCities().forEach(city -> {
            List<Station> cityStations = findStationsForCity(stations, city);
            if (!cityStations.isEmpty()) {
                double cargoMultiplier = selectedScenario.getProductionRate();
                int totalCargo = (int) (city.getTotalGeneratedCargo() * cargoMultiplier);

                cityStations.forEach(station -> addCityCargoToStation(station, totalCargo, maxCapacityStations));
            }
        });
    }

    private List<Station> findStationsForCity(List<Station> stations, City city) {
        return stations.stream()
                .filter(s -> s.getName().contains(city.getName()))
                .collect(Collectors.toList());
    }

    private void addCityCargoToStation(Station station, int totalCargo, Set<Station> maxCapacityStations) {
        if (station.getCapacityOwned() < station.getCapacity()) {
            int actualAddedCargo = addCargoToStation(station, totalCargo, maxCapacityStations);
            station.setPeopleStored(station.getPeopleStored() + (int)(actualAddedCargo * 0.7));
            station.setMailStored(station.getMailStored() + (int)(actualAddedCargo * 0.3));
        }
    }

    public void addIndustryCargo(List<Station> stations, Scenario selectedScenario, Set<Station> maxCapacityStations) {
        List<Industry> mapIndustries = industryRepo.getIndustries().stream()
                .filter(industry -> industry.getMapName().equals(stations.get(0).getMapName()))
                .collect(Collectors.toList());

        for (Industry industry : mapIndustries) {
            List<Station> overlappingStations = stations.stream()
                    .filter(station -> hasStationIndustryOverlap(station, industry))
                    .collect(Collectors.toList());

            if (!overlappingStations.isEmpty()) {
                int cargoPerIndustry = (int) (getCargoPerIndustry(industry) * selectedScenario.getProductionRate());
                if (cargoPerIndustry > 0) {
                    Station targetStation = overlappingStations.get(0);

                    int actualAddedCargo = addCargoToStation(targetStation, cargoPerIndustry, maxCapacityStations);
                    distributeIndustryCargo(targetStation, industry, actualAddedCargo);
                }
            }
        }
    }

    private boolean hasStationIndustryOverlap(Station station, Industry industry) {
        double industryRadius = industry.getArea();
        int industryX = industry.getLocation().getX();
        int industryY = industry.getLocation().getY();
        StationBounds bounds = getStationBounds(station);

        return !(bounds.maxX < industryX - industryRadius ||
                bounds.minX > industryX + industryRadius ||
                bounds.maxY < industryY - industryRadius ||
                bounds.minY > industryY + industryRadius);
    }

    private StationBounds getStationBounds(Station station) {
        int w, h, minX, minY;

        switch (station.getType()) {
            case DEPOT:
                w = h = 3;
                minX = station.getX() - 1;
                minY = station.getY() - 1;
                break;
            case TERMINAL:
                w = h = 5;
                minX = station.getX() - 2;
                minY = station.getY() - 2;
                break;
            case STATION:
                w = h = 4;
                minX = station.getX() - 2; // Fixed: center the 4x4 area
                minY = station.getY() - 2; // Fixed: center the 4x4 area
                break;
            default:
                return new StationBounds(station.getX(), station.getY(), station.getX(), station.getY());
        }

        return new StationBounds(minX, minY, minX + w - 1, minY + h - 1);
    }

    private static class StationBounds {
        final int minX, minY, maxX, maxY;

        StationBounds(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }

    private void distributeIndustryCargo(Station station, Industry industry, int actualAddedCargo) {
        switch (industry.getType()) {
            case IRON_MINE:
                station.setIronStored(station.getIronStored() + actualAddedCargo);
                break;
            case COAL_MINE:
                station.setCoalStored(station.getCoalStored() + actualAddedCargo);
                break;
            case STEEL_MILL:
                if (actualAddedCargo > 0) {
                    station.setSteelStored(station.getSteelStored() + actualAddedCargo);
                    station.setIronStored(station.getIronStored() - 1);
                    station.setCoalStored(station.getCoalStored() - 1);
                }
                break;
        }
    }

    private int getCargoPerIndustry(Industry ind) {
        switch (ind.getType()) {
            case IRON_MINE: return ind.getGeneratedIron();
            case COAL_MINE: return ind.getGeneratedCoal();
            case STEEL_MILL:
                if (ind.getStoredCoal() > 0 && ind.getStoredIron() > 0) return 1;
                return 0;
            default: return 0;
        }
    }

    private int addCargoToStation(Station station, int cargoToAdd, Set<Station> maxCapacityStations) {
        int currentCapacity = station.getCapacityOwned();
        int maxCapacity = station.getCapacity();

        if (currentCapacity >= maxCapacity) return 0;

        int actualCargoAdded = Math.min(cargoToAdd, maxCapacity - currentCapacity);
        station.setCapacityOwned(currentCapacity + actualCargoAdded);

        System.out.printf("\nStored %d cargo for station %s (Total: %d/%d). Type your option: ",
                actualCargoAdded, station.getName(), station.getCapacityOwned(), maxCapacity);

        if (station.getCapacityOwned() >= maxCapacity && !maxCapacityStations.contains(station)) {
            System.out.printf("\nStation %s has reached maximum capacity (%d/%d)!",
                    station.getName(), station.getCapacityOwned(), maxCapacity);
            maxCapacityStations.add(station);
        }

        return actualCargoAdded;
    }

    // Locomotive management methods
    public void payLocomotiveMaintenance(Budget budget) {
        List<Locomotive> affordableLocomotives = currentUser.getLocomotives().stream()
                .filter(loc -> canAffordMaintenance(loc, budget))
                .peek(loc -> payMaintenanceAndFuel(loc, budget))
                .collect(Collectors.toList());

        currentUser.setLocomotives(affordableLocomotives);
    }

    private boolean canAffordMaintenance(Locomotive locomotive, Budget budget) {
        return locomotive.getMaintenanceCost() <= budget.getBudget();
    }

    private void payMaintenanceAndFuel(Locomotive locomotive, Budget budget) {
        budget.subtractBudget(locomotive.getMaintenanceCost());
        locomotive.setAvailability(locomotive.getFuelCost() <= budget.getBudget());

        if (locomotive.getAvailability()) {
            budget.subtractBudget(locomotive.getFuelCost());
        }
    }

    public void removeOutdatedLocomotives(int year) {
        List<Locomotive> activeLocomotives = currentUser.getLocomotives().stream()
                .filter(locomotive -> {
                    if (locomotive.getEndYearOfOperation() <= year) {
                        System.out.println("\n" + locomotive.getModelName() +
                                " removed from user's locomotives since the end of its operation is " + year);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        currentUser.setLocomotives(activeLocomotives);
    }

    public List<Simulation> getSimulations() {
        return simulationRepo.getSimulations();
    }

    public Simulation createSimulation(Map selectedMap, Scenario selectedScenario, int currentYear, Budget userBudget) {
        return simulationRepo.add(new Simulation(selectedMap, selectedScenario, currentUser, currentYear, userBudget));
    }

    public Scenario getScenario() {
        List<Scenario> allScenarios = scenarioRepo.getScenarios();

        if (allScenarios.isEmpty()) {
            System.out.println("No scenarios available.");
            return null;
        }

        List<String> displayList = allScenarios.stream()
                .map(s -> s.getScenarioName() + " - " + s.getSelectedMap())
                .collect(Collectors.toList());

        int idx = Utils.showAndSelectIndex(displayList, "Select the scenario you want to play in:");

        return idx == -1 ? null : allScenarios.get(idx);
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