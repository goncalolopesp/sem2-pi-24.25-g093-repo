package domain;

import java.util.ArrayList;
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
    private final Location location;
    private final int scale;
    private final List<Station> stations = new ArrayList<>();
    private final List<Industry> industries = new ArrayList<>();

    /**
     * Constructs a new Map instance with the specified name and dimensions.
     *
     * @param mapName the unique name of the map
     * @param width   the width of the map (must be positive)
     * @param height  the height of the map (must be positive)
     */
    public Map(String mapName, int width, int height, int scale) {
        this.mapName = mapName;
        this.location = new Location(width, height);
        this.scale = scale;
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
    public int getWidth() {
        return location.getX(); }
    /**
     * Returns the height of the map.
     *
     * @return the height in grid units
     */
    public int getHeight() {
        return location.getY();
    }

    /**
     * Returns the stations on the map.
     *
     * @return the stations on the map
     */
    public List<Station> getStations() { return stations; }

    /**
     * Returns the industries on the map.
     *
     * @return the industries on the map
     */
    public List<Industry> getIndustries() { return industries; }

    /**
     * Returns the scale of the map.
     *
     * @return the scale of the map
     */
    public int getScale() {
        return scale;
    }

    /**
     * Adds a station to the map at the specified coordinates.
     *
     * @param industry   the Station to add
     * @param positionX the x-coordinate of the station
     * @param positionY the y-coordinate of the station
     */
    public boolean placeIndustry(Industry industry, int positionX, int positionY) {
        industry.setLocation(new Location(positionX, positionY));
        return industries.add(industry);
    }
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