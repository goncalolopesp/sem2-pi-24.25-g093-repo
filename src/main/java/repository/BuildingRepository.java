package repository;

import domain.Building;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BuildingRepository {
    private final List<Building> buildings;

    /**
     * Constructs a new MapRepository with an empty list of maps.
     */
    public BuildingRepository() { buildings = new ArrayList<>(); }

    /**
     * Adds a building to the repository if it is not already present.
     *
     * @param building the Building instance to add
     */
    public void add(Building building) {
        if (validateBuilding(building)) {
            buildings.add(building);
        }
    }

    /**
     * Validates that a building is not already contained in the repository.
     *
     * @param building the Building instance to validate
     * @return {@code true} if the building is not already present; {@code false} otherwise
     */
    private boolean validateBuilding(Building building) { return !buildings.contains(building); }

    /**
     * Returns a defensive copy of the list of all buildings in the repository.
     *
     * @return unmodifiable list of stored Building objects
     */
    public List<Building> getBuildings() {
        //This is a defensive copy, so that the repository cannot be modified from the outside.
        return List.copyOf(buildings);
    }
}
