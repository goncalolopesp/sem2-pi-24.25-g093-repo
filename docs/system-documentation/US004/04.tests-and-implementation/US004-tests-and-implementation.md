# US004 - Create a Scenario

## 4. Tests

**Test 1:** Validates getScenarioName.

```java
@Test
void getScenarioName() {
    assertEquals("TestScenario", scenario.getScenarioName());
}
```

**Test 2:** Validates getSelectedMap.

```java
@Test
void getSelectedMap() {
    assertEquals("TestMap", scenario.getSelectedMap());
}
```

**Test 3:** Validates getTimeRestrictions.

```java
@Test
void getTimeRestrictions() {
    assertArrayEquals(new int[]{1870, 1880}, scenario.getTimeRestrictions());
}
```

**Test 4:** Validates getTechnologicalRestrictions.

```java
@Test
void getTechnologicalRestrictions() {
    assertEquals(Collections.singletonList("steam"), scenario.getTechnologicalRestrictions());
}
```

**Test 5:** Validates getHistoricalRestrictions.

```java
@Test
void getHistoricalRestrictions() {
    assertEquals(Collections.singletonList("none"), scenario.getHistoricalRestrictions());
}
```

**Test 6:** Validates adding a new scenario to the repository.

```java
@Test
void add() {
    repo.add(new Scenario("TestScenario3", "TestMap", new int[]{1855, 1867}, Collections.singletonList("steam"), Collections.singletonList("none")));
    assertEquals(3, repo.getScenarios().size());
}
```

**Test 7:** Validates whether the repository list contains the scenario or not.
```java
@Test
void validateScenario() {
    assertTrue(repo.getScenarios().contains(scenario));
    assertFalse(repo.getScenarios().contains(new Scenario("TestScenario4", "TestMap", new int[]{1870, 1880}, Collections.singletonList("diesel"), Collections.singletonList("none"))));
}
```

**Test 8:** Validates whether the scenario name is unique in the selected map.
```java
@Test
void checkNameUniqueness() {
    assertFalse(repo.checkNameUniqueness("TestScenario", "TestMap"));
    assertTrue(repo.checkNameUniqueness("TestScenario2", "TestMap2"));
}
```

**Test 9:** Validates getScenarios list.
```java
@Test
void getScenarios() {
    List<Scenario> scenarios = repo.getScenarios();
    assertEquals(2, scenarios.size());
    assertEquals("TestScenario", scenarios.get(0).getScenarioName());
    assertEquals("TestScenario2", scenarios.get(1).getScenarioName());
    assertThrows(UnsupportedOperationException.class, () -> scenarios.add(null));
}
```

## 5. Construction (Implementation)

### Class CreateScenarioController

```java
package controller;

import domain.Scenario;
import repository.Repositories;
import repository.ScenarioRepository;

import java.util.List;

/**
 * Controller responsible for creating and retrieving scenarios.
 * <p>
 * Delegates persistence to ScenarioRepository
 * </p>
 */

public class CreateScenarioController {
    private ScenarioRepository scenarioRepository;

    /**
     * Constructs a CreateScenarioController using the shared repositories instance.
     */
    public CreateScenarioController() { getScenarioRepository(); }

    /**
     * Constructs a CreateScenarioController with a provided ScenarioRepository (for testing).
     *
     * @param scenarioRepository the ScenarioRepository to use
     */
    public CreateScenarioController(ScenarioRepository scenarioRepository) { this.scenarioRepository = scenarioRepository; }

    /**
     * Returns the ScenarioRepository, initializing it lazily if necessary.
     *
     * @return the ScenarioRepository instance
     */
    private ScenarioRepository getScenarioRepository() {
        if (scenarioRepository == null) {
            Repositories repositories = Repositories.getInstance();
            scenarioRepository = repositories.getScenarioRepository();
        }
        return scenarioRepository;
    }

    /**
     * Creates a new scenario with the given name and associated map.
     *
     * @param scenarioName           the unique name of the scenario
     * @param selectedMap            the name of the map associated with this scenario
     * @param timeRestrictions
     * @param technologicalRestrictions
     * @param historicalRestrictions
     * @return the created Scenario instance
     */
    public Scenario createScenario(String scenarioName, String selectedMap, int[] timeRestrictions, List<String> technologicalRestrictions, List<String> historicalRestrictions) {
        Scenario scenario = new Scenario(scenarioName, selectedMap, timeRestrictions, technologicalRestrictions, historicalRestrictions);
        getScenarioRepository().add(scenario);
        return scenario;
    }

    /**
     * Checks if name is unique.
     *
     * @return boolean
     */
    public boolean checkNameUniqueness(String scenarioName, String mapName) {
        return getScenarioRepository().checkNameUniqueness(scenarioName, mapName);
    }

    /**
     * Retrieves all scenarios stored in the repository.
     *
     * @return unmodifiable list of existing Scenario instances
     */
    public List<Scenario> getScenarios() {
        return getScenarioRepository().getScenarios();
    }
}
```

### Class ScenarioRepository

```java
package repository;

import domain.Scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for storing and retrieving Scenario instances.
 * <p>
 * Provides methods to add scenarios, fetch a scenario by name, and retrieve all stored scenarios.
 * Ensures duplicate prevention via internal validation and protects internal state through defensive copies.
 * </p>
 */

public class ScenarioRepository {
    private final List<Scenario> scenarios
            /**
             * Constructs a new ScenarioRepository with an empty list of scenarios.
             */;
    public ScenarioRepository() { scenarios = new ArrayList<>(); }

    /**
     * Adds a new scenario to the repository if it is not already present.
     *
     * @param scenario the Scenario instance to add
     */
    public void add(Scenario scenario) {
        if (validateScenario(scenario)) {
            scenarios.add(scenario);
        }
    }

    /**
     * Validates that a scenario is not already contained in the repository.
     *
     * @param scenario the Scenario instance to validate
     * @return {@code true} if the scenario is not already present; {@code false} otherwise
     */
    private boolean validateScenario(Scenario scenario) {
        return !scenarios.contains(scenario);
    }

    public boolean checkNameUniqueness(String scenarioName, String selectedMap) {
        return scenarios.stream().noneMatch(s -> s.getScenarioName().equals(scenarioName) && s.getSelectedMap().equals(selectedMap));
    }

    /**
     * Returns a defensive copy of the list of all scenarios in the repository.
     *
     * @return unmodifiable list of stored {@link Scenario} objects
     */
    public List<Scenario> getScenarios() {
        //This is a defensive copy, so that the repository cannot be modified from the outside.
        return List.copyOf(scenarios);
    }
}
```


