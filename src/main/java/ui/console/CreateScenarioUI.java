package ui.console;

import controller.CreateScenarioController;
import domain.Map;
import domain.Scenario;

import java.util.*;

/**
 * Launches the user interface responsible for the creation of a map.
 */
public class CreateScenarioUI implements Runnable {
    /** Controller responsible for scenario creation logic. */
    private final CreateScenarioController ctrl;
    private String scenarioName;
    private Map selectedMap;
    private int[] timeRestrictions;
    private List<String> technologicalRestrictions;
    private List<String> historicalRestrictions;

    /**
     * Constructs a new CreateScenarioUI, initializing its controller.
     */
    public CreateScenarioUI(Map map) { this.selectedMap = map; ctrl = new CreateScenarioController(); }
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
        Optional<Scenario> scenario = Optional.ofNullable(getScenarioController().createScenario(scenarioName, selectedMap.getMapName(), timeRestrictions, technologicalRestrictions, historicalRestrictions));

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
        scenarioName = requestScenarioName(selectedMap.getMapName());
        timeRestrictions = requestTimeRestrictions();
        technologicalRestrictions = requestTechnologicalRestrictions();
        historicalRestrictions = requestHistoricalRestrictions();
    }

    /**
     * Asks the user to enter a scenario name and trims whitespace.
     *
     * @return the entered and trimmed scenario name
     */
    private String requestScenarioName(String selectedMap) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Scenario Name: ");
        String scenarioName;

        do {
            scenarioName = sc.nextLine();
            if (!ctrl.checkNameUniqueness(scenarioName, selectedMap)){
                System.out.print("\nName not unique. Try again: ");
            }
        } while (!ctrl.checkNameUniqueness(scenarioName, selectedMap));

        return scenarioName.trim();
    }

    /**
     * Asks the user to select a starting and final year.
     * Repeats until valid dates are provided.
     *
     * @return the entered time restrictions
     */
    private int[] requestTimeRestrictions() {
        Scanner sc = new Scanner(System.in);
        int[] timePeriod = new int[2];

        System.out.println("\n--- STARTING YEAR --------------------------");
        System.out.println("Note: Starting Year must be equal or higher than 1804 and lower than 2025.");
        System.out.print("Enter Starting Year: ");

        do{
            String input = sc.nextLine().trim();
            try {
                timePeriod[0] = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Error: '" + input + "' is not a valid integer. Please enter a number. ");
            }
            if (timePeriod[0] < 1804 || timePeriod[0] >= 2025) {
                System.out.print("Starting Year must be equal or higher than 1804 and lower than 2025. Try again: ");
            }
        } while (timePeriod[0] < 1804 || timePeriod[0] >= 2025);

        System.out.println("\n--- FINAL YEAR --------------------------");
        System.out.println("Note: Final Year must be higher than the Starting Year and equal or lower than 2025.");
        System.out.print("Enter Final Year: ");

        do{
            String input = sc.nextLine().trim();
            try {
                timePeriod[1] = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Error: '" + input + "' is not a valid integer. Please enter a number. ");
            }
            if (timePeriod[1] > 2025 || timePeriod[1] <= timePeriod[0]) {
                System.out.print("Final Year must be higher than the Starting Year and equal or lower than 2025. Try again: ");
            }
        } while (timePeriod[1] > 2025 || timePeriod[1] <= timePeriod[0]);

        return timePeriod;
    }

    /**
     * Asks the user to select the technological restrictions.
     *
     * @return the entered technological restrictions
     */
    private List<String> requestTechnologicalRestrictions() {
        Scanner sc = new Scanner(System.in);
        List<String> availableTypes = new ArrayList<>();
        String[] validTypes = {"steam", "diesel", "electric"};
        boolean valid;

        System.out.println("\n--- AVAILABLE LOCOMOTIVE TYPES --------------------------");
        System.out.println("Note: Available Locomotive Types must be either 'steam', 'diesel', and/or 'electric'.");
        System.out.print("Enter Available Locomotive Types: ");

        do {
            String[] input = sc.nextLine().split("\\s*,\\s*");

            for (String s : input) {
                valid = true;

                for (String validType : validTypes) {
                    if (s.equalsIgnoreCase(validType) && !availableTypes.contains("none")) {
                        availableTypes.add(validType); // Store the normalized lowercase version
                    } else {
                        valid = false;
                    }
                }
                if (!valid && !availableTypes.contains(s)) {
                    System.out.println("Invalid Type. Type '" + s + "' not added.");
                }
            }

            if (availableTypes.isEmpty()) {
                System.out.print("Invalid input. Please enter only steam, diesel, and/or electric: ");
            }
        } while (availableTypes.isEmpty());

        return availableTypes;
    }

    /**
     * Asks the user to select the historical restrictions.
     *
     * @return the entered technological restrictions
     */
    private List<String> requestHistoricalRestrictions() {
        Scanner sc = new Scanner(System.in);
        List<String> historicalEvents = new ArrayList<>();
        String[] validEvents = {"none", "vaccination campaign", "school program"};
        boolean valid;

        System.out.println("\n--- HISTORICAL EVENTS --------------------------");
        System.out.println("Note: Historical Events must be either 'none', 'vaccination program', and/or 'school program'.");
        System.out.print("Enter Historical Events: ");

        do {
            String[] input = sc.nextLine().split("\\s*,\\s*");

            for (String s : input) {
                valid = true;

                for (String validEvent : validEvents) {
                    if (s.equalsIgnoreCase(validEvent) && !historicalEvents.contains("none")) {
                        historicalEvents.add(validEvent); // Store the normalized lowercase version
                    } else {
                        valid = false;
                    }
                }
                if (!valid && !historicalEvents.contains(s)) {
                    System.out.println("Invalid Event. Event '" + s + "' not added.");
                }
            }

            if (historicalEvents.isEmpty()) {
                System.out.print("Invalid input. Please enter only vaccination campaign and/or school program: ");
            }
        } while (historicalEvents.isEmpty());

        return historicalEvents;
    }
}
