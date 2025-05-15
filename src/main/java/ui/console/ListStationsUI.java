package ui.console;

import controller.*;
import domain.*;

import java.util.List;

public class ListStationsUI implements Runnable {

    ListStationController listStationController = new ListStationController();

    public void run() {
        listStations();
    }

    public void listStations() {
        System.out.println("=========Stations List=========");
        CurrentUser session = CurrentUser.getInstance();
        User user = session.getUser();
        for (Station st : listStationController.getStations()){
            if (user.getEmail().equals(st.getOwnerPlayer())){
                System.out.println("---" + st.getName() + "---\n");

                List<Building> ls = listStationController.getBuildingByStation(st);

                System.out.println("Cargo: " + st.getCapacityOwned() + "/" + st.getCapacity());
                System.out.println("\nBuildings: ");
                for (Building building : ls){
                    System.out.println("*" + building.getBuildingType());
                }

                System.out.println("\n--------------------");
            }
        }
        System.out.println("==============================");
    }

}
