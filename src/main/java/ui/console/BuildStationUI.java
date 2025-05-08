package ui.console;

import controller.CreateStationController;
import controller.CreateStationController.Error;
import domain.Map;
import domain.CurrentUser;
import domain.User;
import domain.Station;
import domain.Station.Orientation;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * UI for buying and placing a station on a map, with orientation
 * prompt for Depot and Terminal.
 */
public class BuildStationUI implements Runnable {

    private final Map map;
    private final CreateStationController ctrl = new CreateStationController();
    private static final List<String> VALID_ORIENTS =
            List.of("NE", "SE", "NW", "SW");

    public BuildStationUI(Map map) {
        this.map = map;
    }

    @Override
    public void run() {
        User user = CurrentUser.getInstance().getUser();
        if (user == null) {
            System.out.println("No user logged in.");
            return;
        }

        List<String> types = List.of(
                "DEPOT (cost 50, cap 100)",
                "STATION (cost 100, cap 200)",
                "TERMINAL (cost 200, cap 400)"
        );
        int typeIdx = Utils.showAndSelectIndex(types, "Select station type:");
        String typeName;
        int cost;
        switch (typeIdx) {
            case 0:
                typeName = "DEPOT";
                cost = 50;
                break;
            case 1:
                typeName = "STATION";
                cost = 100;
                break;
            case 2:
                typeName = "TERMINAL";
                cost = 200;
                break;
            default:
                System.out.println("Invalid selection.");
                return;
        }

        // Validate budget before asking coords
        if (user.getBudget() < cost) {
            System.out.println("Insufficient funds. You have " + user.getBudget() + ", but this station costs " + cost + ".");
            return;
        }

        boolean placed = false;
        Scanner sc = new Scanner(System.in);
        while (!placed) {
            System.out.print("Enter X coordinate: ");
            int x = sc.nextInt();
            System.out.print("Enter Y coordinate: ");
            int y = sc.nextInt();

            // Only prompt orientation for DEPOT or TERMINAL
            Orientation orientation = null;
            if (typeName.equals("DEPOT") || typeName.equals("TERMINAL")) {
                while (true) {
                    System.out.print("Enter orientation (NE, SE, NW or SW): ");
                    String input = sc.next().trim().toUpperCase();
                    if (VALID_ORIENTS.contains(input)) {
                        orientation = Orientation.valueOf(input);
                        break;
                    } else {
                        System.out.println("Orientation is invalid. It must be NE, SE, NW ou SW.");
                    }
                }
            }

            // call controller with orientation (null for STATION)
            Error result = ctrl.buildStation(typeName, map, x, y, orientation);
            switch (result) {
                case OK:
                    Optional<Station> st = ctrl.getStations().stream()
                            .filter(s -> s.getMapName().equals(map.getMapName())
                                    && s.getX() == x && s.getY() == y
                                    && s.getOwnerPlayer().equals(user.getEmail()))
                            .findFirst();
                    String stName = st.map(Station::getName).orElse("<unknown>");
                    System.out.println("Station '" + stName + "' built successfully at (" + x + "," + y + ").");
                    placed = true;
                    break;
                case INVALID_COORDINATES:
                    System.out.println("Error: Coordinates out of map bounds. Please try again.");
                    break;
                case OVERBUILD:
                    System.out.println("Error: Cannot build on occupied cell. Please choose different coordinates.");
                    break;
                default:
                    System.out.println("Error: " + result + ". Aborting station build.");
                    placed = true;
                    break;
            }
        }
    }
}
