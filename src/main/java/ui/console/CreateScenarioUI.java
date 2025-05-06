package ui.console;

import controller.CreateScenarioController;
import controller.DisplayMapController;
import domain.Scenario;

import java.util.Optional;
import java.util.Scanner;

/**
 * Launches the user interface responsible for the creation of a map.
 */
public class CreateScenarioUI implements Runnable {
    /** Controller responsible for scenario creation logic. */
    private final CreateScenarioController ctrl;
    private String scenarioName;
    private String selectedMap;
    private int[] timeRestrictions;
    private String[] technologicalRestrictions;
    private String[] historicalRestrictions;

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
        System.out.println(ctrl.getScenarios());
    }

    /**
     * Collects all data related to scenario creation.
     * Prints a success or failure message based on the result.
     */
    private void submitData() {
        Optional<Scenario> scenario = Optional.ofNullable(getScenarioController().createScenario(scenarioName, selectedMap, timeRestrictions, technologicalRestrictions, historicalRestrictions));

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
        selectedMap = requestSelectedMap();
        scenarioName = requestScenarioName();
        timeRestrictions = requestTimeRestrictions();
        technologicalRestrictions = requestTechnologicalRestrictions();
        historicalRestrictions = requestHistoricalRestrictions();
    }

    /**
     * Asks the user to enter a scenario name and trims whitespace.
     *
     * @return the entered and trimmed scenario name
     */
    private String requestScenarioName() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Scenario Name: ");
        String scenarioName;

        do {
            scenarioName = sc.nextLine();
            if (!ctrl.checkNameUniqueness(scenarioName)){
                System.out.print("\nName not unique. Try again: ");
            }
        } while (!ctrl.checkNameUniqueness(scenarioName));

        return scenarioName.trim();
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

    /**
     * Asks the user to select a starting and final year.
     * Repeats until valid dates are provided.
     *
     * @return the entered time restrictions
     */
    private int[] requestTimeRestrictions() {
        Scanner sc = new Scanner(System.in);
        int[] tR = new int[2];

        System.out.print("Enter Starting Year: ");
        tR[0] = sc.nextInt();
        System.out.print("Enter Final Year: ");
        tR[1] = sc.nextInt();

        return tR;
    }

    /**
     * Asks the user to select the technological restrictions.
     *
     * @return the entered technological restrictions
     */
    private String[] requestTechnologicalRestrictions() {
        Scanner sc = new Scanner(System.in);
        String[] tR = new String[3];

        System.out.print("Does the scenario contain steam locomotives? ");

        do {
            tR[0] = sc.next();
            if (!tR[0].equalsIgnoreCase("Y") && !tR[0].equalsIgnoreCase("N")) {
                System.out.print("Answer must be Y (Yes) or N (No). Try again: ");
            }
        } while (!tR[0].equalsIgnoreCase("Y") && !tR[0].equalsIgnoreCase("N"));

        System.out.print("Does the scenario contain diesel locomotives? ");

        do {
            tR[1] = sc.next();
            if (!tR[1].equalsIgnoreCase("Y") && !tR[1].equalsIgnoreCase("N")) {
                System.out.print("Answer must be Y (Yes) or N (No). Try again: ");
            }
        } while (!tR[1].equalsIgnoreCase("Y") && !tR[1].equalsIgnoreCase("N"));

        System.out.print("Does the scenario contain electric locomotives? ");

        do {
            tR[2] = sc.next();
            if (!tR[2].equalsIgnoreCase("Y") && !tR[2].equalsIgnoreCase("N")) {
                System.out.print("Answer must be Y (Yes) or N (No). Try again: ");
            }
        } while (!tR[2].equalsIgnoreCase("Y") && !tR[2].equalsIgnoreCase("N"));

        return tR;
    }

    /**
     * Asks the user to select the historical restrictions.
     *
     * @return the entered technological restrictions
     */
    private String[] requestHistoricalRestrictions() {
        Scanner sc = new Scanner(System.in);
        String[] hR = new String[2];

        System.out.print("Is a vaccination campaign present in the scenario? ");

        do {
            hR[0] = sc.next();
            if (!hR[0].equalsIgnoreCase("Y") && !hR[0].equalsIgnoreCase("N")) {
                System.out.print("Answer must be Y (Yes) or N (No). Try again: ");
            }
        } while (!hR[0].equalsIgnoreCase("Y") && !hR[0].equalsIgnoreCase("N"));

        System.out.print("Is a schooling program present in the scenario? ");

        do {
            hR[1] = sc.next();
            if (!hR[1].equalsIgnoreCase("Y") && !hR[1].equalsIgnoreCase("N")) {
                System.out.print("Answer must be Y (Yes) or N (No). Try again: ");
            }
        } while (!hR[1].equalsIgnoreCase("Y") && !hR[1].equalsIgnoreCase("N"));

        return hR;
    }
}
