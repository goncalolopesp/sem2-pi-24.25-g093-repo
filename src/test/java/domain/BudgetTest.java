package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BudgetTest {
    private Budget budget;
    private Map map;
    private Scenario scenario;
    private User user;
    private final int initialBudgetAmount = 1000;
    private final int[] timeRestrictions = {1900, 2000};
    private final List<String> techRestrictions = new ArrayList<>();
    private final List<String> histRestrictions = new ArrayList<>();
    private final String mapName = "TestMap";

    @BeforeEach
    void setUp() {
        techRestrictions.add("Steam only");
        histRestrictions.add("Pre-electric era");

        map = new Map(mapName, 100, 100, 2);
        scenario = new Scenario("TestScenario", mapName, timeRestrictions,1,techRestrictions, histRestrictions, 1,1);
        user = new User("Test User", "test@example.com", "password", "PLAYER");
        budget = new Budget(initialBudgetAmount, map, scenario, user);
    }

    @Test
    void getBudget() {
        assertEquals(initialBudgetAmount, budget.getBudget());
    }

    @Test
    void setBudget() {
        int newBudget = 2000;
        budget.setBudget(newBudget);
        assertEquals(newBudget, budget.getBudget());
    }

    @Test
    void addBudget() {
        int amountToAdd = 500;
        budget.addBudget(amountToAdd);
        assertEquals(initialBudgetAmount + amountToAdd, budget.getBudget());
    }

    @Test
    void subtractBudget() {
        int amountToSubtract = 300;
        budget.subtractBudget(amountToSubtract);
        assertEquals(initialBudgetAmount - amountToSubtract, budget.getBudget());
    }

    @Test
    void getMap() {
        assertEquals(map, budget.getMap());
    }

    @Test
    void setMap() {
        Map newMap = new Map("NewMap", 200, 200, 2);
        budget.setMap(newMap);
        assertEquals(newMap, budget.getMap());
    }

    @Test
    void getScenario() {
        assertEquals(scenario, budget.getScenario());
    }

    @Test
    void setScenario() {
        List<String> newTechRestrictions = new ArrayList<>();
        newTechRestrictions.add("Diesel only");
        List<String> newHistRestrictions = new ArrayList<>();
        newHistRestrictions.add("Post-war era");

        Scenario newScenario = new Scenario("NewScenario", "NewMap", new int[]{1950, 2050},1,
                newTechRestrictions, newHistRestrictions,1,1);
        budget.setScenario(newScenario);
        assertEquals(newScenario, budget.getScenario());
    }

    @Test
    void getUser() {
        assertEquals(user, budget.getUser());
    }

    @Test
    void setUser() {
        User newUser = new User("New User", "new@example.com", "newpassword", "EDITOR");
        budget.setUser(newUser);
        assertEquals(newUser, budget.getUser());
    }
}