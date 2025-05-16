package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildingTest {
    private Building building;
    private Station station = new Station(Station.Type.STATION,"Station1", "Map1", "Player1", 100, 200, 50, 5,5, Station.Orientation.NE);

    @BeforeEach
    void setUp() {
        building = new Building(station, "telegraph");
    }

    @Test
    void getStation() {
        assertEquals(station, building.getStation());
    }

    @Test
    void getBuildingType() {
        assertEquals("telegraph", building.getBuildingType());
    }

    @Test
    void toStringTest() {
        String expected = building.getBuildingType();
        assertEquals(expected, building.toString());
    }
}
