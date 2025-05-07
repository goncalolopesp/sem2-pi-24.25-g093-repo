package domain;

/**
 * Represents a Station within a map.
 * <p>
 * The Map class encapsulates the map's unique name and its
 * width and height in grid units.
 * </p>
 */
public class Station {
    /** Available types of stations. */
    public enum Type { DEPOT, STATION, TERMINAL }

    private final Type type;
    private final String name;
    private final String mapName;
    private final String ownerPlayer;
    private final int price;
    private final int capacity;
    private final int x, y;

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
     */
    public Station(Type type, String name, String mapName, String ownerPlayer, int price, int capacity, int x, int y) {
        this.type = type;
        this.name = name;
        this.mapName = mapName;
        this.ownerPlayer = ownerPlayer;
        this.price = price;
        this.capacity = capacity;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the type of the station.
     *
     * @return the type of the station
     */
    public Type getType()           { return type; }
    /**
     * Returns the name of the station.
     *
     * @return the station's name
     */
    public String getName()         { return name; }
    /**
     * Returns the name of the map where the station is placed.
     *
     * @return the name of the map where the station is placed
     */
    public String getMapName()      { return mapName; }
    /**
     * Returns the name of the owner of the station
     *
     * @return the name of the owner of the station
     */
    public String getOwnerPlayer()  { return ownerPlayer; }
    /**
     * Returns the price of the station
     *
     * @return the price of the station
     */
    public int getPrice()           { return price; }
    /**
     * Returns the station's cargo capacity
     *
     * @return the station's cargo capacity
     */
    public int getCapacity()        { return capacity; }
    /**
     * Returns the x-coordinate of the station
     *
     * @return the x-coordinate of the station
     */
    public int getX()               { return x; }
    /**
     * Returns the y-coordinate of the station.
     *
     * @return the y-coordinate of the station
     */
    public int getY()               { return y; }
}
