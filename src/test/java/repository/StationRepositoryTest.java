package repository;

import domain.Station;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StationRepositoryTest {
    private StationRepository repo;
    Station station1 = new Station(Station.Type.STATION,"Station1", "Map1", "Player1", 1, 1, 1, 5,5, Station.Orientation.NE);
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

}