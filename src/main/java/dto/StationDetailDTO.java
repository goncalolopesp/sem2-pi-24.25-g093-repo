package dto;

import domain.Station;
import domain.Building;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A Data Transfer Object (DTO) for representing detailed station information.
 * This class encapsulates a station's complete information including its buildings
 * and is used to transfer this comprehensive data between different layers of the application.
 */
public class StationDetailDTO {
    private final String name;
    private final int x;
    private final int y;
    private final String ownerPlayer;
    private final int capacity;
    private final int capacityOwned;
    private final List<BuildingDTO> buildings;

    /**
     * Constructs a new StationDetailDTO instance.
     *
     * @param name the name of the station
     * @param x the X-coordinate of the station
     * @param y the Y-coordinate of the station
     * @param ownerPlayer the owner of the station
     * @param capacity the total capacity of the station
     * @param capacityOwned the owned capacity of the station
     * @param buildings the list of buildings at the station
     */
    public StationDetailDTO(String name, int x, int y, String ownerPlayer, 
                           int capacity, int capacityOwned, List<BuildingDTO> buildings) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.ownerPlayer = ownerPlayer;
        this.capacity = capacity;
        this.capacityOwned = capacityOwned;
        this.buildings = buildings;
    }

    /**
     * Converts a Station entity and its buildings into a StationDetailDTO.
     *
     * @param station the Station entity to convert
     * @param buildings the list of Building entities associated with the station
     * @return a StationDetailDTO representing the complete information of the Station entity
     */
    public static StationDetailDTO fromEntity(Station station, List<Building> buildings) {
        List<BuildingDTO> buildingDTOs = buildings.stream()
                .map(BuildingDTO::fromEntity)
                .collect(Collectors.toList());

        return new StationDetailDTO(
                station.getName(),
                station.getX(),
                station.getY(),
                station.getOwnerPlayer(),
                station.getCapacity(),
                station.getCapacityOwned(),
                buildingDTOs
        );
    }

    /**
     * Retrieves the name of the station.
     *
     * @return the name of the station
     */
    public String getName() { return name; }

    /**
     * Retrieves the X-coordinate of the station.
     *
     * @return the X-coordinate of the station
     */
    public int getX() { return x; }

    /**
     * Retrieves the Y-coordinate of the station.
     *
     * @return the Y-coordinate of the station
     */
    public int getY() { return y; }

    /**
     * Retrieves the owner of the station.
     *
     * @return the owner player name
     */
    public String getOwnerPlayer() { return ownerPlayer; }

    /**
     * Retrieves the total capacity of the station.
     *
     * @return the total capacity
     */
    public int getCapacity() { return capacity; }

    /**
     * Retrieves the owned capacity of the station.
     *
     * @return the owned capacity
     */
    public int getCapacityOwned() { return capacityOwned; }

    /**
     * Retrieves the list of buildings at the station.
     *
     * @return the list of building DTOs
     */
    public List<BuildingDTO> getBuildings() { return buildings; }

    @Override
    public String toString() {
        return String.format("Station: %s at (%d, %d) - Owner: %s - Capacity: %d/%d - Buildings: %d",
                name, x, y, ownerPlayer, capacityOwned, capacity, buildings.size());
    }
}