package domain;

public class Industry {
    private final String mapName;
    private IndustryType type;
    private Location location;
    private final int generatedIron;
    private final int generatedCoal;

    public Industry(String mapName, IndustryType type, int positionX, int positionY) {
        this.mapName = mapName;
        this.type = type;
        this.location = new Location(positionX, positionY);
        this.generatedIron = 6;
        this.generatedCoal = 4;
    }

    public String getMapName() {
        return mapName;
    }

    public IndustryType getType() {
        return type;
    }

    public void setType(IndustryType type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Industry{" +
                "type=" + type +
                ", location=" + location +
                '}';
    }

}
