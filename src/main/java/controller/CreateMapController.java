package controller;

import domain.Map;
import repository.MapRepository;
import repository.Repositories;

public class CreateMapController {
    private MapRepository mapRepository;

    public CreateMapController() {
        getMapRepository();
    }

    public CreateMapController(MapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    private MapRepository getMapRepository() {
        if (mapRepository == null) {
            Repositories repositories = Repositories.getInstance();

            mapRepository = repositories.getMapRepository();
        }
        return mapRepository;
    }

    public Map createMap(String mapName, int width, int height) {
        if (!getMapRepository().checkNameUniqueness(mapName)){
            System.out.println("\nName not unique.");
            return null;
        }

        Map map = new Map(mapName, width, height);
        getMapRepository().add(map);
        return map;
    }
}
