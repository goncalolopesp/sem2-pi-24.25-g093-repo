package repository;

import domain.Scenario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and retrieving Scenario instances.
 * <p>
 * Provides methods to add scenarios, fetch a scenario by name, and retrieve all stored scenarios.
 * Ensures duplicate prevention via internal validation and protects internal state through defensive copies.
 * Includes file persistence functionality to save and load scenarios from "ScenarioStorage" file.
 * </p>
 */

public class ScenarioRepository {
    private final List<Scenario> scenarios;
    private static final String STORAGE_FILE = "ScenarioStorage.scenario";

    public ScenarioRepository() {
        scenarios = new ArrayList<>();
        loadScenariosFromFile();
    }

    /**
     * Adds a new scenario to the repository if it is not already present.
     * Automatically saves the updated scenario list to file.
     *
     * @param scenario the Scenario instance to add
     */
    public void add(Scenario scenario) {
        if (validateScenario(scenario)) {
            scenarios.add(scenario);
            saveScenariosToFile();
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

    /**
     * Saves all scenarios to the storage file using ObjectOutputStream.
     */
    private void saveScenariosToFile() {
        try (FileOutputStream fos = new FileOutputStream(STORAGE_FILE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(scenarios);

        } catch (IOException e) {
            System.err.println("Error saving scenarios to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads scenarios from the storage file using ObjectInputStream.
     * Called during repository initialization.
     */
    private void loadScenariosFromFile() {
        File file = new File(STORAGE_FILE);
        if (!file.exists()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(STORAGE_FILE);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> loadedList = (List<?>) obj;
                scenarios.clear();

                // Verify all objects in the list are Scenario instances
                for (Object item : loadedList) {
                    if (item instanceof Scenario) {
                        scenarios.add((Scenario) item);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Storage file not found. Starting with empty scenario list.");
        } catch (IOException e) {
            System.err.println("Error reading scenarios from file: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Error deserializing scenarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Manually saves scenarios to file.
     * Can be called externally if needed for backup purposes.
     */
    public void saveToFile() {
        saveScenariosToFile();
    }

    /**
     * Manually reloads scenarios from file.
     * Can be called externally if the file has been modified outside the application.
     */
    public void reloadFromFile() {
        loadScenariosFromFile();
    }
}