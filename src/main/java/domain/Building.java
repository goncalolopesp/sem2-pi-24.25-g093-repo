package domain;

/**
 * Represents a building with a station and type.
 * <p>
 * The Building class encapsulates the building's associated station and its type.
 * </p>
 */
public class Building {
    private final Station station;
    private final String buildingType;

    /**
     * Constructs a new Map instance with the specified name and dimensions.
     *
     * @param station the name of the associated station
     * @param buildingType the type of the building
     */
    public Building(Station station, String buildingType) {
        this.station = station;
        this.buildingType = buildingType;
    }

    /**
     * Returns the name of the associated station.
     *
     * @return the station's name
     */
    public Station getStation() {
        return station;
    }

    /**
     * Returns the type of the building.
     *
     * @return the building's type
     */
    public String getBuildingType() {
        return buildingType;
    }
}
