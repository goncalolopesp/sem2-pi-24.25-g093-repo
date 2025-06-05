package repository;

import domain.Route;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing and storing {@code Route} objects.
 * Provides methods to persist and retrieve routes, as well as querying routes by associated player email.
 */
public class RouteRepository {
    private List<Route> routes = new ArrayList<>();

    /**
     * Saves the specified {@code Route} object to the repository.
     *
     * @param route the {@code Route} object containing details such as ID, points, player email, map name,
     **/
    public void save(Route route) {
        routes.add(route);
    }

    /**
     * Retrieves a list of all routes currently stored in the repository.
     *
     * @return a list of {@code Route} objects representing the stored routes
     */
    public List<Route> getRoutes() {
        return new ArrayList<>(routes);
    }
}