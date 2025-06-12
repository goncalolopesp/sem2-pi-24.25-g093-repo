package repository;

import domain.Budget;
import domain.Map;
import domain.Scenario;
import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class BudgetRepositoryTest {

    private BudgetRepository budgetRepository;
    private User testUser;
    private Scenario testScenario;
    private Map testMap;

    @BeforeEach
    void setUp() {
        budgetRepository = new BudgetRepository();

        testUser = new User("Test User", "test@example.com", "password", "PLAYER");

        testMap = new Map("TestMap", 100, 100,1);

        int[] timeRestrictions = {1900, 1950};
        testScenario = new Scenario("Test Scenario", "TestMap", timeRestrictions,1,
                List.of("Steam", "Diesel"),
                List.of("Test"),1,1);
    }

    @Test
    void createBudgetTest() {
        Budget budget = new Budget(1000, testMap, testScenario, testUser);

        assertTrue(budgetRepository.createBudget(budget));

        assertFalse(budgetRepository.createBudget(budget));
    }

    @Test
    void doesBudgetExistTest() {
        Budget budget1 = new Budget(1000, testMap, testScenario, testUser);
        Budget budget2 = new Budget(2000, testMap, testScenario, testUser);

        assertFalse(budgetRepository.doesBudgetExist(budget1));

        budgetRepository.createBudget(budget1);
        assertTrue(budgetRepository.doesBudgetExist(budget1));

        assertFalse(budgetRepository.doesBudgetExist(budget2));
    }

    @Test
    void getUserBudgetFromScenarioInMapTest() {
        Budget budget = new Budget(1000, testMap, testScenario, testUser);
        budgetRepository.createBudget(budget);

        Optional<Budget> foundBudget = budgetRepository.getUserBudgetFromScenarioInMap(testScenario, testMap, testUser);
        assertTrue(foundBudget.isPresent());
        assertEquals(budget, foundBudget.get());

        User differentUser = new User("Different User", "different@example.com", "password", "PLAYER");
        Optional<Budget> notFoundBudget = budgetRepository.getUserBudgetFromScenarioInMap(testScenario, testMap, differentUser);
        assertFalse(notFoundBudget.isPresent());

        Scenario differentScenario = new Scenario("Different Scenario", "TestMap",
                new int[]{1920, 1970},1,
                List.of("Electric"),
                List.of("American"),1,1);
        notFoundBudget = budgetRepository.getUserBudgetFromScenarioInMap(differentScenario, testMap, testUser);
        assertFalse(notFoundBudget.isPresent());

        Map differentMap = new Map("DifferentMap", 100, 100,1);
        notFoundBudget = budgetRepository.getUserBudgetFromScenarioInMap(testScenario, differentMap, testUser);
        assertFalse(notFoundBudget.isPresent());
    }
}