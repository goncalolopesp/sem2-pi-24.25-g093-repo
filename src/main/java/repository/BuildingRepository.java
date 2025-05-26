package repository;

import domain.Building;
import domain.Station;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BuildingRepository implements Serializable {
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

    public void remove(Station station, String buildingType) {
        buildings.removeIf(b ->
                b.getStation().equals(station) &&
                        b.getBuildingType().equalsIgnoreCase(buildingType)
        );
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

    /**
     * Copies the list of buildings from another {@code BuildingRepository} instance to this instance.
     * This operation clears the current list of buildings in this repository and replaces it
     * with a copy of the buildings from the provided repository.
     *
     * @param other the {@code BuildingRepository} instance from which buildings are copied
     */
    public void copyFrom(BuildingRepository other) {
        this.buildings.clear();
        this.buildings.addAll(other.getBuildings());
    }
}
