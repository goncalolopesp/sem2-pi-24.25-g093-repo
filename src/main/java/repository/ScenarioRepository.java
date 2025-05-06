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
     * Retrieves a scenario by its name.
     *
     * @param name the name of the scenario to find
     * @return an Optional containing the found scenario, or empty if not found
     */
    public Optional<Scenario> getScenario(String name) {
        return scenarios.stream().filter(m -> m.getScenarioName().equals(name)).findFirst();
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

    public boolean checkNameUniqueness(String name) {
        return scenarios.stream().noneMatch(s -> s.getScenarioName().equals(name));
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
