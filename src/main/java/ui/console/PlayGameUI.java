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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Launches the user interface responsible to make the game playable.
 */

public class PlayGameUI implements Runnable {

    private final CreateMapController mapCtrl = new CreateMapController();
    private final CreateScenarioController scenarioCtrl = new CreateScenarioController();
    private final DisplayMapController displayCtrl = new DisplayMapController();

    /**
     * Runs the "PlayGameUI" user interface.
     * <p>
     * Allows the user to select the game options , which will allow him to play the game
     * </p>
     */
    @Override
    public void run() {
        List<Map> maps = mapCtrl.getMaps();
        if (maps.isEmpty()) {
            System.out.println("No maps available.");
            return;
        }
        int mapIndex = Utils.showAndSelectIndex(maps, "Select a map:");
        Map selectedMap = maps.get(mapIndex);

        List<Scenario> scenarios = scenarioCtrl.getScenarios().stream()
                .filter(s -> s.getSelectedMap().equals(selectedMap.getMapName()))
                .collect(Collectors.toList());
        if (scenarios.isEmpty()) {
            System.out.println("No scenarios available for map '" + selectedMap.getMapName() + "'.");
            return;
        }
        int scenIndex = Utils.showAndSelectIndex(scenarios, "Select a scenario:");
        Scenario selectedScenario = scenarios.get(scenIndex);

        // Main loop: display map, budget and menu until user chooses to exit (option 0)
        boolean exit = false;
        while (!exit) {
            displayMapData(selectedMap);

            User currentUser = CurrentUser.getInstance().getUser();
            if (currentUser != null) {
                System.out.println("Your budget is: " + currentUser.getBudget());
            }

            List<String> options = List.of("Buy a Station", "Return to Menu");
            int choice = Utils.showAndSelectIndex(options, "Choose an action:");
            switch (choice) {
                case 0:
                    new BuildStationUI(selectedMap).run();
                    break;
                case 1:
                    exit = true;
                    break;
                default:
                    exit = true;
            }
        }
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

        User currentUser = CurrentUser.getInstance().getUser();
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
