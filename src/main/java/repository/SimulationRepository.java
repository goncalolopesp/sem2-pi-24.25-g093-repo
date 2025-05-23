package repository;

import domain.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SimulationRepository implements Serializable {
    private final List<Simulation> simulations;

    public SimulationRepository() { simulations = new ArrayList<>(); }

    public Simulation add(Simulation simulation) {
        Optional<Simulation> existing = findBy(
                simulation.getSelectedMap(),
                simulation.getSelectedScenario(),
                simulation.getUser()
        );

        if (existing.isPresent()) {
            // Update existing simulation and return it
            Simulation existingSim = existing.get();
            existingSim.setCurrentYear(simulation.getCurrentYear());
            existingSim.setUserBudget(simulation.getUserBudget());
            return existingSim;
        }

        // Add new simulation
        simulations.add(simulation);
        return simulation;
    }

    public void copyFrom(SimulationRepository other) {
        this.simulations.clear();
        this.simulations.addAll(other.getSimulations());
    }

    /**
     * Find every Simulation matching map+scenario+user and mutate its year & budget.
     */
    public void updateSimulation(Map map,
                                 Scenario scenario,
                                 User user,
                                 int newYear,
                                 Budget newBudget) {
        for (Simulation s : simulations) {
            if (s.getSelectedMap().equals(map) &&
                    s.getSelectedScenario().equals(scenario) &&
                    Objects.equals(s.getUser(), user)) {
                s.setCurrentYear(newYear);
                s.setUserBudget(newBudget);
                // if you only want to update the first match, break;
            }
        }
    }

    public List<Simulation> getSimulations() {
        return List.copyOf(simulations);
    }

    public Optional<Simulation> findBy(Map map, Scenario scenario, User user) {
        System.out.println("=== SEARCHING FOR SIMULATION ===");
        System.out.println("Looking for map: '" + map.getMapName() + "'");
        System.out.println("Looking for scenario: '" + scenario.getScenarioName() + "'");
        System.out.println("Looking for user: '" + (user != null ? user.getEmail() : "null") + "'");

        for (Simulation s : simulations) {
            System.out.println("Checking simulation:");
            System.out.println("  Has map: '" + s.getSelectedMap().getMapName() + "'");
            System.out.println("  Has scenario: '" + s.getSelectedScenario().getScenarioName() + "'");
            System.out.println("  Has user: '" + (s.getUser() != null ? s.getUser().getEmail() : "null") + "'");
            System.out.println("  Map equals: " + s.getSelectedMap().equals(map));
            System.out.println("  Scenario equals: " + s.getSelectedScenario().equals(scenario));
            System.out.println("  User equals: " + Objects.equals(s.getUser(), user));

            if (s.getSelectedMap().equals(map) &&
                    s.getSelectedScenario().equals(scenario) &&
                    Objects.equals(s.getUser(), user)) {
                System.out.println("  MATCH FOUND!");
                return Optional.of(s);
            }
        }
        System.out.println("  NO MATCH FOUND");
        return Optional.empty();
    }

    // Overloaded method for backward compatibility (without user parameter)
    public Optional<Simulation> findBy(Map map, Scenario scenario) {
        return findBy(map, scenario, null);
    }
}