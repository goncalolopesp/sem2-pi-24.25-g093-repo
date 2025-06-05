package controller;

import domain.Budget;
import domain.Map;
import domain.Scenario;
import domain.Station;
import domain.Station.Orientation;
import domain.Station.Type;
import repository.Repositories;
import repository.StationRepository;

/**
 * Controller responsible for upgrading a station's type.
 * Replaces original station, attempts creation, and restores on failure.
 */
public class UpgradeStationTypeController {
    private final Map map;
    private final Scenario scenario;
    Budget budget;
    private final StationRepository stationRepo = Repositories.getInstance().getStationRepository();
    private final CreateStationController createCtrl = new CreateStationController();

    /**
     * Constructs a new UpgradeStationTypeController, initializing required dependencies
     * for upgrading station types within a given map and scenario, using the provided budget.
     *
     * @param map the map containing all stations and industries
     * @param scenario the scenario associated with the operations
     * @param budget the budget available for station upgrades
     */
    public UpgradeStationTypeController(Map map, Scenario scenario, Budget budget) {
        this.map = map;
        this.scenario = scenario;
        this.budget = budget;
    }

    /**
     * Attempts to upgrade the type of an existing station to a target type.
     * If the upgrade fails, the original station is restored.
     *
     * @param station the station to be upgraded
     * @param targetType the target type for the station after the upgrade
     * @param orientation the orientation for the upgraded station
     * @param budget the budget constraint for the upgrade
     * @param cost the cost of performing the upgrade
     * @return true if the station is successfully upgraded, otherwise false
     */
    public boolean upgradeType(Station station, Type targetType, Orientation orientation,Budget budget,int cost) {
        Station copy = new Station(
                station.getType(), station.getName(), station.getMapName(),station.getScenarioName(), station.getOwnerPlayer(),
                station.getPrice(), station.getCapacity(), station.getX(), station.getY(), station.getOrientation()
        );

        stationRepo.removeStation(station);
        map.getStations().remove(station);

        CreateStationController.Error err = createCtrl.buildStation(
                targetType.name(), map, scenario,
                station.getX(), station.getY(), orientation, budget, cost
        );
        if (err == CreateStationController.Error.OK) {
            return true;
        } else {
            stationRepo.addStation(copy);
            map.getStations().add(copy);
            System.out.println("Upgrade failed: " + err + ". Original station restored.");
            return false;
        }
    }
}
