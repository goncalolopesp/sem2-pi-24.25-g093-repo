package ui.console;

import controller.CreateStationController;
import controller.CreateStationController.Error;
import domain.Map;
import domain.CurrentUser;
import domain.User;
import domain.Station;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * UI for buying and placing a station on a map.
 */
public class BuildStationUI implements Runnable {

    private final Map map;
    private final CreateStationController ctrl = new CreateStationController();

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
        while (!placed) {
            System.out.print("Enter X coordinate: ");
            Scanner sc = new Scanner(System.in);
            int x = sc.nextInt();
            System.out.print( "Enter Y coordinate: ");
            int y = sc.nextInt();

            Error result = ctrl.buildStation(typeName, map, x, y);
            switch (result) {
                case OK:
                    // find the newly created station to get its name
                    Optional<Station> st = ctrl.getStations().stream()
                            .filter(s -> s.getMapName().equals(map.getMapName())
                                    && s.getX() == x && s.getY() == y
                                    && s.getOwnerPlayer().equals(user.getEmail()))
                            .findFirst();
                    String stName = st.map(Station::getName).orElse("<unknown>");
                    System.out.println("Station '" + stName + "' built successfully at (" + x + "," + y + ").");
                    System.out.println("Remaining budget: " + CurrentUser.getInstance().getUser().getBudget());
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
