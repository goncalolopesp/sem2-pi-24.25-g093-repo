package controller;

import domain.Building;
import domain.CurrentUser;
import domain.User;
import repository.BuildingRepository;
import repository.Repositories;

import java.util.List;

public class UpgradeStationController {
    private BuildingRepository buildingRepository;
    private User currentUser = CurrentUser.getInstance().getUser();

    public UpgradeStationController() { getBuildingRepository(); }

    public UpgradeStationController(BuildingRepository buildingRepository) { this.buildingRepository = buildingRepository; }

    private BuildingRepository getBuildingRepository() {
        if (buildingRepository == null) {
            Repositories repositories = Repositories.getInstance();

            buildingRepository = repositories.getBuildingRepository();
        }
        return buildingRepository;
    }

    public Building upgradeStation(String stationName, String buildingType, String owner) {
        owner = currentUser.getEmail();
        Building building = new Building(stationName, buildingType, owner);
        getBuildingRepository().add(building);
        return building;
    }

    public List<Building> getBuildings() { return getBuildingRepository().getBuildings(); }

    public boolean verifyIfBuildingExists(String stationName, String buildingType) {
        if (getBuildings().stream().anyMatch(station -> station.getStationName().equals(stationName)) &&
                getBuildings().stream().anyMatch(type -> type.getBuildingType().equals(buildingType))) {
            return true;
        }
        return false;
    }
}
