package repository;

import domain.*;

import java.util.*;

/**
 * Repository class for managing Budget entities
 * Responsible for storing, retrieving and validating budget objects
 */
public class BudgetRepository {

    /** Collection of all budgets in the system */
    Set<Budget> budgets = new HashSet<>();

    /** Current user session for context awareness */
    CurrentUser session = CurrentUser.getInstance();

    /**
     * Creates a new budget in the repository if it doesn't already exist
     * @param budget The budget object to add to the repository
     * @return true if the budget was successfully added, false if it already exists
     */
    public boolean createBudget(Budget budget) {
        if (!doesBudgetExist(budget)) {
            budgets.add(budget);
            return true;
        }
        return false;
    }

    /**
     * Checks if a budget already exists in the repository
     * @param budget The budget to check for existence
     * @return true if the budget exists, false otherwise
     */
    public boolean doesBudgetExist(Budget budget) {
        for (Budget b : budgets) {
            if (budget.equals(b)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a specific user's budget for a given scenario and map
     * @param scenario The scenario to find budget for
     * @param map The map to find budget for
     * @param user The user whose budget we're searching for
     * @return Optional containing the found budget or empty if not found
     */
    public Optional<Budget> getUserBudgetFromScenarioInMap(Scenario scenario, domain.Map map, User user) {
        for (Budget budget : budgets) {
            if (budget.getMap().equals(map) && budget.getScenario().equals(scenario) && user.equals(budget.getUser())) {
                return Optional.of(budget);
            }
        }
        return Optional.empty();
    }
}