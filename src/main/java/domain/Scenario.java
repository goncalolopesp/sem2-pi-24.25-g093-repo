package domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
    private final int portBehavior;
    private final List<String> technologicalRestrictions;
    private final List<String> historicalRestrictions;
    private final double productionRate;
    private final double profitMultiplier;

    /**
     * Constructs a new Scenario instance.
     *
     * @param scenarioName           the unique name of the scenario
     * @param selectedMap            the name of the map associated with the scenario
     * @param timeRestrictions       the time restrictions associated with the scenario
     * @param technologicalRestrictions the technological restrictions associated with the scenario
     * @param historicalRestrictions the historical restrictions associated with the scenario
     */
    public Scenario(String scenarioName, String selectedMap, int[] timeRestrictions, int portBehavior, List<String> technologicalRestrictions, List<String> historicalRestrictions, double productionRate, double profitMultiplier) {
        this.scenarioName = scenarioName;
        this.selectedMap = selectedMap;
        this.timeRestrictions = timeRestrictions;
        this.portBehavior = portBehavior;
        this.technologicalRestrictions = technologicalRestrictions;
        this.historicalRestrictions = historicalRestrictions;
        this.productionRate = productionRate;
        this.profitMultiplier = profitMultiplier;
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
     * Returns the production rate for this scenario.
     *
     * @return the production rate
     */
    public double getProductionRate() {
        return productionRate;
    }

    /**
     * Returns the profit multiplier for this scenario.
     *
     * @return the profit multiplier
     */
    public double getProfitMultiplier() {
        return profitMultiplier;
    }

    @Override
    public String toString() {
        return getScenarioName();
    }

    /**
     * Compares this object with the specified object for equality.
     * The comparison is based on the scenarioName field.
     *
     * @param o the object to be compared for equality with this instance
     * @return true if the specified object is equal to this instance, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scenario scenario = (Scenario) o;
        return Objects.equals(scenarioName, scenario.scenarioName);
    }

    /**
     * Computes the hash code for this Scenario instance based on its {@code scenarioName} field.
     *
     * @return the hash code value for this Scenario instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(scenarioName);
    }
}