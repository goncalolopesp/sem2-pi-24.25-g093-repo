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
     * Creates a new Scenario with the provided attributes and adds it to the repository.
     *
     * @param scenarioName the unique name of the scenario
     * @param selectedMap the name of the map associated with the scenario
     * @param timeRestrictions the time restrictions associated with the scenario
     * @param portBehavior the behavior of the ports in the scenario
     * @param technologicalRestrictions the technological restrictions associated with the scenario
     * @param historicalRestrictions the historical restrictions associated with the scenario
     * @param productionRate the production rate applied within the scenario
     * @param profitMultiplier the profit multiplier applicable in the scenario
     * @param saveToFile a flag indicating whether to save the scenario to a file
     * @return the created Scenario instance
     */
    public Scenario createScenario(String scenarioName, String selectedMap, int[] timeRestrictions, int portBehavior,
                                   List<String> technologicalRestrictions, List<String> historicalRestrictions,
                                   double productionRate, double profitMultiplier, boolean saveToFile) {
        Scenario scenario = new Scenario(scenarioName, selectedMap, timeRestrictions, portBehavior,
                technologicalRestrictions, historicalRestrictions, productionRate, profitMultiplier);
        getScenarioRepository().add(scenario, saveToFile);
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
