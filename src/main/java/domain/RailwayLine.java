package domain;

/**
 * Represents a railway line connecting two stations
 */
public class RailwayLine {
    private String id;
    private Station fromStation;
    private Station toStation;
    private RailwayLineType type;

    public RailwayLine(Station fromStation, Station toStation, RailwayLineType type) {
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.id = generateId(fromStation, toStation);
        this.type = type;
    }

    private String generateId(Station from, Station to) {
        return from.getName() + "->" + to.getName() + "_" + from.getMapName() + "_" + from.getScenarioName();
    }

    public String getId() { return id; }
    public Station getFromStation() { return fromStation; }
    public Station getToStation() { return toStation; }
    public RailwayLineType getType() { return type; }

    public void setFromStation(Station fromStation) { this.fromStation = fromStation; }
    public void setToStation(Station toStation) { this.toStation = toStation; }
}