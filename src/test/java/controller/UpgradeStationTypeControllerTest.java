
package controller;

import domain.CurrentUser;
import domain.Map;
import domain.Station;
import domain.Station.Orientation;
import domain.Station.Type;
import domain.User;
import domain.Budget;
import repository.Repositories;
import repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpgradeStationTypeControllerTest {

    private StationRepository stationRepo;
    private Map map;
    private User user;
    private UpgradeStationTypeController upgradeCtrl;

    @BeforeEach
    void setUp() {
        stationRepo = Repositories.getInstance().getStationRepository();
        for (Station s : new java.util.ArrayList<>(stationRepo.getStations())) {
            stationRepo.removeStation(s);
        }

        map = new Map("TestMap", 50, 50);
        upgradeCtrl = new UpgradeStationTypeController(map);

        user = new User("Player", "player@example.com", "pwd", AuthController.ROLE_PLAYER, new Budget(1000));
        CurrentUser.getInstance().setUser(user);
    }

    @Test
    void testSuccessfulUpgrade() {
        CurrentUser.getInstance().setUser(user);
        Station original = new Station(Type.DEPOT, "Depot-1", "TestMap", user.getEmail(), 50, 100, 0, 30, 30, null);
        stationRepo.addStation(original);
        map.getStations().add(original);

        boolean result = upgradeCtrl.upgradeType(original, Type.TERMINAL, null, 150);

        assertTrue(result, "Upgrade should succeed with sufficient funds");
        assertEquals(1, stationRepo.getStations().size());
        Station upgraded = stationRepo.getStations().get(0);
        assertEquals(Type.TERMINAL, upgraded.getType());
        assertEquals(30, upgraded.getX());
        assertEquals(30, upgraded.getY());
        assertEquals(null, upgraded.getOrientation());
        assertEquals(850, user.getBudgetValue());
    }

    @Test
    void testUpgradeFailsDueToPositionOccupied() {
        Station original = new Station(Type.DEPOT, "Depot-1", "TestMap", user.getEmail(), 50, 100, 0, 10, 10, null);
        Station blocking = new Station(Type.STATION, "Blocking", "TestMap", user.getEmail(), 50, 100, 0, 11, 10, null);
        stationRepo.addStation(original);
        stationRepo.addStation(blocking);
        map.getStations().add(original);
        map.getStations().add(blocking);
        boolean result = upgradeCtrl.upgradeType(original, Type.TERMINAL, Orientation.SE, 150);
        assertFalse(result, "Upgrade should fail when position is already occupied");
        assertEquals(2, stationRepo.getStations().size());
        boolean originalRestored = stationRepo.getStations().stream()
                .anyMatch(s -> s.getName().equals("Depot-1") && s.getType() == Type.DEPOT);
        assertTrue(originalRestored, "Original station should be restored");
        assertEquals(1000, user.getBudgetValue(), "Budget should remain unchanged");
    }

    @Test
    void testUpgradeWithDifferentOrientation() {
        Station original = new Station(Type.STATION, "Station-1", "TestMap", user.getEmail(), 100, 200, 0, 20, 20, Orientation.NE);
        stationRepo.addStation(original);
        map.getStations().add(original);
        boolean result = upgradeCtrl.upgradeType(original, Type.STATION, Orientation.SE, 50);
        assertTrue(result, "Upgrade with orientation change should succeed");
        assertEquals(1, stationRepo.getStations().size());
        Station upgraded = stationRepo.getStations().get(0);
        assertEquals(Type.STATION, upgraded.getType());
        assertEquals(Orientation.SE, upgraded.getOrientation());
        assertEquals(950, user.getBudgetValue(), "Budget should be reduced by cost");
    }

    @Test
    void testUpgradeToSameType() {
        Station original = new Station(Type.TERMINAL, "Terminal-1", "TestMap", user.getEmail(), 200, 300, 150, 30, 30, Orientation.NE);
        stationRepo.addStation(original);
        map.getStations().add(original);
        boolean result = upgradeCtrl.upgradeType(original, Type.TERMINAL, Orientation.SW, 100);
        assertTrue(result, "Upgrade to same type should succeed");
        assertEquals(1, stationRepo.getStations().size());
        Station upgraded = stationRepo.getStations().get(0);
        assertEquals(Type.TERMINAL, upgraded.getType());
        assertEquals(Orientation.SW, upgraded.getOrientation());
        assertEquals(900, user.getBudgetValue(), "Budget should be reduced by cost");
    }

    @Test
    void testUpgradeFailsDueToNonExistentUser() {
        CurrentUser.getInstance().setUser(null);
        Station original = new Station(Type.DEPOT, "Depot-2", "TestMap", "player@example.com", 50, 100, 0, 40, 40, null);
        stationRepo.addStation(original);
        map.getStations().add(original);
        boolean result = upgradeCtrl.upgradeType(original, Type.TERMINAL, null, 150);
        assertFalse(result, "Upgrade should fail when user doesn't exist");
        assertEquals(1, stationRepo.getStations().size());
        Station restored = stationRepo.getStations().get(0);
        assertEquals(Type.DEPOT, restored.getType());
        CurrentUser.getInstance().setUser(user);
    }

    @Test
    void testUpgradePreservesCapacity() {
        Station original = new Station(Type.STATION, "Station-2", "TestMap", user.getEmail(), 100, 500, 250, 25, 25, Orientation.NE);
        stationRepo.addStation(original);
        map.getStations().add(original);
        boolean result = upgradeCtrl.upgradeType(original, Type.TERMINAL, Orientation.NW, 200);
        assertTrue(result, "Upgrade should succeed");
        assertEquals(1, stationRepo.getStations().size());
        Station upgraded = stationRepo.getStations().get(0);
        assertEquals(Type.TERMINAL, upgraded.getType());
        assertEquals(25, upgraded.getX());
        assertEquals(25, upgraded.getY());
        assertTrue(upgraded.getCapacity() > 0, "Capacity should be positive");
        assertEquals(800, user.getBudgetValue(), "Budget should be reduced by cost");
    }
}