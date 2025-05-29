package ui.console;

import domain.*;
import domain.Station.Orientation;
import domain.Station.Type;
import controller.UpgradeStationTypeController;
import repository.Repositories;
import repository.StationRepository;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Launches the user interface responsible for upgrading the type of a station.
 */
public class UpgradeStationTypeUI implements Runnable {
    private final Map selectedMap;
    private final Scenario selectedScenario;
    Budget budget;
    /**
     * Gets the selected map.
     */
    public UpgradeStationTypeUI(Map selectedMap, Scenario selectedScenario, Budget budget) {
        this.selectedMap = selectedMap;
        this.selectedScenario = selectedScenario;
        this.budget = budget;
    }

    /**
     * Runs the "UpgradeStationType" user interface.
     * <p>
     * Asks for the name of the station that will be upgraded. According to the type, will validate the possible upgrades, and will upgrade if possible.
     * </p>
     */
    @Override
    public void run() {
        Station station = requestStation();
        if (station == null) return;

        System.out.println("Selected station: " + station.getName() +
                " at (" + station.getX() + "," + station.getY() + ") - type: " + station.getType());

        Type current = station.getType();
        Orientation orientation = null;
        Type targetType;
        int cost;
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

        if (budget.getBudget() < cost) {
            System.out.println("Insufficient budget. Required: " + cost + ", Available: " + budget);
            return;
        }

        UpgradeStationTypeController controller = new UpgradeStationTypeController(selectedMap, selectedScenario, budget);
        boolean success = controller.upgradeType(station, targetType, orientation,budget,cost);
        if (success) {
            System.out.println("Upgrade to " + targetType + " completed.");
        }
    }

    /**
     * Prompts the player to select one of their stations by name,
     * listing all owned stations with center coordinates.
     * If duplicate names exist, asks for coordinates.
     * @return the selected station, or null if none was selected
     */
    private Station requestStation() {
        StationRepository repo = Repositories.getInstance().getStationRepository();
        List<Station> owned = repo.getStations().stream()
                .filter(s -> s.getOwnerPlayer().equals(CurrentUser.getInstance().getUser().getEmail()))
                .filter(s -> s.getScenarioName().equals(selectedScenario.getScenarioName()))
                .filter(s -> s.getMapName().equals(selectedMap.getMapName()))
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
