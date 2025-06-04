package controller;

import domain.*;
import repository.*;

import java.util.ArrayList;
import java.util.List;

public class AssignTrainController {

    private final RailwayLineRepository railwayLineRepo = Repositories.getInstance().getRailwayLineRepository();
    private final RouteRepository routeRepo = Repositories.getInstance().getRouteRepository();
    private final User currentUser = CurrentUser.getInstance().getUser();
    private final MapRepository mapRepo = Repositories.getInstance().getMapRepository();

    public void assignExistingTrainToRoute(Train train, List<PointOfRoute> route,
                                           Scenario scenario, Budget budget) {
        createRailwayLines(route,scenario);

        Route routeEntity = new Route(
                route,
                train.getUserEmail(),
                route.get(0).getStation().getMapName(),
                route.get(0).getStation().getScenarioName()
        );
        routeEntity.setTrain(train);
        routeRepo.save(routeEntity);

        System.out.println("Existing train assigned to route successfully!");
    }

    private void createRailwayLines(List<PointOfRoute> route, Scenario scenario) {
        for (int i = 0; i < route.size() - 1; i++) {
            Station from = route.get(i).getStation();
            Station to   = route.get(i + 1).getStation();

            if (!railwayLineRepo.connectionExists(from, to)) {
                double x1 = from.getX();
                double y1 = from.getY();
                double x2 = to.getX();
                double y2 = to.getY();

                int distance = (int) Math.round(Math.hypot(x2 - x1, y2 - y1));

                Map map = mapRepo.getMaps().stream().filter(m-> m.getMapName().equals(scenario.getSelectedMap())).findFirst().orElse(null);
                railwayLineRepo.save(new RailwayLine(from, to, RailwayLineType.SINGLE, distance,currentUser.getEmail(),scenario,map));
            }
        }
    }

}
