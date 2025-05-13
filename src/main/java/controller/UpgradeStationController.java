package controller;

import domain.Building;
import domain.Map;
import domain.Station;
import repository.BuildingRepository;
import repository.Repositories;

import java.util.List;

/**
 * Controller responsible for the creation and validation of station upgrades.
 * <p>
 * Delegates storage to BuildingRepository.
 * </p>
 */
public class UpgradeStationController {
    private BuildingRepository buildingRepository;

    /**
     * Constructs an UpgradeStationController using the shared repositories instance.
     */
    public UpgradeStationController() { getBuildingRepository(); }

    /**
     * Constructs an UpgradeStationController with a provided BuildingRepository (for testing).
     *
     * @param buildingRepository the BuildingRepository to use
     */
    public UpgradeStationController(BuildingRepository buildingRepository) { this.buildingRepository = buildingRepository; }

    /**
     * Returns the BuildingRepository, initializing it if necessary.
     *
     * @return the BuildingRepository instance
     */
    private BuildingRepository getBuildingRepository() {
        if (buildingRepository == null) {
            Repositories repositories = Repositories.getInstance();

            buildingRepository = repositories.getBuildingRepository();
        }
        return buildingRepository;
    }

    /**
     * Upgrades a station with the given name and building type.
     *
     * @param station the name of the station to be upgraded
     * @param buildingType the type of building to be added to the station
     * @return the created Building instance for the station
     */
    public Building upgradeStation(Station station, String buildingType) {
        Building building = new Building(station, buildingType);
        getBuildingRepository().add(building);
        return building;
    }

    /**
     * Retrieves the list of all buildings stored in the repository.
     *
     * @return unmodifiable list of existing {@link Building} instances
     */
    public List<Building> getBuildings() { return getBuildingRepository().getBuildings(); }

    /**
     * Verifies if provided building type already exists in provided station.
     *
     * @return true if it does or false if otherwise.
     */
    public boolean verifyIfBuildingExists(Station station, String buildingType) {
        if (getBuildings().stream().anyMatch(s -> s.getStation().equals(station)) &&
                getBuildings().stream().anyMatch(type -> type.getBuildingType().equals(buildingType))) {
            return true;
        }
        return false;
    }
}
