package domain;

import java.util.List;
import java.util.ArrayList;

/**
 * Enumeration representing the different types of cargo that can be transported
 */
public enum CargoTypes {
    PEOPLE("People"),
    MAIL("Mail"),
    COAL("Coal"),
    IRON("Iron"),
    STEEL("Steel");

    private final String displayName;

    CargoTypes(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Displays the available cargo types for pickup, allowing users to select them by entering corresponding numbers.
     * The cargo types are displayed with their indexes in the format: [index] CargoTypeName.
     *
     * The options shown include people, mail, coal, and iron, but exclude steel.
     * Each cargo type is represented by its display name.
     */
    public static void printPickupCargoOptions() {
        System.out.println("Select cargo types to collect (enter numbers separated by spaces, for example, '1 2 3'):");
        CargoTypes[] pickupTypes = {PEOPLE, MAIL, COAL, IRON, STEEL}; // Steel excluded from pickup
        for (int i = 0; i < pickupTypes.length; i++) {
            System.out.printf("[%d] %s%n", i + 1, pickupTypes[i].displayName);
        }
    }

    /**
     * Parses a space-separated string of numeric selections and maps them to corresponding cargo types.
     * The numeric input values correspond to predefined cargo type indices, starting from 1.
     * Invalid or out-of-range selections are ignored, and duplicate selections are filtered out.
     * An exception is thrown if no valid cargo types are selected.
     *
     * @param input a space-separated string representing numeric selections of cargo types
     * @return a list of CargoTypes corresponding to the valid selections from the input
     * @throws IllegalArgumentException if no valid cargo selections are provided
     */
    public static List<CargoTypes> fromPickupMenuOptions(String input) {
        List<CargoTypes> selected = new ArrayList<>();
        CargoTypes[] pickupTypes = {PEOPLE, MAIL, COAL, IRON, STEEL};

        String[] choices = input.trim().split("\\s+");
        for (String choice : choices) {
            try {
                int index = Integer.parseInt(choice) - 1;
                if (index >= 0 && index < pickupTypes.length) {
                    CargoTypes cargoType = pickupTypes[index];
                    if (!selected.contains(cargoType)) {
                        selected.add(cargoType);
                    }
                }
            } catch (NumberFormatException ignored) {}
        }

        if (selected.isEmpty()) {
            throw new IllegalArgumentException("No valid cargo choices provided");
        }

        return selected;
    }
}