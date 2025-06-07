package controller;

import domain.CurrentUser;
import domain.Map;
import domain.Scenario;
import domain.Station;
import domain.Station.Orientation;
import domain.Station.Type;
import domain.User;
import domain.Budget;
import repository.Repositories;
import repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class UpgradeStationTypeControllerTest {

    private StationRepository stationRepo;
    private Map map;
    private Scenario scenario;
    private User user;
    private Budget budget;
    private UpgradeStationTypeController upgradeCtrl;

    @BeforeEach
    void setUp() {
        stationRepo = Repositories.getInstance().getStationRepository();
        for (Station s : new java.util.ArrayList<>(stationRepo.getStations())) {
            stationRepo.removeStation(s);
        }

        map = new Map("TestMap", 50, 50,1);
        int[] timeRange = {1928, 1938};
        scenario = new Scenario("TestScenario", "TestMap",  new int[]{1870, 1880},
                1,
                Collections.singletonList("steam"),
                Collections.singletonList("none"),
                1.0,
                1.0);;;
        budget = new Budget(1000, map, scenario, null);
        user = new User("Player", "player@example.com", "pwd", AuthController.ROLE_PLAYER);
        budget.setUser(user);

        CurrentUser.getInstance().setUser(user);
        upgradeCtrl = new UpgradeStationTypeController(map, scenario, budget);
    }

    @Test
    void testSuccessfulUpgrade() {
        Station original = new Station(Type.DEPOT, "Depot-1", "TestMap", "TestScenario",
                user.getEmail(), 50, 100, 0, 30, Orientation.NE);
        stationRepo.addStation(original);
        map.getStations().add(original);
        boolean result = upgradeCtrl.upgradeType(original, Type.TERMINAL, null, budget, 150);
        assertTrue(result, "Upgrade should succeed with sufficient funds");
        assertEquals(1, stationRepo.getStations().size());
        Station upgraded = stationRepo.getStations().get(0);
        assertEquals(Type.TERMINAL, upgraded.getType());
        assertEquals(30, upgraded.getX());
        assertEquals(30, upgraded.getY());
        assertEquals(null, upgraded.getOrientation());
        assertEquals(850, budget.getBudget());
    }

    @Test
    void testUpgradeFailsDueToPositionOccupied() {
        Station original = new Station(Type.DEPOT, "Depot-1", "TestMap", "TestScenario",
                user.getEmail(), 50, 100, 0, 10, Orientation.NE);
        Station blocking = new Station(Type.STATION, "Blocking", "TestMap", "TestScenario",
                user.getEmail(), 50, 100, 0, 11, Orientation.NE);
        stationRepo.addStation(original);
        stationRepo.addStation(blocking);
        map.getStations().add(original);
        map.getStations().add(blocking);
        boolean result = upgradeCtrl.upgradeType(original, Type.TERMINAL, Orientation.SE, budget, 150);
        assertFalse(result, "Upgrade should fail when position is already occupied");
        assertEquals(2, stationRepo.getStations().size());
        boolean originalRestored = stationRepo.getStations().stream()
                .anyMatch(s -> s.getName().equals("Depot-1") && s.getType() == Type.DEPOT);
        assertTrue(originalRestored, "Original station should be restored");
        assertEquals(1000, budget.getBudget(), "Budget should remain unchanged");
    }

    @Test
    void testUpgradeWithDifferentOrientation() {
        Station original = new Station(Type.STATION, "Station-1", "TestMap", "TestScenario",
                user.getEmail(), 100, 200, 0, 20, Orientation.NE);
        stationRepo.addStation(original);
        map.getStations().add(original);
        boolean result = upgradeCtrl.upgradeType(original, Type.STATION, Orientation.SE, budget, 50);
        assertTrue(result, "Upgrade with orientation change should succeed");
        assertEquals(1, stationRepo.getStations().size());
        Station upgraded = stationRepo.getStations().get(0);
        assertEquals(Type.STATION, upgraded.getType());
        assertEquals(Orientation.SE, upgraded.getOrientation());
        assertEquals(950, budget.getBudget(), "Budget should be reduced by cost");
    }

    @Test
    void testUpgradeToSameType() {
        Station original = new Station(Type.TERMINAL, "Terminal-1", "TestMap", "TestScenario",
                user.getEmail(), 200, 300, 150, 30, Orientation.NE);
        stationRepo.addStation(original);
        map.getStations().add(original);
        boolean result = upgradeCtrl.upgradeType(original, Type.TERMINAL, Orientation.SW, budget, 100);
        assertTrue(result, "Upgrade to same type should succeed");
        assertEquals(1, stationRepo.getStations().size());
        Station upgraded = stationRepo.getStations().get(0);
        assertEquals(Type.TERMINAL, upgraded.getType());
        assertEquals(Orientation.SW, upgraded.getOrientation());
        assertEquals(900, budget.getBudget(), "Budget should be reduced by cost");
    }


    @Test
    void testUpgradePreservesCapacity() {
        Station original = new Station(Type.STATION, "Station-2", "TestMap", "TestScenario",
                user.getEmail(), 100, 500, 250, 25, Orientation.NE);
        stationRepo.addStation(original);
        map.getStations().add(original);
        boolean result = upgradeCtrl.upgradeType(original, Type.TERMINAL, Orientation.NW, budget, 200);
        assertTrue(result, "Upgrade should succeed");
        assertEquals(1, stationRepo.getStations().size());
        Station upgraded = stationRepo.getStations().get(0);
        assertEquals(Type.TERMINAL, upgraded.getType());
        assertEquals(25, upgraded.getX());
        assertEquals(25, upgraded.getY());
        assertTrue(upgraded.getCapacity() > 0, "Capacity should be positive");
        assertEquals(800, budget.getBudget(), "Budget should be reduced by cost");
    }
}