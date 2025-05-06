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
        if (!getMapRepository().checkNameUniqueness(mapName)){
            System.out.println("\nName not unique.");
            return null;
        }

        Map map = new Map(mapName, width, height);
        getMapRepository().add(map);
        return map;
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
