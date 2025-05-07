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

