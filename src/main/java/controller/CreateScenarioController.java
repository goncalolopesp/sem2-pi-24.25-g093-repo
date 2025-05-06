package controller;

import domain.Scenario;
import repository.Repositories;
import repository.ScenarioRepository;

import java.util.List;

/**
 * Controller responsible for creating and retrieving scenarios.
 * <p>
 * Delegates persistence to ScenarioRepository}
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
     * @param scenarioName the unique name of the scenario
     * @param selectedMap  the name of the map associated with this scenario
     * @return the created Scenario instance
     */
    public Scenario createScenario(String scenarioName, String selectedMap) {
        Scenario scenario = new Scenario(scenarioName, selectedMap);
        getScenarioRepository().add(scenario);
        return scenario;
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
