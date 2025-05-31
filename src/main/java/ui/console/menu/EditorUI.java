package ui.console.menu;

import domain.Map;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import repository.MapRepository;
import repository.Repositories;
import ui.console.CreateCityUI;
import ui.console.CreateScenarioUI;

import java.util.ArrayList;
import java.util.List;

public class EditorUI implements Runnable {
    private Map map;

    @Override
    public void run() {
        map = requestMap();

        if (map == null) {
            return;
        }

        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Create City", new CreateCityUI(map)));
        options.add(new MenuItem("Create Scenario", new CreateScenarioUI(map)));
        int option;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- ADMIN MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

    private Map requestMap() {
        MapRepository mapRepository = Repositories.getInstance().getMapRepository();
        List<Map> maps = mapRepository.getMaps();
        int mapIndex;

        mapIndex = Utils.showAndSelectIndex(maps, "\n\n--- MAP LIST --------------------------");

        if (mapIndex == -1) {
            return null;
        }

        return maps.get(mapIndex);
    }
}
