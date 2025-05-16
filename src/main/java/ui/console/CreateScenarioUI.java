package ui.console;

import controller.CreateMapController;
import controller.CreateScenarioController;
import controller.DisplayMapController;
import domain.Scenario;
import domain.*;
import repository.MapRepository;
import repository.Repositories;

import java.util.*;

/**
 * Launches the user interface responsible for the creation of a map.
 */
public class CreateScenarioUI implements Runnable {
    /** Controller responsible for scenario creation logic. */
    private final CreateScenarioController ctrl;
    private final Repositories repositories = Repositories.getInstance();
    private final MapRepository mapRepository = repositories.getMapRepository();
    private String scenarioName;
    private String selectedMap;
    private int[] timeRestrictions;
    private List<String> technologicalRestrictions;
    private List<String> historicalRestrictions;

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
        System.out.println(mapRepository.getMaps());
        System.out.print("Select Map: ");

        do {
            selectedMap = sc.nextLine();
            if (mapRepository.checkNameUniqueness(selectedMap)) {
                System.out.print("\nMap doesn't exist! Try again: ");
            }
        } while (!mapRepository.checkNameUniqueness(selectedMap));

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
        int[] timePeriod = new int[2];

        System.out.print("Enter Starting Year (Must be equal or higher than 1804 and lower than 2025): ");

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

        System.out.print("Enter Final Year (Must be equal or lower than 2025): ");

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

        System.out.print("Enter Available Locomotive Types (comma-separated: steam, diesel, electric): ");

        do {
            String[] input = sc.nextLine().split("\\s*,\\s*");

            for (int i = 0; i < input.length; i++) {
                valid = true;
                String item = input[i];

                for (int j = 0; j < validTypes.length; j++) {
                    if (item.equalsIgnoreCase(validTypes[j]) && !availableTypes.contains("none")) {
                        availableTypes.add(validTypes[j]); // Store the normalized lowercase version
                    } else {
                        valid = false;
                    }
                }
                if (!valid && !availableTypes.contains(item)) {
                    System.out.println("Invalid Type. Type '" + item + "' not added.");
                }
            }

            if (availableTypes.size() == 0) {
                System.out.print("Invalid input. Please enter only steam, diesel, and/or electric: ");
            }
        } while (availableTypes.size() == 0);

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

        System.out.print("Enter Historical Events (comma-separated: none, vaccination campaign, school program): ");

        do {
            String[] input = sc.nextLine().split("\\s*,\\s*");

            for (int i = 0; i < input.length; i++) {
                valid = true;
                String item = input[i];

                for (int j = 0; j < validEvents.length; j++) {
                    if (item.equalsIgnoreCase(validEvents[j]) && !historicalEvents.contains("none")) {
                        historicalEvents.add(validEvents[j]); // Store the normalized lowercase version
                    } else {
                        valid = false;
                    }
                }
                if (!valid && !historicalEvents.contains(item)) {
                    System.out.println("Invalid Event. Event '" + item + "' not added.");
                }
            }

            if (historicalEvents.size() == 0) {
                System.out.print("Invalid input. Please enter only vaccination campaign and/or school program: ");
            }
        } while (historicalEvents.size() == 0);

        return historicalEvents;
    }
}
