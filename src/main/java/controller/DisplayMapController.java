package controller;

import domain.City;
import domain.Map;
import repository.CityRepository;
import repository.MapRepository;
import repository.Repositories;

import java.util.List;

/**
 * Controller responsible for retrieving data for display purposes.
 * <p>
 * Provides methods to list all maps, list all cities, and verify the existence of a map.
 * Internally obtains the repositories.
 * </p>
 *
 * @see MapRepository
 * @see CityRepository
 */
public class DisplayMapController {
    private MapRepository mapRepository;
    private CityRepository cityRepository;

    public DisplayMapController() {
        getMapRepository();
        getCityRepository();
    }

    /**
     * Constructs a DisplayMapController using shared repositories.
     */
    public DisplayMapController(MapRepository mapRepository, CityRepository cityRepository) {
        this.mapRepository = mapRepository;
        this.cityRepository = cityRepository;
    }

    /**
     * Returns the MapRepository.
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
     * Returns the CityRepository.
     *
     * @return the CityRepository instance
     */
    private CityRepository getCityRepository() {
        if (cityRepository == null) {
            Repositories repositories = Repositories.getInstance();

            cityRepository = repositories.getCityRepository();
        }
        return cityRepository;
    }

    /**
     * Retrieves all maps stored in the repository.
     *
     * @return unmodifiable list of existing Map instances
     */
    public List<Map> getMaps() {
        return getMapRepository().getMaps();
    }

    /**
     * Retrieves all cities stored in the repository.
     *
     * @return unmodifiable list of existing City instances
     */
    public List<City> getCities() {
        return getCityRepository().getCities();
    }

    /**
     * Verifies whether a map with the given name exists.
     *
     * @param mapName the name of the map to check
     * @return {@code true} if a map with the specified name exists; {@code false} otherwise
     */
    public boolean verifyIfMapExists(String mapName) {
        return getMaps().stream().anyMatch(map -> map.getMapName().equals(mapName));
    }
}
