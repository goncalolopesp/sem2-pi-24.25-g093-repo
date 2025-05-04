package repository;

import domain.City;
import domain.HouseBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CityRepository {
    private final List<City> cities = new ArrayList<>();

    public Optional<City> getCity(String name, String mapName) {
        return cities.stream()
                .filter(c -> c.getName().equals(name) && c.getMapName().equals(mapName))
                .findFirst();
    }

    public boolean add(City city) {
        if (cities.stream().anyMatch(c -> c.getName().equals(city.getName()) && c.getMapName().equals(city.getMapName()))) {
            return false;
        }
        cities.add(city);
        return true;
    }

    public List<City> getAll() { return List.copyOf(cities); }

    /**
     * Checks that no city on the same map has this block position.
     */
    public boolean isBlockPositionUnique(String mapName, HouseBlock block) {
        return cities.stream()
                .filter(c -> c.getMapName().equals(mapName))
                .flatMap(c -> c.getBlocks().stream())
                .noneMatch(b -> b.equals(block));
    }
}