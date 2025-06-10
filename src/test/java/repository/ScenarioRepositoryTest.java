package repository;

import domain.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScenarioRepositoryTest {
    private ScenarioRepository repo;
    private Scenario scenario = new Scenario("TestScenario", "TestMap", new int[]{1870, 1880}, 1, Collections.singletonList("steam"), Collections.singletonList("none"), 1, 1);

    @BeforeEach
    void setup() {
        repo = new ScenarioRepository();
        repo.add(scenario);
        repo.add(new Scenario("TestScenario2", "TestMap", new int[]{1970, 1980}, 1, List.of("steam", "diesel", "electric"), Collections.singletonList("school program"), 1, 1));
    }

    @Test
    void add() {
        repo.add(new Scenario("TestScenario3", "TestMap", new int[]{1855, 1867}, 1, Collections.singletonList("steam"), Collections.singletonList("none"), 1, 1));
        assertEquals(3, repo.getScenarios().size());
    }

    @Test
    void validateScenario() {
        assertTrue(repo.getScenarios().contains(scenario));
        assertFalse(repo.getScenarios().contains(new Scenario("TestScenario4", "TestMap", new int[]{1870, 1880}, 1, Collections.singletonList("diesel"), Collections.singletonList("none"), 1, 1)));
    }

    @Test
    void checkNameUniqueness() {
        assertFalse(repo.checkNameUniqueness("TestScenario", "TestMap"));
        assertTrue(repo.checkNameUniqueness("TestScenario2", "TestMap2"));
    }

    @Test
    void getScenarios() {
        List<Scenario> scenarios = repo.getScenarios();
        assertEquals(2, scenarios.size());
        assertEquals("TestScenario", scenarios.get(0).getScenarioName());
        assertEquals("TestScenario2", scenarios.get(1).getScenarioName());
        assertThrows(UnsupportedOperationException.class, () -> scenarios.add(null));
    }
}
