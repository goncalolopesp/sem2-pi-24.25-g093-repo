package domain;

import java.util.Objects;

public class Map {
    private final String mapName;
    private int width;
    private int height;

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
}
