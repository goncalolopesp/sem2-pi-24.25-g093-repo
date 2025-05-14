package controller;

import domain.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class SimulationController {

    private final User currentUser = CurrentUser.getInstance().getUser();
    private final CreateMapController mapCtrl = new CreateMapController();
    private final CreateScenarioController scenarioCtrl = new CreateScenarioController();
    private final DisplayMapController displayCtrl = new DisplayMapController();

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

        // Print the grid
        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
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
            for (Station st : map.getStations()) {
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
        return scenarios.get(idx);
    }
}
