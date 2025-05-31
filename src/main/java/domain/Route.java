package domain;

import java.util.List;

/**
 * Represents a complete route with multiple points
 */
public class Route {
    private String id;
    private List<PointOfRoute> pointsOfRoute;
    private String playerEmail;
    private String mapName;
    private String scenarioName;

    public Route(List<PointOfRoute> pointsOfRoute, String playerEmail, String mapName, String scenarioName) {
        this.pointsOfRoute = pointsOfRoute;
        this.playerEmail = playerEmail;
        this.mapName = mapName;
        this.scenarioName = scenarioName;
        this.id = generateId();
    }

    private String generateId() {
        StringBuilder sb = new StringBuilder();
        for (PointOfRoute por : pointsOfRoute) {
            sb.append(por.getStation().getName()).append("-");
        }
        return sb.toString() + playerEmail + "_" + mapName + "_" + scenarioName;
    }

    public String getId() { return id; }
    public List<PointOfRoute> getPointsOfRoute() { return pointsOfRoute; }
    public String getPlayerEmail() { return playerEmail; }
    public String getMapName() { return mapName; }
    public String getScenarioName() { return scenarioName; }

    public void setPointsOfRoute(List<PointOfRoute> pointsOfRoute) { this.pointsOfRoute = pointsOfRoute; }
    public void setPlayerEmail(String playerEmail) { this.playerEmail = playerEmail; }
    public void setMapName(String mapName) { this.mapName = mapName; }
    public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }
}