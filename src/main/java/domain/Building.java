package domain;

public class Building {
    private final String stationName;
    private final String buildingType;
    private final String owner;

    public Building(String stationName, String buildingType, String owner) {
        this.stationName = stationName;
        this.buildingType = buildingType;
        this.owner = owner;
    }

    public String getStationName() {
        return stationName;
    }

    public String getBuildingType() {
        return buildingType;
    }
}
