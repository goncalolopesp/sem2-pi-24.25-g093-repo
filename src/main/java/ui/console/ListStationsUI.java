package ui.console;

import controller.*;
import domain.*;
import dto.StationDTO;
import dto.StationDetailDTO;
import dto.BuildingDTO;

import java.util.List;
import java.util.Scanner;

public class ListStationsUI implements Runnable {

    ListStationController listStationController = new ListStationController();

    public void run() {
        listStations();
    }

    public void listStations() {
        System.out.println("=========Stations List=========");
        CurrentUser session = CurrentUser.getInstance();
        User user = session.getUser();
        Scanner scanner = new Scanner(System.in);

        List<StationDTO> stations = listStationController.getStations();

        for (StationDTO st : stations){
            // Note: StationDTO doesn't have owner information, so we need to get it differently
            // For console UI, we might need to modify this approach or get station details
            System.out.println("---" + st.getName() + "---");
            System.out.println("Center:" + st.getX() + "," + st.getY());
        }

        System.out.println("==============================");
        System.out.printf("Chosen Station:");
        String stationName = scanner.nextLine();

        // Find the selected station
        StationDTO selectedStation = null;
        for (StationDTO st : stations) {
            if (st.getName().equalsIgnoreCase(stationName)) {
                selectedStation = st;
                break;
            }
        }

        if (selectedStation != null) {
            int count = 0;
            for (StationDTO st : stations) {
                if (st.getName().equalsIgnoreCase(stationName)) {
                    count++;
                }
            }

            if (count > 1) {
                System.out.printf("Multiple stations named '%s'.\nEnter center X coordinate: ", stationName);
                int x = scanner.nextInt();
                System.out.printf("%nEnter center Y coordinate: ");
                int y = scanner.nextInt();

                // Find station with specific coordinates
                for (StationDTO st : stations) {
                    if (x == st.getX() && y == st.getY() && stationName.equalsIgnoreCase(st.getName())) {
                        selectedStation = st;
                        break;
                    }
                }
            }

            if (selectedStation != null) {
                StationDetailDTO stationDetail = listStationController.getStationDetails(selectedStation);
                if (stationDetail != null) {
                    displayStationDetails(stationDetail);
                } else {
                    System.out.println("Station details not found.");
                }
            }
        } else {
            System.out.println("Station not found.");
        }
    }

    private void displayStationDetails(StationDetailDTO stationDetail) {
        System.out.println("Cargo: " + stationDetail.getCapacityOwned() + "/" + stationDetail.getCapacity());

        if (!stationDetail.getBuildings().isEmpty()) {
            System.out.println("\nBuildings: ");
            for (BuildingDTO building : stationDetail.getBuildings()) {
                System.out.println("*" + building.getBuildingType() + " - " + building.toString());
            }
        } else {
            System.out.println("This station has no buildings");
        }
    }

}
