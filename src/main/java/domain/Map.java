package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Map {
    private final String mapName;
    private final int width;
    private final int height;

    public Map(String mapName, int width, int height) {
        this.mapName = mapName;
        this.width = width;
        this.height = height;
    }

    public String getMapName() { return mapName; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    @Override
    public String toString() {
        return getMapName();
    }
}