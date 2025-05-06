package repository;

import domain.Scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScenarioRepository {
    private final List<Scenario> scenarios;
    public ScenarioRepository() { scenarios = new ArrayList<>(); }

    public void add(Scenario scenario) {
        if (validateScenario(scenario)) {
            scenarios.add(scenario);
        }
    }

    public Optional<Scenario> getScenario(String name) {
        return scenarios.stream().filter(m -> m.getScenarioName().equals(name)).findFirst();
    }

    private boolean validateScenario(Scenario scenario) {
        return !scenarios.contains(scenario);
    }

    public List<Scenario> getScenarios() {
        //This is a defensive copy, so that the repository cannot be modified from the outside.
        return List.copyOf(scenarios);
    }
}
