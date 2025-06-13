package domain;

import java.util.List;

/**
 * Represents a complete route with multiple points and an associated train
 */
public class Route {
    private String id;
    private List<PointOfRoute> pointsOfRoute;
    private String playerEmail;
    private String mapName;
    private String scenarioName;
    private Train train; // Added train association

    /**
     * Constructs a new Route object.
     *
     * @param pointsOfRoute the list of points defining the route, each representing a station and associated cargo operations
     * @param playerEmail   the email of the player associated with this route
     * @param mapName       the name of the map where this route is located
     * @param scenarioName  the name of the scenario associated with this route
     */
    public Route(List<PointOfRoute> pointsOfRoute, String playerEmail, String mapName, String scenarioName) {
        this.pointsOfRoute = pointsOfRoute;
        this.playerEmail = playerEmail;
        this.mapName = mapName;
        this.scenarioName = scenarioName;
        this.id = generateId();
        this.train = null; // Initially no train assigned
    }

    /**
     * Generates a unique identifier for the route based on its points of route, player email, map name, and scenario name.
     * The identifier combines the names of all stations in the route separated by hyphens, followed by the player email,
     * map name, and scenario name, all concatenated with underscores.
     *
     * @return a unique string representing the route identifier
     */
    private String generateId() {
        StringBuilder sb = new StringBuilder();
        for (PointOfRoute por : pointsOfRoute) {
            sb.append(por.getStation().getName()).append("-");
        }
        return sb.toString() + playerEmail + "_" + mapName + "_" + scenarioName;
    }

    /**
     * Returns the unique identifier of the route.
     *
     * @return the unique identifier associated with this route
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the list of points that define the route.
     *
     * @return a list of {@code PointOfRoute} objects representing the stations and cargo operations for this route
     */
    public List<PointOfRoute> getPointsOfRoute() {
        return pointsOfRoute;
    }

    /**
     * Retrieves the points that define the route.
     *
     * @return a list of {@code PointOfRoute} objects representing the stations and associated cargo operations that make up this route
     */
    public List<PointOfRoute> getPoints() {
        return pointsOfRoute;
    }

    /**
     * Retrieves the email address of the player associated with this route.
     *
     * @return the email address of the player as a string
     */
    public String getPlayerEmail() {
        return playerEmail;
    }

    /**
     * Retrieves the name of the map associated with this route.
     *
     * @return the name of the map as a string
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * Retrieves the name of the scenario associated with this route.
     *
     * @return the scenario name as a string
     */
    public String getScenarioName() {
        return scenarioName;
    }

    /**
     * Retrieves the train associated with the route.
     *
     * @return the train associated with this route
     */
    public Train getTrain() {
        return train;
    }

    /**
     * Sets the train associated with the route.
     *
     * @param train the train to be set for the route, encapsulating a locomotive, carriages, and user information
     */
    public void setTrain(Train train) {
        this.train = train;
    }

    /**
     * Sets the name of the map associated with this route and updates the route identifier accordingly.
     *
     * @param mapName the name of the map to be set for this route
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
        this.id = generateId();
    }

    /**
     * Sets the name of the scenario associated with the route and updates*/
    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
        this.id = generateId();
    }

    public boolean isReadyToDepart(int pointIndex, int currentLoad) {
        if (pointIndex >= 0 && pointIndex < pointsOfRoute.size()) {
            return pointsOfRoute.get(pointIndex).isReadyToDepart(currentLoad, (train.getCarriages().size() * train.getCarriages().get(0).getCapacity()));
        }
        return false;
    }

    /**
     * Returns a string representation of the Route object.
     * The string includes key information about the route, such as its unique identifier,
     * the number of points in the route, the associated player email, map name, scenario name,
     * and whether a train is associated with the route.
     *
     * @return a string representation of the Route object
     */
    @Override
    public String toString() {
        return "Route{" +
                "id='" + id + '\'' +
                ", pointsCount=" + (pointsOfRoute != null ? pointsOfRoute.size() : 0) +
                ", playerEmail='" + playerEmail + '\'' +
                ", mapName='" + mapName + '\'' +
                ", scenarioName='" + scenarioName + '\'' +
                ", hasTrain=" + (train != null) +
                '}';
    }
}