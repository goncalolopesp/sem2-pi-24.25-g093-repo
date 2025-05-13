// File: ui/console/UpgradeStationTypeUI.java
package ui.console;

import domain.CurrentUser;
import domain.Map;
import domain.Station;
import domain.Station.Orientation;
import domain.Station.Type;
import controller.UpgradeStationTypeController;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * UI for upgrading a station's type, based on current station type and budget.
 */
public class UpgradeStationTypeUI implements Runnable {
    private final Map selectedMap;

    public UpgradeStationTypeUI(Map selectedMap) {
        this.selectedMap = selectedMap;
    }

    @Override
    public void run() {
        Station station = requestStation();
        if (station == null) return;

        System.out.println("Selected station: " + station.getName() +
                " at (" + station.getX() + "," + station.getY() + ") - type: " + station.getType());

        Type current = station.getType();
        Orientation orientation = null;
        Type targetType = null;
        int cost = 0;
        Scanner sc = new Scanner(System.in);

        switch (current) {
            case DEPOT:
                System.out.println("Upgrade options for DEPOT:");
                System.out.println(" 1) STATION - cost: 50");
                System.out.println(" 2) TERMINAL - cost: 150");
                break;
            case STATION:
                System.out.println("Upgrade options for STATION:");
                System.out.println(" 1) TERMINAL - cost: 100");
                break;
            case TERMINAL:
                System.out.println("This station is already a TERMINAL. No further upgrades available.");
                return;
            default:
                System.out.println("Unknown station type. No upgrades available.");
                return;
        }

        int choice;
        while (true) {
            System.out.print("Enter choice number: ");
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }
            if ((current == Type.DEPOT && (choice == 1 || choice == 2)) ||
                    (current == Type.STATION && choice == 1)) {
                break;
            }
            System.out.println("Invalid choice. Please try again.");
        }

        if (current == Type.DEPOT) {
            if (choice == 1) { targetType = Type.STATION; cost = 50; }
            else { targetType = Type.TERMINAL; cost = 150; }
        } else {
            targetType = Type.TERMINAL;
            cost = 100;
        }

        // If upgrading to STATION, ask for orientation
        if (targetType == Type.STATION) {
            System.out.println("Enter new center orientation (NW, NE, SW, SE): ");
            while (orientation == null) {
                String ori = sc.nextLine().trim().toUpperCase();
                try { orientation = Orientation.valueOf(ori); }
                catch (IllegalArgumentException ex) {
                    System.out.println("Invalid orientation. Choose NW, NE, SW or SE:");
                }
            }
        } else {
            orientation = station.getOrientation();
        }

        int budget = CurrentUser.getInstance().getUser().getBudgetValue();
        if (budget < cost) {
            System.out.println("Insufficient budget. Required: " + cost + ", Available: " + budget);
            return;
        }

        UpgradeStationTypeController controller = new UpgradeStationTypeController(selectedMap);
        boolean success = controller.upgradeType(station, targetType, orientation,cost);
        if (success) {
            System.out.println("Upgrade to " + targetType + " completed.");
        }
    }

    /**
     * Prompts the player to select one of their stations by name,
     * listing all owned stations with center coordinates.
     * If duplicate names exist, asks for coordinates to disambiguate.
     */
    private Station requestStation() {
        List<Station> owned = selectedMap.getStations().stream()
                .filter(s -> s.getOwnerPlayer().equals(CurrentUser.getInstance().getUser().getEmail()))
                .collect(Collectors.toList());
        if (owned.isEmpty()) {
            System.out.println("You have no stations on this map.");
            return null;
        }

        System.out.println("Your stations:");
        for (Station s : owned) {
            System.out.println(" - " + s.getName() +
                    " (center: " + s.getX() + "," + s.getY() + ") - type: " + s.getType());
        }

        Scanner sc = new Scanner(System.in);
        Station chosen = null;

        while (chosen == null) {
            System.out.print("Enter station name: ");
            String name = sc.nextLine().trim();
            List<Station> matches = owned.stream()
                    .filter(s -> s.getName().equalsIgnoreCase(name))
                    .collect(Collectors.toList());

            if (matches.isEmpty()) {
                System.out.println("Invalid name. Please try again.");
                continue;
            }

            if (matches.size() == 1) {
                chosen = matches.get(0);
            } else {
                System.out.println("Multiple stations named '" + name + "'.");
                System.out.print("Enter center X coordinate: ");
                int cx = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Enter center Y coordinate: ");
                int cy = Integer.parseInt(sc.nextLine().trim());
                for (Station s : matches) {
                    if (s.getX() == cx && s.getY() == cy) {
                        chosen = s;
                        break;
                    }
                }
                if (chosen == null) {
                    System.out.println("No station found at those coordinates. Please try again.");
                }
            }
        }
        return chosen;
    }
}
