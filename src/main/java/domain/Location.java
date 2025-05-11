package domain;

import java.util.Objects;

public class Location {

    private int positionX;
    private int positionY;

    public Location(int X, int Y) {
        this.positionX = X;
        this.positionY = Y;
    }

    public int getX() {
        return positionX;
    }

    public int getY() {
        return positionY;
    }

    @Override
    public String toString() {
        return "Location{" +
                "position x=" + positionX +
                ",position y=" + positionY +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return positionX == location.positionX && positionY == location.positionY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionX, positionY);
    }
}
