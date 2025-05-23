package domain;

import java.io.Serializable;
import java.util.Objects;

public class Simulation implements Serializable {
    private final Map selectedMap;
    private final Scenario selectedScenario;
    private final User user; // Added user to simulation
    private int currentYear;
    private Budget userBudget;

    public Simulation(Map selectedMap, Scenario selectedScenario, User user, int currentYear, Budget userBudget) {
        this.selectedMap = selectedMap;
        this.selectedScenario = selectedScenario;
        this.user = user;
        this.currentYear = currentYear;
        this.userBudget = userBudget;
    }

    public Map getSelectedMap() {
        return selectedMap;
    }

    public Scenario getSelectedScenario() {
        return selectedScenario;
    }

    public User getUser() {
        return user;
    }

    /**
     * Gets the current year in the game timeline
     * @return The current year
     */
    public int getCurrentYear() { return currentYear; }

    public Budget getUserBudget() {
        return userBudget;
    }

    /**
     * Sets the current year in the game timeline
     * @param year The new current year
     */
    public void setCurrentYear(int year) { this.currentYear = year; }

    public void setUserBudget(Budget userBudget) {
        this.userBudget = userBudget;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Simulation)) return false;
        Simulation that = (Simulation) o;
        return selectedMap.equals(that.selectedMap) &&
                selectedScenario.equals(that.selectedScenario) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectedMap, selectedScenario, user);
    }
}
