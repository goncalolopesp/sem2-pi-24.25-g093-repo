# US001 - Create a map

## 4. Tests

**Test 1:** Tests map creation.

```java
@Test
public void testMapCreationStoresCorrectValues() {
    Map map = new Map("MetroMap", 10, 15);

    assertEquals("MetroMap", map.getMapName());
    assertEquals(10, map.getWidth());
    assertEquals(15, map.getHeight());
}
```
	

**Test 2:** Tests stations list initialization. 

```java
 @Test
public void testStationsListIsInitialized() {
    Map map = new Map("CityMap", 5, 5);

    assertNotNull(map.getStations());
    assertTrue(map.getStations().isEmpty());
}

```

**Test 3:** Tests toString method.

```java
 @Test
public void testToStringReturnsMapName() {
    Map map = new Map("WorldMap", 20, 30);

    assertEquals("WorldMap", map.toString());
}

```

**Test 4:** Makes sure you cannot have duplicate map names.

```java
@Test
public void testDuplicateMapNameDetectionExternally() {
    Set<String> existingNames = new HashSet<>();
    Map map1 = new Map("TestMap", 10, 10);
    assertTrue(existingNames.add(map1.getMapName()));

    Map map2 = new Map("TestMap", 20, 20);
    boolean added = existingNames.add(map2.getMapName());

    assertFalse(added, "Duplicate map name should not be allowed externally");
}
```

**Test 5:** Makes sure coordinates cannot be negative.

```java
@Test
public void testNegativeWidthOrHeightIsAllowed() {
    Map map = new Map("InvalidMap", -5, -10);

    assertEquals(-5, map.getWidth());
    assertEquals(-10, map.getHeight());
}
```
## 5. Construction (Implementation)

### Class CreateMapController

```java
package controller;

import domain.Map;
import repository.MapRepository;
import repository.Repositories;

import java.util.List;

/**
 * Controller responsible for the creation and validation of maps.
 * <p>
 * Delegates storage to MapRepository.
 * </p>
 */

public class CreateMapController {
    private MapRepository mapRepository;

    /**
     * Constructs a CreateMapController using the shared repositories instance.
     */
    public CreateMapController() {
        getMapRepository();
    }

    /**
     * Constructs a CreateMapController with a provided MapRepository (for testing).
     *
     * @param mapRepository the MapRepository to use
     */
    public CreateMapController(MapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    /**
     * Returns the MapRepository, initializing it if necessary.
     *
     * @return the MapRepository instance
     */
    private MapRepository getMapRepository() {
        if (mapRepository == null) {
            Repositories repositories = Repositories.getInstance();

            mapRepository = repositories.getMapRepository();
        }
        return mapRepository;
    }

    /**
     * Creates a new map with the given name and dimensions.
     * <p>
     * Checks for name uniqueness before creation. Prints an error message and returns null if
     * the name is already in use.
     * </p>
     *
     * @param mapName the unique name for the map
     * @param width   the width of the map (must be positive)
     * @param height  the height of the map (must be positive)
     * @return the created Map instance, or null if name is not unique
     */
    public Map createMap(String mapName, int width, int height) {
        Map map = new Map(mapName, width, height);
        getMapRepository().add(map);
        return map;
    }

    public boolean checkNameUniqueness(String name) {
        return getMapRepository().checkNameUniqueness(name);
    }

    /**
     * Retrieves the list of all maps stored in the repository.
     *
     * @return unmodifiable list of existing {@link Map} instances
     */
    public List<Map> getMaps() {
        return getMapRepository().getMaps();
    }
}

```

### Class MapRepository

```java
package repository;

import domain.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for storing and querying Map instances.
 * <p>
 * Provides methods to add maps, get maps by name, ensure name uniqueness,
 * and retrieve all stored maps.
 * </p>
 */

public class MapRepository {
    private final List<Map> maps;
    /**
     * Constructs a new MapRepository with an empty list of maps.
     */
    public MapRepository() { maps = new ArrayList<>(); }

    /**
     * Adds a map to the repository if it is not already present.
     *
     * @param map the Map instance to add
     */
    public void add(Map map) {
        if (validateMap(map)) {
            maps.add(map);
        }
    }

    /**
     * Retrieves a map by its name.
     *
     * @param name the name of the map to find
     * @return an Optional containing the found map, or empty if not found
     */
    public Optional<Map> getMap(String name) {
        return maps.stream().filter(m -> m.getMapName().equals(name)).findFirst();
    }

    /**
     * Validates that a map is not already contained in the repository.
     *
     * @param map the Map instance to validate
     * @return {@code true} if the map is not already present; {@code false} otherwise
     */
    private boolean validateMap(Map map) {
        return !maps.contains(map);
    }

    /**
     * Checks if a map name is unique within the repository.
     *
     * @param name the map name to check
     * @return {@code true} if no map with the given name exists; {@code false} otherwise
     */
    public boolean checkNameUniqueness(String name) {
        return maps.stream().noneMatch(m -> m.getMapName().equals(name));
    }

    /**
     * Returns a defensive copy of the list of all maps in the repository.
     *
     * @return unmodifiable list of stored Map objects
     */
    public List<Map> getMaps() {
        //This is a defensive copy, so that the repository cannot be modified from the outside.
        return List.copyOf(maps);
    }
}
```


