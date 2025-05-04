package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Map {
    private final String mapName;
    private int width;
    private int height;
    private final List<City> cities = new ArrayList<>();

    public Map(String mapName, int width, int height) {
        validateName(mapName);
        this.mapName = mapName;
        this.width = width;
        this.height = height;
    }

    private void validateName(String mapName) {
        if (mapName == null || mapName.isEmpty() || !mapName.matches("^[a-zA-Z_ ]+$")) { throw new IllegalArgumentException("Map Name cannot be null or empty."); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Map map = (Map) o;
        return width == map.width && height == map.height && Objects.equals(mapName, map.mapName);
    }

    @Override
    public int hashCode() { return Objects.hash(mapName, width, height); }

    public Map clone() { return new Map(this.mapName, this.width, this.height); }

    public String getMapName() {
        return mapName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Adds a city to this map if not already present.
     * @param city the city to add
     * @return true if added, false if a city with same name exists
     */
    public boolean addCity(City city) {
        if (cities.stream().anyMatch(c -> c.getName().equals(city.getName()))) {
            return false;
        }
        cities.add(city);
        return true;
    }

    /**
     * Removes a city and clears its blocks.
     * @param city the city to remove
     */
    public void removeCity(City city) {
        if (cities.remove(city)) {
            city.clearBlocks();
        }
    }

    /**
     * @return the list of cities on that map.
     */
    public List<City> getCities() {
        return Collections.unmodifiableList(cities);
    }

}
