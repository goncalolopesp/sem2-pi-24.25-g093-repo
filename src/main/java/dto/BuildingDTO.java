package dto;

import domain.Building;

/**
 * A Data Transfer Object (DTO) for representing a building's basic information.
 * This class encapsulates the essential details of a building and is used to
 * transfer this specific data between different layers of the application.
 */
public class BuildingDTO {
    private final String buildingType;
    private final String stationName;

    /**
     * Constructs a new BuildingDTO instance.
     *
     * @param buildingType the type of the building
     * @param stationName the name of the station where the building is located
     */
    public BuildingDTO(String buildingType, String stationName) {
        this.buildingType = buildingType;
        this.stationName = stationName;
    }

    /**
     * Converts a Building entity into a BuildingDTO.
     *
     * @param building the Building entity to convert
     * @return a BuildingDTO representing the basic information of the Building entity
     */
    public static BuildingDTO fromEntity(Building building) {
        return new BuildingDTO(
                building.getBuildingType(),
                building.getStation().getName()
        );
    }

    /**
     * Retrieves the type of the building.
     *
     * @return the type of the building
     */
    public String getBuildingType() {
        return buildingType;
    }

    /**
     * Retrieves the name of the station where the building is located.
     *
     * @return the station name
     */
    public String getStationName() {
        return stationName;
    }

    @Override
    public String toString() {
        return String.format("%s at %s", buildingType, stationName);
    }
}