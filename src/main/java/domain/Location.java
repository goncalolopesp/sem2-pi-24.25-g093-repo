package domain;

import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {

    private final int positionX;
    private final int positionY;

    /**
     * Constructs a Location instance with specified x and y coordinates.
     *
     * @param X the x-coordinate of the location
     * @param Y the y-coordinate of the location
     */
    public Location(int X, int Y) {
        this.positionX = X;
        this.positionY = Y;
    }

    /**
     * Returns the x-coordinate of the location.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return positionX;
    }

    /**
     * Returns the y-coordinate associated with this location.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return positionY;
    }

    /**
     * Returns the string representation of this Location instance,
     * including its x and y coordinates.
     *
     * @return a string representation of the location in the format "Location{position x=X, position y=Y}"
     */
    @Override
    public String toString() {
        return "Location{" +
                "position x=" + positionX +
                ",position y=" + positionY +
                '}';
    }

    /**
     * Compares this Location instance with the specified object for equality.
     * Two Location instances are considered equal if they have the same x and y coordinates.
     *
     * @param o the object to compare with this Location instance
     * @return true if the specified object is a Location and has the same x and y coordinates; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return positionX == location.positionX && positionY == location.positionY;
    }

    /**
     * Computes and returns the hash code for this {@code Location} instance.
     * The hash code is computed based on the x and y coordinates of the location.
     *
     * @return the hash code value for this location
     */
    @Override
    public int hashCode() {
        return Objects.hash(positionX, positionY);
    }
}
