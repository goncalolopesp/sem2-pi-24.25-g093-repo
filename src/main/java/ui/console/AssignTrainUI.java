package ui.console;

import controller.AssignTrainController;
import domain.*;
import domain.Carriage.Type;
import repository.Repositories;
import repository.StationRepository;
import domain.Map;
import dto.TrainDTO;
import dto.DTOMapperTrain;
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

        System.out.println(playerEmail);
        List<Station> stationsOnMap = getPlayerStationsOnMap(playerEmail);
        if (stationsOnMap.size() < 2) {
            System.out.println("Error: You need at least 2 stations on this map to assign a train.");
            return;
        }

        List<Train> userTrains = getPlayerTrains(playerEmail);
        if (userTrains.isEmpty()) {
            System.out.println("Error: You need at least 1 train to assign.");
            return;
        }

        Train chosenTrain = selectTrain(userTrains);
        List<PointOfRoute> route = createRoute(stationsOnMap);

        assignTrainController.assignExistingTrainToRoute(
                chosenTrain, route, selectedScenario, budget);
    }

    private List<Train> getPlayerTrains(String playerEmail) {
        return Repositories.getInstance().getTrainRepository().getTrains().stream()
                .filter(train -> train.getUserEmail().equals(playerEmail))
                .collect(Collectors.toList());
    }

    private Train selectTrain(List<Train> userTrains) {
        System.out.println("Select a train to assign:");

        for (int i = 0; i < userTrains.size(); i++) {
            Train train = userTrains.get(i);
            TrainDTO dto = DTOMapperTrain.toDTO(train);

            System.out.printf("[%d] Locomotive: %s, Total Carriages: %d%n",
                    i, dto.getLocomotiveModel(), dto.getTotalCarriages());

            List<String> types = dto.getCarriageTypes();
            List<Integer> counts = dto.getCarriageCounts();

            for (int j = 0; j < types.size(); j++) {
                int count = counts.get(j);
                if (count > 0) {
                    System.out.println("     - " + types.get(j) + ": " + count);
                }
            }
        }

        int index = getValidIndex("Enter train number: ", userTrains.size());
        return userTrains.get(index);
    }





    private List<Station> getPlayerStationsOnMap(String playerEmail) {
        StationRepository stationRepo = Repositories.getInstance().getStationRepository();
        return stationRepo.getStations().stream()
                .filter(s -> s.getMapName().equals(selectedMap.getMapName()))
                .filter(s -> s.getScenarioName().equals(selectedScenario.getScenarioName()))
                .filter(s -> s.getOwnerPlayer().equals(playerEmail))
                .collect(Collectors.toList());
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
            CargoMode cargoMode = selectCargoMode();
            route.add(new PointOfRoute(selectedStation, cargoTypes, cargoMode));

            if (route.size() >= 2 && availableStations.isEmpty()) break;
        }

        ensureMinimumRouteSize(route, availableStations);
        return route;
    }

    private CargoMode selectCargoMode() {
        System.out.println("Select cargo mode:");
        System.out.println("[0] FULL - Wait until all selected carriages are full");
        System.out.println("[1] HALF - Wait until half of selected carriages are full");
        System.out.println("[2] AVAILABLE - Take all available cargo and move on");

        while (true) {
            System.out.print("Enter mode (0-2): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 0: return CargoMode.FULL;
                    case 1: return CargoMode.HALF;
                    case 2: return CargoMode.AVAILABLE;
                    default: System.out.println("Invalid choice. Please enter 0, 1, or 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
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
                route.add(new PointOfRoute(s, defaultCargo, CargoMode.AVAILABLE));
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
}