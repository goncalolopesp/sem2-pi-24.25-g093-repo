package controller;

import domain.City;
import domain.Map;
import repository.CityRepository;
import repository.MapRepository;
import repository.Repositories;

import java.util.List;

public class DisplayMapController {
    private MapRepository mapRepository;
    private CityRepository cityRepository;

    public DisplayMapController() {
        getMapRepository();
        getCityRepository();
    }

    public DisplayMapController(MapRepository mapRepository, CityRepository cityRepository) {
        this.mapRepository = mapRepository;
        this.cityRepository = cityRepository;
    }

    private MapRepository getMapRepository() {
        if (mapRepository == null) {
            Repositories repositories = Repositories.getInstance();

            mapRepository = repositories.getMapRepository();
        }
        return mapRepository;
    }

    private CityRepository getCityRepository() {
        if (cityRepository == null) {
            Repositories repositories = Repositories.getInstance();

            cityRepository = repositories.getCityRepository();
        }
        return cityRepository;
    }

    public List<Map> getMaps() {
        return getMapRepository().getMaps();
    }

    public List<City> getCities() {
        return getCityRepository().getCities();
    }

    public boolean verifyIfMapExists(String mapName) {
        return getMaps().stream().anyMatch(map -> map.getMapName().equals(mapName));
    }
}
