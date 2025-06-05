package controller;

import domain.*;
import repository.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller responsible for assigning trains to routes and managing railway line creation.
 * Provides functionality to assign existing trains to specific routes
 * and ensures that necessary railway lines exist for a valid connection between stations.
 */
public class AssignTrainController {

    private final RailwayLineRepository railwayLineRepo = Repositories.getInstance().getRailwayLineRepository();
    private final RouteRepository routeRepo = Repositories.getInstance().getRouteRepository();
    private final User currentUser = CurrentUser.getInstance().getUser();
    private final MapRepository mapRepo = Repositories.getInstance().getMapRepository();

    /**
     * Assigns an existing train to a specified route within a particular scenario and budget.
     * This method ensures that all necessary railway lines along the route are created before
     * assigning the train to the route. Once completed, the route is saved and associated
     * with the given train.
     *
     * @param train the existing train to be assigned to the route
     * @param route the list of points comprising the route to which the train will be assigned
     * @param scenario the current simulation scenario within which the operation takes place
     * @param budget the budget of the player
     */
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

    /**
     * Creates necessary railway lines between consecutive stations in the given route if they do not already exist.
     * The railway lines are saved in the associated repository and include details such as distance and type.
     *
     * @param route the list of points along the route, each containing a station
     * @param scenario the current simulation scenario, including contextual data such as the selected map
     */
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
