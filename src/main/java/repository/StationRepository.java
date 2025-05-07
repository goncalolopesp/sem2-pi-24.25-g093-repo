package repository;

import domain.Station;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and querying Station instances.
 */
public class StationRepository {
    private final List<Station> stations = new ArrayList<>();

    /**
     * Add a new station to the repository.
     * @param s the station to add
     * @return true if added successfully
     */
    public boolean addStation(Station s) {
        return stations.add(s);
    }

    /**
     * Returns a defensive copy of all stations.
     */
    public List<Station> getStations() {
        return List.copyOf(stations);
    }

    /**
     * Checks whether the given coordinates on a map are already occupied by another station.
     */
    public boolean existsAt(String mapName, int x, int y) {
        return stations.stream()
                .anyMatch(s -> s.getMapName().equals(mapName)
                        && s.getX() == x
                        && s.getY() == y);
    }
}
