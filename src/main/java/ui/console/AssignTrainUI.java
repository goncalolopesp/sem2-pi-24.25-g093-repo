package ui.console;

import controller.SimulationController;
import domain.CurrentUser;
import domain.Locomotive;
import domain.Map;
import domain.Station;
import repository.Repositories;
import repository.StationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * UI for assigning a locomotive to a route.
 * Validates that the player has at least two stations on the map
 * and at least one locomotive before proceeding.
 * Guides the user through selecting locomotive, start, end, and optional intermediate stations.
 */
public class AssignTrainUI implements Runnable {

    private final Map selectedMap;
    private final Scanner scanner = new Scanner(System.in);

    public AssignTrainUI(Map selectedMap) {
        this.selectedMap = selectedMap;
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

        System.out.println("Select a locomotive to assign:");
        for (int i = 0; i < userLocos.size(); i++) {
            System.out.printf("[%d] %s (Top Speed: %d)%n", i, userLocos.get(i).getModelName(), userLocos.get(i).getTopSpeed());
        }
        int locoIndex = -1;
        boolean locoValid = false;
        while (!locoValid) {
            System.out.print("Enter locomotive number: ");
            String line = scanner.nextLine();
            try {
                locoIndex = Integer.parseInt(line);
                locoValid = (locoIndex >= 0 && locoIndex < userLocos.size());
            } catch (NumberFormatException e) {
                locoValid = false;
            }
            if (!locoValid) {
                System.out.println("Invalid selection. Try again.");
            }
        }
        Locomotive chosenLoco = userLocos.get(locoIndex);

        Station start;
        Station end;
        if (stationsOnMap.size() == 2) {
            start = stationsOnMap.get(0);
            end = stationsOnMap.get(1);
            System.out.printf("Start: %s, End: %s%n", start.getName(), end.getName());
            return;
        } else {
            System.out.println("Select start station:");
            for (int i = 0; i < stationsOnMap.size(); i++) {
                Station s = stationsOnMap.get(i);
                System.out.printf("[%d] %s at (x=%d, y=%d)%n", i, s.getName(), s.getX(), s.getY());
            }
            int startIndex = -1;
            boolean startValid = false;
            while (!startValid) {
                System.out.print("Enter start station number: ");
                String line = scanner.nextLine();
                try {
                    startIndex = Integer.parseInt(line);
                    startValid = (startIndex >= 0 && startIndex < stationsOnMap.size());
                } catch (NumberFormatException e) {
                    startValid = false;
                }
                if (!startValid) {
                    System.out.println("Invalid selection. Try again.");
                }
            }
            start = stationsOnMap.get(startIndex);

            List<Station> remaining = new ArrayList<>(stationsOnMap);
            remaining.remove(startIndex);
            System.out.println("Select end station:");
            for (int i = 0; i < remaining.size(); i++) {
                Station s = remaining.get(i);
                System.out.printf("[%d] %s at (x=%d, y=%d)%n", i, s.getName(), s.getX(), s.getY());
            }
            int endIndex = -1;
            boolean endValid = false;
            while (!endValid) {
                System.out.print("Enter end station number: ");
                String line = scanner.nextLine();
                try {
                    endIndex = Integer.parseInt(line);
                    endValid = (endIndex >= 0 && endIndex < remaining.size());
                } catch (NumberFormatException e) {
                    endValid = false;
                }
                if (!endValid) {
                    System.out.println("Invalid selection. Try again.");
                }
            }
            end = remaining.get(endIndex);

            remaining.remove(endIndex);
            List<Station> intermediates = new ArrayList<>();
            System.out.print("Add intermediate stations? (Y/N): ");
            String reply = scanner.nextLine().trim();
            if (reply.equalsIgnoreCase("y")) {
                boolean adding = true;
                while (adding && !remaining.isEmpty()) {
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
                    if (idx == -1) {
                        adding = false;
                    } else if (idx >= 0 && idx < remaining.size()) {
                        intermediates.add(remaining.get(idx));
                        remaining.remove(idx);
                    } else {
                        System.out.println("Invalid selection. Try again.");
                    }
                }
            }
            System.out.println("Sucesso");
            return;
        }
    }
}
