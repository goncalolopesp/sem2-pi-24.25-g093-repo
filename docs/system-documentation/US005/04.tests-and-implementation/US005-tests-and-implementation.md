# US005 - Build a Station

## 4. Tests

**Test 1:**  Check that it is not possible to instantiate the Station class with null values. 

```java
@Test(expected = IllegalArgumentException.class)
public void ensureNullIsNotAllowed() {
    new Station(null, null, null, null, null);
}
```
	

**Test 2:** Check that overbuilding is not permitted (AC01: Overbuilding is not possible).

```java
@Test(expected = IllegalArgumentException.class)
public void ensureOverbuildingNotAllowed() {
    MapRepository mapRepo = new MapRepository();
    CityRepository cityRepo = new CityRepository();
    StationRepository stationRepo = new StationRepository();
    // Assuming that a station already exists on that position, on that Map
    Station existing = new Station("Depot", "Lisbon Depot", new Position(5, 5), Center.GEOMETRIC, "M1");
    stationRepo.add(existing);

    // Attempt to build a new station on the same position as an existing one
    BuildStationController controller = new BuildStationController(mapRepo, cityRepo, stationRepo);
    controller.buildStation("Depot", new Position(5, 5), null, "M1");
}

```


## 5. Construction (Implementation)

### Class CreateStationController

```java
package controller;

import domain.Station;
import domain.Position;
import domain.Map;
import domain.City;
import domain.Center;
import domain.Scenario;
import repository.Repositories;
import repository.MapRepository;
import repository.CityRepository;
import repository.ScenarioRepository;

public class CreateStationController {

    private final MapRepository mapRepository;
    private final CityRepository cityRepository;
    private final ScenarioRepository scenarioRepository;
    private final StationRepository stationRepository;

    public BuildStationController() {
        Repositories repos = Repositories.getInstance();
        this.mapRepository = repos.getMapRepository();
        this.cityRepository = repos.getCityRepository();
        this.scenarioRepository = repos.getScenarioRepository();
        this.stationRepository = repos.getStationRepository();
    }

    public Station buildStation(String type,
                                Position position,
                                Center centerOption,
                                String mapName,
                                String scenarioName) {
        if (type == null || position == null || mapName == null || scenarioName == null) {
            System.out.println("Error: Parameters must not be null.");
            return null;
        }

        Map map = mapRepository.getByName(mapName);
        if (map == null) {
            System.out.println("Error: Map not found.");
            return null;
        }

        Scenario scenario = scenarioRepository.findByName(scenarioName);
        if (scenario == null || !scenario.getMapName().equals(mapName)) {
            System.out.println("Error: Scenario not found for the given map");
            return null;
        }

        City nearestCity = cityRepository.findClosest(position);
        if (nearestCity == null) {
            System.out.println("Error: No city found near the given position for the station");
            return null;
        }

        String displayName = nearestCity.getName() + " " + type;
        Station station = new Station(type, displayName, position, centerOption, mapName, scenarioName);

        Station station = stationRepository.add(station);
        return station;
    }
}
```

### Class StationRepository

```java
package repository;

import domain.Station;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StationRepository {

    private final List<Station> stations = new ArrayList<>();
    
    public Station add(Station station) {
        boolean overlaps = stations.stream()
                .anyMatch(existing -> existing.overlaps(station));
        if (overlaps) {
            System.out.println("Error: Overbuilding not allowed.");
            return null;
        }
        stations.add(station);
        return station;
    }
    
    public List<Station> getAll() {
        return new ArrayList<>(stations);
    }
    
    public boolean exists(String mapName, String scenarioName, String stationName) {
        String key = mapName + ":" + scenarioName;
        return stations.stream()
                .anyMatch(s -> s.getMapScenarioId().equals(key)
                        && s.getType().equals(stationName));
    }
    
    public List<Station> getByMapScenario(String mapName, String scenarioName) {
        String key = mapName + ":" + scenarioName;
        return stations.stream()
                .filter(s -> s.getMapScenarioId().equals(key))
                .collect(Collectors.toList());
    }
}
```


## 6. Integration and Demo 

* The Build Station option was added to the Player menu, allowing the user to select the station type, its position on the map, and the scenario.


## 7. Observations

n/a