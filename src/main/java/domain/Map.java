package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Map {
    private final String mapName;
    private final int width;
    private final int height;
    private final List<City> cities = new ArrayList<>();

    public Map(String mapName, int width, int height) {
        this.mapName = mapName;
        this.width = width;
        this.height = height;
    }

    public String getMapName() { return mapName; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    /**
     * Adds a city if its name is unique on this map.
     */
    public boolean addCity(City city) {
        if (cities.stream().anyMatch(c -> c.getName().equals(city.getName()))) {
            return false;
        }
        cities.add(city);
        return true;
    }

    /**
     * Removes a city and clears its blocks (cascade due to composition).
     */
    public void removeCity(City city) {
        if (cities.remove(city)) {
            city.clearBlocks();
        }
    }

    /**
     * @return a list of cities
     */
    public List<City> getCities() { return Collections.unmodifiableList(cities); }
}