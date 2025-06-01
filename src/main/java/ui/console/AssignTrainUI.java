package ui.console;

import controller.AssignTrainController;
import domain.*;
import domain.Carriage.Type;
import repository.Repositories;
import repository.StationRepository;
import domain.Map;

import java.util.*;
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

        List<Station> stationsOnMap = getPlayerStationsOnMap(playerEmail);
        if (stationsOnMap.size() < 2) {
            System.out.println("Error: You need at least 2 stations on this map to assign a train.");
            return;
        }

        List<Locomotive> userLocos = currentUser.getLocomotives();
        if (userLocos.isEmpty()) {
            System.out.println("Error: You need at least 1 locomotive to assign a train.");
            return;
        }

        Locomotive chosenLoco = selectLocomotive(userLocos);
        List<PointOfRoute> route = createRoute(stationsOnMap);
        java.util.Map<Type, Integer> carriageCounts = getCarriageCounts();

        assignTrainController.createTrainAndAssignToRoute(
                chosenLoco, route, carriageCounts, selectedScenario, budget, playerEmail);
    }

    private List<Station> getPlayerStationsOnMap(String playerEmail) {
        StationRepository stationRepo = Repositories.getInstance().getStationRepository();
        return stationRepo.getStations().stream()
                .filter(s -> s.getMapName().equals(selectedMap.getMapName()))
                .filter(s -> s.getScenarioName().equals(selectedScenario.getScenarioName()))
                .filter(s -> s.getOwnerPlayer().equals(playerEmail))
                .collect(Collectors.toList());
    }

    private Locomotive selectLocomotive(List<Locomotive> userLocos) {
        System.out.println("Select a locomotive to assign:");
        for (int i = 0; i < userLocos.size(); i++) {
            System.out.printf("[%d] %s (Top Speed: %d)%n", i, userLocos.get(i).getModelName(), userLocos.get(i).getTopSpeed());
        }
        int index = getValidIndex("Enter locomotive number: ", userLocos.size());
        return userLocos.get(index);
    }

    private java.util.Map<Type, Integer> getCarriageCounts() {
        java.util.Map<Type, Integer> counts = new HashMap<>();

        System.out.print("Enter the number of People carriages: ");
        counts.put(Type.PEOPLE, getValidPositiveInt());

        System.out.print("Enter the number of Mail carriages: ");
        counts.put(Type.MAIL, getValidPositiveInt());

        System.out.print("Enter the number of Coal carriages: ");
        counts.put(Type.COAL, getValidPositiveInt());

        System.out.print("Enter the number of Iron carriages: ");
        counts.put(Type.IRON, getValidPositiveInt());

        System.out.print("Enter the number of Steel carriages: ");
        counts.put(Type.STEEL, getValidPositiveInt());

        return counts;
    }

    private List<PointOfRoute> createRoute(List<Station> stationsOnMap) {
        List<PointOfRoute> route = new ArrayList<>();
        List<Station> availableStations = new ArrayList<>(stationsOnMap);

        System.out.println("Creating route - select stations and cargo types for each point:");

        while (!availableStations.isEmpty()) {
            displayAvailableStations(availableStations);

            if (route.size() >= 2) {
                System.out.println("[-1] Finish route");
            }

            int stationIndex = getStationSelection(availableStations, route.size() >= 2);
            if (stationIndex == -1) break;

            Station selectedStation = availableStations.remove(stationIndex);
            List<Type> cargoTypes = selectCargoTypes();
            route.add(new PointOfRoute(selectedStation, cargoTypes));

            if (route.size() >= 2 && availableStations.isEmpty()) break;
        }

        ensureMinimumRouteSize(route, availableStations);
        return route;
    }

    private void displayAvailableStations(List<Station> stations) {
        System.out.println("\nAvailable stations:");
        for (int i = 0; i < stations.size(); i++) {
            Station s = stations.get(i);
            System.out.printf("[%d] %s at (x=%d, y=%d)%n", i, s.getName(), s.getX(), s.getY());
        }
    }

    private int getStationSelection(List<Station> availableStations, boolean canFinish) {
        String prompt = canFinish ? "Select station (-1 to finish): " : "Select station: ";

        while (true) {
            System.out.print(prompt);
            try {
                int index = Integer.parseInt(scanner.nextLine());
                if ((canFinish && index == -1) || (index >= 0 && index < availableStations.size())) {
                    return index;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid selection. Try again.");
        }
    }

    private List<Type> selectCargoTypes() {
        while (true) {
            try {
                CargoTypes.printPickupCargoOptions();
                System.out.print("Enter cargo choices: ");
                String input = scanner.nextLine();

                List<CargoTypes> selectedCargoTypes = CargoTypes.fromPickupMenuOptions(input);
                return convertToCarriageTypes(selectedCargoTypes);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid cargo selection. Try again.");
            }
        }
    }

    private List<Type> convertToCarriageTypes(List<CargoTypes> cargoTypes) {
        List<Type> result = new ArrayList<>();
        for (CargoTypes cargoType : cargoTypes) {
            switch (cargoType) {
                case PEOPLE:
                    result.add(Type.PEOPLE);
                    break;
                case MAIL:
                    result.add(Type.MAIL);
                    break;
                case COAL:
                    result.add(Type.COAL);
                    break;
                case IRON:
                    result.add(Type.IRON);
                    break;
            }
        }
        return result;
    }

    private void ensureMinimumRouteSize(List<PointOfRoute> route, List<Station> availableStations) {
        if (route.size() < 2) {
            System.out.println("Route must have at least 2 points. Adding remaining stations with default cargo types.");
            while (route.size() < 2 && !availableStations.isEmpty()) {
                Station s = availableStations.remove(0);
                List<Type> defaultCargo = List.of(Type.PEOPLE, Type.MAIL);
                route.add(new PointOfRoute(s, defaultCargo));
            }
        }
    }

    private int getValidIndex(String prompt, int maxSize) {
        while (true) {
            System.out.print(prompt);
            try {
                int index = Integer.parseInt(scanner.nextLine());
                if (index >= 0 && index < maxSize) return index;
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid selection. Try again.");
        }
    }

    private int getValidPositiveInt() {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value >= 0) return value; // Allow 0 carriages
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid number. Must be >= 0.");
        }
    }
}