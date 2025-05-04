package controller;

import domain.City;
import domain.HouseBlock;
import repository.CityRepository;
import repository.MapRepository;
import repository.Repositories;

import java.util.List;
import java.util.Optional;

/**
 * Controller for creating cities and assigning house blocks.
 */
public class CreateCityController {
    private final MapRepository mapRepo = Repositories.getInstance().getMapRepository();
    private final CityRepository cityRepo = Repositories.getInstance().getCityRepository();

    /**
     * Creates a city if map exists and center within bounds.
     * @return created City or null if invalid or duplicate.
     */
    public City createCity(String name, String mapName, int cx, int cy) {
        Optional<domain.Map> m = mapRepo.getMap(mapName);
        if (m.isEmpty() || cx < 0 || cx >= m.get().getWidth() || cy < 0 || cy >= m.get().getHeight()) return null;
        City city = new City(name, mapName, cx, cy);
        return cityRepo.add(city) ? city : null;
    }

    /**
     * Assigns blocks manually, ensuring no conflict on same map.
     */
    public boolean assignBlocksManual(City city, List<HouseBlock> blocks) {
        for (HouseBlock b : blocks) {
            if (!cityRepo.isBlockPositionUnique(city.getMapName(), b)) return false;
            if (!city.addBlockManual(b)) return false;
        }
        return true;
    }

    /**
     * Assigns blocks automatically using normal distribution.
     */
    public void assignBlocksAuto(City city, int count, double sigma) {
        city.generateBlocksAuto(count, sigma, cityRepo);
    }

    /**
     * Lists all cities.
     */
    public List<City> listCities() { return cityRepo.getAll(); }
}

