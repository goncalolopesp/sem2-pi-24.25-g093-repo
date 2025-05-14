# US003 - Add a City to the Map

## 4. Tests

**Test 1:** Validate map name not found.

```java
@Test
void validateNameMap_MapNotFound() {
    assertEquals(CreateCityController.NameMapError.MAP_NOT_FOUND,
            controller.validateNameMap("CityA", "NoMap"));
}
```
	

**Test 2:** Validates duplicate map name. 

```java
@Test
void validateNameMap_NameDuplicate() {
    assertEquals(CreateCityController.NameMapError.NAME_DUPLICATE,
            controller.validateNameMap("ExistingCity", "TestMap"));
}

```

**Test 3:** Validates map name.

```java
@Test
void validateNameMap_OK() {
    assertEquals(CreateCityController.NameMapError.OK,
            controller.validateNameMap("NewCity", "TestMap"));
}

```

**Test 4:** Validates city center out of bounds.

```java
@Test
void validateCenter_OutOfBounds() {
    assertEquals(CreateCityController.CenterError.OUT_OF_BOUNDS,
            controller.validateCenter("NewCity", "TestMap", 0, 11));
}
```

**Test 5:** Validate city center position taken.

```java
 @Test
void validateCenter_PositionTaken() {
    assertEquals(CreateCityController.CenterError.POSITION_TAKEN,
            controller.validateCenter("NewCity", "TestMap", 2, 2));
}
```

**Test 6:** Validates city center.

```java
@Test
void validateCenter_OK() {
    assertEquals(CreateCityController.CenterError.OK,
            controller.validateCenter("NewCity", "TestMap", 5, 5));
}
```

**Test 7:** Validate house block doesn't collide with city center.

```java
@Test
void validateBlock_CollidesCityCenter() {
assertEquals(CreateCityController.BlockError.COLLIDES_CITY_CENTER,
controller.validateBlock("TestMap", new HouseBlock(2, 2)));
}
```
**Test 8:** Validate house block,
```java
    @Test
    void validateBlock_OK() {
        assertEquals(CreateCityController.BlockError.OK,
                controller.validateBlock("TestMap", new HouseBlock(3, 3)));
    }
```
**Test 9:** City creation success.
```java
    @Test
    void createCity_SuccessfulCreation() {
        var c = controller.createCity("CityA", "TestMap", 4, 4);
        assertEquals("CityA", c.getName());
        assertTrue(Repositories.getInstance().getCityRepository().getCities().contains(c));
    }
```
**Test 10:** Manual house block success and fails.
```java
    @Test
    void addBlockManual_SucceedsAndFails() {
        var repos = Repositories.getInstance();
        var c = new City("CityB", "TestMap", 6, 6);
        repos.getCityRepository().add(c);
        assertTrue(controller.addBlockManual(c, new HouseBlock(7, 7)));
        assertFalse(controller.addBlockManual(c, new HouseBlock(6, 6)));
    }
```

**Test 11:** Validate city center.
```java
    @Test
    void validateCenter_BlockPresent() {
        var repos = Repositories.getInstance();
        var c = new City("CityC", "TestMap", 1, 1);
        repos.getCityRepository().add(c);
        c.addBlockManual(new HouseBlock(15, 15));
        assertEquals(CreateCityController.CenterError.BLOCK_PRESENT,
                controller.validateCenter("OK", "TestMap", 15, 15));
    }
```
**Test 12:** Validate house out of map.
```java
    @Test
    void validateBlock_OutOfBoundsAndMapNotFound() {
        assertEquals(CreateCityController.BlockError.OUT_OF_BOUNDS,
                controller.validateBlock("TestMap", new HouseBlock(0, 11)));
        assertEquals(CreateCityController.BlockError.OUT_OF_BOUNDS,
                controller.validateBlock("NoMap", new HouseBlock(1, 1)));
    }
```
**Test 13:** Validate house blocks no collisions.
```java
    @Test
    void validateBlock_CollidesBlock() {
        var repos = Repositories.getInstance();
        var c = new City("CityD", "TestMap", 2, 3);
        repos.getCityRepository().add(c);
        c.addBlockManual(new HouseBlock(47, 47));
        assertEquals(CreateCityController.BlockError.COLLIDES_BLOCK,
                controller.validateBlock("TestMap", new HouseBlock(47, 47)));
    }
```
**Test 14:** Create city duplicate name.
```java
    @Test
    void createCity_ThrowsOnDuplicateName() {
        assertThrows(IllegalStateException.class, () ->
                controller.createCity("ExistingCity", "TestMap", 5, 5)
        );
    }
```
**Test 15:** Create city invalid center.
```java
    @Test
    void createCity_ThrowsOnInvalidCenter() {
        assertThrows(IllegalStateException.class, () ->
                controller.createCity("CityE", "TestMap", -1, -1)
        );
    }
```
**Test 16:** Assign blocks successfully.
```java
    @Test
    void assignBlocksAuto_AddsBlocksSuccessfully() {
        var repos = Repositories.getInstance();
        var city = new City("AutoCity", "TestMap", 5, 5);
        repos.getCityRepository().add(city);
        controller.assignBlocksAuto(city, 5, 1.0);
        assertEquals(5, city.getBlocks().size());
    }
```
## 5. Construction (Implementation)

### Class CreateCityController

```java
package controller;

import domain.City;
import domain.HouseBlock;
import repository.CityRepository;
import repository.MapRepository;
import repository.Repositories;

import java.util.Optional;

/**
 * Controller responsible for the creation and validation of cities on maps.
 * <p>
 * Provides methods to validate city and map names, validate center and block positions,
 * create cities, and add blocks (both manual and automatic assignment).
 * </p>
 */

public class CreateCityController {
    private final MapRepository mapRepo;
    private final CityRepository cityRepo;

    public enum NameMapError { OK, MAP_NOT_FOUND, NAME_DUPLICATE }
    public enum CenterError { OK, OUT_OF_BOUNDS, POSITION_TAKEN, BLOCK_PRESENT }
    public enum BlockError { OK, OUT_OF_BOUNDS, COLLIDES_CITY_CENTER, COLLIDES_BLOCK }


    /**
     * Constructs a new CreateCityController, initializing repositories.
     */
    public CreateCityController() {
        var repos = Repositories.getInstance();
        this.mapRepo = repos.getMapRepository();
        this.cityRepo = repos.getCityRepository();
    }

    /**
     * Validates whether the specified city name and map name are acceptable.
     *
     * @param name the desired city name
     * @param map  the name of the map to use
     * @return NameMapError#OK if valid;
     *         NameMapError#MAP_NOT_FOUND if the map does not exist;
     *         NameMapError#NAME_DUPLICATE}if the name is already used on the map
     */
    public NameMapError validateNameMap(String name, String map) {
        Optional<domain.Map> m = mapRepo.getMap(map);
        if (m.isEmpty()) return NameMapError.MAP_NOT_FOUND;
        if (!cityRepo.isNameUnique(name, map)) return NameMapError.NAME_DUPLICATE;
        return NameMapError.OK;
    }

    /**
     * Validates the city name and map name for creating a new city.
     *
     * @param cityName the desired city name
     * @param mapName  the name of the map to use
     * @param x        the x-coordinate of the center
     * @param y        the y-coordinate of the center
     * @return true if both names are valid and coordinates are acceptable; false otherwise
     */
    public boolean validateCity(String cityName, String mapName, int x, int y) {
        NameMapError nameError = validateNameMap(cityName, mapName);
        if (nameError != NameMapError.OK) {
            return false;
        }

        CenterError centerError = validateCenter(cityName, mapName, x, y);
        if (centerError != CenterError.OK) {
            return false;
        }

        return true;
    }

    /**
     * Validates the center coordinates for a new city on a map.
     *
     * @param name the city name (unused for validation but kept for symmetry)
     * @param map  the map name where the city will be placed
     * @param x    the x-coordinate of the center
     * @param y    the y-coordinate of the center
     * @return CenterError#O if valid;
     *         CenterError#OUT_OF_BOUNDS if coordinates are outside bounds or map missing;
     *         CenterError#POSITION_TAKEN if another city center exists there;
     *         CenterError#BLOCK_PRESENT if a house block occupies the position
     */
    public CenterError validateCenter(String name, String map, int x, int y) {
        Optional<domain.Map> m = mapRepo.getMap(map);
        if (m.isEmpty()) return CenterError.OUT_OF_BOUNDS;
        var mp = m.get();
        if (x < 1 || x > mp.getWidth() || y < 1 || y > mp.getHeight()) return CenterError.OUT_OF_BOUNDS;
        if (cityRepo.existsAtPosition(map, x, y)) return CenterError.POSITION_TAKEN;
        HouseBlock hb = new HouseBlock(x, y);
        if (!cityRepo.isBlockPositionUnique(map, hb)) return CenterError.BLOCK_PRESENT;
        return CenterError.OK;
    }

    /**
     * Validates a house block's coordinates on a map.
     *
     * @param map the map name where the block will be placed
     * @param b   the HouseBlock with coordinates to validate
     * @return BlockError#OK if valid;
     *         BlockError#OUT_OF_BOUNDS if outside bounds or map missing;
     *         BlockError#COLLIDES_CITY_CENTER if collides with a city center;
     *         BlockError#COLLIDES_BLOCK if collides with another block
     */
    public BlockError validateBlock(String map, HouseBlock b) {
        Optional<domain.Map> m = mapRepo.getMap(map);
        if (m.isEmpty()) return BlockError.OUT_OF_BOUNDS;
        var mp = m.get();
        int x = b.getX(), y = b.getY();
        if (x < 1 || x > mp.getWidth() || y < 1 || y > mp.getHeight()) return BlockError.OUT_OF_BOUNDS;
        if (cityRepo.existsAtPosition(map, x, y)) return BlockError.COLLIDES_CITY_CENTER;
        if (!cityRepo.isBlockPositionUnique(map, b)) return BlockError.COLLIDES_BLOCK;
        return BlockError.OK;
    }

    /**
     * Creates a new city with validated parameters and stores it in the repository.
     *
     * @param name the city name
     * @param map  the map name
     * @param x    the center x-coordinate
     * @param y    the center y-coordinate
     * @return the created City
     * @throws IllegalStateException if the name or center validation fails
     */
    public City createCity(String name, String map, int x, int y) {
        if (validateNameMap(name, map) != NameMapError.OK || validateCenter(name, map, x, y) != CenterError.OK) {
            throw new IllegalStateException("Cannot create city: validation failed");
        }
        City c = new City(name, map, x, y);
        cityRepo.add(c);
        return c;
    }

    /**
     * Adds a house block to a city after validating its position.
     *
     * @param city the City to which the block will be added
     * @param b    the HouseBlock to add
     * @return true if block was added; false if the validation failed
     */
    public boolean addBlockManual(City city, HouseBlock b) {
        BlockError err = validateBlock(city.getMapName(), b);
        if (err != BlockError.OK) return false;
        city.addBlockManual(b);
        return true;
    }

    /**
     * Automatically assigns a number of blocks around the city's center using a Gaussian distribution.
     *
     * @param city  the City to which blocks will be added
     * @param count the number of blocks to generate
     * @param sigma the standard deviation for Gaussian distribution
     */
    public void assignBlocksAuto(City city, int count, double sigma) {
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < count; i++) {
            HouseBlock hb;
            BlockError err;
            do {
                int x = city.getCenterX() + (int) (rnd.nextGaussian() * sigma);
                int y = city.getCenterY() + (int) (rnd.nextGaussian() * sigma);
                hb = new HouseBlock(x, y);
                err = validateBlock(city.getMapName(), hb);
            } while (err != BlockError.OK);
            city.addBlockManual(hb);
        }
    }
}



```

### Class CityRepository

```java
package repository;

import controller.CreateCityController;
import domain.City;
import domain.HouseBlock;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and querying City instances.
 * <p>
 * Provides methods to add cities, retrieve the list of cities, and validate
 * uniqueness and existence constraints related to city names, positions, and blocks.
 * </p>
 */

public class CityRepository {
    private final List<City> cities = new ArrayList<>();

    /**
     * Adds a new city to the repository.
     *
     * @param c the City to add
     */
    public void add(City c) { cities.add(c); }
    /**
     * Returns an unmodifiable list of all cities in the repository.
     *
     * @return list of City objects
     */
    public List<City> getCities() { return List.copyOf(cities); }

    /**
     * Checks if a city name is unique within a given map.
     *
     * @param name the city name to check
     * @param map  the map name where uniqueness needs to be enforced
     * @return {@code true} if no city with the same name exists on the map;
     *         {@code false} otherwise
     */
    public boolean isNameUnique(String name, String map) {
        return cities.stream().noneMatch(c->c.getName().equals(name) && c.getMapName().equals(map));
    }

    /**
     * Checks if a city center exists at the specified coordinates on a map.
     *
     * @param map the map name to search
     * @param x   the x-coordinate of the city center
     * @param y   the y-coordinate of the city center
     * @return {@code true} if a city center occupies the position;
     *         {@code false} otherwise
     */
    public boolean existsAtPosition(String map, int x, int y) {
        return cities.stream().filter(c->c.getMapName().equals(map))
                .anyMatch(c->c.getCenterX()==x && c.getCenterY()==y);
    }

    /**
     * Checks if a house block position is unique within the specified map.
     *
     * @param map the map name to check
     * @param b   the HouseBlock to validate
     * @return {@code true} if no other block equals {@code b} on the map;
     *         {@code false} otherwise
     */
    public boolean isBlockPositionUnique(String map, HouseBlock b) {
        return cities.stream().filter(c->c.getMapName().equals(map))
                .flatMap(c->c.getBlocks().stream())
                .noneMatch(b::equals);
    }

    /**
     * Validates a block position by delegating to the {@link CreateCityController} logic.
     * <p>
     * This method currently returns a stub value and should delegate to the controller
     * for actual validation.
     * </p>
     *
     * @param map the map name where the block is placed
     * @param b   the HouseBlock to validate
     * @return a {@link CreateCityController.BlockError} indicating the validation result
     */
    public CreateCityController.BlockError validateBlock(String map, HouseBlock b) {
        // delegated to controller, stub here
        return CreateCityController.BlockError.OK;
    }
}
```


