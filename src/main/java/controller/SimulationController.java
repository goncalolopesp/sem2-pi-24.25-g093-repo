package controller;

import domain.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import repository.StationRepository;
import ui.console.menu.MenuItem;

import domain.User;
import domain.City;
import domain.CurrentUser;
import domain.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SimulationController {

    private final User currentUser = CurrentUser.getInstance().getUser();
    private final CreateMapController mapCtrl = new CreateMapController();
    private final CreateScenarioController scenarioCtrl = new CreateScenarioController();
    private final StationRepository stationrepo = new CreateStationController().getStationRepository();
    private final DisplayMapController displayCtrl = new DisplayMapController();
    private final CreateCityController cityCtrl = new CreateCityController();
    private final List<City> cities = cityCtrl.getCityRepo().getCities();

    public void displayMapData(Map map) {
        int width = map.getWidth();
        int height = map.getHeight();
        String[][] grid = new String[height + 1][width + 1];
        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                grid[y][x] = "⬛";
            }
        }

        addCitiesToMap(grid, map);
        addStationsToMap(grid, map, width, height);

        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }

    public Runnable passTime(Map selectedMap, Scenario selectedScenario, AtomicReference<AtomicIntegerArray> timePeriod, AtomicBoolean running, List<MenuItem> options, ScheduledExecutorService scheduler, AtomicBoolean gameOver) {
        Runnable task = () -> {
            if (running.get()) {
                timePeriod.get().getAndAdd(0, (1));
                if (timePeriod.get().get(0) <= timePeriod.get().get(1)) {
                    System.out.print("\nYou have reached year " + timePeriod.get().get(0) + ".");
                    removeOutdatedLocomotives(timePeriod.get().get(0));
                    payLocomotiveMaintenance();
                    System.out.print("Type your option: ");
                }
            }
            if (timePeriod.get().get(0) > timePeriod.get().get(1)) {
                scheduler.shutdownNow();
                timePeriod.set(new AtomicIntegerArray(selectedScenario.getTimeRestrictions()));
                options.clear();
                gameOver.set(true);
                System.out.print("\nGame Over. Please insert 0 to return to menu: ");
            }
        };
        return task;
    }

    public Runnable generateCargo(Map selectedMap, AtomicReference<AtomicIntegerArray> timePeriod, AtomicBoolean running, ScheduledExecutorService generationScheduler) {
        AtomicBoolean printedMaxCapacity = new AtomicBoolean(false);
        Runnable task = () -> {
            if (running.get()) {
                if (timePeriod.get().get(0) <= timePeriod.get().get(1)) {
                    addCargoToStation(selectedMap, printedMaxCapacity);
                }
            }
            if (timePeriod.get().get(0) > timePeriod.get().get(1)) {
                generationScheduler.shutdownNow();
            }
        };
        return task;
    }

    public void addCargoToStation(Map selectedMap, AtomicBoolean printedMaxCapacity) {
        String mapName = selectedMap.getMapName();

        for (City city : cities) {
            int totalGeneratedCargo = city.getTotalGeneratedCargo();

            stationrepo.getStations().stream()
                    .filter(s -> s.getMapName().equals(mapName))
                    .filter(s -> s.getOwnerPlayer().equals(currentUser.getEmail()))
                    .filter(s -> s.getName().contains(city.getName()))
                    .forEach(station -> {
                        if (station.getCapacityOwned() < station.getCapacity()) {
                            station.setCapacityOwned(station.getCapacityOwned() + totalGeneratedCargo);
                            System.out.printf("\nStored %d cargo for station %s. Type your option: ", station.getCapacityOwned(), station.getName());
                            printedMaxCapacity.set(false);
                        } else if (!printedMaxCapacity.get()) {
                            station.setCapacityOwned(station.getCapacity());
                            System.out.print("\nMax capacity reached for station " + station.getName() + ". Type your option: ");
                            printedMaxCapacity.set(true);
                        }
                    });
        }
    }

    public void resetGame(Map selectedMap) {
        currentUser.setBudget(new Budget(1000));
        stationrepo.removeStationsByMapAndOwner(
                selectedMap.getMapName(),
                currentUser.getEmail()
        );
    }

    private void addCitiesToMap(String[][] grid, Map map) {
        // Draw cities and house blocks
        List<City> cities = displayCtrl.getCities().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());
        for (City city : cities) {
            grid[city.getCenterY()][city.getCenterX()] = "🌆";
            for (HouseBlock hb : city.getBlocks()) {
                grid[hb.getY()][hb.getX()] = "🏠";
            }
        }
    }

    private void addStationsToMap(String[][] grid, Map map, int width, int height) {
        // Draw stations owned by current user
        if (currentUser != null) {
            String owner = currentUser.getEmail();
            for (Station st : stationrepo.getStations()) {
                if (!st.getMapName().equals(map.getMapName()) || !st.getOwnerPlayer().equals(owner)) {
                    continue;
                }

                int w, h, ox, oy;
                switch (st.getType()) {
                    case DEPOT:
                        w = h = 3;
                        ox = st.getX() - 1;
                        oy = st.getY() - 1;
                        // paint 3x3
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
                                sdx = 1; sdy = 1; break;
                            case NE:
                                sdx = 2; sdy = 1; break;
                            case SW:
                                sdx = 1; sdy = 2; break;
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
        List<Map> maps = mapCtrl.getMaps();
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
        List<Scenario> scenarios = scenarioCtrl.getScenarios().stream()
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

    public void payLocomotiveMaintenance(){
        boolean canPay = true;
        int yearlyBudget = 0;
        int yearlyManagementCost = 0;
        List<Locomotive> canUse = new ArrayList<>();
        for (Locomotive loc : currentUser.getLocomotives()){
            yearlyBudget = loc.getFuelCost();
            yearlyManagementCost = loc.getMaintenanceCost();
            if ((yearlyManagementCost > currentUser.getBudgetValue())) {
                canPay = false;
            }else{
                canPay = true;
            }

            if (canPay){
                currentUser.getBudget().subtractBudget(yearlyManagementCost);
                if (yearlyBudget > currentUser.getBudgetValue()) {
                    loc.setAvailability(false);
                }else{
                    loc.setAvailability(true);
                }
                canUse.add(loc);
                if (loc.getAvailability())
                    currentUser.getBudget().subtractBudget(yearlyBudget);

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
}
