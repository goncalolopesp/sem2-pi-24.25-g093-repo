package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PointOfRouteTest {

    private Station station;
    private List<Carriage.Type> cargoTypes;
    private CargoMode cargoMode;
    private PointOfRoute pointOfRoute;

    @BeforeEach
    void setUp() {
        Station station = new Station(Station.Type.DEPOT, "EN002", "Map002", "Test", "Player002", 100, 200, 30, 45, Station.Orientation.NE);
        cargoTypes = Arrays.asList(Carriage.Type.PEOPLE, Carriage.Type.MAIL);
        cargoMode = CargoMode.FULL;
        pointOfRoute = new PointOfRoute(station, cargoTypes, cargoMode);
    }

    @Test
    void whenSetNewStation_thenStationShouldBeUpdated() {
        Station newStation = new Station(Station.Type.DEPOT, "EN002", "Map002", "Test", "Player002", 100, 200, 0, 5, Station.Orientation.NE);
        pointOfRoute.setStation(newStation);
        assertEquals(newStation, pointOfRoute.getStation());
    }

    @Test
    void whenGetCargoTypes_thenShouldReturnCorrectList() {
        List<Carriage.Type> expectedCargoTypes = Arrays.asList(Carriage.Type.PEOPLE, Carriage.Type.MAIL);
        assertEquals(expectedCargoTypes, pointOfRoute.getCargoTypes());
    }

    @Test
    void whenCreatePointOfRouteWithNullStation_thenShouldNotThrowException() {
        PointOfRoute nullStationPoint = new PointOfRoute(null, cargoTypes, cargoMode);
        assertNull(nullStationPoint.getStation());
    }

    @Test
    void whenCreatePointOfRouteWithEmptyCargoTypes_thenShouldAcceptEmptyList() {
        List<Carriage.Type> emptyCargoTypes = Arrays.asList();
        PointOfRoute emptyCargoPoint = new PointOfRoute(station, emptyCargoTypes, cargoMode);
        assertTrue(emptyCargoPoint.getCargoTypes().isEmpty());
    }
}
