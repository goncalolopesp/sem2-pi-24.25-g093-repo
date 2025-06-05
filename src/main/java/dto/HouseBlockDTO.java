package dto;

/**
 * Data Transfer Object for the HouseBlock domain object.
 */
public class HouseBlockDTO {
    public int x;
    public int y;

    /**
     * Constructs a new instance of HouseBlockDTO.
     *
     * @param x the X-coordinate of the house block
     * @param y the Y-coordinate of the house block
     */
    public HouseBlockDTO(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
