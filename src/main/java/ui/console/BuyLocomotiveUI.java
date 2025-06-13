package ui.console;

import controller.BuyLocomotiveController;
import domain.*;
import domain.Map;
import dto.LocomotiveDTO;

import java.util.*;

public class BuyLocomotiveUI implements Runnable {

    private final Scenario scenario;
    private final Map map;
    private final Budget budget;
    private final BuyLocomotiveController locomotiveController = new BuyLocomotiveController();

    public BuyLocomotiveUI(Scenario scenario, Map map, Budget budget) {
        this.scenario = scenario;
        this.map = map;
        this.budget = budget;
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
            buyLocomotive(modelName, scenario, map);
        }
    }

    public void buyLocomotive(String modelName, Scenario scenario, Map map) {
        // Pass the budget directly instead of looking it up
        if (locomotiveController.buyLocomotiveWithBudget(modelName, scenario, budget)) {
            System.out.println("Locomotive: " + modelName + " bought!");
        } else {
            System.out.println("Error buying Locomotive " + modelName + ": Not Enough Budget!");
        }
    }

    public void listLocomotives() {
        List<LocomotiveDTO> locomotiveList = locomotiveController.getLocomotives(scenario);
        for (LocomotiveDTO locomotive : locomotiveList) {
            System.out.printf("Name: %-20s | Price: %-5s | TopSpeed: %-5s%n",
                    locomotive.getModelName(),
                    locomotive.getPrice(),
                    locomotive.getTopSpeed());
        }
        System.out.println();
        System.out.println("Cancel");
    }

}
