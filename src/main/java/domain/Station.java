package domain;

import java.io.Serializable;

/**
 * Represents a Station within a map, now including orientation.
 */
public class Station implements Serializable {
    /** Available types of stations. */
    public enum Type { DEPOT, STATION, TERMINAL }
    /** Orientation if the station is a STATION type. */
    public enum Orientation { NE, SE, NW, SW }

    private final Type type;
    private final String name;
    private final String mapName;
    private final String scenarioName;
    private final String ownerPlayer;
    private final int price;
    private final int capacity;
    private int peopleStored;
    private int mailStored;
    private int coalStored;
    private int ironStored;
    private int steelStored;
    private int capacityOwned;
    private transient boolean maxCapacityNotified = false;
    private final int x, y;
    private final Orientation orientation;

    /**
     * Constructs a new Station with the specified attributes.
     *
     * @param type         the type of the station (DEPOT, STATION, TERMINAL)
     * @param name         the name of the station
     * @param mapName      the name of the map where the station is located
     * @param scenarioName the scenario associated with the station
     * @param ownerPlayer  the name of the owner of the station
     * @param price        the price of the station
     * @param capacity     the maximum cargo capacity of the station
     * @param x            the x-coordinate of the station's location
     * @param y            the y-coordinate of the station's location
     * @param orientation  the orientation of the station (NE, SE, NW, SW) if the type is STATION
     */
    public Station(Type type, String name, String mapName,String scenarioName, String ownerPlayer,
                   int price, int capacity, int x, int y, Orientation orientation) {
        this.type        = type;
        this.name        = name;
        this.mapName     = mapName;
        this.scenarioName = scenarioName;
        this.ownerPlayer = ownerPlayer;
        this.price       = price;
        this.capacity    = capacity;
        this.peopleStored = 0;
        this.mailStored = 0;
        this.coalStored  = 0;
        this.ironStored  = 0;
        this.steelStored = 0;
        this.capacityOwned = 0;
        this.x           = x;
        this.y           = y;
        this.orientation = orientation;
    }

    /**
     * Returns the type of a staion.
     *
     * @return the type of a station
     */
    public Type getType()           { return type; }
    /**
     * Returns the name of a staion.
     *
     * @return the name of a station
     */
    public String getName()         { return name; }
    /**
     * Returns the name of the map where a staion is placed.
     *
     * @return the name of the map where a staion is placed
     */
    public String getMapName()      { return mapName; }

    /**
     * Returns the name of the scenario where a staion is placed.
     *
     * @return the name of the scenario where a staion is placed
     */
    public String getScenarioName()      { return scenarioName; }
    /**
     * Returns the owner of a station.
     *
     * @return the owner of a staiton
     */
    public String getOwnerPlayer()  { return ownerPlayer; }
    /**
     * Returns the price of a station.
     *
     * @return the price of a staiton
     */
    public int getPrice()           { return price; }
    /**
     * Returns the cargo capacity of a station.
     *
     * @return the cargo capacity of a staiton
     */
    public int getCapacity()        { return capacity; }

    /**
     * Returns the number of people currently stored at the station.
     *
     * @return the number of people stored
     */
    public int getPeopleStored() {
        return peopleStored;
    }

    /**
     * Updates the number of people currently stored at the station.
     *
     * @param peopleStored the new number of people to store at the station
     */
    public void setPeopleStored(int peopleStored) {
        this.peopleStored = peopleStored;
    }

    /**
     * Returns the amount of mail currently stored at the station.
     *
     * @return the amount of mail stored
     */
    public int getMailStored() {
        return mailStored;
    }

    /**
     * Updates the amount of mail currently stored at the station.
     *
     * @param mailStored the new amount of mail to store at the station
     */
    public void setMailStored(int mailStored) {
        this.mailStored = mailStored;
    }

    /**
     * Returns the amount of coal currently stored at the station.
     *
     * @return the amount of coal stored
     */
    public int getCoalStored() {
        return coalStored;
    }

    /**
     * Sets the amount of coal currently stored at the station.
     *
     * @param coalStored the amount of coal to set, representing the current coal storage
     */
    public void setCoalStored(int coalStored) {
        this.coalStored = coalStored;
    }

    /**
     * Returns the amount of iron currently stored at the station.
     *
     * @return the amount of iron stored
     */
    public int getIronStored() {
        return ironStored;
    }

    /**
     * Sets the amount of iron currently stored at the station.
     *
     * @param ironStored the amount of iron to set, representing the current iron storage
     */
    public void setIronStored(int ironStored) {
        this.ironStored = ironStored;
    }

    /**
     * Returns the amount of steel currently stored at the station.
     *
     * @return the amount of steel stored
     */
    public int getSteelStored() {
        return steelStored;
    }

    /**
     * Sets the amount of steel currently stored at the station.
     *
     * @param steelStored the amount of steel to set, representing the current steel storage
     */
    public void setSteelStored(int steelStored) {
        this.steelStored = steelStored;
    }

    public int getCapacityOwned() {
        return capacityOwned;
    }

    public void setCapacityOwned(int capacityOwned) {
        this.capacityOwned = capacityOwned;
    }

    /**
     * Returns the x-coordinate of a station.
     *
     * @return the x-coordinate of a staiton
     */
    public int getX()               { return x; }
    /**
     * Returns the y-coordinate of a station.
     *
     * @return the y-coordinate of a staiton
     */
    public int getY()               { return y; }
    /**
     * Returns the center's orientation of a station (null for DEPOT and TERMINAL).
     *
     * @return the center's orientation of a station (null for DEPOT and TERMINAL)
     */
    public Orientation getOrientation() { return orientation; }

    public boolean isMaxCapacityNotified() {
        return maxCapacityNotified;
    }
    public void setMaxCapacityNotified(boolean b) {
        this.maxCapacityNotified = b;
    }

    /**
     * Returns a String formatted print.
     *
     * @return a String formatted print
     */
    @Override
    public String toString() {
        return name + " " + type + " (" + x + ',' + y + ')';
    }
}
