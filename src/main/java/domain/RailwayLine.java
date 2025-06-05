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

    /**
     * Constructs a RailwayLine object representing a connection between two stations.
     *
     * @param fromStation the starting Station of the railway line
     * @param toStation the destination Station of the railway line
     * @param type the type of the railway line, represented by a RailwayLineType instance
     * @param distance the distance of the railway line in units
     * @param ownerMail the email address of the owner of the railway line
     * @param scenario the Scenario context in which the railway line exists
     * @param map the Map related to this railway line
     */
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

    /**
     * Generates a unique identifier for a railway line based on the provided stations, their map names, and scenario names.
     *
     * @param from the starting station of the railway line
     * @param to the destination station of the railway line
     * @return a unique identifier string in the format "fromStationName->toStationName_mapName_scenarioName"
     */
    private String generateId(Station from, Station to) {
        return from.getName() + "->" + to.getName() + "_" + from.getMapName() + "_" + from.getScenarioName();
    }

    /**
     * Retrieves the unique identifier of the railway line.
     *
     * @return the unique identifier of the railway line
     */
    public String getId() { return id; }

    /**
     * Retrieves the starting station of the railway line.
     *
     * @return the fromStation representing the starting point of the railway line
     */
    public Station getFromStation() { return fromStation; }

    /**
     * Retrieves the destination station of the railway line.
     *
     * @return the toStation representing the destination point of the railway line
     */
    public Station getToStation() { return toStation; }

    /**
     * Retrieves the type of the railway line.
     *
     * @return the type of the railway line, represented as a RailwayLineType
     */
    public RailwayLineType getType() { return type; }

    /**
     * Retrieves the distance of the railway line.
     *
     */
    public int getDistance() { return distance; }

    /**
     * Retrieves the email address of the owner of the railway line.
     *
     * @return the owner's email address as a String
     */
    public String getOwnerMail() { return ownerMail; }

    /**
     * Retrieves the scenario associated with the railway line.
     *
     * @return the scenario of the railway line
     */
    public Scenario getScenario() { return scenario; }

    /**
     * Retrieves the map associated with the railway line.
     *
     * @return the map related to this railway line
     */
    public Map getMap() { return map; }
}