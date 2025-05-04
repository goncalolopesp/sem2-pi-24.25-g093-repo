package domain;

import repository.CityRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class City {
    private String name;
    private String mapName;
    private int centerX;
    private int centerY;
    private final List<HouseBlock> blocks = new ArrayList<>();
    private final Random random = new Random();

    public City(String name, String mapName, int centerX, int centerY) {
        this.name = name;
        this.mapName = mapName;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public String getName() {
        return name;
    }

    public String getMapName() {
        return mapName;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    /**
     * Adds a house block manually if not already present.
     * @param block the block to add
     * @return true if added, false if duplicate
     */
    public boolean addBlockManual(HouseBlock block) {
        if (blocks.contains(block)) {
            return false;
        }
        blocks.add(block);
        return true;
    }

    /**
     * Generates and adds blocks automatically around the city center using normal distribution.
     * @param count number of blocks
     * @param sigma standard deviation
     */
    public void generateBlocksAuto(int count, double sigma, CityRepository repo) {
        Random rand = new Random();
        int generated = 0;
        while (generated < count) {
            int x = (int) Math.round(centerX + rand.nextGaussian() * sigma);
            int y = (int) Math.round(centerY + rand.nextGaussian() * sigma);
            HouseBlock hb = new HouseBlock(x, y);
            if (repo.isBlockPositionUnique(mapName, hb)) {
                blocks.add(hb);
                generated++;
            }
        }
    }

    /**
     * Clears all house blocks (used if a city needs to be removed).
     */
    public void clearBlocks() {
        blocks.clear();
    }

    public List<HouseBlock> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }
}
