package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a railway line connecting two stations
 */
public class RailwayLine {
    private String id;
    private Station fromStation;
    private Station toStation;
    private RailwayLineType type;
    private int distance;
    private String ownerMail;
    private Scenario scenario;
    private Map map;
    private static final List<RailwayLine> allRailwayLines = new ArrayList<>();

    public RailwayLine(Station fromStation, Station toStation, RailwayLineType type, int distance,String ownerMail, Scenario scenario,Map map) {
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.id = generateId(fromStation, toStation);
        this.type = type;
        this.distance = distance;
        this.ownerMail = ownerMail;
        this.scenario =  scenario;
        this.map =  map;
        allRailwayLines.add(this);
    }

    private String generateId(Station from, Station to) {
        return from.getName() + "->" + to.getName() + "_" + from.getMapName() + "_" + from.getScenarioName();
    }

    public String getId() { return id; }
    public Station getFromStation() { return fromStation; }
    public Station getToStation() { return toStation; }
    public RailwayLineType getType() { return type; }
    public int getDistance() { return distance; }
    public String getOwnerMail() { return ownerMail; }
    public Scenario getScenario() { return scenario; }
    public Map getMap() { return map; }
}