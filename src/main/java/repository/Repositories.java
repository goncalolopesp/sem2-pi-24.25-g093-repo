package repository;

public class Repositories {
    private static Repositories instance;
    private final MapRepository mapRepository;
    private final CityRepository cityRepository;
    private final ScenarioRepository scenarioRepository;

    private Repositories() {
        mapRepository = new MapRepository();
        cityRepository = new CityRepository();
        scenarioRepository = new ScenarioRepository();
    }

    public static Repositories getInstance() {
        if (instance == null) {
            synchronized (Repositories.class) {
                instance = new Repositories();
            }
        }
        return instance;
    }

    public MapRepository getMapRepository() { return mapRepository; }
    public CityRepository getCityRepository() { return cityRepository; }
    public ScenarioRepository getScenarioRepository() { return scenarioRepository; }
}
