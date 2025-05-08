package repository;

import domain.Building;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BuildingRepository {
    private final List<Building> buildings;

    public BuildingRepository() { buildings = new ArrayList<>(); }

    public void add(Building building) {
        if (validateBuilding(building)) {
            buildings.add(building);
        }
    }

    public Optional<Building> getBuildingType(String type) {
        return buildings.stream().filter(b -> b.getBuildingType().equals(type)).findFirst();
    }

    private boolean validateBuilding(Building building) { return !buildings.contains(building); }

    public List<Building> getBuildings() {
        //This is a defensive copy, so that the repository cannot be modified from the outside.
        return List.copyOf(buildings);
    }
}
