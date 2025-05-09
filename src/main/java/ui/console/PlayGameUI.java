package ui.console;

import controller.CreateMapController;
import controller.CreateScenarioController;
import controller.DisplayMapController;
import domain.Map;
import domain.Scenario;
import domain.City;
import domain.HouseBlock;
import domain.Station;
import domain.CurrentUser;
import domain.User;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import ui.console.menu.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Launches the user interface responsible to make the game playable.
 */

public class PlayGameUI implements Runnable {

    private final CreateMapController mapCtrl = new CreateMapController();
    private final CreateScenarioController scenarioCtrl = new CreateScenarioController();
    private final DisplayMapController displayCtrl = new DisplayMapController();
    private final User currentUser = CurrentUser.getInstance().getUser();

    /**
     * Runs the "PlayGameUI" user interface.
     * <p>
     * Allows the user to select the game options , which will allow him to play the game
     * </p>
     */
    @Override
    public void run() {

        Map selectedMap = getMap();
        Scenario selectedScenario = getScenario(selectedMap);

        if (selectedMap == null || selectedScenario == null) {
            System.out.println("No Map or Scenario chosen.");
            return;
        }

        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Build A Station", new BuildStationUI(selectedMap)));
        options.add(new MenuItem("Upgrade A Station", new UpgradeStationUI()));
        int option;
        do {
            displayMapData(selectedMap);

            if (currentUser != null) {
                System.out.println("Your budget is: " + currentUser.getBudget());
            }

            option = Utils.showAndSelectIndex(options, "\n\n--- GAME MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }

        } while (option != -1);
    }

    private Map getMap() {
        List<Map> maps = mapCtrl.getMaps();
        if (maps.isEmpty()) {
            System.out.println("No maps available.");
            return null;
        }
        int mapIndex = Utils.showAndSelectIndex(maps, "Select a map:");
        return maps.get(mapIndex);
    }

    private Scenario getScenario(Map selectedMap) {
        List<Scenario> scenarios = scenarioCtrl.getScenarios().stream()
                .filter(s -> s.getSelectedMap().equals(selectedMap.getMapName()))
                .collect(Collectors.toList());
        if (scenarios.isEmpty()) {
            System.out.println("No scenarios available for map '" + selectedMap.getMapName() + "'.");
            return null;
        }
        int scenIndex = Utils.showAndSelectIndex(scenarios, "Select a scenario:");
        return scenarios.get(scenIndex);
    }

    /**
     * Renders the map grid using 1-based coordinates and prints it to the console.
     * Displays only the city centers, house blocks, and stations owned by
     * the current user.
     * <p>
     * Symbols:
     * <ul>
     *   <li>⬛ empty</li>
     *   <li>🌆 city center</li>
     *   <li>🏠 house block</li>
     *   <li>🚉 station</li>
     * </ul>
     * </p>
     *
     * @param map the map to render
     */
    private void displayMapData(Map map) {
        int width = map.getWidth();
        int height = map.getHeight();

        String[][] grid = new String[height + 1][width + 1];

        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                grid[y][x] = "⬛";
            }
        }

        List<City> cities = displayCtrl.getCities().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());

        for (City city : cities) {
            int cy = city.getCenterY();
            int cx = city.getCenterX();
            grid[cy][cx] = "🌆";

            for (HouseBlock hb : city.getBlocks()) {
                int by = hb.getY();
                int bx = hb.getX();
                grid[by][bx] = "🏠";
            }
        }

        if (currentUser != null) {
            String me = currentUser.getEmail();
            for (Station st : map.getStations()) {
                if (st.getMapName().equals(map.getMapName())
                        && st.getOwnerPlayer().equals(me)) {
                    int sy = st.getY();
                    int sx = st.getX();
                    grid[sy][sx] = "🚉";
                }
            }
        }


        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }
}
