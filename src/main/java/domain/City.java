package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a city located on a map, consisting of house blocks.
 */
public class City {
    private final String name;
    private final String mapName;
    private final List<HouseBlock> houseBlocks;

    /**
     * Constructs a City with a name, map association, and a list of house blocks.
     *
     * @param name        The name of the city.
     * @param mapName     The name of the map where the city is located.
     * @param houseBlocks The list of house blocks belonging to the city.
     */
    public City(String name, String mapName, List<HouseBlock> houseBlocks) {
        this.name = name;
        this.mapName = mapName;
        this.houseBlocks = new ArrayList<>(houseBlocks);
    }

    /**
     * Gets the name of the city.
     *
     * @return The city name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the name of the map this city belongs to.
     *
     * @return The map name.
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * Gets the list of house blocks in the city.
     *
     * @return List of house blocks.
     */
    public List<HouseBlock> getHouseBlocks() {
        return new ArrayList<>(houseBlocks); // Defensive copy
    }

    /**
     * Checks whether a given house block is already part of this city.
     *
     * @param block The block to check.
     * @return True if it exists, false otherwise.
     */
    public boolean hasHouseBlock(HouseBlock block) {
        return houseBlocks.contains(block);
    }

    /**
     * Adds a house block to the city.
     *
     * @param block The block to add.
     */
    public void addHouseBlock(HouseBlock block) {
        if (!houseBlocks.contains(block)) {
            houseBlocks.add(block);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City city = (City) o;
        return name.equalsIgnoreCase(city.name) && mapName.equalsIgnoreCase(city.mapName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), mapName.toLowerCase());
    }
}
