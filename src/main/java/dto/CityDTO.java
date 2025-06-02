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

    public CityDTO(String name, String mapName, int centerX, int centerY, List<HouseBlockDTO> blocks) {
        this.name = name;
        this.mapName = mapName;
        this.centerX = centerX;
        this.centerY = centerY;
        this.blocks = blocks;
    }
}
