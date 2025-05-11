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
    private final AuthRepository authRepository;
    private final StationRepository stationRepository;
    private final BuildingRepository buildingRepository;
    private final IndustryRepository industryRepository;

    /**
     * Private constructor to prevent external instantiation.
     * Initializes all repositories.
     */
    private Repositories() {
        mapRepository = new MapRepository();
        cityRepository = new CityRepository();
        scenarioRepository = new ScenarioRepository();
        authRepository = new AuthRepository();
        stationRepository = new StationRepository();
        buildingRepository = new BuildingRepository();
        industryRepository = new IndustryRepository();
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
     * @return the mapRepository
     */
    public MapRepository getMapRepository() {
        return mapRepository;
    }

    /**
     * Gets the repository for city entities.
     *
     * @return the cityRepository
     */
    public CityRepository getCityRepository() {
        return cityRepository;
    }

    /**
     * Gets the repository for scenario entities.
     *
     * @return the scenarioRepository
     */
    public ScenarioRepository getScenarioRepository() {
        return scenarioRepository;
    }

    /**
     * Gets the repository for authentication.
     *
     * @return the authRepository
     */
    public AuthRepository getAuthRepository() {
        return authRepository;
    }

    /**
     * Gets the repository for station entities.
     *
     * @return the stationRepository
     */
    public StationRepository getStationRepository() {
        return stationRepository;
    }

    /**
     * Gets the repository for building entities.
     *
     * @return the buildingRepository
     */
    public BuildingRepository getBuildingRepository() {
        return buildingRepository;
    }

    /**
     * Gets the repository for industry entities.
     *
     * @return the industryRepository
     */
    public IndustryRepository getIndustryRepository() {
        return industryRepository;
    }
}
