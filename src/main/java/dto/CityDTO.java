package dto;

import java.util.List;

/**
 * Data Transfer Object for the City domain object.
 */
public class CityDTO {
    public String name;
    public String mapName;
    public int centerX;
    public int centerY;
    public List<HouseBlockDTO> blocks;

    /**
     * Constructs a new CityDTO instance.
     *
     * @param name the name of the city
     * @param mapName the name of the map associated with the city
     * @param centerX the X-coordinate representing the city's center
     * @param centerY the Y-coordinate representing the city's center
     * @param blocks the list of HouseBlockDTO objects representing the blocks in the city
     */
    public CityDTO(String name, String mapName, int centerX, int centerY, List<HouseBlockDTO> blocks) {
        this.name = name;
        this.mapName = mapName;
        this.centerX = centerX;
        this.centerY = centerY;
        this.blocks = blocks;
    }
}
