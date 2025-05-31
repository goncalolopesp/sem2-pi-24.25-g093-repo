package domain;

import java.util.List;

/**
 * Represents a point in a route with a station and specific cargo types to pick up
 */
public class PointOfRoute {
    private Station station;
    private List<Carriage.Type> cargoTypes;

    public PointOfRoute(Station station, List<Carriage.Type> cargoTypes) {
        this.station = station;
        this.cargoTypes = cargoTypes;
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