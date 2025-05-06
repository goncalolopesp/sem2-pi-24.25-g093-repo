package domain;

public class Scenario {
    private final String scenarioName;
    private final String selectedMap;

    public Scenario(String scenarioName, String selectedMap) {
        this.scenarioName = scenarioName;
        this.selectedMap = selectedMap;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public String getSelectedMap() {
        return selectedMap;
    }

    @Override
    public String toString() {
        return getScenarioName();
    }
}