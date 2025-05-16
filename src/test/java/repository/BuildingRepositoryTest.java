package repository;

import domain.Building;
import domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuildingRepositoryTest {
    private BuildingRepository repo;
    private Station station = new Station(Station.Type.STATION,"Station1", "Map1", "Player1", 100, 200, 50, 5,5, Station.Orientation.NE);
    private Building building = new Building(station, "telegraph");

    @BeforeEach
    void setup() {
        repo = new BuildingRepository();
        repo.add(building);
        repo.add(new Building(station, "telephone"));
    }

    @Test
    void add() {
        repo.add(new Building(station, "small cafe"));
        assertEquals(3, repo.getBuildings().size());
    }

    @Test
    void remove() {
        repo.remove(station, "telegraph");
        assertEquals(1, repo.getBuildings().size());
    }

    @Test
    void validateBuilding() {
        assertTrue(repo.getBuildings().contains(building));
        assertFalse(repo.getBuildings().contains(new Building(station, "large cafe")) );
    }

    @Test
    void getBuildings() {
        List<Building> buildings = repo.getBuildings();
        assertEquals(2, buildings.size());
        assertEquals("telegraph", buildings.get(0).getBuildingType());
        assertEquals("telephone", buildings.get(1).getBuildingType());
    }
}
