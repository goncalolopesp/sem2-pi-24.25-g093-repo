package domain;

/**
 * Budget entity representing financial resources for a player within a specific map and scenario
 * Manages the budget balance and related game context information
 */
public class Budget {

    /** Current budget amount */
    int budget;

    /** Map associated with this budget */
    Map map;

    /** Scenario associated with this budget */
    Scenario scenario;

    /** User who owns this budget */
    User user;

    /** Current year in the game timeline */
    private int currentYear;

    /**
     * Creates a new Budget with the specified parameters
     * @param budget Initial budget amount
     * @param map The map this budget is valid for
     * @param scenario The scenario this budget is valid for
     * @param user The user who owns this budget
     */
    public Budget(int budget, Map map, Scenario scenario, User user) {
        this.budget = budget;
        this.map = map;
        this.scenario = scenario;
        this.user = user;
        this.currentYear = scenario.getTimeRestrictions()[0];
    }

    /**
     * Gets the current budget amount
     * @return Current budget value
     */
    public int getBudget() {
        return budget;
    }

    /**
     * Sets the budget to a specific value
     * @param budget The new budget value
     */
    public void setBudget(int budget) {
        this.budget = budget;
    }

    /**
     * Increases the budget by the specified amount
     * @param budget Amount to add to the current budget
     */
    public void addBudget(int budget) {
        this.budget += budget;
    }

    /**
     * Decreases the budget by the specified amount
     * @param budget Amount to subtract from the current budget
     */
    public void subtractBudget(int budget) {
        this.budget -= budget;
    }

    /**
     * Gets the map associated with this budget
     * @return The associated map
     */
    public Map getMap() {
        return map;
    }

    /**
     * Sets the map associated with this budget
     * @param map The new map to associate
     */
    public void setMap(Map map) {
        this.map = map;
    }

    /**
     * Gets the scenario associated with this budget
     * @return The associated scenario
     */
    public Scenario getScenario() {
        return scenario;
    }

    /**
     * Sets the scenario associated with this budget
     * @param scenario The new scenario to associate
     */
    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    /**
     * Gets the user who owns this budget
     * @return The budget owner
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who owns this budget
     * @param user The new budget owner
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the current year in the game timeline
     * @return The current year
     */
    public int getCurrentYear() { return currentYear; }

    /**
     * Sets the current year in the game timeline
     * @param year The new current year
     */
    public void setCurrentYear(int year) { this.currentYear = year; }

}