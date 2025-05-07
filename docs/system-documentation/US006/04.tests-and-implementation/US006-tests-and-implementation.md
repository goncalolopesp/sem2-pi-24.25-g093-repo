# US006 - Upgrade a Station With a Building

## 4. Tests

**Test 1:** Ensure that null parameters are not allowed when upgrading a station.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureNullParametersNotAllowed() {
    UpgradeStationController controller = new UpgradeStationController();
    controller.upgradeStation(null, null, null);
}
```
	

**Test 2:** Ensure that upgrading a non-existent station fails. 

```java
@Test(expected = IllegalArgumentException.class)
public void ensureStationMustExistBeforeUpgrade() {
    Repositories repos = Repositories.getInstance();
    repos.getStationRepository().getAll().clear();

    UpgradeStationController controller = new UpgradeStationController();
    controller.upgradeStation("M1", "S1", "Central Depot");
}

```

**Test 3:** Ensure that selecting an unavailable building type fails.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureBuildingTypeMustBeAvailable() {
    Repositories repos = Repositories.getInstance();
    repos.getMapRepository().add(new Map("M1", 100, 100));
    repos.getScenarioRepository().add(new Scenario("S1", "M1"));
    Station station = new Station("Depot", "Depot", new Position(0,0), Center.GEOMETRIC, "M1:S1");
    repos.getStationRepository().add(station);
    // Building option that doesn't exist
    UpgradeStationController controller = new UpgradeStationController();
    controller.upgradeStation("M1", "S1", "Signal Tower");
}

```

**Test 4:** Successful upgrade adds the building to the station.

```java
@Test
public void ensureSuccessfulUpgrade() {
    Repositories repos = Repositories.getInstance();
    repos.getMapRepository().add(new Map("M1", 100, 100));
    repos.getScenarioRepository().add(new Scenario("S1", "M1"));
    Station station = new Station("Depot", "Depot", new Position(1,1), Center.GEOMETRIC, "M1:S1");
    repos.getStationRepository().add(station);
    repos.getBuildingRepository().add(new Building("Signal Tower"));

    UpgradeStationController controller = new UpgradeStationController();
    boolean result = controller.upgradeStation("M1", "S1", "Signal Tower");
    assertTrue(result);
    assertTrue(station.getBuildings().contains("Signal Tower"));
}
```


## 5. Construction (Implementation)

### Class UpgradeStationController

```java
package controller;

import domain.Map;
import domain.Scenario;
import domain.Station;
import domain.Building;
import repository.Repositories;
import repository.MapRepository;
import repository.ScenarioRepository;
import repository.BuildingRepository;

public class UpgradeStationController {

    private final MapRepository mapRepository;
    private final ScenarioRepository scenarioRepository;
    private final StationRepository stationRepository;
    private final BuildingRepository buildingRepository;

    public UpgradeStationController() {
        Repositories repos = Repositories.getInstance();
        this.mapRepository = repos.getMapRepository();
        this.scenarioRepository = repos.getScenarioRepository();
        this.stationRepository = repos.getStationRepository();
        this.buildingRepository = repos.getBuildingRepository();
    }

    public boolean upgradeStation(String mapName,
                                  String scenarioName,
                                  String buildingName) {
        if (mapName == null || scenarioName == null || buildingName == null) {
            throw new IllegalArgumentException("Parameters must not be null");
        }

        Map map = mapRepository.getByName(mapName);
        if (map == null) {
            throw new IllegalArgumentException("Error: Map not found.");
        }

        Scenario scenario = scenarioRepository.findByName(scenarioName);
        if (scenario == null || !scenario.getMapName().equals(mapName)) {
            throw new IllegalArgumentException("Error: Scenario not found for given map.");
        }

        Optional<Station> optStation = stationRepository.findByMapScenarioAndName(mapName, scenarioName, buildingName);
        if (!optStation.isPresent()) {
            throw new IllegalArgumentException("Error: Station does not exist.");
        }
        Station station = optStation.get();

        Building building = buildingRepository.findByName(buildingName);
        if (building == null) {
            throw new IllegalArgumentException("Error: Building type not available.");
        }

        station.addBuilding(buildingName);
        stationRepository.update(station);
        return true;
    }
}
```

### Class BuildingRepository

```java
package repository;

import domain.Building;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BuildingRepository {
    private final List<Building> buildings = new ArrayList<>();

    public Building add(Building building) {
        buildings.add(building);
        return building;
    }

    public Building findByName(String name) {
        Optional<Building> result = buildings.stream()
                .filter(b -> b.getName().equals(name))
                .findFirst();
        return result.orElse(null);
    }
}
```


## 6. Integration and Demo 

* The Upgrade Station option was added to the Player menu, allowing selection of station, scenario, and building type.


## 7. Observations

n/a