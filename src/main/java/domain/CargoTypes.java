package domain;

import java.util.List;

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
     * Prints all available cargo types with their menu options
     */
    public static void printCargoOptions() {
        System.out.println("Select cargo type to collect:");
        CargoTypes[] all = values();
        for (int i = 0; i < all.length; i++) {
            System.out.printf("[%d] %s%n", i + 1, all[i].displayName);
        }
        System.out.printf("[%d] Both%n", all.length + 1);
    }

    /**
     * Returns the CargoTypes corresponding to a menu choice.
     * If choice == values().length+1, returns both.
     */
    public static List<CargoTypes> fromMenuOption(int choice) {
        CargoTypes[] all = values();
        if (choice >= 1 && choice <= all.length) {
            return List.of(all[choice - 1]);
        } else if (choice == all.length + 1) {
            return List.of(all);
        }
        throw new IllegalArgumentException("Invalid cargo choice: " + choice);
    }
}
