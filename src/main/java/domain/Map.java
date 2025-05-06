package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a map with a name and fixed dimensions.
 * <p>
 * The Map class encapsulates the map's unique name and its
 * width and height in grid units.
 * </p>
 */

public class Map {
    private final String mapName;
    private final int width;
    private final int height;

    /**
     * Constructs a new Map instance with the specified name and dimensions.
     *
     * @param mapName the unique name of the map
     * @param width   the width of the map (must be positive)
     * @param height  the height of the map (must be positive)
     */
    public Map(String mapName, int width, int height) {
        this.mapName = mapName;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the name of the map.
     *
     * @return the map's unique name
     */
    public String getMapName() { return mapName; }
    /**
     * Returns the width of the map.
     *
     * @return the width in grid units
     */
    public int getWidth() { return width; }
    /**
     * Returns the height of the map.
     *
     * @return the height in grid units
     */
    public int getHeight() { return height; }

    /**
     * Returns the string representation of this map, which is its name.
     *
     * @return the map's name
     */
    @Override
    public String toString() {
        return getMapName();
    }
}