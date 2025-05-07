package controller;

import domain.CurrentUser;
import domain.Map;
import domain.Station;
import domain.User;
import repository.Repositories;
import repository.StationRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Controller for buying/placing stations.
 */
public class CreateStationController {
    public enum Error { OK, INVALID_TYPE, INSUFFICIENT_FUNDS, OVERBUILD, INVALID_COORDINATES }

    private final StationRepository stationRepo = Repositories.getInstance().getStationRepository();

    // station costs & capacities
    private static final int COST_DEPOT    = 50,  CAP_DEPOT    = 100;
    private static final int COST_STATION  = 100, CAP_STATION  = 200;
    private static final int COST_TERMINAL = 200, CAP_TERMINAL = 400;

    /**
     * Attempts to build a station of the given type at (x,y) on map.
     * @param typeStr one of "DEPOT","STATION","TERMINAL"
     * @param map the map to build on
     * @param x x-coordinate
     * @param y y-coordinate
     * @return Error code indicating success or failure reason
     */
    public Error buildStation(String typeStr, Map map, int x, int y) {
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

        // coordinates must lie inside map
        if (x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight()) {
            return Error.INVALID_COORDINATES;
        }

        // cannot overbuild: existing station in repo or on map
        if (stationRepo.existsAt(map.getMapName(), x, y) ||
                map.getStations().stream().anyMatch(s -> s.getX() == x && s.getY() == y)) {
            return Error.OVERBUILD;
        }

        // name = nearest city name + type
        String baseName = "Unknown";
        List<domain.City> cities = new DisplayMapController().getCities();
        Optional<domain.City> nearest = cities.stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .min(Comparator.comparingInt(c -> Math.abs(c.getCenterX() - x) + Math.abs(c.getCenterY() - y)));
        if (nearest.isPresent()) {
            baseName = nearest.get().getName();
        }
        String stationName = baseName + "-" + type.name();

        // build station with ownerPlayer = user.getEmail()
        Station st = new Station(type, stationName, map.getMapName(), user.getEmail(), cost, capacity, x, y);
        stationRepo.addStation(st);

        // deduct budget
        user.setBudget(new domain.Budget(user.getBudget() - cost));

        return Error.OK;
    }

    /**
     * Expose for UI to list all existing stations.
     */
    public List<Station> getStations() {
        return stationRepo.getStations();
    }
}
