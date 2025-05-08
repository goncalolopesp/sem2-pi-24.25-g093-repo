package ui.console;

import controller.CreateStationController;
import controller.UpgradeStationController;
import domain.Building;
import domain.CurrentUser;
import domain.Station;
import domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UpgradeStationUI implements Runnable{
    private final UpgradeStationController ctrl;
    private final User currentUser = CurrentUser.getInstance().getUser();
    private String stationName;
    private String buildingType;

    public UpgradeStationUI() { ctrl = new UpgradeStationController(); }

    private UpgradeStationController getUpgradeStationController() { return ctrl; }

    @Override
    public void run() {
        System.out.println("\n\n--- Upgrade Station ------------------------");
        requestData();

        if (stationName != null && buildingType != (null)) {
            submitData();
        }
    }

    private void submitData() {
        Optional<Building> building = Optional.ofNullable(getUpgradeStationController().upgradeStation(stationName, buildingType, currentUser.getEmail()));

        if (building.isPresent()) {
            System.out.println("\nBuilding successfully created!");
        } else {
            System.out.println("\nBuilding not created!");
        }
    }

    private void requestData() {
        stationName = requestStation();

        if (stationName != null){
            buildingType = requestBuildingType(stationName);
        }
    }

    private String requestStation() {
        Scanner sc = new Scanner(System.in);
        List<Station> availableStations = displayStationList();
        String stationName; boolean found = false;

        if (availableStations.isEmpty()) {
            System.out.println("No Available Stations found.");
            return null;
        }

        System.out.println(availableStations);
        System.out.print("Enter Station Name: ");

        do {
            stationName = sc.nextLine().trim();

            for (int i = 0; i < availableStations.size(); i++) {
                if (availableStations.get(i).getName().equalsIgnoreCase(stationName)) {
                    found = true;
                }
            }

            if (!found) {
                System.out.print("Invalid Station. Try again: ");
            }

        } while (!found);

        return stationName;
    }

    private String requestBuildingType(String stationName) {
        Scanner sc = new Scanner(System.in);
        String buildingType;
        String[] validTypes = {"telegraph" , "telephone", "small cafe", "large cafe", "customs", "post office", "small hotel", "large hotel", "silo", "liquid storage"};
        boolean valid = false;

        System.out.print("Enter Available Building Type (Telegraph, Telephone, Small Cafe, Large Cafe, Customs, Post Office, Small Hotel, Large Hotel, Silo, Liquid Storage): ");

        do {

            buildingType = sc.nextLine().trim().toLowerCase();

            for (int i = 0; i < validTypes.length; i++) {
                if (buildingType.equals(validTypes[i]) && !ctrl.verifyIfBuildingExists(stationName, buildingType)) {
                    valid = true;
                }
            }

            if (!valid) {
                System.out.print("Invalid input. Please try again: ");
            } else if (ctrl.verifyIfBuildingExists(stationName, buildingType)) {
                System.out.print(buildingType + " already exists in station " + stationName + ". Please try again: ");
            }

        } while (!valid && ctrl.verifyIfBuildingExists(stationName, buildingType));

        return buildingType;
    }

    private List<Station> displayStationList() {
        CreateStationController sctrl = new CreateStationController();
        List<Station> availableStations = new ArrayList<>();

        for (int i = 0; i < sctrl.getStations().size(); i++) {
            Station station = sctrl.getStations().get(i);
            if (station.getOwnerPlayer().equals(currentUser.getEmail())) {
                availableStations.add(station);
            }
        }

        return availableStations;
    }
}
