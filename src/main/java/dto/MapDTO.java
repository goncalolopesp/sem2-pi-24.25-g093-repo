package dto;

import java.util.Objects;

public class MapDTO {
    private final String mapName;

    /**
     * Constructs a new MapDTO instance.
     *
     * @param mapName the name of the map being represented by this DTO
     */
    public MapDTO(String mapName) {
        this.mapName = mapName;
    }

    /**
     * Returns the name of the map.
     *
     * @return the map's unique name
     */
    public String getMapName() { return mapName; }

    /**
     * Compares this map to the specified object for equality.
     * The maps are considered equal if they have the same class and their names are equal.
     *
     * @param o the object to compare against this map
     * @return true if the specified object represents a map with the same name; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapDTO map = (MapDTO) o;
        return Objects.equals(mapName, map.mapName);
    }

    /**
     * Computes the hash code for this map.
     * The hash code is based on the unique name of the map.
     *
     * @return the hash code of the map
     */
    @Override
    public int hashCode() {
        return Objects.hash(mapName);
    }

    @Override
    public String toString() {
        return mapName;
    }
}
