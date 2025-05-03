package domain;

import java.util.Objects;

/**
 * Represents a single house block within a city, with specific coordinates.
 */
public class HouseBlock {
    private final int x;
    private final int y;

    /**
     * Constructs a house block at the specified coordinates.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     */
    public HouseBlock(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the X coordinate of the house block.
     *
     * @return The X coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the Y coordinate of the house block.
     *
     * @return The Y coordinate.
     */
    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HouseBlock)) return false;
        HouseBlock that = (HouseBlock) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
