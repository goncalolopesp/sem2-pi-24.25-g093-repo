package controller;

import domain.CurrentUser;
import domain.Map;
import domain.Station;
import domain.Station.Orientation;
import domain.User;
import domain.City;
import repository.Repositories;
import repository.StationRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller responsible for creating and retrieving stations.
 * <p>
 * Delegates persistence to StationRepository
 * </p>
 */
public class CreateStationController {
    public enum Error { OK, INVALID_TYPE, INSUFFICIENT_FUNDS, OVERBUILD, INVALID_COORDINATES }

    private final StationRepository stationRepo = Repositories.getInstance().getStationRepository();

    private static final int COST_DEPOT    = 50,  CAP_DEPOT    = 100;
    private static final int COST_STATION  = 100, CAP_STATION  = 200;
    private static final int COST_TERMINAL = 200, CAP_TERMINAL = 400;

    /**
     * Attempts to build a station of the given type at (x,y) on map.
     * @param typeStr user input type name
     * @param map the Map instance
     * @param x x-coordinate
     * @param y y-coordinate
     * @return Error status
     */
    public Error buildStation(String typeStr, Map map, int x, int y,Orientation orientation) {

        Station.Type type;
        try {
            type = Station.Type.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Error.INVALID_TYPE;
        }

        User user = CurrentUser.getInstance().getUser();
        int cost;
        int capacity;
        switch (type) {
            case DEPOT:
                cost = COST_DEPOT;
                capacity = CAP_DEPOT;
                break;
            case STATION:
                cost = COST_STATION;
                capacity = CAP_STATION;
                break;
            case TERMINAL:
                cost = COST_TERMINAL;
                capacity = CAP_TERMINAL;
                break;
            default:
                return Error.INVALID_TYPE;
        }

        if (user.getBudget() < cost) {
            return Error.INSUFFICIENT_FUNDS;
        }

        if (x < 1 || x > map.getWidth() || y < 1 || y > map.getHeight()) {
            return Error.INVALID_COORDINATES;
        }

        List<City> cities = new DisplayMapController().getCities().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());
        boolean onCity = cities.stream()
                .anyMatch(c -> c.getCenterX() == x && c.getCenterY() == y);
        boolean onBlock = cities.stream()
                .flatMap(c -> c.getBlocks().stream())
                .anyMatch(b -> b.getX() == x && b.getY() == y);
        String owner = user.getEmail();
        boolean onStation = stationRepo.getStations().stream()
                .filter(s -> s.getMapName().equals(map.getMapName()) && s.getOwnerPlayer().equals(owner))
                .anyMatch(s -> s.getX() == x && s.getY() == y);
        if (onCity || onBlock || onStation) {
            return Error.OVERBUILD;
        }

        String baseName = cities.stream()
                .min(Comparator.comparingInt(c -> Math.abs(c.getCenterX()-x) + Math.abs(c.getCenterY()-y)))
                .map(City::getName)
                .orElse("Unknown");
        String stationName = baseName + "-" + type.name();

        Station st = new Station(type, stationName,
                map.getMapName(),
                owner,
                cost,
                capacity,
                x, y,orientation);

        stationRepo.addStation(st);
        map.getStations().add(st);

        user.setBudget(new domain.Budget(user.getBudget() - cost));

        return Error.OK;
    }

    /** Returns all stations (for display or testing).
     * @return a list of all the stations
     */
    public List<Station> getStations() {
        return stationRepo.getStations();
    }
}
