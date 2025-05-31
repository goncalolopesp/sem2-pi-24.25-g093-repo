package domain;

public class Industry {
    private final String mapName;
    private IndustryType type;
    private Location location;
    private final int generatedIron;
    private final int generatedCoal;
    private final int area;

    /**
     * Constructs an Industry instance with the specified map name, industry type,
     * and location coordinates.
     *
     * @param mapName   the name of the map where the industry is located
     * @param type      the type of the industry (e.g., IRON_MINE, COAL_MINE)
     * @param positionX the x-coordinate of the industry's location
     * @param positionY the y-coordinate of the industry's location
     */
    public Industry(String mapName, IndustryType type, int positionX, int positionY) {
        this.mapName = mapName;
        this.type = type;
        this.location = new Location(positionX, positionY);
        this.generatedIron = 6;
        this.generatedCoal = 4;
        this.area = 9;
    }

    /**
     * Returns the name of the map associated with this Industry.
     *
     * @return the name of the map
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * Returns the type of the current industry.
     *
     * @return the IndustryType of this industry
     */
    public IndustryType getType() {
        return type;
    }

    /**
     * Sets the type of the current industry.
     *
     * @param type the new type of the industry (e.g., IRON_MINE, COAL_MINE)
     */
    public void setType(IndustryType type) {
        this.type = type;
    }

    /**
     * Returns the location of this Industry.
     *
     * @return the Location object representing the coordinates of this Industry
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the location of this Industry.
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    public int getArea() {
        return area;
    }

    /**
     * Returns a string representation of the Industry object.
     * The representation includes the type and location of the industry.
     *
     * @return a string describing the Industry instance
     */
    @Override
    public String toString() {
        return "Industry{" +
                "type=" + type +
                ", location=" + location +
                '}';
    }

}
