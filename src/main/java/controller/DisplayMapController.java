package controller;

import domain.Map;
import repository.MapRepository;
import repository.Repositories;

import java.util.List;

public class DisplayMapController {
    private MapRepository mapRepository;

    public DisplayMapController() {
        getMapRepository();
    }

    public DisplayMapController(MapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    private MapRepository getMapRepository() {
        if (mapRepository == null) {
            Repositories repositories = Repositories.getInstance();

            mapRepository = repositories.getMapRepository();
        }
        return mapRepository;
    }

    public List<Map> getMaps() {
        return getMapRepository().getMaps();
    }

    public boolean verifyIfMapExists(String mapName) {
        return getMaps().stream().anyMatch(map -> map.getMapName().equals(mapName));
    }
}
