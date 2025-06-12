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
    private int portBehavior;
    private List<String> technologicalRestrictions;
    private List<String> historicalRestrictions;
    private double productionRate;
    private double profitMultiplier;

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
        System.out.println("\n\n--- CREATE SCENARIO ------------------------");
        requestData();
        submitData();
    }

    /**
     * Submits the data required to create a new scenario.
     *
     * The method prompts the user to indicate whether the scenario should be saved
     * to a file or only retained in memory. It then initiates the creation of a
     * scenario using the specified parameters, which include the scenario name,
     * map name, time restrictions, port behavior, technological restrictions,
     * historical restrictions, production rate, and profit multiplier.
     *
     * Upon successful creation, the scenario is either saved to memory or persisted
     * to a file based on the user's input. If the creation fails, the user is
     * notified accordingly. Feedback messages about the operation's success or
     * failure are displayed to the console.
     */
    private void submitData() {
        boolean saveToFile = askUserToSaveToFile();

        Optional<Scenario> scenario = Optional.ofNullable(
                getScenarioController().createScenario(scenarioName, selectedMap.getMapName(), timeRestrictions,
                        portBehavior, technologicalRestrictions, historicalRestrictions,
                        productionRate, profitMultiplier, saveToFile)
        );

        if (scenario.isPresent()) {
            System.out.println("\nScenario successfully created!");
            if (!saveToFile) {
                System.out.println("Note: Scenario was saved in memory but not persisted to file.");
            }
        } else {
            System.out.println("\nScenario not created!");
        }
    }

    /**
     * Prompts the user to decide whether or not to save the scenario to a file.
     * The user is asked to enter "yes" or "no", and the input is validated.
     * If the input is invalid, the user is prompted again until valid input is provided.
     *
     * @return true if the user chooses to save the scenario to a file, false otherwise
     */
    private boolean askUserToSaveToFile() {
        Scanner sc = new Scanner(System.in);
        String input;

        while (true) {
            System.out.print("\nDo you want to save the scenario to the file? (yes/no): ");
            input = sc.nextLine().trim().toLowerCase();

            if (input.equals("yes")) {
                return true;
            } else if (input.equals("no")) {
                return false;
            } else {
                System.out.println("Invalid input. Please type 'yes' or 'no'.");
            }
        }
    }




    /**
     * Asks the user for scenario name and map selection.
     */
    private void requestData() {
        scenarioName = requestScenarioName(selectedMap.getMapName());
        timeRestrictions = requestTimeRestrictions();
        portBehavior = requestPortBehavior();
        technologicalRestrictions = requestTechnologicalRestrictions();
        historicalRestrictions = requestHistoricalRestrictions();
        productionRate = requestProductionRate();
        profitMultiplier = requestProfitMultiplier();
    }

    /**
     * Asks the user to enter a scenario name and trims whitespace.
     *
     * @return the entered and trimmed scenario name
     */
    private String requestScenarioName(String selectedMap) {
        Scanner sc = new Scanner(System.in);
        String scenarioName;

        System.out.println("\n--- SCENARIO NAME --------------------------");
        System.out.println("Note: Scenario Name must be unique and contain only alphanumeric characters and spaces.");
        System.out.print("Enter Scenario Name: ");

        do {
            scenarioName = sc.nextLine();
            if (!ctrl.checkNameUniqueness(scenarioName, selectedMap)){
                System.out.print("Error: Name not unique. Try again: ");
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
                System.out.print("Error: Starting Year must be equal or higher than 1804 and lower than 2025. Try again: ");
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
                System.out.print("Error: Final Year must be higher than the Starting Year and equal or lower than 2025. Try again: ");
            }
        } while (timePeriod[1] > 2025 || timePeriod[1] <= timePeriod[0]);

        return timePeriod;
    }

    private int requestPortBehavior() {
        Scanner sc = new Scanner(System.in);
        int portBehavior = 0;

        System.out.println("\n--- PORT BEHAVIOR --------------------------");
        System.out.println("Note: Chosen behavior must be included in the following list:");
        System.out.println("1 - Port1 (All Cargo available to import and export)");
        System.out.println("2 - Port2 (Only PEOPLE and MAIL are allowed)");
        System.out.print("Enter Port Behavior: ");

        do {
            String input = sc.nextLine().trim();

            try {
                portBehavior = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Error: '" + input + "' is not a valid integer. Please enter a number: ");
            }

            if (portBehavior < 1 || portBehavior > 2) {
                System.out.print("Error: Chosen behavior must be included in the list. Try again: ");
            }
        } while (portBehavior < 1 || portBehavior > 2);

        return portBehavior;
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
                        availableTypes.add(validType);
                    } else {
                        valid = false;
                    }
                }
                if (!valid && !availableTypes.contains(s)) {
                    System.out.println("Invalid Type. Type '" + s + "' not added.");
                }
            }

            if (availableTypes.isEmpty()) {
                System.out.print("Error: Please enter only steam, diesel, and/or electric: ");
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
        System.out.println("Note: Historical Events must be either 'none', 'vaccination campaign', and/or 'school program'.");
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
                System.out.print("Error: Please enter only vaccination campaign and/or school program: ");
            }
        } while (historicalEvents.isEmpty());

        return historicalEvents;
    }

    private double requestProductionRate() {
        Scanner sc = new Scanner(System.in);
        double productionRate = 1.0;

        System.out.println("\n--- PRODUCTION RATE --------------------------");
        System.out.println("Note: Production Rate must be between 0.5 and 2.0.");
        System.out.print("Enter Production Rate: ");

        do {
            String input = sc.nextLine().trim();

            try {
                productionRate = Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Error: '" + input + "' is not a valid integer. Please enter a number: ");
            }

            if (productionRate < 0.5 || productionRate > 2.0) {
                System.out.print("Error: Production Rate must be between 0.5 and 2.0. Try again: ");
            }
        } while (productionRate < 0.5 || productionRate > 2.0);

        return productionRate;
    }

    private double requestProfitMultiplier() {
        Scanner sc = new Scanner(System.in);
        double profitMultiplier = 1.0;

        System.out.println("\n--- PROFIT MULTIPLIER --------------------------");
        System.out.println("Note: Profit Multiplier must be between 0.5 and 2.0.");
        System.out.print("Enter Profit Multiplier: ");

        do {
            String input = sc.nextLine().trim();

            try {
                profitMultiplier = Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Error: '" + input + "' is not a valid integer. Please enter a number: ");
            }

            if (profitMultiplier < 0.5 || profitMultiplier > 2.0) {
                System.out.print("Error: Profit Multiplier must be between 0.5 and 2.0. Try again: ");
            }
        } while (profitMultiplier < 0.5 || profitMultiplier > 2.0);

        return profitMultiplier;
    }
}
