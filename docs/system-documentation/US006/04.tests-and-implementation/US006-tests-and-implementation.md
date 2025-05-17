# US006 - Upgrade a Station With a Building

## 4. Tests

**Test 1:** Validates if a building is successfully added to a certain station

```java
    @Test
void add() {
    repo.add(new Building(station, "small cafe"));
    assertEquals(3, repo.getBuildings().size());
}
```
	

**Test 2:** Validates if a building is successfully removed from a station.

```java
    @Test
void remove() {
    repo.remove(station, "telegraph");
    assertEquals(1, repo.getBuildings().size());
}

```

**Test 3:** Validates if a building is correctly attached to a station 

```java
    @Test
void validateBuilding() {
    assertTrue(repo.getBuildings().contains(building));
    assertFalse(repo.getBuildings().contains(new Building(station, "large cafe")) );
}

```

**Test 4:** Validates if the list of buildings is beeing correctly showcased.

```java
    void getBuildings() {
    List<Building> buildings = repo.getBuildings();
    assertEquals(2, buildings.size());
    assertEquals("telegraph", buildings.get(0).getBuildingType());
    assertEquals("telephone", buildings.get(1).getBuildingType());
}
```


## 5. Construction (Implementation)

### Class UpgradeStationController

```java
package controller;

import domain.Building;
import domain.Map;
import domain.Station;
import repository.BuildingRepository;
import repository.Repositories;

import java.util.List;

/**
 * Controller responsible for the creation and validation of station upgrades.
 * <p>
 * Delegates storage to BuildingRepository.
 * </p>
 */
public class UpgradeStationController {
    private BuildingRepository buildingRepository;

    /**
     * Constructs an UpgradeStationController using the shared repositories instance.
     */
    public UpgradeStationController() { getBuildingRepository(); }

    /**
     * Constructs an UpgradeStationController with a provided BuildingRepository (for testing).
     *
     * @param buildingRepository the BuildingRepository to use
     */
    public UpgradeStationController(BuildingRepository buildingRepository) { this.buildingRepository = buildingRepository; }

    /**
     * Returns the BuildingRepository, initializing it if necessary.
     *
     * @return the BuildingRepository instance
     */
    public BuildingRepository getBuildingRepository() {
        if (buildingRepository == null) {
            Repositories repositories = Repositories.getInstance();

            buildingRepository = repositories.getBuildingRepository();
        }
        return buildingRepository;
    }

    /**
     * Upgrades a station with the given name and building type.
     *
     * @param station the name of the station to be upgraded
     * @param buildingType the type of building to be added to the station
     * @return the created Building instance for the station
     */
    public Building upgradeStation(Station station, String buildingType) {
        Building building = new Building(station, buildingType);
        getBuildingRepository().add(building);
        return building;
    }

    /**
     * Retrieves the list of all buildings stored in the repository.
     *
     * @return unmodifiable list of existing {@link Building} instances
     */
    public List<Building> getBuildings() { return getBuildingRepository().getBuildings(); }

    /**
     * Verifies if provided building type already exists in provided station.
     *
     * @return true if it does or false if otherwise.
     */
    public boolean verifyIfBuildingExists(Station station, String buildingType) {
        return getBuildings().stream()
                .anyMatch(b ->
                        b.getStation().equals(station) &&
                                b.getBuildingType().equalsIgnoreCase(buildingType)
                );
    }
}

```

### Class BuildingRepository

```java
package repository;

import domain.Building;
import domain.Station;

import java.util.ArrayList;
import java.util.List;

public class BuildingRepository {
    private final List<Building> buildings;

    /**
     * Constructs a new MapRepository with an empty list of maps.
     */
    public BuildingRepository() { buildings = new ArrayList<>(); }

    /**
     * Adds a building to the repository if it is not already present.
     *
     * @param building the Building instance to add
     */
    public void add(Building building) {
        if (validateBuilding(building)) {
            buildings.add(building);
        }
    }

    public void remove(Station station, String buildingType) {
        buildings.removeIf(b ->
                b.getStation().equals(station) &&
                        b.getBuildingType().equalsIgnoreCase(buildingType)
        );
    }

    /**
     * Validates that a building is not already contained in the repository.
     *
     * @param building the Building instance to validate
     * @return {@code true} if the building is not already present; {@code false} otherwise
     */
    private boolean validateBuilding(Building building) { return !buildings.contains(building); }

    /**
     * Returns a defensive copy of the list of all buildings in the repository.
     *
     * @return unmodifiable list of stored Building objects
     */
    public List<Building> getBuildings() {
        //This is a defensive copy, so that the repository cannot be modified from the outside.
        return List.copyOf(buildings);
    }
}
```


## 6. Integration and Demo 

* The Upgrade Station option was added to the Player menu, allowing selection of station, scenario, and building type.


## 7. Observations

n/a