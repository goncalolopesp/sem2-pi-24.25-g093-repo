package ui.console;

import controller.CreateScenarioController;
import controller.DisplayMapController;
import domain.Scenario;

import java.util.Optional;
import java.util.Scanner;

public class CreateScenarioUI implements Runnable {
    private final CreateScenarioController ctrl;
    private String scenarioName;
    private String selectedMap;

    public CreateScenarioUI() { ctrl = new CreateScenarioController(); }
    private CreateScenarioController getScenarioController() { return ctrl; }

    @Override
    public void run() {
        System.out.println("\n\n--- Create Scenario ------------------------");
        requestData();
        submitData();
    }

    private void submitData() {
        Optional<Scenario> scenario = Optional.ofNullable(getScenarioController().createScenario(scenarioName, selectedMap));

        if (scenario.isPresent()) {
            System.out.println("\nScenario successfully created!");
        } else {
            System.out.println("\nScenario not created!");
        }
    }

    private void requestData() {
        scenarioName = requestScenarioName();
        selectedMap = requestSelectedMap();
    }

    private String requestScenarioName() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Scenario Name: ");
        return sc.nextLine().trim();
    }

    private String requestSelectedMap() {
        Scanner sc = new Scanner(System.in);
        DisplayMapController mctrl = new DisplayMapController();

        System.out.print("Select Map: ");

        do {
            selectedMap = sc.nextLine();
            if (!mctrl.verifyIfMapExists(selectedMap)) {
                System.out.print("\nMap doesn't exist! Try again: ");
            }
        } while (!mctrl.verifyIfMapExists(selectedMap));

        return selectedMap.trim();
    }
}
