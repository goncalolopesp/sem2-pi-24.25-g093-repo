package controller;

import domain.Budget;
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
    public Scenario createScenario(String scenarioName, String selectedMap, int[] timeRestrictions, List<String> technologicalRestrictions, List<String> historicalRestrictions, Budget budget) {
        Scenario scenario = new Scenario(scenarioName, selectedMap, timeRestrictions, technologicalRestrictions, historicalRestrictions, budget);
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
