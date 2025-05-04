package domain;
import controller.CreateCityController;
import repository.CityRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class City {
    private final String name;
    private final String mapName;
    private final int centerX;
    private final int centerY;
    private final List<HouseBlock> blocks = new ArrayList<>();

    public City(String name, String mapName, int centerX, int centerY) {
        this.name = name;
        this.mapName = mapName;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public String getName() { return name; }
    public String getMapName() { return mapName; }
    public int getCenterX() { return centerX; }
    public int getCenterY() { return centerY; }
    public List<HouseBlock> getBlocks() { return Collections.unmodifiableList(blocks); }

    /**
     * Adds a house block (manual).
     */
    public void addBlockManual(HouseBlock b) { blocks.add(b); }

    /**
     * Clears all house blocks (cascade removal).
     */
    public void clearBlocks() { blocks.clear(); }
}