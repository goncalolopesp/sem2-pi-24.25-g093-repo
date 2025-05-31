package ui.console;

import controller.AssignTrainController;
import domain.*;
import domain.Carriage.Type;
import repository.Repositories;
import repository.StationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AssignTrainUI implements Runnable {

    private final Map selectedMap;
    private final Scenario selectedScenario;
    private final Budget budget;
    private final Scanner scanner = new Scanner(System.in);
    private final AssignTrainController assignTrainController;

    public AssignTrainUI(Map selectedMap, Scenario selectedScenario, Budget budget) {
        this.selectedMap = selectedMap;
        this.selectedScenario = selectedScenario;
        this.budget = budget;
        this.assignTrainController = new AssignTrainController();
    }

    @Override
    public void run() {
        var currentUser = CurrentUser.getInstance().getUser();
        String playerEmail = currentUser.getEmail();

        StationRepository stationRepo = Repositories.getInstance().getStationRepository();
        List<Station> stationsOnMap = stationRepo.getStations().stream()
                .filter(s -> s.getMapName().equals(selectedMap.getMapName()))
                .filter(s -> s.getScenarioName().equals(selectedScenario.getScenarioName()))
                .filter(s -> s.getOwnerPlayer().equals(playerEmail))
                .collect(Collectors.toList());

        if (stationsOnMap.size() < 2) {
            System.out.println("Error: You need at least 2 stations on this map to assign a train.");
            return;
        }

        List<Locomotive> userLocos = currentUser.getLocomotives();
        if (userLocos.isEmpty()) {
            System.out.println("Error: You need at least 1 locomotive to assign a train.");
            return;
        }

        System.out.println("Select a locomotive to assign:");
        for (int i = 0; i < userLocos.size(); i++) {
            System.out.printf("[%d] %s (Top Speed: %d)%n", i, userLocos.get(i).getModelName(), userLocos.get(i).getTopSpeed());
        }
        Locomotive chosenLoco = userLocos.get(getValidIndex("Enter locomotive number: ", userLocos.size()));

        List<PointOfRoute> route = createRoute(stationsOnMap);

        System.out.print("Enter the number of People carriages: ");
        int peopleCarriageCount = getValidPositiveInt();
        System.out.print("Enter the number of Mail carriages: ");
        int mailCarriageCount = getValidPositiveInt();

        assignTrainController.createTrainAndAssignToRoute(
                chosenLoco, route, peopleCarriageCount, mailCarriageCount,
                selectedScenario, budget, playerEmail);
    }

    private List<PointOfRoute> createRoute(List<Station> stationsOnMap) {
        List<PointOfRoute> route = new ArrayList<>();
        List<Station> availableStations = new ArrayList<>(stationsOnMap);

        System.out.println("Creating route - select stations and cargo types for each point:");

        while (availableStations.size() > 0) {
            System.out.println("\nAvailable stations:");
            for (int i = 0; i < availableStations.size(); i++) {
                Station s = availableStations.get(i);
                System.out.printf("[%d] %s at (x=%d, y=%d)%n", i, s.getName(), s.getX(), s.getY());
            }

            if (route.size() >= 2) {
                System.out.println("[-1] Finish route");
            }

            String promptMessage = route.size() >= 2 ?
                    "Select station (-1 to finish): " :
                    "Select station: ";

            int stationIndex;
            if (route.size() >= 2) {
                stationIndex = getValidIndex(promptMessage, availableStations.size(), true);
            } else {
                stationIndex = getValidIndex(promptMessage, availableStations.size(), false);
            }

            if (stationIndex == -1) {
                break;
            }

            Station selectedStation = availableStations.get(stationIndex);
            availableStations.remove(stationIndex);

            CargoTypes.printCargoOptions();
            int totalCargoOptions = CargoTypes.values().length + 1; // +1 for "Both" option
            int cargoChoice = getValidMenuOption("Enter cargo choice (1–" + totalCargoOptions + "): ", totalCargoOptions);

            List<CargoTypes> selectedCargoTypes = CargoTypes.fromMenuOption(cargoChoice);
            List<Type> cargoTypes = new ArrayList<>();

            if (selectedCargoTypes.contains(CargoTypes.PEOPLE)) cargoTypes.add(Type.PEOPLE);
            if (selectedCargoTypes.contains(CargoTypes.MAIL)) cargoTypes.add(Type.MAIL);

            route.add(new PointOfRoute(selectedStation, cargoTypes));

            if (route.size() >= 2 && availableStations.isEmpty()) break;
        }

        if (route.size() < 2) {
            System.out.println("Route must have at least 2 points. Adding remaining stations with default cargo types.");
            while (route.size() < 2 && !availableStations.isEmpty()) {
                Station s = availableStations.remove(0);
                List<Type> defaultCargo = List.of(Type.PEOPLE, Type.MAIL);
                route.add(new PointOfRoute(s, defaultCargo));
            }
        }

        return route;
    }

    private int getValidIndex(String prompt, int maxSize) {
        return getValidIndex(prompt, maxSize, false);
    }

    private int getValidIndex(String prompt, int maxSize, boolean allowNegativeOne) {
        while (true) {
            System.out.print(prompt);
            try {
                int index = Integer.parseInt(scanner.nextLine());
                if ((allowNegativeOne && index == -1) || (index >= 0 && index < maxSize)) return index;
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid selection. Try again.");
        }
    }

    private int getValidMenuOption(String prompt, int maxOption) {
        while (true) {
            System.out.print(prompt);
            try {
                int option = Integer.parseInt(scanner.nextLine());
                if (option >= 1 && option <= maxOption) return option;
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid selection. Try again.");
        }
    }

    private int getValidPositiveInt() {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value > 0) return value;
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid number. Must be > 0.");
        }
    }
}