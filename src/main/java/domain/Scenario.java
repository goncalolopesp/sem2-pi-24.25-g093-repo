package domain;

public class Scenario {
    /**
     * Represents a scenario, defined by a unique name and associated with a specific map, alongside restrictions.
     * <p>
     * A Scenario holds the scenario name and the name of the map on which it is based.
     * </p>
     */
    private final String scenarioName;
    private final String selectedMap;

    /**
     * Constructs a new Scenario instance.
     *
     * @param scenarioName the unique name of the scenario
     * @param selectedMap  the name of the map associated with the scenario
     */
    public Scenario(String scenarioName, String selectedMap) {
        this.scenarioName = scenarioName;
        this.selectedMap = selectedMap;
    }

    /**
     * Returns the name of the scenario.
     *
     * @return the scenario name
     */
    public String getScenarioName() {
        return scenarioName;
    }

    /**
     * Returns the name of the map selected for this scenario.
     *
     * @return the selected map name
     */
    public String getSelectedMap() {
        return selectedMap;
    }

    /**
     * Returns the string representation of the scenario, which is its name.
     *
     * @return the scenario name
     */
    @Override
    public String toString() {
        return getScenarioName();
    }
}