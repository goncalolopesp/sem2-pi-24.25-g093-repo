package ui.console.menu;

import controller.CreateMapController;
import controller.EditorController;
import domain.Map;
import dto.MapDTO;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import repository.MapRepository;
import repository.Repositories;
import ui.console.CreateCityUI;
import ui.console.CreateScenarioUI;

import java.util.ArrayList;
import java.util.List;

public class EditorUI implements Runnable {
    private MapDTO mapDTO;
    private final EditorController ctrl = new EditorController();

    @Override
    public void run() {
        mapDTO = requestMap();

        if (mapDTO == null) {
            return;
        }

        CreateMapController mapCtrl = new CreateMapController(Repositories.getInstance().getMapRepository());
        List<Map> mapList = mapCtrl.getMaps();
        Map map = mapList.stream().filter(m -> m.getMapName().equals(mapDTO.getMapName())).findFirst().get();

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

    private MapDTO requestMap() {
        List<MapDTO> maps = ctrl.getMaps();
        int mapIndex;

        mapIndex = Utils.showAndSelectIndex(maps, "\n\n--- MAP LIST --------------------------");

        if (mapIndex == -1) {
            return null;
        }

        return maps.get(mapIndex);
    }
}
