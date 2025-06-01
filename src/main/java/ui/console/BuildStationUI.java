package ui.console;

import controller.CreateStationController;
import controller.CreateStationController.Error;
import domain.*;
import domain.Station.Orientation;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;
import java.util.Scanner;

/**
 * Launches the user interface responsible to build the station.
 */
public class BuildStationUI implements Runnable {

    private final Map map;
    private final Scenario scenario;
    Budget budget;
    /** Controller handling station creation and validation. */
    private final CreateStationController ctrl = new CreateStationController();
    private static final List<String> VALID_ORIENTS =
            List.of("NE", "SE", "NW", "SW");

    public BuildStationUI(Map map, Scenario scenario, Budget budget) {
        this.map = map;
        this.scenario = scenario;
        this.budget = budget;
    }

    /**
     * Runs the "Build Station" user interface.
     * <p>
     * Asks for station type, the coordinates of the center, and if the station type is "STATION", asks the orientation (NE,SE,NW or SW)
     * </p>
     */
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

        if (budget.getBudget() < cost) {
            System.out.println("Insufficient funds. You have " + budget.getBudget() + ", but this station costs " + cost + ".");
            return;
        }

        boolean placed = false;
        Scanner sc = new Scanner(System.in);
        while (!placed) {
            int x = 0;
            int y = 0;
            boolean validX = false;
            boolean validY = false;
            while (!validX) {
                System.out.print("Enter X coordinate: ");
                String inputX = sc.nextLine().trim();
                try {
                    x = Integer.parseInt(inputX);
                    validX = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid coordinate.");
                }
            }
            while (!validY) {
                System.out.print("Enter Y coordinate: ");
                String inputY = sc.nextLine().trim();
                try {
                    y = Integer.parseInt(inputY);
                    validY = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid coordinate.");
                }
            }

            Orientation orientation = null;
            if (typeName.equals("STATION")) {
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

            Error result = ctrl.buildStation(typeName, map, scenario, x, y, orientation, budget);
            switch (result) {
                case OK:
                        List<Station> all = ctrl.getStations();
                        Station st = all.get(all.size() - 1);
                        System.out.println("Station '" + st.getName() +
                                "' built successfully at (" + st.getX() + "," + st.getY() + ")." + st.getMapName() + "." + st.getScenarioName());
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
