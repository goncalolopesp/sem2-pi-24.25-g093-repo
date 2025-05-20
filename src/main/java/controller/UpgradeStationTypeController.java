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

    public UpgradeStationTypeController(Map map, Scenario scenario, Budget budget) {
        this.map = map;
        this.scenario = scenario;
        this.budget = budget;
    }

    public boolean upgradeType(Station station, Type targetType, Orientation orientation,Budget budget,int cost) {
        Station copy = new Station(
                station.getType(), station.getName(), station.getMapName(),station.getScenarioName(), station.getOwnerPlayer(),
                station.getPrice(), station.getCapacity(), station.getCapacityOwned(),station.getX(), station.getY(), station.getOrientation()
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
