package domain;

import java.util.Objects;

/**
 * Represents a house block at specific coordinates within a map.
 * <p>
 * Encapsulates the x and y positions of a block. A house block belongs to one city and one city only.
 * </p>
 */
public class HouseBlock {
    private final int x;
    private final int y;
    private final int generatedPeople;
    private final int generatedMail;
    private final int generatedCargo;

    /**
     * Constructs a new HouseBlock at the given coordinates.
     *
     * @param x the x-coordinate of the block
     * @param y the y-coordinate of the block
     */
    public HouseBlock(int x, int y) {
        this.x = x;
        this.y = y;
        this.generatedPeople = 4;
        this.generatedMail = 3;
        this.generatedCargo = generatedPeople + generatedMail;
    }

    /**
     * Returns the x-coordinate of the block.
     *
     * @return the x-coordinate
     */
    public int getX() { return x; }
    /**
     * Returns the y-coordinate of the block.
     *
     * @return the y-coordinate
     */
    public int getY() { return y; }

    /**
     * Returns amount of people the houseblock generates at a certain rate.
     *
     * @return the amount of people the houseblock generates
     */
    public int getGeneratedPeople() {
        return generatedPeople;
    }

    /**
     * Returns amount of mail the houseblock generates at a certain rate.
     *
     * @return the amount of mail the houseblock generates
     */
    public int getGeneratedMail() {
        return generatedMail;
    }

    /**
     * Returns the total amount of cargo the houseblock produces
     *
     * @return the total amount of cargo the houseblock produces
     */
    public int getGeneratedCargo() {
        return generatedCargo;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare
     * @return {@code true} if this block has the same coordinates as the given object;
     *         {@code false} otherwise
     */
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HouseBlock)) return false;
        HouseBlock that = (HouseBlock) o;
        return x == that.x && y == that.y;
    }

    /**
     * Returns a hash code value for the object based on its coordinates.
     *
     * @return the hash code of this block
     */
    @Override public int hashCode() { return Objects.hash(x, y); }
}