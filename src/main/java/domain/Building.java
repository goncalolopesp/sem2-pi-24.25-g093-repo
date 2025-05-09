package domain;

/**
 * Represents a building with a station and type.
 * <p>
 * The Building class encapsulates the building's associated station and its type.
 * </p>
 */
public class Building {
    private final String stationName;
    private final String buildingType;

    /**
     * Constructs a new Map instance with the specified name and dimensions.
     *
     * @param stationName the name of the associated station
     * @param buildingType the type of the building
     */
    public Building(String stationName, String buildingType) {
        this.stationName = stationName;
        this.buildingType = buildingType;
    }

    /**
     * Returns the name of the associated station.
     *
     * @return the station's name
     */
    public String getStationName() {
        return stationName;
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
