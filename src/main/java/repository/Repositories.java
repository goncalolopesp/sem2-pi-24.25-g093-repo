package repository;

import domain.SimulationFile;

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
    private final LocomotiveRepository locomotiveRepository;
    private final TrainRepository trainRepository;
    private final CarriageRepository carriageRepository;
    private final BudgetRepository budgetRepository;
    private SimulationRepository simulationRepository;
    /**
     * Private constructor to prevent external instantiation.
     * Initializes all repositories.
     */
    private Repositories() {
        mapRepository = new MapRepository();
        cityRepository = new CityRepository();
        scenarioRepository = new ScenarioRepository();
        authRepository = new AuthRepository();
        buildingRepository = new BuildingRepository();
        industryRepository = new IndustryRepository();
        stationRepository = new StationRepository();
        locomotiveRepository = new LocomotiveRepository();
        trainRepository = new TrainRepository();
        carriageRepository = new CarriageRepository();
        budgetRepository = new BudgetRepository();
        simulationRepository = new SimulationRepository();

        SimulationFile simFile = new SimulationFile();
        simFile.load(this.simulationRepository, this.stationRepository, this.buildingRepository);
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

    /**
     * Gets the repository for locomotive entities.
     *
     * @return the locomotiveRepository
     */
    public LocomotiveRepository getLocomotiveRepository() { return locomotiveRepository; }

    /**
     * Gets the repository for train entities.
     *
     * @return the trainRepository
     */
    public TrainRepository getTrainRepository() { return trainRepository; }

    /**
     * Gets the repository for carriage entities.
     *
     * @return the carriageRepository
     */
    public CarriageRepository getCarriageRepository() { return carriageRepository; }

    /**
     * Gets the repository for Budget entities.
     *
     * @return the BudgetRepository
     */
    public BudgetRepository getBudgetRepository() { return budgetRepository; }

    /**
     * Gets the repository for Simulation entities.
     *
     * @return the SimulationRepository
     */
    public SimulationRepository getSimulationRepository() { return simulationRepository; }

    public void setSimulationRepository(SimulationRepository repo) {
        this.simulationRepository = repo;
    }

}
