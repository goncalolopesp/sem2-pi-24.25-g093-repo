package controller;

import domain.Scenario;
import repository.Repositories;
import repository.ScenarioRepository;

import java.util.List;

public class CreateScenarioController {
    private ScenarioRepository scenarioRepository;

    public CreateScenarioController() { getScenarioRepository(); }

    public CreateScenarioController(ScenarioRepository scenarioRepository) { this.scenarioRepository = scenarioRepository; }

    private ScenarioRepository getScenarioRepository() {
        if (scenarioRepository == null) {
            Repositories repositories = Repositories.getInstance();
            scenarioRepository = repositories.getScenarioRepository();
        }
        return scenarioRepository;
    }

    public Scenario createScenario(String scenarioName, String selectedMap) {
        Scenario scenario = new Scenario(scenarioName, selectedMap);
        getScenarioRepository().add(scenario);
        return scenario;
    }

    public List<Scenario> getScenarios() {
        return getScenarioRepository().getScenarios();
    }
}
