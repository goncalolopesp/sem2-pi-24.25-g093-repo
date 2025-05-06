package domain;
import controller.CreateCityController;
import repository.CityRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents a city located on a specific map with a central position and a group of house blocks.
 * <p>
 * A City has a unique name, is associated with a map by name, and has a defined center coordinate.
 * </p>
 */

public class City {
    private final String name;
    private final String mapName;
    private final int centerX;
    private final int centerY;
    private final List<HouseBlock> blocks = new ArrayList<>();

    /**
     * Constructs a new City instance with the specified name, map, and center coordinates.
     *
     * @param name     the unique name of the city
     * @param mapName  the name of the map where the city is located
     * @param centerX  the x-coordinate of the city center
     * @param centerY  the y-coordinate of the city center
     */
    public City(String name, String mapName, int centerX, int centerY) {
        this.name = name;
        this.mapName = mapName;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    /**
     * Returns the city name.
     *
     * @return the name of the city
     */
    public String getName() { return name; }
    /**
     * Returns the name of the map associated with the city.
     *
     * @return the map name
     */
    public String getMapName() { return mapName; }
    /**
     * Returns the x-coordinate of the city's center.
     *
     * @return the x-coordinate
     */
    public int getCenterX() { return centerX; }
    /**
     * Returns the y-coordinate of the city's center.
     *
     * @return the y-coordinate
     */
    public int getCenterY() { return centerY; }
    /**
     * Returns an unmodifiable view of the house blocks in the city.
     *
     * @return list of HouseBlocks associated with the city
     */
    public List<HouseBlock> getBlocks() { return Collections.unmodifiableList(blocks); }

    /**
     * Adds a house block to the city manually.
     *
     * @param b the HouseBlock to add
     */
    public void addBlockManual(HouseBlock b) { blocks.add(b); }
}