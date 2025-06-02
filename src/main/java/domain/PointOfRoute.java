package domain;

import java.util.List;

/**
 * Represents a point in a route with a station and specific cargo types to pick up
 */
public class PointOfRoute {
    private Station station;
    private List<Carriage.Type> cargoTypes;

    // Add this field to PointOfRoute class
    private CargoMode cargoMode;

    public PointOfRoute(Station station, List<Carriage.Type> cargoTypes, CargoMode cargoMode) {
        this.station = station;
        this.cargoTypes = cargoTypes;
        this.cargoMode = cargoMode;
    }

    public CargoMode getCargoMode() {
        return cargoMode;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public List<Carriage.Type> getCargoTypes() {
        return cargoTypes;
    }

    public void setCargoTypes(List<Carriage.Type> cargoTypes) {
        this.cargoTypes = cargoTypes;
    }
}