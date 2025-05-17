package domain;

/**
 * Represents a Station within a map, now including orientation.
 */
public class Station {
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
    private int capacityOwned;
    private final int x, y;
    private final Orientation orientation;

    /**
     * Constructs a new Station.
     *
     * @param type         station type
     * @param name         generated station name
     * @param mapName      name of the map where station is placed
     * @param scenarioName name of the scenario where the station is placed
     * @param ownerPlayer  email of the player who owns this station
     * @param price        cost to build the station
     * @param capacity     maximum cargo capacity
     * @param capacityOwned the current amount of cargo owned by the station
     * @param x            x-coordinate on the map
     * @param y            y-coordinate on the map
     * @param orientation  orientation of the center (null for DEPOT and TERMINAL)
     */
    public Station(Type type, String name, String mapName,String scenarioName, String ownerPlayer,
                   int price, int capacity, int capacityOwned, int x, int y, Orientation orientation) {
        this.type        = type;
        this.name        = name;
        this.mapName     = mapName;
        this.scenarioName = scenarioName;
        this.ownerPlayer = ownerPlayer;
        this.price       = price;
        this.capacity    = capacity;
        this.capacityOwned = capacityOwned;
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
    public String getScenarioName()      { return mapName; }
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
     * Returns the cargo owned by the station
     *
     * @return the cargo owned by the station
     */
    public int getCapacityOwned()        { return capacityOwned; }

    /**
     * Sets the cargo owned by the station
     *
     * @param capacityOwned  the cargo that the station owns
     */
    public void setCapacityOwned(int capacityOwned)        { this.capacityOwned = capacityOwned; }

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
