package repository;

import controller.CreateCityController;
import domain.City;
import domain.HouseBlock;
import java.util.ArrayList;
import java.util.List;

public class CityRepository {
    private final List<City> cities = new ArrayList<>();

    public void add(City c) { cities.add(c); }
    public List<City> getCities() { return List.copyOf(cities); }

    public boolean isNameUnique(String name, String map) {
        return cities.stream().noneMatch(c->c.getName().equals(name) && c.getMapName().equals(map));
    }

    public boolean existsAtPosition(String map, int x, int y) {
        return cities.stream().filter(c->c.getMapName().equals(map))
                .anyMatch(c->c.getCenterX()==x && c.getCenterY()==y);
    }

    public boolean isBlockPositionUnique(String map, HouseBlock b) {
        return cities.stream().filter(c->c.getMapName().equals(map))
                .flatMap(c->c.getBlocks().stream())
                .noneMatch(b::equals);
    }

    public CreateCityController.BlockError validateBlock(String map, HouseBlock b) {
        // delegated to controller, stub here
        return CreateCityController.BlockError.OK;
    }
}