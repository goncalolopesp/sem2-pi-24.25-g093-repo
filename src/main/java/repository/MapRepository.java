package repository;

import controller.CreateCityController;
import domain.City;
import domain.HouseBlock;
import domain.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for storing and querying Map instances.
 * <p>
 * Provides methods to add maps, get maps by name, ensure name uniqueness,
 * and retrieve all stored maps.
 * </p>
 */

public class MapRepository {
    private final List<Map> maps;
    /**
     * Constructs a new MapRepository with an empty list of maps.
     */
    public MapRepository() { maps = new ArrayList<>(); }

    /**
     * Adds a map to the repository if it is not already present.
     *
     * @param map the Map instance to add
     */
    public void add(Map map) {
        if (validateMap(map)) {
            maps.add(map);
        }
    }

    /**
     * Retrieves a map by its name.
     *
     * @param name the name of the map to find
     * @return an Optional containing the found map, or empty if not found
     */
    public Optional<Map> getMap(String name) {
        return maps.stream().filter(m -> m.getMapName().equals(name)).findFirst();
    }

    /**
     * Validates that a map is not already contained in the repository.
     *
     * @param map the Map instance to validate
     * @return {@code true} if the map is not already present; {@code false} otherwise
     */
    private boolean validateMap(Map map) {
        return !maps.contains(map);
    }

    /**
     * Checks if a map name is unique within the repository.
     *
     * @param name the map name to check
     * @return {@code true} if no map with the given name exists; {@code false} otherwise
     */
    public boolean checkNameUniqueness(String name) {
        return maps.stream().noneMatch(m -> m.getMapName().equals(name));
    }

    /**
     * Returns a defensive copy of the list of all maps in the repository.
     *
     * @return unmodifiable list of stored Map objects
     */
    public List<Map> getMaps() {
        //This is a defensive copy, so that the repository cannot be modified from the outside.
        return List.copyOf(maps);
    }
}