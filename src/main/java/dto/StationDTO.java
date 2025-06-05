package dto;

import domain.Station;

/**
 * A Data Transfer Object (DTO) for representing a station's basic information.
 * This class encapsulates the name and coordinates (x, y) of a station
 * and is used to transfer this specific data between different layers of the application.
 */
public class StationDTO {
    private final String name;
    private final int x;
    private final int y;

    /**
     * Constructs a new StationDTO instance.
     *
     * @param name the name of the station
     * @param x the X-coordinate of the station
     * @param y the Y-coordinate of the station
     */
    public StationDTO(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    /**
     * Converts a Station entity into a StationDTO.
     *
     * @param s the Station entity to convert
     * @return a StationDTO representing the basic information (name and coordinates) of the Station entity
     */
    public static StationDTO fromEntity(Station s) {
        return new StationDTO(
                s.getName(),
                s.getX(),
                s.getY()
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
}
