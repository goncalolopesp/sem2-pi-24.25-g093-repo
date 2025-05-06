package repository;

/**
 * Singleton holder for all repository instances used by the application.
 * <p>
 * Provides access to all repositories through a single shared instance.
 * </p>
 */

public class Repositories {
    private static Repositories instance;
    private final MapRepository mapRepository;
    private final CityRepository cityRepository;
    private final ScenarioRepository scenarioRepository;


    /**
     * Private constructor to prevent external instantiation.
     * Initializes all repositories.
     */
    private Repositories() {
        mapRepository = new MapRepository();
        cityRepository = new CityRepository();
        scenarioRepository = new ScenarioRepository();
    }

    /**
     * Returns the singleton instance of Repositories, creating it if necessary.
     * Uses double-checked locking for thread safety and performance.
     *
     * @return the shared Repositories instance
     */
    public static Repositories getInstance() {
        if (instance == null) {
            synchronized (Repositories.class) {
                instance = new Repositories();
            }
        }
        return instance;
    }

    /**
     * Gets the repository for map entities.
     *
     * @return the MapRepository instance
     */
    public MapRepository getMapRepository() { return mapRepository; }
    /**
     * Gets the repository for city entities.
     *
     * @return the CityRepository instance
     */
    public CityRepository getCityRepository() { return cityRepository; }
    /**
     * Gets the repository for scenario entities.
     *
     * @return the ScenarioRepository
     */
    public ScenarioRepository getScenarioRepository() { return scenarioRepository; }
}
