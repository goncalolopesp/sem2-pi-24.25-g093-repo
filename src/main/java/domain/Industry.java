package domain;

public class Industry {
    private IndustryType type;
    private Location location;

    public Industry(IndustryType type, int positionX, int positionY) {
        this.type = type;
        this.location = new Location(positionX, positionY);
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
