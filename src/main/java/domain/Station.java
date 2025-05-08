package domain;

/**
 * Represents a Station within a map, now including orientation.
 */
public class Station {
    /** Available types of stations. */
    public enum Type { DEPOT, STATION, TERMINAL }
    /** Orientation if DEPOT or TERMINAL. */
    public enum Orientation { NE, SE, NW, SW }

    private final Type type;
    private final String name;
    private final String mapName;
    private final String ownerPlayer;
    private final int price;
    private final int capacity;
    private final int x, y;
    private final Orientation orientation;

    /**
     * Constructs a new Station.
     *
     * @param type         station type
     * @param name         generated station name
     * @param mapName      name of the map where station is placed
     * @param ownerPlayer  email of the player who owns this station
     * @param price        cost to build the station
     * @param capacity     maximum cargo capacity
     * @param x            x-coordinate on the map
     * @param y            y-coordinate on the map
     * @param orientation  orientation of the center (null for STATION)
     */
    public Station(Type type, String name, String mapName, String ownerPlayer,
                   int price, int capacity, int x, int y, Orientation orientation) {
        this.type        = type;
        this.name        = name;
        this.mapName     = mapName;
        this.ownerPlayer = ownerPlayer;
        this.price       = price;
        this.capacity    = capacity;
        this.x           = x;
        this.y           = y;
        this.orientation = orientation;
    }

    public Type getType()           { return type; }
    public String getName()         { return name; }
    public String getMapName()      { return mapName; }
    public String getOwnerPlayer()  { return ownerPlayer; }
    public int getPrice()           { return price; }
    public int getCapacity()        { return capacity; }
    public int getX()               { return x; }
    public int getY()               { return y; }
    public Orientation getOrientation() { return orientation; }
}
