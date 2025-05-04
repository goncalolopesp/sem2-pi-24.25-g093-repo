package domain;
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
    public void generateBlocksAuto(int count, double sigma) {
        for (int i = 0; i < count; i++) {
            int x = (int) Math.round(centerX + random.nextGaussian() * sigma);
            int y = (int) Math.round(centerY + random.nextGaussian() * sigma);
            HouseBlock block = new HouseBlock(x, y);
            if (!blocks.contains(block)) {
                blocks.add(block);
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
