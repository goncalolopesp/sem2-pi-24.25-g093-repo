package domain;

import static domain.Station.Orientation.NE;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class StationTest {
    private Station station;

    @BeforeEach
    void setUp() {
        station = new Station(Station.Type.STATION,"madrid-STATION","MapTest","Test","Player@this.app",100,200,0,5, NE);
    }

    @Test
    void getType_ReturnsCorrectValue() {
        assertEquals(Station.Type.STATION   , station.getType());
    }

    @Test
    void getName_ReturnsCorrectValue() {
        assertEquals("madrid-STATION"   , station.getName());
    }

    @Test
    void getMap_ReturnsCorrectValue() {
        assertEquals("MapTest"   , station.getMapName());
    }

    @Test
    void getScenario_ReturnsCorrectValue() {
        assertEquals("Test"   , station.getScenarioName());
    }

    @Test
    void getOwnerPlayer_ReturnsCorrectValue() {
        assertEquals("Player@this.app"   , station.getOwnerPlayer());
    }

    @Test
    void getPrice_ReturnsCorrectValue() {
        assertEquals(100   , station.getPrice());
    }

    @Test
    void getCapacity_ReturnsCorrectValue() {
        assertEquals(200   , station.getCapacity());
    }

    @Test
    void getCapacityOwned_ReturnsCorrectValue() {
        assertEquals(0  , station.getCapacityOwned());
    }

    @Test
    void getX_ReturnsCorrectValue() {
        assertEquals(5  , station.getX());
    }

    @Test
    void getY_ReturnsCorrectValue() {
        assertEquals(5  , station.getY());
    }

    @Test
    void setCapacityOwned_SetsCorrectValue() {
        station.setCapacityOwned(100);
        assertEquals(100, station.getCapacityOwned());
    }

    @Test
    void getOrientation_ReturnsCorrectValue() {
        assertEquals(Station.Orientation.NE, station.getOrientation());
    }

    @Test
    void printStation(){
        String teste = station.toString();
        assertEquals(teste,station.toString());
    }
}