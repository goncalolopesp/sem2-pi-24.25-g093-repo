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

    public Route(List<PointOfRoute> pointsOfRoute, String playerEmail, String mapName, String scenarioName) {
        this.pointsOfRoute = pointsOfRoute;
        this.playerEmail = playerEmail;
        this.mapName = mapName;
        this.scenarioName = scenarioName;
        this.id = generateId();
        this.train = null; // Initially no train assigned
    }

    private String generateId() {
        StringBuilder sb = new StringBuilder();
        for (PointOfRoute por : pointsOfRoute) {
            sb.append(por.getStation().getName()).append("-");
        }
        return sb.toString() + playerEmail + "_" + mapName + "_" + scenarioName;
    }

    // Existing getters
    public String getId() {
        return id;
    }

    public List<PointOfRoute> getPointsOfRoute() {
        return pointsOfRoute;
    }

    // Added alias method for compatibility with SimulationController
    public List<PointOfRoute> getPoints() {
        return pointsOfRoute;
    }

    public String getPlayerEmail() {
        return playerEmail;
    }

    public String getMapName() {
        return mapName;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    // Added train getter and setter
    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    // Existing setters
    public void setPointsOfRoute(List<PointOfRoute> pointsOfRoute) {
        this.pointsOfRoute = pointsOfRoute;
        // Regenerate ID when points change
        this.id = generateId();
    }

    public void setPlayerEmail(String playerEmail) {
        this.playerEmail = playerEmail;
        // Regenerate ID when player email changes
        this.id = generateId();
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
        // Regenerate ID when map name changes
        this.id = generateId();
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
        // Regenerate ID when scenario name changes
        this.id = generateId();
    }

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