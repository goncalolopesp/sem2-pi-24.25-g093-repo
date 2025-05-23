package domain;

import java.io.Serializable;
import java.util.List;

public class Scenario implements Serializable {
    /**
     * Represents a scenario, defined by a unique name and associated with a specific map, alongside restrictions.
     * <p>
     * A Scenario holds the scenario name and the name of the map on which it is based.
     * </p>
     */
    private final String scenarioName;
    private final String selectedMap;
    private final int[] timeRestrictions;
    private final List<String> technologicalRestrictions;
    private final List<String> historicalRestrictions;

    /**
     * Constructs a new Scenario instance.
     *
     * @param scenarioName           the unique name of the scenario
     * @param selectedMap            the name of the map associated with the scenario
     * @param timeRestrictions       the time restrictions associated with the scenario
     * @param technologicalRestrictions the technological restrictions associated with the scenario
     * @param historicalRestrictions the historical restrictions associated with the scenario
     */
    public Scenario(String scenarioName, String selectedMap, int[] timeRestrictions, List<String> technologicalRestrictions, List<String> historicalRestrictions) {
        this.scenarioName = scenarioName;
        this.selectedMap = selectedMap;
        this.timeRestrictions = timeRestrictions;
        this.technologicalRestrictions = technologicalRestrictions;
        this.historicalRestrictions = historicalRestrictions;
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
    public String getSelectedMap() { return selectedMap; }

    /**
     * Returns the time restrictions for this scenario.
     *
     * @return the time restrictions
     */
    public int[] getTimeRestrictions() {
        return timeRestrictions;
    }

    /**
     * Returns the technological restrictions for this scenario.
     *
     * @return the technological restrictions
     */
    public List<String> getTechnologicalRestrictions() { return technologicalRestrictions; }

    /**
     * Returns the historical restrictions for this scenario.
     *
     * @return the historical restrictions
     */
    public List<String> getHistoricalRestrictions() {
        return historicalRestrictions;
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