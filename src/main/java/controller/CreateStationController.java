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
import java.util.stream.Collectors;

public class CreateStationController {
    public enum Error { OK, INVALID_TYPE, INSUFFICIENT_FUNDS, OVERBUILD, INVALID_COORDINATES }

    /**
     * Constructs a Repository for Station objects.
     */
    private final StationRepository stationRepo = Repositories.getInstance().getStationRepository();

    private static final int COST_DEPOT = 50, CAP_DEPOT = 100;
    private static final int COST_STATION = 100, CAP_STATION = 200;
    private static final int COST_TERMINAL = 200, CAP_TERMINAL = 400;

    /**
     * Method responsible to build a station on the given map.
     * @param typeStr the type of station to be built
     * @param map     the map where the station will be built
     * @param x       the x-coordinate of the station's center
     * @param y       the y-coordinate of the station's center
     * @param orientation the station's orientation
     * @return Error#OK if valid;Error#INSUFFICIENT_FUNDS if the player doesn't have enough budget; Error#INVALID_COORDINATES if the player tries to place the station or its area outside the map;Error#OVERBUILD if the player tries to build the station in the same position as another structure; Error#INVALID_TYPE if the station isn't from a valid type;
     */
    public Error buildStation(String typeStr, Map map, int x, int y, Orientation orientation) {
        Station.Type type;
        try {
            type = Station.Type.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Error.INVALID_TYPE;
        }
        User user = CurrentUser.getInstance().getUser();
        int cost, capacity;
        switch (type) {
            case DEPOT:
                cost = COST_DEPOT; capacity = CAP_DEPOT; break;
            case STATION:
                cost = COST_STATION; capacity = CAP_STATION; break;
            case TERMINAL:
                cost = COST_TERMINAL; capacity = CAP_TERMINAL; break;
            default:
                return Error.INVALID_TYPE;
        }
        if (user.getBudgetValue() < cost) return Error.INSUFFICIENT_FUNDS;

        int origX = x;
        int origY = y;
        int centerX, centerY;
        int areaW, areaH;

        int difx= 0,dify = 0;
        if (type == Station.Type.STATION) {
            int dx = 0,dy = 0;
            areaW = areaH = 4;
            if(orientation == Orientation.NE) dx = 1;
            else if(orientation == Orientation.SW) dy = 1;
            else if(orientation == Orientation.SE){
                dx=1;
                dy=1;
            }
            origX += dx;
            origY += dy;
            centerX = origX;
            centerY = origY;
            difx = dx;
            dify=dy;
        }  else {
            areaW = (type == Station.Type.DEPOT ? 3 : 5);
            areaH = areaW;
            centerX = origX;
            centerY = origY;
        }
        int originX = centerX - (areaW - 1) / 2;
        int originY = centerY - (areaH - 1) / 2;
        if (originX < 1 || originY < 1 || originX + areaW - 1 > map.getWidth() || originY + areaH - 1 > map.getHeight()) {
            return Error.INVALID_COORDINATES;
        }
        List<City> cities = new DisplayMapController().getCities().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());
        for (int dx = 0; dx < areaW; dx++) {
            for (int dy = 0; dy < areaH; dy++) {
                int cx = originX + dx;
                int cy = originY + dy;
                boolean onCity = cities.stream().anyMatch(c -> c.getCenterX() == cx && c.getCenterY() == cy);
                boolean onBlock = cities.stream().flatMap(c -> c.getBlocks().stream())
                        .anyMatch(b -> b.getX() == cx && b.getY() == cy);
                if (onCity || onBlock) return Error.OVERBUILD;
                String owner = user.getEmail();
                for (Station s : stationRepo.getStations()) {
                    if (!s.getMapName().equals(map.getMapName()) || !s.getOwnerPlayer().equals(owner)) continue;
                    int sw, sh, sox, soy;
                    if (s.getType() == Station.Type.STATION) {
                        sw = sh = 4;
                        int sbX = s.getX();
                        int sbY = s.getY();
                        int sdx = (s.getOrientation() == Orientation.NE || s.getOrientation() == Orientation.SE) ? 1 : 0;
                        int sdy = (s.getOrientation() == Orientation.SW || s.getOrientation() == Orientation.SE) ? 1 : 0;
                        sox = sbX + sdx - 1;
                        soy = sbY + sdy - 1;
                    } else {
                        sw = (s.getType() == Station.Type.DEPOT ? 3 : 5);
                        sh = sw;
                        sox = s.getX() - sw/2;
                        soy = s.getY() - sh/2;
                    }
                    if (cx >= sox && cx < sox + sw && cy >= soy && cy < soy + sh) return Error.OVERBUILD;
                }
            }
        }
        final int nameRefX = origX - difx;
        final int nameRefY = origY - dify;

        String name = cities.stream()
                .min(Comparator.comparingInt(c -> Math.abs(c.getCenterX() - nameRefX) + Math.abs(c.getCenterY() - nameRefY)))
                .map(City::getName)
                .orElse("Unknown") + "-" + type.name();


        Station st = new Station(type, name, map.getMapName(), user.getEmail(), cost, capacity,0,origX, origY, orientation);
        stationRepo.addStation(st);
        map.getStations().add(st);
        user.setBudget(new domain.Budget(user.getBudgetValue() - cost));
        return Error.OK;
    }

    /**
     * Method responsible to try to build a station on the given map (used to upgrade the station).
     * @param typeStr the type of station to be built
     * @param map     the map where the station will be built
     * @param x       the x-coordinate of the station's center
     * @param y       the y-coordinate of the station's center
     * @param orientation the station's orientation
     * @param cost the cost of the upgrade
     * @return Error#OK if valid;Error#INSUFFICIENT_FUNDS if the player doesn't have enough budget; Error#INVALID_COORDINATES if the player tries to place the station or its area outside the map;Error#OVERBUILD if the player tries to build the station in the same position as another structure; Error#INVALID_TYPE if the station isn't from a valid type;
     */
    public Error buildStation(String typeStr, Map map, int x, int y, Orientation orientation,int cost) {
        Station.Type type;
        try {
            type = Station.Type.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Error.INVALID_TYPE;
        }
        User user = CurrentUser.getInstance().getUser();
        int capacity;
        switch (type) {
            case DEPOT:
                capacity = CAP_DEPOT; break;
            case STATION:
                capacity = CAP_STATION; break;
            case TERMINAL:
                capacity = CAP_TERMINAL; break;
            default:
                return Error.INVALID_TYPE;
        }

        int origX = x;
        int origY = y;
        int centerX, centerY;
        int areaW, areaH;

        int difx= 0,dify = 0;
        if (type == Station.Type.STATION) {
            int dx = 0,dy = 0;
            areaW = areaH = 4;
            if(orientation == Orientation.NE) dx = 1;
            else if(orientation == Orientation.SW) dy = 1;
            else if(orientation == Orientation.SE){
                dx=1;
                dy=1;
            }
            origX += dx;
            origY += dy;
            centerX = origX;
            centerY = origY;
            difx = dx;
            dify=dy;
        }  else {
            areaW = (type == Station.Type.DEPOT ? 3 : 5);
            areaH = areaW;
            centerX = origX;
            centerY = origY;
        }
        int originX = centerX - (areaW - 1) / 2;
        int originY = centerY - (areaH - 1) / 2;
        if (originX < 1 || originY < 1 || originX + areaW - 1 > map.getWidth() || originY + areaH - 1 > map.getHeight()) {
            return Error.INVALID_COORDINATES;
        }
        List<City> cities = new DisplayMapController().getCities().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());
        for (int dx = 0; dx < areaW; dx++) {
            for (int dy = 0; dy < areaH; dy++) {
                int cx = originX + dx;
                int cy = originY + dy;
                boolean onCity = cities.stream().anyMatch(c -> c.getCenterX() == cx && c.getCenterY() == cy);
                boolean onBlock = cities.stream().flatMap(c -> c.getBlocks().stream())
                        .anyMatch(b -> b.getX() == cx && b.getY() == cy);
                if (onCity || onBlock) return Error.OVERBUILD;
                String owner = user.getEmail();
                for (Station s : stationRepo.getStations()) {
                    if (!s.getMapName().equals(map.getMapName()) || !s.getOwnerPlayer().equals(owner)) continue;
                    int sw, sh, sox, soy;
                    if (s.getType() == Station.Type.STATION) {
                        sw = sh = 4;
                        int sbX = s.getX();
                        int sbY = s.getY();
                        int sdx = (s.getOrientation() == Orientation.NE || s.getOrientation() == Orientation.SE) ? 1 : 0;
                        int sdy = (s.getOrientation() == Orientation.SW || s.getOrientation() == Orientation.SE) ? 1 : 0;
                        sox = sbX + sdx - 1;
                        soy = sbY + sdy - 1;
                    } else {
                        sw = (s.getType() == Station.Type.DEPOT ? 3 : 5);
                        sh = sw;
                        sox = s.getX() - sw/2;
                        soy = s.getY() - sh/2;
                    }
                    if (cx >= sox && cx < sox + sw && cy >= soy && cy < soy + sh) return Error.OVERBUILD;
                }
            }
        }
        final int nameRefX = origX - difx;
        final int nameRefY = origY - dify;
        String name = cities.stream()
                .min(Comparator.comparingInt(c -> Math.abs(c.getCenterX() - nameRefX) + Math.abs(c.getCenterY() - nameRefY)))
                .map(City::getName)
                .orElse("Unknown") + "-" + type.name();
        Station st = new Station(type, name, map.getMapName(), user.getEmail(), cost, capacity,0,origX, origY, orientation);
        stationRepo.addStation(st);
        map.getStations().add(st);
        user.setBudget(new domain.Budget(user.getBudgetValue() - cost));
        return Error.OK;
    }

    /**
     * Goes to the repository and returns a defensive copy of all stations.
     * @return a list of all stored stations in the repository
     */
    public List<Station> getStations() {
        return stationRepo.getStations();
    }

    public StationRepository getStationRepository() {
        return stationRepo;
    }
}
