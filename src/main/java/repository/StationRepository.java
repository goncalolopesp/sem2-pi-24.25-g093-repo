package repository;

import domain.Station;

import java.util.ArrayList;
import java.util.List;

/**
 * Thread-safe repository for storing and querying Station instances.
 * <p>
 * Provides methods to add, get, and remove stations safely across threads.
 * </p>
 */
public class StationRepository {
    private final List<Station> stations = new ArrayList<>();

    /**
     * Adds a new station to the repository.
     * @param s the station to add
     * @return true if added successfully
     */
    public synchronized boolean addStation(Station s) {
        return stations.add(s);
    }

    /**
     * Returns a defensive copy of all stations.
     * @return unmodifiable list of stored Station objects
     */
    public synchronized List<Station> getStations() {
        return List.copyOf(stations);
    }

    /**
     * Removes a certain station from the list of all stations.
     * @param station the station to remove
     * @return true if removed; false if not
     */
    public synchronized boolean removeStation(Station station) {
        return stations.remove(station);
    }

    /**
     * Atomically removes all stations matching the given map name and owner.
     * @param mapName the map name to match
     * @param scenarioName the name of the scenario
     * @param ownerEmail the owner email to match
     */
    public synchronized void removeStationsByMapScenarioAndOwner(String mapName,String scenarioName, String ownerEmail) {
        stations.removeIf(s ->
                s.getMapName().equalsIgnoreCase(mapName) &&
                        s.getOwnerPlayer().equalsIgnoreCase(ownerEmail) && s.getScenarioName().equalsIgnoreCase(scenarioName)
        );
    }
}