package repository;

import domain.*;

import java.rmi.MarshalledObject;
import java.util.*;

public class BudgetRepository {

    Set<Budget> budgets = new HashSet<>();

    CurrentUser session = CurrentUser.getInstance();

    public boolean createBudget(Budget budget) {
        if (!doesBudgetExist(budget)) {
            budgets.add(budget);
            return true;
        }
        return false;
    }

    public boolean doesBudgetExist(Budget budget) {
        for (Budget b : budgets) {
            if (budget.equals(b)) {
                return true;
            }
        }
        return false;
    }

    public Optional<Budget> getUserBudgetFromScenarioInMap(Scenario scenario, domain.Map map, User user) {
        for (Budget budget : budgets) {
            if (budget.getMap().equals(map) && budget.getScenario().equals(scenario) && user.equals(budget.getUser())) {
                return Optional.of(budget);
            }
        }
        return Optional.empty();
    }

}
