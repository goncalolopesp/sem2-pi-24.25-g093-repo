package ui.console;

import controller.CreateMapController;
import controller.CreateScenarioController;
import controller.DisplayMapController;
import domain.Scenario;

import java.util.Optional;
import java.util.Scanner;

/**
 * Launches the user interface responsible for the creation of a map.
 * @see CreateScenarioController
 */
public class CreateScenarioUI implements Runnable {
    /** Controller responsible for scenario creation logic. */
    private final CreateScenarioController ctrl;
    private String scenarioName;
    private String selectedMap;

    /**
     * Constructs a new CreateScenarioUI, initializing its controller.
     */
    public CreateScenarioUI() { ctrl = new CreateScenarioController(); }
    /**
     * Returns the controller used to create scenarios.
     *
     * @return the CreateScenarioController instance
     */
    private CreateScenarioController getScenarioController() { return ctrl; }

    /**
     * Runs the user interface section.
     */
    @Override
    public void run() {
        System.out.println("\n\n--- Create Scenario ------------------------");
        requestData();
        submitData();
    }

    /**
     * Collects all data related to scenario creation.
     * Prints a success or failure message based on the result.
     */
    private void submitData() {
        Optional<Scenario> scenario = Optional.ofNullable(getScenarioController().createScenario(scenarioName, selectedMap));

        if (scenario.isPresent()) {
            System.out.println("\nScenario successfully created!");
        } else {
            System.out.println("\nScenario not created!");
        }
    }

    /**
     * Asks the user for scenario name and map selection.
     */
    private void requestData() {
        scenarioName = requestScenarioName();
        selectedMap = requestSelectedMap();
    }

    /**
     * Asks the user to enter a scenario name and trims whitespace.
     *
     * @return the entered and trimmed scenario name
     */
    private String requestScenarioName() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Scenario Name: ");
        return sc.nextLine().trim();
    }

    /**
     * Asks the user to select an existing map by name, verifying its existence.
     * Repeats until a valid map name is provided.
     *
     * @return the entered and trimmed map name
     */
    private String requestSelectedMap() {
        Scanner sc = new Scanner(System.in);
        DisplayMapController mctrl = new DisplayMapController();

        System.out.print("Select Map: ");

        do {
            selectedMap = sc.nextLine();
            if (!mctrl.verifyIfMapExists(selectedMap)) {
                System.out.print("\nMap doesn't exist! Try again: ");
            }
        } while (!mctrl.verifyIfMapExists(selectedMap));

        return selectedMap.trim();
    }
}
