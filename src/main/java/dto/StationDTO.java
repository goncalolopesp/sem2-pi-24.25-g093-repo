package dto;

import domain.Station;

public class StationDTO {
    private final String name;
    private final int x;
    private final int y;

    public StationDTO(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public static StationDTO fromEntity(Station s) {
        return new StationDTO(
                s.getName(),
                s.getX(),
                s.getY()
        );
    }

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
}
