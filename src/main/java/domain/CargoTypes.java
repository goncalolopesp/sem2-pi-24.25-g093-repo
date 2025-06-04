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
     * Prints available cargo types for pickup (excludes Steel)
     */
    public static void printPickupCargoOptions() {
        System.out.println("Select cargo types to collect (enter numbers separated by spaces, for example, '1 2 3'):");
        CargoTypes[] pickupTypes = {PEOPLE, MAIL, COAL, IRON, STEEL}; // Steel excluded from pickup
        for (int i = 0; i < pickupTypes.length; i++) {
            System.out.printf("[%d] %s%n", i + 1, pickupTypes[i].displayName);
        }
    }

    /**
     * Returns CargoTypes list from space-separated menu choices for pickup
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

    public String getDisplayName() {
        return displayName;
    }
}