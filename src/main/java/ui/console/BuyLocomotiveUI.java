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
        System.out.print("Chosen Locomotive: ");
        String modelName = scanner.nextLine();
        if (buyLocomotive(modelName)){
            getPlayerLocomotives();
        }

    }

    public void getPlayerLocomotives() {
        for (Locomotive locomotive : locomotiveController.getPlayerLocomotives()){
            System.out.print(locomotive.getModelName() + "  |  ");
        }
        System.out.println();
    }

    public boolean buyLocomotive(String modelName){
        if (locomotiveController.buyLocomotive(modelName)){

            System.out.println("Locomotive: " + modelName + " bought!");
            return true;
        }else{

            System.out.println("Error buying Locomotive " + modelName + ": Not Enough Budget!");
            return false;
        }
    }

    public void listLocomotives(){
        List<Locomotive> locomotiveList = locomotiveController.getLocomotives(scenario);
        for (Locomotive locomotive : locomotiveList) {
            System.out.print(locomotive.getModelName() + " - " + locomotive.getPrice() + "  |  ");
        }
    }

}
