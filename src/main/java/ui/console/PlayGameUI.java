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
     * Renders the map grid using 1-based coordinates, and only shows stations belonging to the current user.
     * Symbols: ⬛ empty, 🌆 city center, 🏠 house block, 🚉 station.
     *
     * @param map the map to render
     */
    private void displayMapData(Map map) {
        int width = map.getWidth();   // ex: se width == 10, x varia 1..10
        int height = map.getHeight(); // ex: se height == 8, y varia 1..8

        // Criamos array [0..height][0..width], mas só usamos índices 1..height / 1..width
        String[][] grid = new String[height + 1][width + 1];

        // Inicializa tudo a ⬛ em 1..height e 1..width
        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                grid[y][x] = "⬛";
            }
        }

        // ––– Desenha as cidades e blocos de casas –––
        List<City> cities = displayCtrl.getCities().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());

        for (City city : cities) {
            // Já estão em 1-based, não adicionar +1
            int cy = city.getCenterY();
            int cx = city.getCenterX();
            grid[cy][cx] = "🌆";

            for (HouseBlock hb : city.getBlocks()) {
                int by = hb.getY();
                int bx = hb.getX();
                grid[by][bx] = "🏠";
            }
        }

        // ––– Desenha só as estações do jogador atual –––
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

        // ––– Imprime o grid linhas 1..height e colunas 1..width –––
        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }
}
