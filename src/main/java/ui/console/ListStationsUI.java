package ui.console;

import controller.*;
import domain.*;

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
        for (Station st : listStationController.getStations()){
            if (user.getEmail().equals(st.getOwnerPlayer())){
                System.out.println("---" + st.getName() + "---");
                System.out.println("Center:" + st.getX() + "," + st.getY());
                System.out.println("Type:" + st.getType());
            }
        }
        System.out.println("==============================");
        System.out.printf("Chosen Station:");
        String stationName = scanner.nextLine();
        int count = 0;
        for (Station st : listStationController.getStations()){
            if (user.getEmail().equals(st.getOwnerPlayer())) {
                count++;
            }
        }
        if (count > 1){
            System.out.printf("Multiple stations named '%s'.\nEnter center X coordinate: ", stationName);
            int x = scanner.nextInt();
            System.out.printf("%nEnter center Y coordinate: ");
            int y = scanner.nextInt();
            for (Station st : listStationController.getStations()){
                if (user.getEmail().equals(st.getOwnerPlayer())) {
                    if (x == st.getX() && y == st.getY()){
                        if (stationName.equalsIgnoreCase(st.getName())) {
                            List<Building> ls = listStationController.getBuildingByStation(st);

                            if (!ls.isEmpty()) {
                                System.out.println("Cargo: " + st.getCapacityOwned() + "/" + st.getCapacity());
                                System.out.println("\nBuildings: ");
                                for (Building building : ls) {
                                    System.out.println("*" + building.getBuildingType());
                                }
                            } else {
                                System.out.println("This station has no buildings");
                            }
                        }
                    }
                }
            }
        }
    }

}
