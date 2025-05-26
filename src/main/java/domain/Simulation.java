package domain;

import java.io.Serializable;
import java.util.Objects;

public class Simulation implements Serializable {
    private final Map selectedMap;
    private final Scenario selectedScenario;
    private final User user; // Added user to simulation
    private int currentYear;
    private Budget userBudget;

    /**
     * Constructs a new Simulation instance with the specified map, scenario, user, year, and budget.
     *
     * @param selectedMap the map chosen for the simulation
     * @param selectedScenario the scenario selected for the simulation
     * @param user the user participating in the simulation
     * @param currentYear the starting year of the simulation
     * @param userBudget the budget allocated to the user in the simulation
     */
    public Simulation(Map selectedMap, Scenario selectedScenario, User user, int currentYear, Budget userBudget) {
        this.selectedMap = selectedMap;
        this.selectedScenario = selectedScenario;
        this.user = user;
        this.currentYear = currentYear;
        this.userBudget = userBudget;
    }

    /**
     * Retrieves the map currently selected for the simulation.
     *
     * @return the selected map for the simulation
     */
    public Map getSelectedMap() {
        return selectedMap;
    }

    /**
     * Retrieves the scenario currently selected for the simulation.
     *
     * @return the selected scenario for the simulation
     */
    public Scenario getSelectedScenario() {
        return selectedScenario;
    }

    /**
     * Retrieves the user participating in the simulation.
     *
     * @return the user associated with the simulation
     */
    public User getUser() {
        return user;
    }

    /**
     * Retrieves the current year in the simulation's timeline.
     *
     * @return the current year of the simulation
     */
    public int getCurrentYear() { return currentYear; }

    /**
     * Retrieves the budget allocated to the user in the simulation.
     *
     * @return the user's budget within the simulation
     */
    public Budget getUserBudget() { return userBudget; }

    /**
     * Updates the current year for the simulation.
     *
     * @param year the new current year to set for the simulation
     */
    public void setCurrentYear(int year) { this.currentYear = year; }

    /**
     * Updates the user's budget for the simulation.
     *
     * @param userBudget the new budget to set for the user
     */
    public void setUserBudget(Budget userBudget) { this.userBudget = userBudget; }

    /**
     * Returns a string representation of the Simulation object.
     * The string contains information about the selected map, selected scenario,
     * the associated user (if any, represented by the user's email),
     * the current year of the simulation, and the user's budget.
     *
     * @return a string detailing the current state of the Simulation instance
     */
    @Override
    public String toString() {
        return "Simulation{" +
                "selectedMap=" + selectedMap +
                ", selectedScenario=" + selectedScenario +
                ", user=" + (user != null ? user.getEmail() : "null") +
                ", currentYear=" + currentYear +
                ", userBudget=" + userBudget +
                '}';
    }

    /**
     * Compares this Simulation object to the specified object for equality.
     * Determines if the given object is equivalent to this instance by comparing
     * the selected map, selected scenario, and user fields.
     *
     * @param o the object to be compared for equality with this instance
     * @return true if the specified object is equal to this Simulation instance; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Simulation)) return false;
        Simulation that = (Simulation) o;
        return selectedMap.equals(that.selectedMap) &&
                selectedScenario.equals(that.selectedScenario) &&
                Objects.equals(user, that.user);
    }

    /**
     * Generates a hash code for this Simulation object based on its selected map,
     * selected scenario, and user.
     *
     * @return an integer hash code value for this Simulation instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(selectedMap, selectedScenario, user);
    }
}
