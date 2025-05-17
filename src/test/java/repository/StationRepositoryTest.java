package repository;

import domain.Station;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StationRepositoryTest {
    private StationRepository repo;
    Station station1 = new Station(Station.Type.STATION,"Station1", "Map1", "Test","Player1", 1, 1, 1, 5,5, Station.Orientation.NE);
    @BeforeEach
    void setup(){
        repo = new StationRepository();
    }

    @Test
    void add_Repository(){
        assertEquals(true, repo.addStation(station1));
    }

    @Test
    void getStation_Repository(){
        repo.addStation(station1);
        List<Station> stations = repo.getStations();
        assertEquals(stations, repo.getStations());
    }

    @Test
    void removeStation(){
        repo.addStation(station1);
        assertEquals(true, repo.removeStation(station1));
    }

    @Test
    void removeStationsByMapAndOwner() {
        Station station1 = new Station(Station.Type.STATION, "Station1", "Map1", "Test" ,"player1@email.com", 1, 1, 1, 5, 5, Station.Orientation.NE);
        Station station2 = new Station(Station.Type.STATION, "Station2", "Map1", "Test" ,"player1@email.com", 2, 2, 2, 6, 6, Station.Orientation.NW);
        Station station3 = new Station(Station.Type.STATION, "Station3", "Map2", "Test2" ,"player1@email.com", 3, 3, 3, 7, 7, Station.Orientation.SE);
        Station station4 = new Station(Station.Type.STATION, "Station4", "Map1","Test6" ,"player2@email.com", 4, 4, 4, 8, 8, Station.Orientation.SW);
        repo.addStation(station1);
        repo.addStation(station2);
        repo.addStation(station3);
        repo.addStation(station4);
        assertEquals(4, repo.getStations().size());
        repo.removeStationsByMapScenarioAndOwner("Map1","Test", "player1@email.com");
        List<Station> remainingStations = repo.getStations();
        assertEquals(2, remainingStations.size());
        assertTrue(remainingStations.contains(station3));
        assertTrue(remainingStations.contains(station4));
        assertFalse(remainingStations.contains(station1));
        assertFalse(remainingStations.contains(station2));
    }

}