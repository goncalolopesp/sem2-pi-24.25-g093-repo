package ui.console;

import domain.CurrentUser;
import domain.Locomotive;
import domain.User;

public class ListLocomotiveUI implements Runnable {
    @Override public void run() {
        System.out.println("==========Locomotives List==========");
        CurrentUser session = CurrentUser.getInstance();
        User user = session.getUser();
        for (Locomotive locomotive : user.getLocomotives()) {
            System.out.printf("* Name: %-30s|Type: %-15s|topSpeed: %-15s|maintenanceCost: %-15s|fuelCost: %-15s%n", locomotive.getModelName(), locomotive.getType(), locomotive.getTopSpeed(), locomotive.getMaintenanceCost(), locomotive.getFuelCost());
        }
        System.out.println("==============================");
    }

}
