package ui.console;

import controller.CreateStationController;
import controller.UpgradeStationController;
import domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Launches the user interface responsible for the upgrade of a station.
 */

public class UpgradeStationUI implements Runnable{
    /** Controller responsible for station upgrade logic. */
    private final UpgradeStationController ctrl;
    private final User currentUser = CurrentUser.getInstance().getUser();
    private Station station;
    private String buildingType;
    private final Map selectedMap;
    private final AtomicReference<AtomicIntegerArray> timePeriod;
    private static final int COST_TELEGRAPH = 250,  COST_TELEPHONE = 270;
    private static final int COST_SCAFE = 340, COST_LCAFE = 430;
    private static final int COST_CUSTOMS = 570, COST_POFFICE = 730;
    private static final int COST_SHOTEL = 940, COST_LHOTEL = 1170;
    private static final int COST_SILO = 1450, COST_LSTORAGE = 1750;

    public UpgradeStationUI(Map selectedMap, AtomicReference<AtomicIntegerArray> timePeriod) { ctrl = new UpgradeStationController(); this.selectedMap = selectedMap; this.timePeriod = timePeriod; }

    /**
     * Returns the controller used to upgrade Stations.
     *
     * @return the UpgradeStationController instance
     */
    private UpgradeStationController getUpgradeStationController() { return ctrl; }

    /**
     * Runs the user interface section.
     */
    @Override
    public void run() {
        System.out.println("\n\n--- Upgrade Station ------------------------");
        requestData();

        if (station != null && buildingType != (null)) {
            submitData();
        }
    }

    /**
     * Collects all data related to upgrading a station.
     * Prints a success or failure message based on the result.
     */
    private void submitData() {
        Optional<Building> building = Optional.ofNullable(getUpgradeStationController().upgradeStation(station, buildingType));

        if (building.isPresent()) {
            System.out.println("\nBuilding successfully created!");
        } else {
            System.out.println("\nBuilding not created!");
        }
    }

    /**
     * Asks the user for station name and building type.
     */
    private void requestData() {
        station = requestStation();

        if (station != null){
            buildingType = requestBuildingType(station);
        }
    }

    /**
     * Asks the user to enter a valid station.
     * <p>
     * Repeats until an available station is found.
     * </p>
     *
     * @return the validated station name
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

    /**
     * Asks the user to enter a valid building type.
     * <p>
     * Repeats until a valid building type is provided.
     * </p>
     *
     * @return the validated building type
     */
    private String requestBuildingType(Station station) {
        Scanner sc = new Scanner(System.in);
        String buildingType;
        String[] validTypes = {"telegraph" , "telephone", "small cafe", "large cafe", "customs", "post office", "small hotel", "large hotel", "silo", "liquid storage"};

        int[] costs = {COST_TELEGRAPH, COST_TELEPHONE, COST_SCAFE, COST_LCAFE, COST_CUSTOMS, COST_POFFICE, COST_SHOTEL, COST_LHOTEL, COST_SILO, COST_LSTORAGE};

        boolean valid = false; int cost = 0;

        System.out.printf("Enter Available Building Type\n" +
                        "(Telegraph (%d) (1855-1875), Telephone (%d) (1876-Present), Small Cafe (%d), Large Cafe (%d), Customs (%d))\n" +
                        "(Post Office (%d), Small Hotel (%d), Large Hotel (%d), Silo (%d), Liquid Storage (%d)): ", COST_TELEGRAPH, COST_TELEPHONE,
                COST_SCAFE, COST_LCAFE, COST_CUSTOMS, COST_POFFICE, COST_SHOTEL, COST_LHOTEL, COST_SILO, COST_LSTORAGE);

        do {

            buildingType = sc.nextLine().trim().toLowerCase();
            int currentYear = timePeriod.get().get(0);

            for (int i = 0; i < validTypes.length; i++) {
                if (buildingType.equals(validTypes[i]) && !ctrl.verifyIfBuildingExists(station, buildingType)) {
                    valid = true;
                    cost = costs[i];

                    if (currentYear < 1855 || currentYear > 1875) {
                        if (buildingType.equals("telegraph")) {
                            System.out.println("Telegraph can only be built in the year 1855-1875.");
                            valid = false;
                        }
                        if (ctrl.verifyIfBuildingExists(station, "telegraph")) {
                            ctrl.getBuildingRepository().remove(station, "telegraph");
                        }

                    } else if (currentYear < 1876 || currentYear > 2026) {
                        if (buildingType.equals("telephone")) {
                            System.out.println("Telephone can only be built in the year 1876-Present.");
                            valid = false;
                        }
                        if (ctrl.verifyIfBuildingExists(station, "telephone")) {
                            ctrl.getBuildingRepository().remove(station, "telephone");
                        }
                    }
                    checkMutualExclusivity(ctrl.getBuildings(), buildingType);
                } else if (buildingType == null) {
                    return null;
                }
            }

            if (!valid) {
                System.out.print("Invalid input. Building type doesn't exist/isn't available in the current year or Player doesn't have enough budget. Please try again: ");
            } else if (ctrl.verifyIfBuildingExists(station, buildingType)) {
                System.out.print(buildingType + " already exists in station " + station.getName() + ". Please try again: ");
            }

        } while (!valid || ctrl.verifyIfBuildingExists(station, buildingType));

        currentUser.setBudget(new Budget(currentUser.getBudgetValue() - cost));

        return buildingType;
    }

    private String checkMutualExclusivity(List<Building> existingBuildings, String newType) {
        String newLower = newType.toLowerCase();

        for (Building building : existingBuildings) {
            String existing = building.getBuildingType().toLowerCase();
            if (!existing.equals(newLower) &&
                    ((existing.contains("hotel") && newLower.contains("hotel")) ||
                            (existing.contains("cafe") && newLower.contains("cafe")))) {
                return existing;
            }
        }
        return null;
    }
}