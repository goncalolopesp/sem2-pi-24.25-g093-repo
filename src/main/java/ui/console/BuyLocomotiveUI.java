package ui.console;

import controller.BuyLocomotiveController;
import domain.*;

import java.util.*;

public class BuyLocomotiveUI implements Runnable {

    private Scenario scenario;
    private BuyLocomotiveController locomotiveController = new BuyLocomotiveController();

    public BuyLocomotiveUI(Scenario scenario) {
        this.scenario = scenario;
    }

    @Override public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("==========Available locomotives==========");
        listLocomotives();
        System.out.println();
        String modelName;
        do{
            System.out.print("Chosen Locomotive: ");
            modelName = scanner.nextLine().trim();
            if (!(modelName.equalsIgnoreCase("cancel"))){
                if (!locomotiveController.canBuyLocomotive(modelName, scenario)) System.out.println("Error buying Locomotive " + modelName + ": Locomotive not available!");
            }
        }while(!locomotiveController.canBuyLocomotive(modelName, scenario) && !modelName.equalsIgnoreCase("cancel"));
        if (!(modelName.equalsIgnoreCase("cancel") || modelName.equalsIgnoreCase(""))) {
            buyLocomotive(modelName);
        }
    }

    public void getPlayerLocomotives() {
        for (Locomotive locomotive : locomotiveController.getPlayerLocomotives()){
            System.out.print(locomotive.getModelName() + "  |  ");
        }
        System.out.println();
    }

    public boolean buyLocomotive(String modelName){
        if (locomotiveController.buyLocomotive(modelName, scenario)){

            System.out.println("Locomotive: " + modelName + " bought!");
            return true;

        }else{
            System.out.println("Error buying Locomotive " + modelName + ": Not Enough Budget!");
            return false;

        }
    }

    public void listLocomotives() {
        List<Locomotive> locomotiveList = locomotiveController.getLocomotives(scenario);
        for (Locomotive locomotive : locomotiveList) {
            System.out.printf("Name: %-20s | Price: %-5s | TopSpeed: %-5s | Maintenance Cost per year: %-5s | Fuel Cost per year: %-5s%n", locomotive.getModelName(), locomotive.getPrice(), locomotive.getTopSpeed(), locomotive.getMaintenanceCost(), locomotive.getFuelCost());
        }
        System.out.println();
        System.out.println("Cancel");
    }

}
