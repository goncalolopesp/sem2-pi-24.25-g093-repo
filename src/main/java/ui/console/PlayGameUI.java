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
 * User interface for playing the game: select a map and a scenario, then display the map and actions menu.
 */
public class PlayGameUI implements Runnable {

    private final CreateMapController mapCtrl = new CreateMapController();
    private final CreateScenarioController scenarioCtrl = new CreateScenarioController();
    private final DisplayMapController displayCtrl = new DisplayMapController();

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
     * Renders the map grid without coordinates headers, using symbols:
     * ⬛ empty, 🌆 city center, 🏠 house block, 🚉 station.
     *
     * @param map the map to render
     */
    private void displayMapData(Map map) {
        int width = map.getWidth();
        int height = map.getHeight();
        String[][] grid = new String[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = "⬛";
            }
        }

        List<City> cities = displayCtrl.getCities().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());
        for (City city : cities) {
            grid[city.getCenterY()][city.getCenterX()] = "🌆";
            for (HouseBlock hb : city.getBlocks()) {
                grid[hb.getY()][hb.getX()] = "🏠";
            }
        }

        for (Station st : map.getStations()) {
            if (st.getMapName().equals(map.getMapName())) {
                grid[st.getY()][st.getX()] = "🚉";
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }
}
