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
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Successful upgrade replaces station")
    void testSuccessfulUpgrade() {
        CurrentUser.getInstance().setUser(user);
        Station original = new Station(Type.DEPOT, "Depot-1", "TestMap", user.getEmail(), 50, 100, 0, 30, 30, null);
        stationRepo.addStation(original);
        map.getStations().add(original);

        upgradeCtrl.upgradeType(original, Type.TERMINAL, null, 150);

        assertEquals(1, stationRepo.getStations().size());
        Station upgraded = stationRepo.getStations().get(0);
        assertEquals(Type.TERMINAL, upgraded.getType());
        assertEquals(30, upgraded.getX());
        assertEquals(30, upgraded.getY());
        assertEquals(null, upgraded.getOrientation());
        assertEquals(850, user.getBudgetValue());
    }

    @Test
    @DisplayName("Failed upgrade restores original station")
    void testFailedUpgrade() {
        CurrentUser.getInstance().setUser(user);
        Station original = new Station(Type.STATION, "Station-1", "TestMap", user.getEmail(), 100, 200, 0, 6, 6, Orientation.NE);
        stationRepo.addStation(original);
        map.getStations().add(original);

        user.setBudget(new Budget(0));

        boolean result = upgradeCtrl.upgradeType(original, Type.TERMINAL, Orientation.SE, 200);
        assertFalse(result, "Upgrade should fail when insufficient funds");

        assertEquals(1, stationRepo.getStations().size());
        Station restored = stationRepo.getStations().get(0);
        assertEquals(original.getType(), restored.getType());
        assertEquals(original.getX(), restored.getX());
        assertEquals(original.getY(), restored.getY());
        assertEquals(original.getOrientation(), restored.getOrientation());
        assertEquals(0, user.getBudgetValue());
    }
}
