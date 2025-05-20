package repository;

import controller.CreateCityController;
import domain.City;
import domain.HouseBlock;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and querying City instances.
 * <p>
 * Provides methods to add cities, retrieve the list of cities, and validate
 * uniqueness and existence constraints related to city names, positions, and blocks.
 * </p>
 */

public class CityRepository {
    private final List<City> cities = new ArrayList<>();

    /**
     * Adds a new city to the repository.
     *
     * @param c the City to add
     */
    public void add(City c) { cities.add(c); }
    /**
     * Returns an unmodifiable list of all cities in the repository.
     *
     * @return list of City objects
     */
    public List<City> getCities() { return List.copyOf(cities); }

    /**
     * Checks if a city name is unique within a given map.
     *
     * @param name the city name to check
     * @param map  the map name where uniqueness needs to be enforced
     * @return {@code true} if no city with the same name exists on the map;
     *         {@code false} otherwise
     */
    public boolean isNameUnique(String name, String map) {
        return cities.stream().noneMatch(c->c.getName().equals(name) && c.getMapName().equals(map));
    }

    /**
     * Checks if a city center exists at the specified coordinates on a map.
     *
     * @param map the map name to search
     * @param x   the x-coordinate of the city center
     * @param y   the y-coordinate of the city center
     * @return {@code true} if a city center occupies the position;
     *         {@code false} otherwise
     */
    public boolean existsAtPosition(String map, int x, int y) {
        return cities.stream().filter(c->c.getMapName().equals(map))
                .anyMatch(c->c.getCenterX()==x && c.getCenterY()==y);
    }

    /**
     * Checks if a house block position is unique within the specified map.
     *
     * @param map the map name to check
     * @param b   the HouseBlock to validate
     * @return {@code true} if no other block equals {@code b} on the map;
     *         {@code false} otherwise
     */
    public boolean isBlockPositionUnique(String map, HouseBlock b) {
        return cities.stream().filter(c->c.getMapName().equals(map))
                .flatMap(c->c.getBlocks().stream())
                .noneMatch(b::equals);
    }

    /**
     * Validates a block position by delegating to the {@link CreateCityController} logic.
     * <p>
     * This method currently returns a stub value and should delegate to the controller
     * for actual validation.
     * </p>
     *
     * @return a {@link CreateCityController.BlockError} indicating the validation result
     */
    public CreateCityController.BlockError validateBlock() {
        // delegated to controller, stub here
        return CreateCityController.BlockError.OK;
    }
}