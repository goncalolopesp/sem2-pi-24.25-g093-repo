package repository;

import domain.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SimulationRepository implements Serializable {
    private final List<Simulation> simulations;

    /**
     * Default constructor for the SimulationRepository class.
     * Initializes an empty list of Simulation objects to manage in this repository.
     */
    public SimulationRepository() { simulations = new ArrayList<>(); }

    /**
     * Adds a simulation to the repository. If a simulation with the same map, scenario,
     * and user already exists, updates the existing simulation's year and budget instead.
     *
     * @param simulation the simulation to be added or updated in the repository
     * @return the existing simulation if updated, or the newly added simulation
     */
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

    /**
     * Copies the simulations from another SimulationRepository into this repository.
     * Clears all existing simulations in this repository before copying.
     *
     * @param other the SimulationRepository from which simulations will be copied
     */
    public void copyFrom(SimulationRepository other) {
        this.simulations.clear();
        this.simulations.addAll(other.getSimulations());
    }

    /**
     * Updates the simulation parameters in the repository that match the specified map, scenario, and user.
     * Updates the simulation's year and budget if a match is found.
     *
     * @param map       the map associated with the simulation to update
     * @param scenario  the scenario associated with the simulation to update
     * @param user      the user associated with the simulation to update
     * @param newYear   the new current year to set for the simulation
     * @param newBudget the new budget to set for the simulation
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

    /**
     * Retrieves an immutable copy of the list of simulations managed by this repository.
     *
     * @return a list containing all simulations in the repository, returned as an unmodifiable copy
     */
    public List<Simulation> getSimulations() {
        return List.copyOf(simulations);
    }

    /**
     * Searches for a simulation in the repository that matches the specified map, scenario, and user.
     * If a matching simulation is found, it is returned wrapped in an Optional.
     * If no match is found, an empty Optional is returned.
     *
     * @param map the map to match in the simulation
     * @param scenario the scenario to match in the simulation
     * @param user the user associated with the simulation to match (can be null)
     * @return an Optional containing the matching simulation if found, or an empty Optional if no match is found
     */
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
}