package repository;

public class Repositories {
    private static Repositories instance;
    private final MapRepository mapRepository;
    private final CityRepository cityRepo;

    private Repositories() {
        mapRepository = new MapRepository();
        cityRepo = new CityRepository();
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
    public CityRepository getCityRepository() { return cityRepo; }
}
