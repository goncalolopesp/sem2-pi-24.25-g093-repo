package ui.console;

import controller.AssignTrainController;
import domain.*;
import domain.Carriage.Type;
import repository.Repositories;
import repository.StationRepository;
import repository.TrainRepository;
import repository.CarriageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AssignTrainUI implements Runnable {

    private final Map selectedMap;
    private final Scanner scanner = new Scanner(System.in);
    private final AssignTrainController assignTrainController;

    public AssignTrainUI(Map selectedMap) {
        this.selectedMap = selectedMap;
        this.assignTrainController = new AssignTrainController();
    }

    @Override
    public void run() {
        var currentUser = CurrentUser.getInstance().getUser();
        String playerEmail = currentUser.getEmail();

        StationRepository stationRepo = Repositories.getInstance().getStationRepository();
        List<Station> stationsOnMap = stationRepo.getStations().stream()
                .filter(s -> s.getMapName().equals(selectedMap.getMapName()))
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

        // Select locomotive
        System.out.println("Select a locomotive to assign:");
        for (int i = 0; i < userLocos.size(); i++) {
            System.out.printf("[%d] %s (Top Speed: %d)%n", i, userLocos.get(i).getModelName(), userLocos.get(i).getTopSpeed());
        }
        int locoIndex;
        while (true) {
            System.out.print("Enter locomotive number: ");
            String line = scanner.nextLine();
            try {
                locoIndex = Integer.parseInt(line);
                if (locoIndex >= 0 && locoIndex < userLocos.size()) break;
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid selection. Try again.");
        }
        Locomotive chosenLoco = userLocos.get(locoIndex);

        // Select route: start, end, and optional intermediates
        Station start;
        Station end;
        List<Station> intermediates = new ArrayList<>();
        if (stationsOnMap.size() == 2) {
            start = stationsOnMap.get(0);
            end = stationsOnMap.get(1);
            System.out.printf("Start: %s, End: %s%n", start.getName(), end.getName());
        } else {
            // choose start
            System.out.println("Select start station:");
            for (int i = 0; i < stationsOnMap.size(); i++) {
                Station s = stationsOnMap.get(i);
                System.out.printf("[%d] %s at (x=%d, y=%d)%n", i, s.getName(), s.getX(), s.getY());
            }
            int startIndex;
            while (true) {
                System.out.print("Enter start station number: ");
                String line = scanner.nextLine();
                try {
                    startIndex = Integer.parseInt(line);
                    if (startIndex >= 0 && startIndex < stationsOnMap.size()) break;
                } catch (NumberFormatException ignored) {}
                System.out.println("Invalid selection. Try again.");
            }
            start = stationsOnMap.get(startIndex);

            // choose end
            List<Station> remaining = new ArrayList<>(stationsOnMap);
            remaining.remove(start);
            System.out.println("Select end station:");
            for (int i = 0; i < remaining.size(); i++) {
                Station s = remaining.get(i);
                System.out.printf("[%d] %s at (x=%d, y=%d)%n", i, s.getName(), s.getX(), s.getY());
            }
            int endIndex;
            while (true) {
                System.out.print("Enter end station number: ");
                String line = scanner.nextLine();
                try {
                    endIndex = Integer.parseInt(line);
                    if (endIndex >= 0 && endIndex < remaining.size()) break;
                } catch (NumberFormatException ignored) {}
                System.out.println("Invalid selection. Try again.");
            }
            end = remaining.get(endIndex);

            // optional intermediates
            remaining.remove(end);
            System.out.print("Add intermediate stations? (Y/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                while (!remaining.isEmpty()) {
                    System.out.println("Select intermediate station or -1 to finish:");
                    for (int i = 0; i < remaining.size(); i++) {
                        Station s = remaining.get(i);
                        System.out.printf("[%d] %s at (x=%d, y=%d)%n", i, s.getName(), s.getX(), s.getY());
                    }
                    System.out.print("Enter number: ");
                    String line = scanner.nextLine();
                    int idx;
                    try {
                        idx = Integer.parseInt(line);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid selection. Try again.");
                        continue;
                    }
                    if (idx == -1) break;
                    if (idx >= 0 && idx < remaining.size()) {
                        intermediates.add(remaining.get(idx));
                        remaining.remove(idx);
                    } else {
                        System.out.println("Invalid selection. Try again.");
                    }
                }
            }
        }

        System.out.println("Select cargo type to collect:");
        System.out.println("[1] People");
        System.out.println("[2] Mail");
        System.out.println("[3] Both");
        int cargoChoice;
        while (true) {
            System.out.print("Enter choice: ");
            String line = scanner.nextLine();
            try {
                cargoChoice = Integer.parseInt(line);
                if (cargoChoice >= 1 && cargoChoice <= 3) break;
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid selection. Try again.");
        }

        List<Carriage> carriages = new ArrayList<>();
        CarriageRepository carriageRepo = Repositories.getInstance().getCarriageRepository();

        List<Type> cargoTypes = new ArrayList<>();

        if (cargoChoice == 1 || cargoChoice == 3) {
            System.out.print("Enter number of People carriages: ");
            int num;
            while (true) {
                try {
                    num = Integer.parseInt(scanner.nextLine());
                    if (num > 0) break;
                } catch (NumberFormatException ignored) {}
                System.out.println("Invalid number. Must be > 0.");
            }
            for (int i = 0; i < num; i++) {
                Carriage c = new Carriage(Type.PEOPLE);
                carriageRepo.save(c);
                carriages.add(c);
            }
            cargoTypes.add(Type.PEOPLE);
        }
        if (cargoChoice == 2 || cargoChoice == 3) {
            System.out.print("Enter number of Mail carriages: ");
            int num;
            while (true) {
                try {
                    num = Integer.parseInt(scanner.nextLine());
                    if (num > 0) break;
                } catch (NumberFormatException ignored) {}
                System.out.println("Invalid number. Must be > 0.");
            }
            for (int i = 0; i < num; i++) {
                Carriage c = new Carriage(Type.MAIL);
                carriageRepo.save(c);
                carriages.add(c);
            }
            cargoTypes.add(Type.MAIL);
        }

        User user = CurrentUser.getInstance().getUser();

        Train train = new Train(chosenLoco, carriages, user.getEmail());
        TrainRepository trainRepo = Repositories.getInstance().getTrainRepository();
        trainRepo.save(train);

        // Create the complete route (start -> intermediates -> end)
        List<Station> completeRoute = new ArrayList<>();
        completeRoute.add(start);
        completeRoute.addAll(intermediates);
        completeRoute.add(end);

        // Send the train, route, and cargo types to the controller
        assignTrainController.assignTrainToRoute(train, completeRoute, cargoTypes);

        System.out.println("Success: Train created with " + carriages.size() + " carriages and assigned to route with " + completeRoute.size() + " stations.");
    }
}