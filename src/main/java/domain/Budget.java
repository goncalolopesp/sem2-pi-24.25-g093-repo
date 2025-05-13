package domain;

public class Budget {

    int budget;

    public Budget(int budget) {
        this.budget = budget;
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

}
