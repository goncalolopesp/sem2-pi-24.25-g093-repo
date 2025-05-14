package repository;

import domain.Station;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and querying Station instances.
 * <p>
 * Provides methods to add stations and get a list of all the stations created.
 * </p>
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
     * @return unmodifiable list of stored Station objects
     */
    public List<Station> getStations() {
        return List.copyOf(stations);
    }

    /**
     * Removes a certain station from the list of all the stations.
     */
    public boolean removeStation(Station station) {
        return stations.remove(station);
    }

}
