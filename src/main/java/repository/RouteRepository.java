package repository;

import domain.Route;
import java.util.ArrayList;
import java.util.List;

public class RouteRepository {
    private List<Route> routes = new ArrayList<>();

    public void save(Route route) {
        routes.add(route);
    }

    public List<Route> getRoutes() {
        return new ArrayList<>(routes);
    }

    public List<Route> getRoutesByPlayer(String playerEmail) {
        return routes.stream()
                .filter(r -> r.getPlayerEmail().equals(playerEmail))
                .collect(java.util.stream.Collectors.toList());
    }
}