# US007 - List All Stations and Details

## 4. Tests

**Test 1:** Validates if method is returning all stations

```java
@Test
void getStation_Repository(){
    repo.addStation(station1);
    List<Station> stations = repo.getStations();
    assertEquals(stations, repo.getStations());
}
```


## 5. Construction (Implementation)

### Class ListStationController

```java
package controller;

import domain.Building;
import domain.Station;
import repository.BuildingRepository;
import repository.Repositories;
import repository.StationRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for retrieving station and building information
 * Provides methods to list stations and find buildings associated with specific stations
 */
public class ListStationController {

    /** Singleton instance of repositories */
    Repositories repositories = Repositories.getInstance();

    /** Repository for station data */
    StationRepository sr = repositories.getStationRepository();

    /** Repository for building data */
    BuildingRepository br = repositories.getBuildingRepository();

    /**
     * Retrieves all stations in the system
     * @return List of all stations
     */
    public List<Station> getStations(){
        return sr.getStations();
    }

    /**
     * Retrieves all buildings associated with a specific station
     * @param station The station to find buildings for
     * @return List of buildings located at the specified station
     */
    public List<Building> getBuildingByStation(Station station){
        List<Building> lstBuilding = new ArrayList<>();
        List<Building> fetched = br.getBuildings();

        for (Building building : fetched) {
            if (building.getStation().equals(station)){
                lstBuilding.add(building);
            }
        }
        return lstBuilding;
    }
}
```

### Class StationRepository

```java
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
```


## 6. Integration and Demo 

* The Station Listing option was added to the Player menu, allowing selection of the desired station.


## 7. Observations

n/a