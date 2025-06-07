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

    /**
     * Constructs a PointOfRoute object with a specified station, list of cargo types, and cargo mode.
     *
     * @param station    the station associated with this point in the route
     * @param cargoTypes the list of cargo types to be handled at this point in the route
     * @param cargoMode  the mode of cargo operation at this point in the route
     */
    public PointOfRoute(Station station, List<Carriage.Type> cargoTypes, CargoMode cargoMode) {
        this.station = station;
        this.cargoTypes = cargoTypes;
        this.cargoMode = cargoMode;
    }

    /**
     * Retrieves the station associated with this point in the route.
     *
     * @return the station linked to this point in the route
     */
    public Station getStation() {
        return station;
    }

    /**
     * Sets the station associated with this point in the route.
     *
     * @param station the station to be set for this point in the route
     */
    public void setStation(Station station) {
        this.station = station;
    }

    /**
     * Retrieves the list of cargo types associated with this point in the route.
     *
     * @return a list of cargo types represented by {@code Carriage.Type}
     */
    public List<Carriage.Type> getCargoTypes() {
        return cargoTypes;
    }

    /**
     * Retrieves the cargo mode for this point in the route.
     *
     * @return the cargo mode of type {@code CargoMode}, representing the mode of cargo handling or utilization
     */
    public CargoMode getCargoMode() {
        return cargoMode;
    }

}