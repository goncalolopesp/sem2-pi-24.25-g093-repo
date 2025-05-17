package domain;

public class Budget {

    int budget;
    Map map;
    Scenario scenario;
    User user;
    private int currentYear;

    public Budget(int budget, Map map, Scenario scenario, User user) {
        this.budget = budget;
        this.map = map;
        this.scenario = scenario;
        this.user = user;
        this.currentYear = scenario.getTimeRestrictions()[0];
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public void addBudget(int budget) {
        this.budget += budget;
    }

    public void subtractBudget(int budget) {
        this.budget -= budget;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCurrentYear() { return currentYear; }

    public void setCurrentYear(int year) { this.currentYear = year; }

}
