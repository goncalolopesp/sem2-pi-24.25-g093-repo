package controller;

import domain.*;
import repository.Repositories;
import repository.TrainRepository;
import repository.CarriageRepository;
import repository.RailwayLineRepository;
import repository.RouteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssignTrainController {

    private final RailwayLineRepository railwayLineRepo = Repositories.getInstance().getRailwayLineRepository();
    private final RouteRepository routeRepo = Repositories.getInstance().getRouteRepository();

    public void createTrainAndAssignToRoute(Locomotive locomotive, List<PointOfRoute> route,
                                            Map<Carriage.Type, Integer> carriageCounts,
                                            Scenario scenario, Budget budget, String playerEmail) {

        List<Carriage> carriages = createCarriages(carriageCounts);
        Train train = new Train(locomotive, carriages, playerEmail);
        TrainRepository trainRepo = Repositories.getInstance().getTrainRepository();
        trainRepo.save(train);

        createRailwayLines(route);

        Route routeEntity = new Route(route, playerEmail,
                route.get(0).getStation().getMapName(),
                route.get(0).getStation().getScenarioName());
        routeEntity.setTrain(train);
        routeRepo.save(routeEntity);

        System.out.println("Train assigned to route successfully!");
    }

    private List<Carriage> createCarriages(Map<Carriage.Type, Integer> carriageCounts) {
        List<Carriage> carriages = new ArrayList<>();
        CarriageRepository carriageRepo = Repositories.getInstance().getCarriageRepository();

        for (Map.Entry<Carriage.Type, Integer> entry : carriageCounts.entrySet()) {
            Carriage.Type type = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                Carriage carriage = new Carriage(type);
                carriageRepo.save(carriage);
                carriages.add(carriage);
            }
        }

        return carriages;
    }

    private void createRailwayLines(List<PointOfRoute> route) {
        for (int i = 0; i < route.size() - 1; i++) {
            Station from = route.get(i).getStation();
            Station to = route.get(i + 1).getStation();

            if (!railwayLineRepo.connectionExists(from, to)) {
                railwayLineRepo.save(new RailwayLine(from, to, RailwayLineType.SINGLE));
            }
        }
    }
}